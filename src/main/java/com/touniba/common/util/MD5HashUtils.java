package com.touniba.common.util;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * MD5 hash utils
 */
public class MD5HashUtils {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    /**
     * Make a signature for string.
     *
     * @param string
     * @return
     */
    public static String makeSignature(String string) {
        assert null != string;
        return makeSignature(string.getBytes(DEFAULT_CHARSET));
    }

    /**
     * * Make a signature with length for string.
     *
     * @param string
     * @return
     */
    public static String makeSignatureWithLength(String string) {
        assert null != string;
        return makeSignatureWithLength(string.getBytes(DEFAULT_CHARSET));
    }

    /**
     * Make a signature for data.
     *
     * @param data
     * @return
     */
    public static String makeSignature(byte[] data) {
        assert null != data;
        return MD5Hash.digest(data).toString();
    }

    /**
     * Make a signature with length for data.
     *
     * @param data
     * @return
     */
    public static String makeSignatureWithLength(byte[] data) {
        assert null != data;
        MD5Hash md5 = MD5Hash.digest(data);
        int length = data.length;
        return makeSignature(md5, length);
    }

    /**
     * Make a signature for data.
     *
     * @param md5
     * @param length
     * @return
     */
    public static String makeSignature(MD5Hash md5, int length) {
        String hex = Integer.toHexString(length);
        StringBuilder sb = new StringBuilder(MD5Hash.MD5_LEN * 2 + 8);// 40位
        sb.append(md5.toString());
        for (int i = hex.length(); i < 8; ++i) {
            sb.append('0');
        }
        sb.append(hex);
        return sb.toString();
    }

    /**
     * MD5 Hash
     */
    public static class MD5Hash {
        public static final int MD5_LEN = 16;
        private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        private static final MessageDigest DIGESTER;

        static {
            try {
                DIGESTER = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        private byte[] digest;


        private MD5Hash() {
            this.digest = new byte[MD5_LEN];
        }

        private MD5Hash(String hex) {
            setDigest(hex);
        }

        private MD5Hash(byte[] digest) {
            if (digest.length != MD5_LEN) {
                throw new IllegalArgumentException("Wrong length: " + digest.length);
            }
            this.digest = digest;
        }

        // End of Constructor

        public static MD5Hash digest(String string) {
            return digest(string.getBytes());
        }

        public static MD5Hash digest(byte[] data) {
            return digest(data, 0, data.length);
        }

        public static MD5Hash digest(byte[] data, int start, int len) {
            byte[] digest;
            synchronized (DIGESTER) {
                DIGESTER.update(data, start, len);
                digest = DIGESTER.digest();
            }
            return new MD5Hash(digest);
        }

        private static int charToNibble(char c) {
            if (c >= '0' && c <= '9') {
                return c - '0';
            } else if (c >= 'a' && c <= 'f') {
                return 0xa + (c - 'a');
            } else if (c >= 'A' && c <= 'F') {
                return 0xA + (c - 'A');
            } else {
                throw new RuntimeException("Not a hex character: " + c);
            }
        }

        public byte[] getDigest() {
            return digest;
        }

        public void setDigest(String hex) {
            if (hex.length() != MD5_LEN * 2) {
                throw new IllegalArgumentException("Wrong length: " + hex.length());
            }
            byte[] digest = new byte[MD5_LEN];
            for (int i = 0; i < MD5_LEN; i++) {
                int j = i << 1;
                digest[i] = (byte) (charToNibble(hex.charAt(j)) << 4 | charToNibble(hex.charAt(j + 1)));
            }
            this.digest = digest;
        }

        public long halfDigest() {
            long value = 0;
            for (int i = 0; i < 8; i++)
                value |= ((digest[i] & 0xffL) << (8 * (7 - i)));
            return value;
        }

        public int hashCode() {
            return // xor four ints
                    (digest[0] | (digest[1] << 8) | (digest[2] << 16) | (digest[3] << 24)) ^
                            (digest[4] | (digest[5] << 8) | (digest[6] << 16) | (digest[7] << 24)) ^
                            (digest[8] | (digest[9] << 8) | (digest[10] << 16) | (digest[11] << 24)) ^
                            (digest[12] | (digest[13] << 8) | (digest[14] << 16) | (digest[15] << 24));
        }

        public boolean equals(Object o) {
            if (!(o instanceof MD5Hash)) {
                return false;
            }
            MD5Hash other = (MD5Hash) o;
            return Arrays.equals(this.digest, other.digest);
        }

        public MD5Hash clone() {
            MD5Hash that = new MD5Hash();
            System.arraycopy(this.digest, 0, that.digest, 0, MD5_LEN);
            return that;
        }

        public String toString() {
            StringBuilder buf = new StringBuilder(MD5_LEN * 2);
            for (int i = 0; i < MD5_LEN; i++) {
                int b = digest[i];
                buf.append(HEX_DIGITS[(b >> 4) & 0xf]);
                buf.append(HEX_DIGITS[b & 0xf]);
            }
            return buf.toString();
        }
    }
}
