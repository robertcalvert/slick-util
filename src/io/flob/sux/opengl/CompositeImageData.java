package io.flob.sux.opengl;

import io.flob.sux.util.Log;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * A composite data source that checks multiple loaders in order of preference
 *
 * @author kevin
 */
public class CompositeImageData implements LoadableImageData {

    /**
     * The list of images sources in order of preference to try loading the data
     * with
     */
    private final ArrayList sources = new ArrayList();
    /**
     * The data source that worked and was used - or null if no luck
     */
    private LoadableImageData picked;

    /**
     * Add a potential source of image data
     *
     * @param data The data source to try
     */
    public void add(LoadableImageData data) {
        sources.add(data);
    }

    @Override
    public ByteBuffer loadImage(InputStream fis) throws IOException {
        return loadImage(fis, false, null);
    }

    @Override
    public ByteBuffer loadImage(InputStream fis, boolean flipped, int[] transparent) throws IOException {
        return loadImage(fis, flipped, false, transparent);
    }

    @Override
    public ByteBuffer loadImage(InputStream is, boolean flipped, boolean forceAlpha, int[] transparent) throws IOException {
        CompositeIOException exception = new CompositeIOException();
        ByteBuffer buffer = null;

        BufferedInputStream in = new BufferedInputStream(is, is.available());
        in.mark(is.available());

        // cycle through our source until one of them works
        for (Object source : sources) {
            in.reset();
            try {
                LoadableImageData data = (LoadableImageData) source;
                buffer = data.loadImage(in, flipped, forceAlpha, transparent);
                picked = data;
                break;
            } catch (Exception e) {
                Log.warn(source.getClass() + " failed to read the data", e);
                exception.addException(e);
            }
        }

        if (picked == null) {
            throw exception;
        }

        return buffer;
    }

    /**
     * Check the state of the image data and throw a runtime exception if theres
     * a problem
     */
    private void checkPicked() {
        if (picked == null) {
            throw new RuntimeException("Attempt to make use of uninitialised or invalid composite image data");
        }
    }

    @Override
    public int getDepth() {
        checkPicked();

        return picked.getDepth();
    }

    @Override
    public int getHeight() {
        checkPicked();

        return picked.getHeight();
    }

    @Override
    public ByteBuffer getImageBufferData() {
        checkPicked();

        return picked.getImageBufferData();
    }

    @Override
    public int getTexHeight() {
        checkPicked();

        return picked.getTexHeight();
    }

    @Override
    public int getTexWidth() {
        checkPicked();

        return picked.getTexWidth();
    }

    @Override
    public int getWidth() {
        checkPicked();

        return picked.getWidth();
    }

    @Override
    public void configureEdging(boolean edging) {
        for (Object source : sources) {
            ((LoadableImageData) source).configureEdging(edging);
        }
    }

}
