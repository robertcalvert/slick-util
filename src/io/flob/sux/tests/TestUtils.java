package io.flob.sux.tests;

import io.flob.sux.Color;
import io.flob.sux.Sys;
import io.flob.sux.TrueTypeFont;
import io.flob.sux.openal.Audio;
import io.flob.sux.openal.AudioLoader;
import io.flob.sux.openal.SoundStore;
import io.flob.sux.opengl.Texture;
import io.flob.sux.opengl.TextureLoader;
import io.flob.sux.util.Log;
import io.flob.sux.util.ResourceLoader;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * A simple utility test to use the internal SUX API framework.
 *
 * @author kevin
 * @author rcalvert
 */
public class TestUtils {

    /**
     * The display window handle
     */
    private long window;
    private final int WIDTH = 400;
    private final int HEIGHT = 400;
    /**
     * Reference for callback
     */
    private GLFWErrorCallback errorCallback;
    /**
     * Reference for callback
     */
    private GLFWKeyCallback keyCallback;

    /**
     * The texture that's been loaded
     */
    private Texture texture;
    /**
     * The OGG sound effect
     */
    private Audio oggEffect;
    /**
     * The WAV sound effect
     */
    private Audio wavEffect;
    /**
     * The AIF source effect
     */
    private Audio aifEffect;
    /**
     * The OGG stream thats been loaded
     */
    private Audio oggStream;
    /**
     * The MOD stream thats been loaded
     */
    private Audio modStream;
    /**
     * The font to draw to the screen
     */
    private TrueTypeFont font1;
    /**
     * The font to draw to the screen
     */
    private TrueTypeFont font2;

    /**
     * Entry point to the tests
     *
     * @param args The arguments to the test
     */
    public static void main(String[] args) {
        new TestUtils().run();
    }

    /**
     * Run the test
     */
    public void run() {

        Log.setVerbose(false);
        System.out.println("LWJGL: " + org.lwjgl.Sys.getVersion());
        System.out.println("SUX: " + Sys.getVersion());

        try {
            initWindow();
            initGL();
            initResources();
            initKeyboard();
            loop();

            // Release window and window callbacks
            glfwDestroyWindow(window);
            keyCallback.release();
        } finally {
            // Destroy the SUX sound store
            SoundStore.get().destroy();
            // Terminate GLFW and release the GLFWErrorCallback
            glfwTerminate();
            errorCallback.release();
        }
    }

    /**
     * Initialise the GL window
     */
    private void initWindow() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        glfwSetErrorCallback(errorCallback = Callbacks.errorCallbackPrint(System.err));

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (glfwInit() != GL_TRUE) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(WIDTH, HEIGHT, "SUX " + Sys.getVersion(), NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Get the resolution of the primary monitor
        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(
                window,
                (GLFWvidmode.width(vidmode) - WIDTH) / 2,
                (GLFWvidmode.height(vidmode) - HEIGHT) / 2
        );

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    /**
     * Setup GL
     */
    private void initGL() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_TEXTURE_2D);
        glShadeModel(GL_SMOOTH);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_LIGHTING);

        // Set the clear color
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClearDepth(1);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glViewport(0, 0, WIDTH, HEIGHT);
        glMatrixMode(GL_MODELVIEW);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, WIDTH, HEIGHT, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
    }

    /**
     * Initialise the resources
     */
    private void initResources() {
        try {
            texture = TextureLoader.getTexture("PNG", new FileInputStream("../test/resource/texture/wall.png"));

            System.out.println("Texture loaded: " + texture);
            System.out.println(">> Image width: " + texture.getImageWidth());
            System.out.println(">> Image height: " + texture.getImageHeight());
            System.out.println(">> Texture width: " + texture.getTextureWidth());
            System.out.println(">> Texture height: " + texture.getTextureHeight());
            System.out.println(">> Texture ID: " + texture.getTextureID());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Font awtFont = new Font("Times New Roman", Font.BOLD, 16);
            font1 = new TrueTypeFont(awtFont, true);

            InputStream inputStream = ResourceLoader.getResourceAsStream("../test/resource/font/Grand9KPixel.ttf");
            Font awtFont2 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtFont2 = awtFont2.deriveFont(16f);
            font2 = new TrueTypeFont(awtFont2, true);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // you can play oggs by loading the complete thing into  a sound
            oggEffect = AudioLoader.getAudio("OGG",
                    new FileInputStream("../test/resource/audio/restart.ogg"));

            // or setting up a stream to read from. Note that the argument becomes
            // a URL here so it can be reopened when the stream is complete. Probably
            // should have reset the stream by thats not how the original stuff worked
            oggStream = AudioLoader.getStreamingAudio("OGG",
                    new File("../test/resource/audio/bongos.ogg").toURI().toURL());

            // you can play aifs by loading the complete thing into a sound
            aifEffect = AudioLoader.getAudio("AIF",
                    new FileInputStream("../test/resource/audio/burp.aif"));

            // you can play wavs by loading the complete thing into a sound
            wavEffect = AudioLoader.getAudio("WAV",
                    new FileInputStream("../test/resource/audio/coin.wav"));

            // can load mods (XM, MOD) using ibxm which is then played through OpenAL. MODs
            // are always streamed based on the way IBXM works
            modStream = AudioLoader.getStreamingAudio("MOD",
                    new File("../test/resource/audio/SMB-X.XM").toURI().toURL());

            // playing as music uses that reserved source to play the sound. The first
            // two arguments are pitch and gain, the boolean is whether to loop the content
            modStream.playAsMusic(1.0f, 1.0f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialise the keyboard
     */
    private void initKeyboard() {

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, GL_TRUE);
                }
                if (key == GLFW_KEY_Q && action == GLFW_PRESS) {
                    // play as a one off sound effect
                    oggEffect.playAsSoundEffect(1.0f, 1.0f, false);
                }
                if (key == GLFW_KEY_W && action == GLFW_PRESS) {
                    // replace the music thats curretly playing with the OGG
                    oggStream.playAsMusic(1.0f, 1.0f, true);
                }
                if (key == GLFW_KEY_E && action == GLFW_PRESS) {
                    // replace the music thats curretly playing with the mod
                    modStream.playAsMusic(1.0f, 1.0f, true);
                }
                if (key == GLFW_KEY_R && action == GLFW_PRESS) {
                    // play as a one off sound effect
                    aifEffect.playAsSoundEffect(1.0f, 1.0f, false);
                }
                if (key == GLFW_KEY_T && action == GLFW_PRESS) {
                    // play as a one off sound effect
                    wavEffect.playAsSoundEffect(1.0f, 1.0f, false);
                }
            }
        });
    }

    /**
     * Game loop
     */
    private void loop() {

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (glfwWindowShouldClose(window) == GL_FALSE) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            update();
            render();
            glfwSwapBuffers(window); // swap the color buffers
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    /**
     * Game loop update
     */
    public void update() {
        // polling is required to allow streaming to get a chance to queue buffers.
        SoundStore.get().poll(0);
    }

    /**
     * Game loop render
     */
    public void render() {
        // Flush out any bleeding
        Color.white.bind();

        // Render the image
        texture.bind();
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(10, 10);
        glTexCoord2f(1, 0);
        glVertex2f(10 + texture.getTextureWidth(), 10);
        glTexCoord2f(1, 1);
        glVertex2f(10 + texture.getTextureWidth(), 10 + texture.getTextureHeight());
        glTexCoord2f(0, 1);
        glVertex2f(10, 10 + texture.getTextureHeight());
        glEnd();

        // Render the text
        font1.drawString(10, 150, "Hello LWJGL " + org.lwjgl.Sys.getVersion() + " world!", Color.yellow);
        font2.drawString(10, 180, "Hello LWJGL " + org.lwjgl.Sys.getVersion() + " world!", Color.green);
    }

}
