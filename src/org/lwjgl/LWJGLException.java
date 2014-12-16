package org.lwjgl;

/**
 * An implementation of the LWJGL2 Exception still used by IBXM
 *
 * @author rcalvert
 */
public class LWJGLException extends Exception {

    /**
     * Create a new exception
     */
    public LWJGLException() {
        super();
    }

    /**
     * Create a new exception with a detail message
     *
     * @param message The message describing the cause of this exception
     */
    public LWJGLException(String message) {
        super(message);
    }

    /**
     * Create a new exception with a detail message
     *
     * @param message The message describing the cause of this exception
     * @param e The exception causing this exception to be thrown
     */
    public LWJGLException(String message, Throwable e) {
        super(message, e);
    }

    /**
     * Create a new exception with a detail message
     *
     * @param e The exception causing this exception to be thrown
     */
    public LWJGLException(Throwable e) {
        super(e);
    }
}
