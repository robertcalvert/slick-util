package io.flob.sux.openal;

/**
 * A null implementation used to provide an object reference when sound has
 * failed.
 *
 * @author kevin
 */
public class NullAudio implements Audio {

    @Override
    public int getBufferID() {
        return 0;
    }

    @Override
    public float getPosition() {
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int playAsMusic(float pitch, float gain, boolean loop) {
        return 0;
    }

    @Override
    public int playAsSoundEffect(float pitch, float gain, boolean loop) {
        return 0;
    }

    @Override
    public int playAsSoundEffect(float pitch, float gain, boolean loop,
            float x, float y, float z) {
        return 0;
    }

    @Override
    public boolean setPosition(float position) {
        return false;
    }

    @Override
    public void stop() {
    }

}
