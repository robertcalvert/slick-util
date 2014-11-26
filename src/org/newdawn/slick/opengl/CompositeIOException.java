package org.newdawn.slick.opengl;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A collection of IOException that failed image data loading
 *
 * @author kevin
 */
public class CompositeIOException extends IOException {

    /**
     * The list of exceptions causing this one
     */
    private final ArrayList exceptions = new ArrayList();

    /**
     * Create a new composite IO Exception
     */
    public CompositeIOException() {
        super();
    }

    /**
     * Add an exception that caused this exception
     *
     * @param e The exception
     */
    public void addException(Exception e) {
        exceptions.add(e);
    }

    @Override
    public String getMessage() {
        String msg = "Composite Exception: \n";
        for (Object exception : exceptions) {
            msg += "\t" + ((IOException) exception).getMessage() + "\n";
        }

        return msg;
    }
}
