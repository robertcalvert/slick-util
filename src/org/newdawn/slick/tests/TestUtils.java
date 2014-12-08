package org.newdawn.slick.tests;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import static org.lwjgl.system.MemoryUtil.NULL;
import org.newdawn.slick.Color;
import org.newdawn.slick.Sys;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

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
    private TrueTypeFont font1;
    /**
     * The font to draw to the screen
     */
    private TrueTypeFont font2;
    /**
     * The display window
     */
    private long window;
    /**
     * Reference for callback
     */
    private GLFWErrorCallback errorCallBack;
    /**
     * Reference for callback
     */
    private GLFWKeyCallback keyCallBack;

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
        System.out.println("LWJGL: " + org.lwjgl.Sys.getVersion());
        System.out.println("slick-util: " + Sys.getVersion());

        try {
            initGL(600, 400);
            init();

            GLContext.createFromCurrent();
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            while (GLFW.glfwWindowShouldClose(window) == GL11.GL_FALSE) {
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                update();
                render();
                GLFW.glfwSwapBuffers(window);
                GLFW.glfwPollEvents();
            }

            GLFW.glfwDestroyWindow(window);
            keyCallBack.release();
        } finally {
            SoundStore.get().destroy();
            GLFW.glfwTerminate();
            errorCallBack.release();
        }
    }

    /**
     * Initialise the GL display
     *
     * @param width The width of the display
     * @param height The height of the display
     */
    private void initGL(int width, int height) {
        GLFW.glfwSetErrorCallback(errorCallBack = Callbacks.errorCallbackPrint(System.err));
        if (GLFW.glfwInit() != GL11.GL_TRUE) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);

        int WIDTH = width;
        int HEIGHT = height;

        window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, "slick-util " + Sys.getVersion(), NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        ByteBuffer vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSetWindowPos(
                window,
                (GLFWvidmode.width(vidmode) - WIDTH) / 2,
                (GLFWvidmode.height(vidmode) - HEIGHT) / 2
        );

        GLFW.glfwMakeContextCurrent(window);
        GLFW.glfwSwapInterval(1);

        GLFW.glfwShowWindow(window);

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
        // turn off all but errors
        Log.setVerbose(false);

        try {
            
            Font awtFont = new Font("Times New Roman", Font.BOLD, 16);
            font1 = new TrueTypeFont(awtFont, true);
            
            InputStream inputStream = ResourceLoader.getResourceAsStream("../test/resource/font/Grand9KPixel.ttf");
            Font awtFont2 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtFont2 = awtFont2.deriveFont(16);
            font2 = new TrueTypeFont(awtFont2, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
            // you can play oggs by loading the complete thing into 
            // a sound
            oggEffect = AudioLoader.getAudio("OGG", new FileInputStream("../test/resource/audio/restart.ogg"));

            // or setting up a stream to read from. Note that the argument becomes
            // a URL here so it can be reopened when the stream is complete. Probably
            // should have reset the stream by thats not how the original stuff worked
            oggStream = AudioLoader.getStreamingAudio("OGG", new File("../test/resource/audio/bongos.ogg").toURI().toURL());

            // you can play aifs by loading the complete thing into 
            // a sound
            aifEffect = AudioLoader.getAudio("AIF", new FileInputStream("../test/resource/audio/burp.aif"));

            // you can play wavs by loading the complete thing into 
            // a sound
            wavEffect = AudioLoader.getAudio("WAV", new FileInputStream("../test/resource/audio/coin.wav"));

            // can load mods (XM, MOD) using ibxm which is then played through OpenAL. MODs
            // are always streamed based on the way IBXM works
            modStream = AudioLoader.getStreamingAudio("MOD", new File("../test/resource/audio/SMB-X.XM").toURI().toURL());

            // playing as music uses that reserved source to play the sound. The first
            // two arguments are pitch and gain, the boolean is whether to loop the content
            modStream.playAsMusic(1.0f, 1.0f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Game loop update
     */
    public void update() {
        GLFW.glfwSetKeyCallback(window, keyCallBack = new GLFWKeyCallback() {
            @Override
            @SuppressWarnings("CallToPrintStackTrace")
            public void invoke(long window, int key, int scancode, int action, int mods) {
                try {
                    if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
                        GLFW.glfwSetWindowShouldClose(window, GL11.GL_TRUE);
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
                } catch (Exception e) {
                    e.printStackTrace();
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

        font1.drawString(10, 150, "Hello LWJGL " + org.lwjgl.Sys.getVersion() + "world!", Color.yellow);

        font2.drawString(10, 200, "Hello LWJGL " + org.lwjgl.Sys.getVersion() + "world!", Color.yellow);
    }

}
