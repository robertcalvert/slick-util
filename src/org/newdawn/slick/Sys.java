package org.newdawn.slick;

/**
 *
 * @author rcalvert
 */
public class Sys {

    /**
     * Current version of library.
     */
    public static final int VERSION_MAJOR = 0,
            VERSION_MINOR = 1,
            VERSION_REVISION = 0;

    /**
     * The development state of the current build.
     */
    public static final BuildType BUILD_TYPE = BuildType.ALPHA;

    /**
     * *
     *
     * @return The slick-util version
     */
    public static String getVersion() {
        return String.valueOf(VERSION_MAJOR) + '.' + VERSION_MINOR + '.' + VERSION_REVISION + BUILD_TYPE.postfix;
    }

    /**
     * The development state of the current build.
     */
    public static enum BuildType {

        /**
         * Work in progress, unstable.
         */
        ALPHA("a"),
        /**
         * Feature complete, unstable.
         */
        BETA("b"),
        /**
         * Feature complete, stable, official release.
         */
        STABLE("");

        final String postfix;

        BuildType(String postfix) {
            this.postfix = postfix;
        }
    }

}
