package com.touniba.common.util;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The utils for io.
 */
public class IOUtil {
    /**
     * Read string, represent in.readUTF()
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static String readString(DataInput in) throws IOException {
        int length = in.readInt();
        if (length == -1) {
            return null;
        }
        byte[] buffer = new byte[length];
        in.readFully(buffer); // could/should use readFully(buffer,0,length)?
        return new String(buffer, "UTF-8");
    }

    /**
     * Write string, represent out.writeUTF()
     *
     * @param out
     * @param s
     * @throws IOException
     */
    public static void writeString(DataOutput out, String s) throws IOException {
        if (s != null) {
            byte[] buffer = s.getBytes("UTF-8");
            int len = buffer.length;
            out.writeInt(len);
            out.write(buffer, 0, len);
        } else {
            out.writeInt(-1);
        }
    }

    /**
     * Read the bytes
     *
     * @param input
     * @return
     * @throws IOException
     */
    public static byte[] readBytes(InputStream input) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(1024)) {
            byte[] temp = new byte[1024];
            int size;
            while ((size = input.read(temp)) != -1) {
                out.write(temp, 0, size);
            }
            out.flush();
            return out.toByteArray();
        }
    }

    /**
     * Read the bytes
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] readBytes(String file) throws IOException {
        try (InputStream input = new FileInputStream(file)) {
            return readBytes(input);
        }
    }


    /**
     * 关闭对象, 屏蔽所有异常。
     * 调用对象的close方法（如果对象有该方法的话）。
     *
     * @param objects 对象列表
     */
    public static void closeQuietly(Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                continue;
            }
            if (obj instanceof Closeable) {
                try {
                    ((Closeable) obj).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Method method;
            try {
                method = obj.getClass().getMethod("close");
                if (method != null) {
                    method.invoke(obj);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
    }
}
