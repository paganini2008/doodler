package com.github.doodler.common.utils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.experimental.UtilityClass;

/**
 * 
 * @Description: RandomUtils
 * @Author: Fred Feng
 * @Date: 30/12/2024
 * @Version 1.0.0
 */
@UtilityClass
public class RandomUtils {

    public static byte randomChoice(byte[] choice) {
        return choice[randomInt(0, choice.length)];
    }

    public static byte randomChoice(byte[] choice, int length) {
        return choice[randomInt(0, Math.min(length, choice.length))];
    }

    public static short randomChoice(short[] choice) {
        return choice[randomInt(0, choice.length)];
    }

    public static short randomChoice(short[] choice, int length) {
        return choice[randomInt(0, Math.min(length, choice.length))];
    }

    public static int randomChoice(int[] choice) {
        return choice[randomInt(0, choice.length)];
    }

    public static int randomChoice(int[] choice, int length) {
        return choice[randomInt(0, Math.min(length, choice.length))];
    }

    public static long randomChoice(long[] choice) {
        return choice[randomInt(0, choice.length)];
    }

    public static long randomChoice(long[] choice, int length) {
        return choice[randomInt(0, Math.min(length, choice.length))];
    }

    public static float randomChoice(float[] choice) {
        return choice[randomInt(0, choice.length)];
    }

    public static float randomChoice(float[] choice, int length) {
        return choice[randomInt(0, Math.min(length, choice.length))];
    }

    public static double randomChoice(double[] choice) {
        return choice[randomInt(0, choice.length)];
    }

    public static double randomChoice(double[] choice, int length) {
        return choice[randomInt(0, Math.min(length, choice.length))];
    }

    public static char randomChoice(char[] choice) {
        return choice[randomInt(0, choice.length)];
    }

    public static char randomChoice(char[] choice, int length) {
        return choice[randomInt(0, Math.min(length, choice.length))];
    }

    public static boolean randomChoice(boolean[] choice) {
        return choice[randomInt(0, choice.length)];
    }

    public static boolean randomChoice(boolean[] choice, int length) {
        return choice[randomInt(0, Math.min(length, choice.length))];
    }

    public static <T> T randomChoice(T[] choice) {
        return choice[randomInt(0, choice.length)];
    }

    public static <T> T randomChoice(T[] choice, int length) {
        return choice[randomInt(0, Math.min(length, choice.length))];
    }

    public static <T> T randomChoice(List<T> list) {
        return list.get(randomInt(0, list.size()));
    }

    public static <T> T randomChoice(List<T> list, int length) {
        return list.get(randomInt(0, Math.min(length, list.size())));
    }

    public static int randomInt(int from, int to) {
        return from != to ? ThreadLocalRandom.current().nextInt(from, to) : from;
    }

    public static boolean randomBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    public static byte randomByte(byte from, byte to) {
        return (byte) randomInt(from, to);
    }

    public static char randomChar(char from, char to) {
        return (char) randomInt(from, to);
    }

    public static short randomShort(short from, short to) {
        return (short) randomInt(from, to);
    }

    public static float randomFloat() {
        return ThreadLocalRandom.current().nextFloat();
    }

    public static float randomFloat(float from, float to) {
        return from != to ? randomFloat() * (to - from) + from : from;
    }

    public static double randomDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    public static double randomDouble(double from, double to) {
        return from != to ? ThreadLocalRandom.current().nextDouble(from, to) : from;
    }

    public static long randomLong(long from, long to) {
        return from != to ? ThreadLocalRandom.current().nextLong(from, to) : from;
    }

}
