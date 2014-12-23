package io.flob.sux.opengl;

import io.flob.sux.SUXException;
import io.flob.sux.opengl.renderer.Renderer;
import org.lwjgl.opengl.GL11;

/**
 * A utility to allow performing GL operations without contaminating the sux
 * OpenGL state. Note this will not protect you from OpenGL programming errors
 * like a glBegin() without a glEnd(), or glPush() without glPop() etc.
 *
 * Expected usage:
 *
 * <code>
 * SUXCallable callable = new SUXCallable() {
 * 	   public performGLOperations() throws SUXException {
 * 			GL.glTranslate(0,0,1);
 * 			glBegin(GL.GL_POLYGONS);
 *  			glVertex(..);
 *              ...
 *          glEnd();
 *     }
 * }
 * callable.call();
 * </code>
 *
 * Alternatively you can use the static methods directly
 *
 * <code>
 * SUXCallable.enterSafeBlock();
 *
 * GL.glTranslate(0,0,1);
 * glBegin(GL.GL_POLYGONS);
 *     glVertex(..);
 *     ...
 * glEnd();
 *
 * SUXCallable.leaveSafeBlock();
 * </code>
 *
 * @author kevin
 */
public abstract class SUXCallable {

    /**
     * The last texture used
     */
    private static Texture lastUsed;
    /**
     * True if we're in a safe block
     */
    private static boolean inSafe = false;

    /**
     * Enter a safe block ensuring that all the OpenGL state that SUX uses is
     * safe before touching the GL state directly.
     */
    public static void enterSafeBlock() {
        if (inSafe) {
            return;
        }

        Renderer.get().flush();
        lastUsed = TextureImpl.getLastBind();
        TextureImpl.bindNone();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushClientAttrib(GL11.GL_CLIENT_ALL_ATTRIB_BITS);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

        inSafe = true;
    }

    /**
     * Leave a safe block ensuring that all of SUX's OpenGL state is restored
     * since the last enter.
     */
    public static void leaveSafeBlock() {
        if (!inSafe) {
            return;
        }

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        GL11.glPopClientAttrib();
        GL11.glPopAttrib();

        if (lastUsed != null) {
            lastUsed.bind();
        } else {
            TextureImpl.bindNone();
        }

        inSafe = false;
    }

    /**
     * Cause this callable to perform it's GL operations This method will block
     * until the GL operations have been performed.
     *
     * @throws SUXException Indicates a failure while performing the GL
     * operations or maintaining SUXState
     */
    public final void call() throws SUXException {
        enterSafeBlock();

        performGLOperations();

        leaveSafeBlock();
    }

    /**
     * Perform the GL operations that this callable is intended to. This
     * operations should not effect the SUXOpenGL state.
     *
     * @throws SUXException Indicates a failure of some sort. This is user
     * exception
     */
    protected abstract void performGLOperations() throws SUXException;
}
