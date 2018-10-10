package com.touniba.common.util;

import java.io.*;

/**
 * The utils for io.
 */
public class IOUtils {
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
}
