package com.touniba.common.util;

/**
 * The assert utils.
 * <p>
 * Created by Magoo on 2016/8/5.
 */
public class AssertUtils {

    /**
     * Throw a IllegalArgumentException if argument is null.
     *
     * @param argument
     * @param name
     */
    public static void notNullArgument(final Object argument, final String name) {
        if (null == argument) {
            throw new IllegalArgumentException(name + " is null");
        }
    }

    /**
     * Throw a IllegalArgumentException if expression is false.
     *
     * @param expression
     * @param message
     */
    public static void checkArgument(final boolean expression, final String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Throw a IllegalStateException if expression is false.
     *
     * @param expression
     * @param message
     */
    public static void check(final boolean expression, final String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Throw a IllegalStateException if expression is false.
     *
     * @param expression
     * @param message
     * @param args
     */
    public static void check(final boolean expression, final String message, final Object... args) {
        if (!expression) {
            throw new IllegalStateException(String.format(message, args));
        }
    }

    /**
     * Throw a IllegalStateException if expression is false.
     *
     * @param expression
     * @param message
     * @param arg
     */
    public static void check(final boolean expression, final String message, final Object arg) {
        if (!expression) {
            throw new IllegalStateException(String.format(message, arg));
        }
    }

    /***
     * Throw a IllegalStateException if object is null.
     * @param object
     * @param name
     */
    public static void notNull(final Object object, final String name) {
        if (object == null) {
            throw new IllegalStateException(name + " is null");
        }
    }

    /**
     * Throw a IllegalStateException if s is empty.
     *
     * @param s
     * @param name
     */
    public static void notEmpty(final CharSequence s, final String name) {
        if (CharacterUtils.isEmpty(s)) {
            throw new IllegalStateException(name + " is empty");
        }
    }

    /**
     * Throw a IllegalStateException if s is blank.
     *
     * @param s
     * @param name
     */
    public static void notBlank(final CharSequence s, final String name) {
        if (CharacterUtils.isBlank(s)) {
            throw new IllegalStateException(name + " is blank");
        }
    }

}
