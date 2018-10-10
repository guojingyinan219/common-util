package com.touniba.common.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * CharacterUtils.
 * <p>
 * Created by Magoo on 2016/8/5.
 */
public class CharacterUtils {
    public static final String COMMA_STR = ",";
    public static final String COLON_STR = ":";
    public static final String[] EMPTY_STRING_ARRAY = {};

    /**
     * Print stackTrace
     *
     * @param throwables
     * @return
     */
    public static String printStackTrace(Throwable... throwables) {
        if (throwables.length > 0) {
            try (StringWriter writer = new StringWriter(); PrintWriter printWriter = new PrintWriter(writer)) {
                for (Throwable throwable : throwables) {
                    if (null != throwable) {
                        writer.append("\n");
                        throwable.printStackTrace(printWriter);
                    }
                }
                printWriter.flush();
                return writer.toString();
            } catch (IOException e) {
                // Never
            }
        }
        return "";
    }

    /**
     * Returns true if the parameter is null or of zero length
     */
    public static boolean isEmpty(final CharSequence s) {
        return null == s || s.length() == 0;
    }

    /**
     * Returns true if the parameter is null or contains only whitespace
     */
    public static boolean isBlank(final CharSequence s) {
        if (s == null) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!isWhiteSpace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return true if the parameter is a whitespace.
     * <p>
     * Contains all the whitespace:<br/>
     * 'u00a0'  -- non-breaking space<br/>
     * '\n'      -- <br/>
     * '\r'      -- <br/>
     * '\t'      -- <br/>
     * 'u3000'  -- em space <br/>
     * ' '     -- normal space
     *
     * @param c
     * @return
     */
    public static boolean isWhiteSpace(char c) {
        return c == '\u00a0' || c == '\n' || c == '\r' || c == '\t' || c == '\u3000' || c == ' ';
    }

    /**
     * Returns false if the parameter is null or contains not only whitespace
     */
    public static boolean containsBlanks(final CharSequence s) {
        if (s == null) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (isWhiteSpace(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /***
     * Normalize all the blanks
     * @param s
     * @return
     */
    public static CharSequence normalizeBlanks(final CharSequence s) {
        if (null == s || s.length() < 1) {
            return s;
        }
        int index = 0;
        for (; index < s.length(); ++index) {
            char c = s.charAt(index);
            if (isWhiteSpace(c)) {
                break;
            }
        }

        if (index > s.length() - 1) {
            return s;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(s, 0, index);
        for (; index < s.length(); ++index) {
            char c = s.charAt(index);
            if (isWhiteSpace(c)) {
                builder.append(' ');
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    /**
     * Trim all whitespaces.
     *
     * @param s
     * @return
     */
    public static String trimAll(final String s) {
        return null == s ? null : normalizeBlanks(s).toString().trim();
    }


    /**
     * Splits a comma separated value <code>String</code>, trimming leading and trailing whitespace on each value.
     *
     * @param str a comma separated <String> with values
     * @return a <code>Collection</code> of <code>String</code> values
     */
    public static Collection<String> getTrimmedStringCollection(String str) {
        return new ArrayList<>(Arrays.asList(getTrimmedStrings(str)));
    }

    /**
     * Splits a comma separated value <code>String</code>, trimming leading and trailing whitespace on each value.
     *
     * @param str a comma separated <String> with values
     * @return an array of <code>String</code> values
     */
    public static String[] getTrimmedStrings(String str) {
        if (null == str || str.trim().isEmpty()) {
            return EMPTY_STRING_ARRAY;
        }
        return str.trim().split("\\s*,\\s*");
    }

    /**
     * Get a hex value
     *
     * @param value
     * @return
     */
    public static String getHexDigits(String value) {
        boolean negative = false;
        String str = value;
        String hexString;
        if (value.startsWith("-")) {
            negative = true;
            str = value.substring(1);
        }
        if (str.startsWith("0x") || str.startsWith("0X")) {
            hexString = str.substring(2);
            if (negative) {
                hexString = "-" + hexString;
            }
            return hexString;
        }
        return null;
    }

    /**
     * Array to string
     *
     * @param array
     * @param split
     * @return
     */
    public static String arrayToString(Object[] array, String split) {
        assert null != array;
        if (array.length == 0) {
            return "[]";
        }
        if (null == split) {
            split = COMMA_STR;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append(split);
            sb.append(array[i]);
        }
        return "[" + sb.toString() + "]";
    }

    /**
     * upper the first letter
     *
     * @param str
     * @return
     */
    public static String upperFirst(String str) {
        return (str.charAt(0) + "").toUpperCase() + str.substring(1);
    }

    /**
     * Get not null string value.
     *
     * @param obj
     * @return
     */
    public static String notNull(Object obj) {
        return notNull(obj, "");
    }

    /**
     * Get not null string value.
     *
     * @param obj
     * @param defaultStr
     * @return
     */
    public static String notNull(Object obj, String defaultStr) {
        return null == obj ? defaultStr : String.valueOf(obj).trim();
    }

    /**
     * Object to string
     *
     * @param object
     * @param split
     * @return
     */
    public static String objectToString(Object object, String split) {
        return objectToString(object, split, null);
    }

    /**
     * Object to String
     *
     * @param object
     * @param split
     * @param fields
     * @return
     */
    public static String objectToString(Object object, String split, String[] fields) {
        if (null == split) {
            split = COMMA_STR;
        }
        List<Field> list = null;
        if (null != fields && fields.length > 0) {
            list = ReflectUtils.getDeclaredFields(object.getClass(), fields);
        } else {
            list = ReflectUtils.getAllDeclaredFields(object.getClass());
        }
        StringBuilder sb = null;
        for (Field field : list) {
            Object value = ReflectUtils.getValue(object, field);
            if (null == sb) {
                sb = new StringBuilder();
            } else {
                sb.append(split);
            }
            sb.append(field.getName()).append(COLON_STR);
            if (ReflectUtils.isBasicType(field.getType()) || ReflectUtils.isArray(field.getType()) || ReflectUtils.isCollection(field.getType()) ||
                    ReflectUtils.isMap(field.getType())) {
                sb.append(toString(value, COMMA_STR));
            } else {
                sb.append("{").append(objectToString(value, split)).append("}");
            }
        }
        if (null != sb) {
            return sb.toString();
        } else {
            return "";
        }
    }

    /**
     * To String
     *
     * @param object
     * @return
     */
    public static String toString(Object object, String split) {
        if (null == object) {
            return "null";
        }
        if (null == split) {
            split = COMMA_STR;
        }
        Class<?> clazz = object.getClass();
        if (clazz.equals(String.class)) {
            String val = "";
            try {
                val = String.valueOf(object);
            } catch (Throwable e) {
            }
            return val;
        } else if (clazz.equals(Character.class) || clazz.equals(char.class)) {
            char val = 0;
            try {
                val = (Character) object;
            } catch (Throwable e) {
            }
            return String.valueOf(val);
        } else if (clazz.equals(Byte.class) || clazz.equals(byte.class)) {
            byte val = 0;
            try {
                val = (Byte) object;
            } catch (Throwable e) {
            }
            return String.valueOf(val);
        } else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
            float val = 0f;
            try {
                val = (Float) object;
            } catch (Throwable e) {
            }
            return String.valueOf(val);
        } else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
            double val = 0;
            try {
                val = (Double) object;
            } catch (Throwable e) {
            }
            return String.valueOf(val);
        } else if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
            int val = 0;
            try {
                val = (Integer) object;
            } catch (Throwable e) {
            }
            return String.valueOf(val);
        } else if (clazz.equals(Short.class) || clazz.equals(short.class)) {
            short val = 0;
            try {
                val = (Short) object;
            } catch (Throwable e) {
            }
            return String.valueOf(val);
        } else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
            long val = 0;
            try {
                val = (Long) object;
            } catch (Throwable e) {
            }
            return String.valueOf(val);
        } else if (ReflectUtils.isArray(clazz)) {
            StringBuilder sb = null;
            int length = Array.getLength(object);
            for (int i = 0; i < length; i++) {
                if (null == sb) {
                    sb = new StringBuilder("[");
                } else {
                    sb.append(split);
                }
                sb.append(toString(Array.get(object, i), split));
            }
            if (null != sb) {
                return sb.append("]").toString();
            }
            return "[]";
        } else if (ReflectUtils.isCollection(clazz)) {
            StringBuilder sb = null;
            Collection<?> collection = (Collection<?>) object;
            for (Object param : collection) {
                if (null == sb) {
                    sb = new StringBuilder("[");
                } else {
                    sb.append(split);
                }
                sb.append(toString(param, split));
            }
            if (null != sb) {
                return sb.append("]").toString();
            }
            return "[]";
        } else if (ReflectUtils.isMap(clazz)) {
            StringBuilder sb = null;
            Map<?, ?> map = (Map<?, ?>) object;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (null == sb) {
                    sb = new StringBuilder("(");
                } else {
                    sb.append(split);
                }
                sb.append(toString(entry.getKey(), split)).append(COLON_STR).append(toString(entry.getValue(), split));
            }
            if (null != sb) {
                return sb.append(")").toString();
            }
            return "()";
        }
        return String.valueOf(object);
    }


}

