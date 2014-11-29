package org.newdawn.slick.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.lwjgl.system.glfw.ErrorCallback;
import org.lwjgl.system.glfw.GLFW;
import static org.lwjgl.system.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.system.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.system.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.system.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.system.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.system.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.system.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.system.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.system.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.system.glfw.GLFW.glfwWindowShouldClose;
import org.lwjgl.system.glfw.GLFWvidmode;
import org.lwjgl.system.glfw.WindowCallback;
import org.lwjgl.system.glfw.WindowCallbackAdapter;
import org.newdawn.slick.About;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.Log;

/**
 * A simple utility test to use the internal slick API without the slick
 * framework.
 *
 * @author kevin
 */
public class TestUtils {

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
    private Font font;
    /**
     * The display window
     */
    private long window;

    /**
     * Entry point to the tests
     *
     * @param argv The arguments to the test
     */
    public static void main(String[] argv) {
        TestUtils utils = new TestUtils();
        utils.start();
    }

    /**
     * Start the test
     */
    public void start() {
        initGL(400, 400);
        init();

        while (true) {
            GLContext.createFromCurrent();
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            while (glfwWindowShouldClose(window) == GL11.GL_FALSE) {
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                update();
                render();
                glfwSwapBuffers(window);
                glfwPollEvents();
            }
            SoundStore.get().destroy();
            System.exit(0);
        }
    }

    /**
     * Initialise the GL display
     *
     * @param width The width of the display
     * @param height The height of the display
     */
    @SuppressWarnings("CallToPrintStackTrace")
    private void initGL(int width, int height) {
        GLFW.glfwSetErrorCallback(ErrorCallback.Util.getDefault());
        if (GLFW.glfwInit() != GL11.GL_TRUE) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);

        int WIDTH = width;
        int HEIGHT = height;

        window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, "Hello LWJGL3 world!", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
                window,
                (GLFWvidmode.width(vidmode) - WIDTH) / 2,
                (GLFWvidmode.height(vidmode) - HEIGHT) / 2
        );

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        glfwShowWindow(window);

        GLContext.createFromCurrent();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glClearDepth(1);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glViewport(0, 0, width, height);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, width, height, 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    /**
     * Initialise resources
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public void init() {

        System.out.println("Version: " + About.VERSION);

        // turn off all but errors
        Log.setVerbose(false);

        java.awt.Font awtFont = new java.awt.Font("Times New Roman", java.awt.Font.BOLD, 16);
        font = new TrueTypeFont(awtFont, false);

        try {
            texture = TextureLoader.getTexture("PNG", new FileInputStream("../test/resource/texture/wall.png"));

            System.out.println("Texture loaded: " + texture);
            System.out.println(">> Image width: " + texture.getImageWidth());
            System.out.println(">> Image height: " + texture.getImageWidth());
            System.out.println(">> Texture width: " + texture.getTextureWidth());
            System.out.println(">> Texture height: " + texture.getTextureHeight());
            System.out.println(">> Texture ID: " + texture.getTextureID());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // you can play oggs by loading the complete thing into 
            // a sound
            oggEffect = AudioLoader.getAudio("OGG", new FileInputStream("../test/resource/audio/restart.ogg"));

            // or setting up a stream to read from. Note that the argument becomes
            // a URL here so it can be reopened when the stream is complete. Probably
            // should have reset the stream by thats not how the original stuff worked
            oggStream = AudioLoader.getStreamingAudio("OGG", new File("../test/resource/audio/bongos.ogg").toURI().toURL());

            // can load mods (XM, MOD) using ibxm which is then played through OpenAL. MODs
            // are always streamed based on the way IBXM works
            // TODO: PORT - mod
            // modStream = AudioLoader.getStreamingAudio("MOD", new File("../test/resource/audio/SMB-X.XM").toURI().toURL());
            
            // playing as music uses that reserved source to play the sound. The first
            // two arguments are pitch and gain, the boolean is whether to loop the content
            // TODO: PORT - mod
            // modStream.playAsMusic(1.0f, 1.0f, true);
            
            // you can play aifs by loading the complete thing into 
            // a sound
            aifEffect = AudioLoader.getAudio("AIF", new FileInputStream("../test/resource/audio/burp.aif"));

            // you can play wavs by loading the complete thing into 
            // a sound
            wavEffect = AudioLoader.getAudio("WAV", new FileInputStream("../test/resource/audio/coin.wav"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // TODO: Test after exception
            //oggStream.playAsMusic(1.0f, 1.0f, true); //YES!
            //aifEffect.playAsSoundEffect(1.0f, 1.0f, false); //YES!
            wavEffect.playAsSoundEffect(1.0f, 1.0f, false); // YES!
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Game loop update
     */
    public void update() {
        WindowCallback.set(window, new WindowCallbackAdapter() {
            @Override
            public void key(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
                    glfwSetWindowShouldClose(window, GL11.GL_TRUE);
                }
                if (key == GLFW.GLFW_KEY_Q && action == GLFW.GLFW_PRESS) {
                    // play as a one off sound effect
                    oggEffect.playAsSoundEffect(1.0f, 1.0f, false);
                }
                if (key == GLFW.GLFW_KEY_W && action == GLFW.GLFW_PRESS) {
                    // replace the music thats curretly playing with the OGG
                    oggStream.playAsMusic(1.0f, 1.0f, true);
                }
                if (key == GLFW.GLFW_KEY_E && action == GLFW.GLFW_PRESS) {
                    // replace the music thats curretly playing with the mod
                    modStream.playAsMusic(1.0f, 1.0f, true);
                }
                if (key == GLFW.GLFW_KEY_R && action == GLFW.GLFW_PRESS) {
                    // play as a one off sound effect
                    aifEffect.playAsSoundEffect(1.0f, 1.0f, false);
                }
                if (key == GLFW.GLFW_KEY_T && action == GLFW.GLFW_PRESS) {
                    // play as a one off sound effect
                    wavEffect.playAsSoundEffect(1.0f, 1.0f, false);
                }
            }
        });

        // polling is required to allow streaming to get a chance to
        // queue buffers.
        SoundStore.get().poll(0);
    }

    /**
     * Game loop render
     */
    public void render() {
        Color.white.bind();
        texture.bind();

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(10, 10);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(10 + texture.getTextureWidth(), 10);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(10 + texture.getTextureWidth(), 10 + texture.getTextureHeight());
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(10, 10 + texture.getTextureHeight());
        GL11.glEnd();

        font.drawString(10, 150, "Hello LWJGL3 world!", Color.yellow);
    }

}
