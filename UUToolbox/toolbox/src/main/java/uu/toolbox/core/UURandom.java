package uu.toolbox.core;

import android.support.annotation.NonNull;

import java.nio.ByteBuffer;
import java.security.SecureRandom;

/**
 * UURandom
 * Useful Utilities - UURandom contains helper methods for generating random data
 *
 * License:
 * You are free to use this code for whatever purposes you desire.
 * The only requirement is that you smile everytime you use it. :-)
 */
@SuppressWarnings("unused")
public final class UURandom
{
    private static SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a random byte array using java.security.SecureRandom
     *
     * @param length length of byte array to generate
     * @return byte array filled with data.
     * @see java.security.SecureRandom
     */
    @SuppressWarnings("unused")
    @NonNull
    public static byte[] randomBytes(final int length)
    {
        byte[] output = new byte[length];
        secureRandom.nextBytes(output);
        return output;
    }

    /**
     * Generates a random integer with an upper bound
     *
     * @param max maximum value
     * @return a random integer
     */
    @SuppressWarnings("unused")
    public static int randomInt(final int max)
    {
        return secureRandom.nextInt(max);
    }

    /**
     * Generates a random integer
     *
     * @return a random integer
     */
    @SuppressWarnings("unused")
    public static int randomInt()
    {
        return secureRandom.nextInt();
    }

    /**
     * Generates a random long
     *
     * @return a random long
     */
    @SuppressWarnings("unused")
    public static long randomLong()
    {
        return secureRandom.nextLong();
    }

    /**
     * Generates a random double
     *
     * @return a random double
     */
    @SuppressWarnings("unused")
    public static double randomDouble()
    {
        return secureRandom.nextDouble();
    }

    /**
     * Generates a random float
     *
     * @return a random float
     */
    @SuppressWarnings("unused")
    public static float randomFloat()
    {
        return secureRandom.nextFloat();
    }

    /**
     * Generates a random boolean
     *
     * @return a random boolean
     */
    @SuppressWarnings("unused")
    public static boolean randomBoolean()
    {
        return secureRandom.nextBoolean();
    }

    /**
     * Generates a random byte
     *
     * @return a random byte
     */
    @SuppressWarnings("unused")
    public static byte randomByte()
    {
        byte[] result = randomBytes(1);
        return result[0];
    }

    /**
     * Generates a random short
     *
     * @return a random short
     */
    @SuppressWarnings("unused")
    public static short randomShort()
    {
        byte[] result = randomBytes(2);
        ByteBuffer bb = ByteBuffer.wrap(result);
        return bb.getShort();
    }

    private UURandom()
    {
        // Static class only, do not allow instantiation
    }
}
