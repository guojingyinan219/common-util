package com.touniba.common.util;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The net utils
 * Create by Magoo on 2016/7/22
 */
public class NetUtils {
    public static final String LOCALHOST_IP = "127.0.0.1";

    public static final ByteBuffer YES_VAL = ByteBuffer.wrap(new byte[]{'y'});
    public static final String LOCALHOST = "localhost";

    public static final String REGEX_TOP_DOMAMIN =
            "[^.]+(\\.com|\\.edu|\\.gov|\\.int|\\.mil|\\.net|\\.org|\\.biz|\\.info|\\.pro|\\.name|\\.museum|\\.coop|\\.aero|\\.xxx|\\.idv)(\\..*)?$";
    public static final String REGEX_IP = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";

    public static final Pattern PATTERN_IP = Pattern.compile(REGEX_IP);
    public static final Pattern PATTERN_TOP_DOMAIN = Pattern.compile(REGEX_TOP_DOMAMIN);

    public static final String REGEX_OTHER_DOMAIN = "[^.]+\\.[^.]+$";

    public static final Pattern PATTERN_OTHER_DOMAIN = Pattern.compile(REGEX_OTHER_DOMAIN);

    public static final Pattern PATTERN_WITH_HTTPS = Pattern.compile("^https:(//.*)$");


    /**
     * Get the protocol
     *
     * @param url
     * @return
     */
    public static String getProtocol(String url) {
        try {
            return new URL(url).getProtocol();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Ignore https, replace with http
     *
     * @param url
     * @return
     */
    public static String ignoreHttps(String url) {
        if (StringUtils.isEmpty(url)) {
            return url;
        }
        return PATTERN_WITH_HTTPS.matcher(url).replaceAll("http:$1");
    }


    /**
     * Build abstract url
     *
     * @param base
     * @param path
     * @return
     */
    public static URL buildAbsURL(String base, String path) {
        if (!base.endsWith("/")) {
            base += "/";
        }
        if (path.startsWith("/")) {
            path = path.substring(1, path.length());
        }
        return buildURL(base, path);
    }

    /**
     * Build url
     *
     * @param base
     * @param path
     * @return
     */
    public static URL buildURL(String base, String path) {
        try {
            URL URL = new URL(base);
            return new URL(URL, path);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * Build url
     *
     * @param base
     * @param path
     * @return
     */
    public static URL buildURL(URL base, String path) {
        try {
            return new URL(base, path);
        } catch (Throwable e) {
            return null;
        }
    }

    /**
     * Transform content-type to mime-type
     *
     * @param contentType
     * @return
     */
    public static String contentType2MimeType(String contentType) {
        if (null == contentType || "".equals(contentType)) {
            return "";
        }
        return clearSpaceAndSplit(contentType)[0];
    }

    /**
     * Detect the port usable
     *
     * @param port
     * @return
     */
    public static boolean detectPortUsable(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Get charset by the content-type
     *
     * @param contentType
     * @return
     */
    public static String getCharsetByContentType(String contentType) {
        String encoding = null;
        if (contentType != null) {
            int start = contentType.indexOf("charset=");
            if (start >= 0) {
                encoding = contentType.substring(start + 8);
                int end = encoding.indexOf(';');
                if (end >= 0) {
                    encoding = encoding.substring(0, end);
                }
                encoding = encoding.trim();
                if ((encoding.length() > 2) && (encoding.startsWith("\"")) && (encoding.endsWith("\""))) {
                    encoding = encoding.substring(1, encoding.length() - 1);
                }
                encoding = (encoding.trim());
            }
        }
        return encoding;
    }

    /**
     * Get the domain by host
     *
     * @param host
     * @return
     */
    public static String getDomainByHost(String host) {
        if (PATTERN_IP.matcher(host).matches()) {
            return host;
        }
        if (LOCALHOST.equalsIgnoreCase(host)) {
            return host;
        }
        Matcher matcher = PATTERN_TOP_DOMAIN.matcher(host);
        if (matcher.find()) {
            return matcher.group();
        } else {
            matcher = PATTERN_OTHER_DOMAIN.matcher(host);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return null;
    }

    /**
     * Get domain by url
     *
     * @param url
     * @return
     */
    public static String getDomainByUrl(String url) {
        String host = getHost(url);
        if (!StringUtils.isEmpty(host)) {
            return getDomainByHost(host);
        }
        return null;
    }

    /**
     * Get the file-extension from url
     *
     * @param url
     * @return
     */
    public static String getFileExtensionByUrl(String url) {
        if (null == url || url.length() < 1) {
            return null;
        }
        int query = url.lastIndexOf('?');
        if (query > 0) {
            url = url.substring(0, query);
        }
        int index = url.lastIndexOf("/");
        if (index > 0 && index < url.length()) {
            int dotIndex = url.lastIndexOf(".");
            if (dotIndex > index && dotIndex < url.length()) {
                return url.substring(dotIndex + 1);
            }
        }
        return null;
    }

    /**
     * Get the file-name from disposition
     *
     * @param disposition
     * @return
     */
    public static String getFileNameByDisposition(String disposition) {
        String[] splits = clearSpaceAndSplit(disposition);
        for (String str : splits) {
            if (str.startsWith("filename") && str.contains("=")) {
                return str.substring(str.indexOf("=")).replaceFirst("=", "");
            }
        }
        return null;
    }

    /**
     * Get the file-name from url
     *
     * @param url
     * @return
     */
    public static String getFileNameByUrl(String url) {
        if (null == url || url.length() < 1) {
            return null;
        }
        int query = url.lastIndexOf('?');
        if (query > 0) {
            url = url.substring(0, query);
        }
        int index = url.lastIndexOf("/");
        if (index > 0 && index < url.length()) {
            int dotIndex = url.lastIndexOf(".");
            if (dotIndex > index) {
                return url.substring(index + 1);
            }
        }
        return null;
    }

    /**
     * Get the host
     *
     * @param url
     * @return
     */
    public static String getHost(String url) {
        try {
            URL u = new URL(url);
            return u.getHost().toLowerCase();
        } catch (MalformedURLException e) {
            // nothing
        }
        return null;
    }

    /**
     * Get the ip
     *
     * @param host
     * @return
     */
    public static String getIp(String host) {
        try {
            InetAddress address = InetAddress.getByName(host);
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            // nothing
        }
        return null;
    }

    /**
     * Get the local-ips. Without localhost.
     *
     * @return
     */
    public static String[] getLocalIpS() {
        List<String> ips = new ArrayList<>();
        Enumeration<NetworkInterface> netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException ignored) {
        }
        if (null != netInterfaces) {
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (null != address && address instanceof Inet4Address
                            && !address.isAnyLocalAddress() && !address.isLinkLocalAddress()) {
                        if (!LOCALHOST_IP.equals(address.getHostAddress())) {
                            ips.add(address.getHostAddress());
                        }
                    }
                }
            }
        }
        return ips.toArray(new String[]{});
    }

    /**
     * Is accessible ?
     *
     * @param ip
     * @param port
     * @param timeout
     * @return
     */
    public static boolean isAccessible(String ip, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(InetAddress.getByName(ip), port), timeout);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * Get the net-name
     *
     * @return
     */
    public static String getLocalNetName() {
        try {
            InetAddress ia = InetAddress.getLocalHost();
            return ia.getHostName() + "/" + ia.getHostAddress();
        } catch (UnknownHostException e) {
            return ManagementFactory.getRuntimeMXBean().getName();
        }
    }

    /**
     * Get the mac-address
     *
     * @return
     */
    public static byte[] getMacAddress() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            return network.getHardwareAddress();
        } catch (Throwable e) {
            // nothing
        }
        return null;
    }

    /**
     * Reverset the host
     *
     * @param host
     * @return
     */
    public static String reverseHost(String host) {
        StringBuilder buf = new StringBuilder();
        reverseAppendSplits(host, buf);
        return buf.toString();

    }

    /**
     * Reverses a url's domain. This form is better for storing in hbase. Because scans within the same domain are faster.
     * <p/>
     * E.g. "http://bar.foo.com:8983/to/index.html?a=b" becomes "com.foo.bar:8983:http/to/index.html?a=b".
     *
     * @param url url to be reversed
     * @return Reversed url
     * @throws MalformedURLException
     */
    public static String reverseUrl(String url) throws MalformedURLException {
        return reverseUrl(new URL(url));
    }

    /**
     * Reverses a url's domain. This form is better for storing in hbase. Because scans within the same domain are faster.
     * <p/>
     * E.g. "http://bar.foo.com:8983/to/index.html?a=b" becomes "com.foo.bar:http:8983/to/index.html?a=b".
     *
     * @param url url to be reversed
     * @return Reversed url
     */
    public static String reverseUrl(URL url) {
        String host = url.getHost();
        String file = url.getFile();
        String protocol = url.getProtocol();
        int port = url.getPort();

        StringBuilder buf = new StringBuilder();

        /* reverse host */
        reverseAppendSplits(host, buf);

        /* add protocol */
        buf.append(':');
        buf.append(protocol);

        /* add port if necessary */
        if (port != -1) {
            buf.append(':');
            buf.append(port);
        }

        /* add path */
        if (file.length() > 0 && '/' != file.charAt(0)) {
            buf.append('/');
        }
        buf.append(file);

        return buf.toString();
    }

    /**
     * Unreverse the reverse-host
     *
     * @param reversedHostName
     * @return
     */
    public static String unreverseHost(String reversedHostName) {
        return reverseHost(reversedHostName); // Reversible
    }

    /**
     * Unreverse the reverse-url
     *
     * @param reversedUrl
     * @return
     */
    public static String unreverseUrl(String reversedUrl) {
        StringBuilder buf = new StringBuilder(reversedUrl.length() + 2);

        int pathBegin = reversedUrl.indexOf('/');
        if (pathBegin == -1) {
            pathBegin = reversedUrl.length();
        }
        String sub = reversedUrl.substring(0, pathBegin);

        String[] splits = StringUtils.splitPreserveAllTokens(sub, ':'); // {<reversed host>, <port>, <protocol>}

        buf.append(splits[1]); // add protocol
        buf.append("://");
        reverseAppendSplits(splits[0], buf); // splits[0] is reversed
        // host
        if (splits.length == 3) { // has a port
            buf.append(':');
            buf.append(splits[2]);
        }
        buf.append(reversedUrl.substring(pathBegin));
        return buf.toString();
    }

    // clear space and split
    private static String[] clearSpaceAndSplit(String str) {
        assert null != str;
        return str.replaceAll("\\s", "").split(";");
    }

    //
    private static void reverseAppendSplits(String string, StringBuilder buf) {
        String[] splits = StringUtils.split(string, '.');
        if (splits.length > 0) {
            for (int i = splits.length - 1; i > 0; i--) {
                buf.append(splits[i]);
                buf.append('.');
            }
            buf.append(splits[0]);
        } else {
            buf.append(string);
        }
    }


    /**
     * MimeTypes
     */
    public final static class MimeTypeMap {
        private static MimeTypeMap instance;

        /**
         * MIME-type to file extension mapping:
         */
        private HashMap<String, String> mMimeTypeToExtensionMap;

        /**
         * File extension to MIME type mapping:
         */
        private HashMap<String, String> mExtensionToMimeTypeMap;

        /**
         * Creates a new MIME-type map.
         */
        private MimeTypeMap() {
            mMimeTypeToExtensionMap = new HashMap<>();
            mExtensionToMimeTypeMap = new HashMap<>();
        }

        /**
         * Returns the file extension or an empty string iff there is no extension. This method is a convenience method for obtaining
         * the extension of a url and has undefined results for other Strings.
         *
         * @param url
         * @return The file extension of the given url.
         */
        public static String getFileExtensionFromUrl(String url) {
            if (url != null && url.length() > 0) {
                int query = url.lastIndexOf('?');
                if (query > 0) {
                    url = url.substring(0, query);
                }
                int filenamePos = url.lastIndexOf('/');
                String filename = 0 <= filenamePos ? url.substring(filenamePos + 1) : url;

                // if the filename contains special characters, we don't
                // consider it valid for our matching purposes:
                if (filename.length() > 0 && Pattern.matches("[a-zA-Z_0-9\\.\\-\\(\\)\\%]+", filename)) {
                    int dotPos = filename.lastIndexOf('.');
                    if (0 <= dotPos) {
                        return filename.substring(dotPos + 1);
                    }
                }
            }

            return "";
        }

        /**
         * Get the singleton instance of MimeTypeMap.
         *
         * @return The singleton instance of the MIME-type map.
         */
        public synchronized static MimeTypeMap instance() {
            if (instance == null) {
                instance = new MimeTypeMap();

                // The following table is based on /etc/mime.types data minus
                // chemical/* MIME types and MIME types that don't map to any
                // file extensions. We also exclude top-level domain names to
                // deal with cases like:
                //
                // mail.google.com/a/google.com
                //
                // and "active" MIME types (due to potential security issues).

                instance.loadEntry("application/andrew-inset", "ez");
                instance.loadEntry("application/dsptype", "tsp");
                instance.loadEntry("application/futuresplash", "spl");
                instance.loadEntry("application/hta", "hta");
                instance.loadEntry("application/mac-binhex40", "hqx");
                instance.loadEntry("application/mac-compactpro", "cpt");
                instance.loadEntry("application/mathematica", "nb");
                instance.loadEntry("application/msaccess", "mdb");
                instance.loadEntry("application/oda", "oda");
                instance.loadEntry("application/ogg", "ogg");
                instance.loadEntry("application/pdf", "pdf");
                instance.loadEntry("application/pgp-keys", "key");
                instance.loadEntry("application/pgp-signature", "pgp");
                instance.loadEntry("application/pics-rules", "prf");
                instance.loadEntry("application/rar", "rar");
                instance.loadEntry("application/rdf+xml", "rdf");
                instance.loadEntry("application/rss+xml", "rss");
                instance.loadEntry("application/zip", "zip");
                instance.loadEntry("application/vnd.android.package-archive", "apk");
                instance.loadEntry("application/vnd.cinderella", "cdy");
                instance.loadEntry("application/vnd.ms-pki.stl", "stl");
                instance.loadEntry("application/vnd.oasis.opendocument.database", "odb");
                instance.loadEntry("application/vnd.oasis.opendocument.formula", "odf");
                instance.loadEntry("application/vnd.oasis.opendocument.graphics", "odg");
                instance.loadEntry("application/vnd.oasis.opendocument.graphics-template", "otg");
                instance.loadEntry("application/vnd.oasis.opendocument.image", "odi");
                instance.loadEntry("application/vnd.oasis.opendocument.spreadsheet", "ods");
                instance.loadEntry("application/vnd.oasis.opendocument.spreadsheet-template", "ots");
                instance.loadEntry("application/vnd.oasis.opendocument.text", "odt");
                instance.loadEntry("application/vnd.oasis.opendocument.text-master", "odm");
                instance.loadEntry("application/vnd.oasis.opendocument.text-template", "ott");
                instance.loadEntry("application/vnd.oasis.opendocument.text-web", "oth");
                instance.loadEntry("application/msword", "doc");
                instance.loadEntry("application/msword", "dot");
                instance.loadEntry("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");
                instance.loadEntry("application/vnd.openxmlformats-officedocument.wordprocessingml.template", "dotx");
                instance.loadEntry("application/vnd.ms-excel", "xls");
                instance.loadEntry("application/vnd.ms-excel", "xlt");
                instance.loadEntry("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");
                instance.loadEntry("application/vnd.openxmlformats-officedocument.spreadsheetml.template", "xltx");
                instance.loadEntry("application/vnd.ms-powerpoint", "ppt");
                instance.loadEntry("application/vnd.ms-powerpoint", "pot");
                instance.loadEntry("application/vnd.ms-powerpoint", "pps");
                instance.loadEntry("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx");
                instance.loadEntry("application/vnd.openxmlformats-officedocument.presentationml.template", "potx");
                instance.loadEntry("application/vnd.openxmlformats-officedocument.presentationml.slideshow", "ppsx");
                instance.loadEntry("application/vnd.rim.cod", "cod");
                instance.loadEntry("application/vnd.smaf", "mmf");
                instance.loadEntry("application/vnd.stardivision.calc", "sdc");
                instance.loadEntry("application/vnd.stardivision.draw", "sda");
                instance.loadEntry("application/vnd.stardivision.impress", "sdd");
                instance.loadEntry("application/vnd.stardivision.impress", "sdp");
                instance.loadEntry("application/vnd.stardivision.math", "smf");
                instance.loadEntry("application/vnd.stardivision.writer", "sdw");
                instance.loadEntry("application/vnd.stardivision.writer", "vor");
                instance.loadEntry("application/vnd.stardivision.writer-global", "sgl");
                instance.loadEntry("application/vnd.sun.xml.calc", "sxc");
                instance.loadEntry("application/vnd.sun.xml.calc.template", "stc");
                instance.loadEntry("application/vnd.sun.xml.draw", "sxd");
                instance.loadEntry("application/vnd.sun.xml.draw.template", "std");
                instance.loadEntry("application/vnd.sun.xml.impress", "sxi");
                instance.loadEntry("application/vnd.sun.xml.impress.template", "sti");
                instance.loadEntry("application/vnd.sun.xml.math", "sxm");
                instance.loadEntry("application/vnd.sun.xml.writer", "sxw");
                instance.loadEntry("application/vnd.sun.xml.writer.global", "sxg");
                instance.loadEntry("application/vnd.sun.xml.writer.template", "stw");
                instance.loadEntry("application/vnd.visio", "vsd");
                instance.loadEntry("application/x-abiword", "abw");
                instance.loadEntry("application/x-apple-diskimage", "dmg");
                instance.loadEntry("application/x-bcpio", "bcpio");
                instance.loadEntry("application/x-bittorrent", "torrent");
                instance.loadEntry("application/x-cdf", "cdf");
                instance.loadEntry("application/x-cdlink", "vcd");
                instance.loadEntry("application/x-chess-pgn", "pgn");
                instance.loadEntry("application/x-cpio", "cpio");
                instance.loadEntry("application/x-debian-package", "deb");
                instance.loadEntry("application/x-debian-package", "udeb");
                instance.loadEntry("application/x-director", "dcr");
                instance.loadEntry("application/x-director", "dir");
                instance.loadEntry("application/x-director", "dxr");
                instance.loadEntry("application/x-dms", "dms");
                instance.loadEntry("application/x-doom", "wad");
                instance.loadEntry("application/x-dvi", "dvi");
                instance.loadEntry("application/x-flac", "flac");
                instance.loadEntry("application/x-font", "pfa");
                instance.loadEntry("application/x-font", "pfb");
                instance.loadEntry("application/x-font", "gsf");
                instance.loadEntry("application/x-font", "pcf");
                instance.loadEntry("application/x-font", "pcf.Z");
                instance.loadEntry("application/x-freemind", "mm");
                instance.loadEntry("application/x-futuresplash", "spl");
                instance.loadEntry("application/x-gnumeric", "gnumeric");
                instance.loadEntry("application/x-go-sgf", "sgf");
                instance.loadEntry("application/x-graphing-calculator", "gcf");
                instance.loadEntry("application/x-gtar", "gtar");
                instance.loadEntry("application/x-gtar", "tgz");
                instance.loadEntry("application/x-gtar", "taz");
                instance.loadEntry("application/x-hdf", "hdf");
                instance.loadEntry("application/x-ica", "ica");
                instance.loadEntry("application/x-internet-signup", "ins");
                instance.loadEntry("application/x-internet-signup", "isp");
                instance.loadEntry("application/x-iphone", "iii");
                instance.loadEntry("application/x-iso9660-image", "iso");
                instance.loadEntry("application/x-jmol", "jmz");
                instance.loadEntry("application/x-kchart", "chrt");
                instance.loadEntry("application/x-killustrator", "kil");
                instance.loadEntry("application/x-koan", "skp");
                instance.loadEntry("application/x-koan", "skd");
                instance.loadEntry("application/x-koan", "skt");
                instance.loadEntry("application/x-koan", "skm");
                instance.loadEntry("application/x-kpresenter", "kpr");
                instance.loadEntry("application/x-kpresenter", "kpt");
                instance.loadEntry("application/x-kspread", "ksp");
                instance.loadEntry("application/x-kword", "kwd");
                instance.loadEntry("application/x-kword", "kwt");
                instance.loadEntry("application/x-latex", "latex");
                instance.loadEntry("application/x-lha", "lha");
                instance.loadEntry("application/x-lzh", "lzh");
                instance.loadEntry("application/x-lzx", "lzx");
                instance.loadEntry("application/x-maker", "frm");
                instance.loadEntry("application/x-maker", "maker");
                instance.loadEntry("application/x-maker", "frame");
                instance.loadEntry("application/x-maker", "fb");
                instance.loadEntry("application/x-maker", "book");
                instance.loadEntry("application/x-maker", "fbdoc");
                instance.loadEntry("application/x-mif", "mif");
                instance.loadEntry("application/x-ms-wmd", "wmd");
                instance.loadEntry("application/x-ms-wmz", "wmz");
                instance.loadEntry("application/x-msi", "msi");
                instance.loadEntry("application/x-ns-proxy-autoconfig", "pac");
                instance.loadEntry("application/x-nwc", "nwc");
                instance.loadEntry("application/x-object", "o");
                instance.loadEntry("application/x-oz-application", "oza");
                instance.loadEntry("application/x-pkcs12", "p12");
                instance.loadEntry("application/x-pkcs7-certreqresp", "p7r");
                instance.loadEntry("application/x-pkcs7-crl", "crl");
                instance.loadEntry("application/x-quicktimeplayer", "qtl");
                instance.loadEntry("application/x-shar", "shar");
                instance.loadEntry("application/x-shockwave-flash", "swf");
                instance.loadEntry("application/x-stuffit", "sit");
                instance.loadEntry("application/x-sv4cpio", "sv4cpio");
                instance.loadEntry("application/x-sv4crc", "sv4crc");
                instance.loadEntry("application/x-tar", "tar");
                instance.loadEntry("application/x-texinfo", "texinfo");
                instance.loadEntry("application/x-texinfo", "texi");
                instance.loadEntry("application/x-troff", "t");
                instance.loadEntry("application/x-troff", "roff");
                instance.loadEntry("application/x-troff-man", "man");
                instance.loadEntry("application/x-ustar", "ustar");
                instance.loadEntry("application/x-wais-source", "src");
                instance.loadEntry("application/x-wingz", "wz");
                instance.loadEntry("application/x-webarchive", "webarchive");
                instance.loadEntry("application/x-x509-ca-cert", "crt");
                instance.loadEntry("application/x-x509-user-cert", "crt");
                instance.loadEntry("application/x-xcf", "xcf");
                instance.loadEntry("application/x-xfig", "fig");
                instance.loadEntry("application/xhtml+xml", "xhtml");
                instance.loadEntry("audio/3gpp", "3gpp");
                instance.loadEntry("audio/amr", "amr");
                instance.loadEntry("audio/basic", "snd");
                instance.loadEntry("audio/midi", "mid");
                instance.loadEntry("audio/midi", "midi");
                instance.loadEntry("audio/midi", "kar");
                instance.loadEntry("audio/midi", "xmf");
                instance.loadEntry("audio/mobile-xmf", "mxmf");
                instance.loadEntry("audio/mpeg", "mpga");
                instance.loadEntry("audio/mpeg", "mpega");
                instance.loadEntry("audio/mpeg", "mp2");
                instance.loadEntry("audio/mpeg", "mp3");
                instance.loadEntry("audio/mpeg", "m4a");
                instance.loadEntry("audio/mpegurl", "m3u");
                instance.loadEntry("audio/prs.sid", "sid");
                instance.loadEntry("audio/x-aiff", "aif");
                instance.loadEntry("audio/x-aiff", "aiff");
                instance.loadEntry("audio/x-aiff", "aifc");
                instance.loadEntry("audio/x-gsm", "gsm");
                instance.loadEntry("audio/x-mpegurl", "m3u");
                instance.loadEntry("audio/x-ms-wma", "wma");
                instance.loadEntry("audio/x-ms-wax", "wax");
                instance.loadEntry("audio/x-pn-realaudio", "ra");
                instance.loadEntry("audio/x-pn-realaudio", "rm");
                instance.loadEntry("audio/x-pn-realaudio", "ram");
                instance.loadEntry("audio/x-realaudio", "ra");
                instance.loadEntry("audio/x-scpls", "pls");
                instance.loadEntry("audio/x-sd2", "sd2");
                instance.loadEntry("audio/x-wav", "wav");
                instance.loadEntry("image/bmp", "bmp");
                instance.loadEntry("image/gif", "gif");
                instance.loadEntry("image/ico", "cur");
                instance.loadEntry("image/ico", "ico");
                instance.loadEntry("image/ief", "ief");
                instance.loadEntry("image/jpeg", "jpeg");
                instance.loadEntry("image/jpeg", "jpg");
                instance.loadEntry("image/jpeg", "jpe");
                instance.loadEntry("image/pcx", "pcx");
                instance.loadEntry("image/png", "png");
                instance.loadEntry("image/svg+xml", "svg");
                instance.loadEntry("image/svg+xml", "svgz");
                instance.loadEntry("image/tiff", "tiff");
                instance.loadEntry("image/tiff", "tif");
                instance.loadEntry("image/vnd.djvu", "djvu");
                instance.loadEntry("image/vnd.djvu", "djv");
                instance.loadEntry("image/vnd.wap.wbmp", "wbmp");
                instance.loadEntry("image/x-cmu-raster", "ras");
                instance.loadEntry("image/x-coreldraw", "cdr");
                instance.loadEntry("image/x-coreldrawpattern", "pat");
                instance.loadEntry("image/x-coreldrawtemplate", "cdt");
                instance.loadEntry("image/x-corelphotopaint", "cpt");
                instance.loadEntry("image/x-icon", "ico");
                instance.loadEntry("image/x-jg", "art");
                instance.loadEntry("image/x-jng", "jng");
                instance.loadEntry("image/x-ms-bmp", "bmp");
                instance.loadEntry("image/x-photoshop", "psd");
                instance.loadEntry("image/x-portable-anymap", "pnm");
                instance.loadEntry("image/x-portable-bitmap", "pbm");
                instance.loadEntry("image/x-portable-graymap", "pgm");
                instance.loadEntry("image/x-portable-pixmap", "ppm");
                instance.loadEntry("image/x-rgb", "rgb");
                instance.loadEntry("image/x-xbitmap", "xbm");
                instance.loadEntry("image/x-xpixmap", "xpm");
                instance.loadEntry("image/x-xwindowdump", "xwd");
                instance.loadEntry("model/iges", "igs");
                instance.loadEntry("model/iges", "iges");
                instance.loadEntry("model/mesh", "msh");
                instance.loadEntry("model/mesh", "mesh");
                instance.loadEntry("model/mesh", "silo");
                instance.loadEntry("text/calendar", "ics");
                instance.loadEntry("text/calendar", "icz");
                instance.loadEntry("text/comma-separated-values", "csv");
                instance.loadEntry("text/css", "css");
                instance.loadEntry("text/html", "htm");
                instance.loadEntry("text/html", "html");
                instance.loadEntry("text/h323", "323");
                instance.loadEntry("text/iuls", "uls");
                instance.loadEntry("text/mathml", "mml");
                // add it first so it will be the default for ExtensionFromMimeType
                instance.loadEntry("text/plain", "txt");
                instance.loadEntry("text/plain", "asc");
                instance.loadEntry("text/plain", "text");
                instance.loadEntry("text/plain", "diff");
                instance.loadEntry("text/plain", "po"); // reserve "pot" for vnd.ms-powerpoint
                instance.loadEntry("text/richtext", "rtx");
                instance.loadEntry("text/rtf", "rtf");
                instance.loadEntry("text/texmacs", "ts");
                instance.loadEntry("text/text", "phps");
                instance.loadEntry("text/tab-separated-values", "tsv");
                instance.loadEntry("text/xml", "xml");
                instance.loadEntry("text/x-bibtex", "bib");
                instance.loadEntry("text/x-boo", "boo");
                instance.loadEntry("text/x-c++hdr", "h++");
                instance.loadEntry("text/x-c++hdr", "hpp");
                instance.loadEntry("text/x-c++hdr", "hxx");
                instance.loadEntry("text/x-c++hdr", "hh");
                instance.loadEntry("text/x-c++src", "c++");
                instance.loadEntry("text/x-c++src", "cpp");
                instance.loadEntry("text/x-c++src", "cxx");
                instance.loadEntry("text/x-chdr", "h");
                instance.loadEntry("text/x-component", "htc");
                instance.loadEntry("text/x-csh", "csh");
                instance.loadEntry("text/x-csrc", "c");
                instance.loadEntry("text/x-dsrc", "d");
                instance.loadEntry("text/x-haskell", "hs");
                instance.loadEntry("text/x-java", "java");
                instance.loadEntry("text/x-literate-haskell", "lhs");
                instance.loadEntry("text/x-moc", "moc");
                instance.loadEntry("text/x-pascal", "p");
                instance.loadEntry("text/x-pascal", "pas");
                instance.loadEntry("text/x-pcs-gcd", "gcd");
                instance.loadEntry("text/x-setext", "etx");
                instance.loadEntry("text/x-tcl", "tcl");
                instance.loadEntry("text/x-tex", "tex");
                instance.loadEntry("text/x-tex", "ltx");
                instance.loadEntry("text/x-tex", "sty");
                instance.loadEntry("text/x-tex", "cls");
                instance.loadEntry("text/x-vcalendar", "vcs");
                instance.loadEntry("text/x-vcard", "vcf");
                instance.loadEntry("video/3gpp", "3gpp");
                instance.loadEntry("video/3gpp", "3gp");
                instance.loadEntry("video/3gpp", "3g2");
                instance.loadEntry("video/dl", "dl");
                instance.loadEntry("video/dv", "dif");
                instance.loadEntry("video/dv", "dv");
                instance.loadEntry("video/fli", "fli");
                instance.loadEntry("video/m4v", "m4v");
                instance.loadEntry("video/mpeg", "mpeg");
                instance.loadEntry("video/mpeg", "mpg");
                instance.loadEntry("video/mpeg", "mpe");
                instance.loadEntry("video/mp4", "mp4");
                instance.loadEntry("video/mpeg", "VOB");
                instance.loadEntry("video/quicktime", "qt");
                instance.loadEntry("video/quicktime", "mov");
                instance.loadEntry("video/vnd.mpegurl", "mxu");
                instance.loadEntry("video/x-la-asf", "lsf");
                instance.loadEntry("video/x-la-asf", "lsx");
                instance.loadEntry("video/x-mng", "mng");
                instance.loadEntry("video/x-ms-asf", "asf");
                instance.loadEntry("video/x-ms-asf", "asx");
                instance.loadEntry("video/x-ms-wm", "wm");
                instance.loadEntry("video/x-ms-wmv", "wmv");
                instance.loadEntry("video/x-ms-wmx", "wmx");
                instance.loadEntry("video/x-ms-wvx", "wvx");
                instance.loadEntry("video/x-msvideo", "avi");
                instance.loadEntry("video/x-sgi-movie", "movie");
                instance.loadEntry("x-conference/x-cooltalk", "ice");
                instance.loadEntry("x-epoc/x-sisx-app", "sisx");

                // Some more mime-pairs
                instance.loadEntry("video/vnd.rn-realmedia", "rmvb");
                instance.loadEntry("video/vnd.rn-realmedia", "rm");
                instance.loadEntry("video/vnd.rn-realvideo", "rv");
                instance.loadEntry("video/x-flv", "flv");
                instance.loadEntry("video/x-flv", "hlv");
                instance.loadEntry("video/x-matroska", "mkv");
                instance.loadEntry("audio/vnd.rn-realaudio", "ra");
                instance.loadEntry("audio/vnd.rn-realaudio", "ram");
                instance.loadEntry("text/plain", "lrc");
                // End more mime-pairs
            }

            return instance;
        }

        // Static method called by jni.
        @SuppressWarnings("unused")
        private static String mimeTypeFromExtension(String extension) {
            return instance().getMimeTypeFromExtension(extension);
        }

        /**
         * Return the registered extension for the given MIME type. Note that some MIME types map to multiple extensions. This call
         * will return the most common extension for the given MIME type.
         *
         * @param mimeType A MIME type (i.e. text/plain)
         * @return The extension for the given MIME type or null iff there is none.
         */
        public String getExtensionFromMimeType(String mimeType) {
            if (mimeType != null && mimeType.length() > 0) {
                return mMimeTypeToExtensionMap.get(mimeType);
            }

            return null;
        }

        /**
         * Return the MIME type for the given extension.
         *
         * @param extension A file extension without the leading '.'
         * @return The MIME type for the given extension or null iff there is none.
         */
        public String getMimeTypeFromExtension(String extension) {
            if (extension != null && extension.length() > 0) {
                return mExtensionToMimeTypeMap.get(extension);
            }

            return null;
        }

        /**
         * Return true if the given extension has a registered MIME type.
         *
         * @param extension A file extension without the leading '.'
         * @return True iff there is an extension entry in the map.
         */
        public boolean hasExtension(String extension) {
            return extension != null && extension.length() > 0 && mExtensionToMimeTypeMap.containsKey(extension);
        }

        /**
         * Return true if the given MIME type has an entry in the map.
         *
         * @param mimeType A MIME type (i.e. text/plain)
         * @return True iff there is a mimeType entry in the map.
         */
        public boolean hasMimeType(String mimeType) {
            return mimeType != null && mimeType.length() > 0 && mMimeTypeToExtensionMap.containsKey(mimeType);

        }

        /**
         * Load an entry into the map. This does not check if the item already exists, it trusts the caller!
         */
        private void loadEntry(String mimeType, String extension) {
            //
            // if we have an existing x --> y mapping, we do not want to
            // override it with another mapping x --> ?
            // this is mostly because of the way the mime-type map below
            // is constructed (if a mime type maps to several extensions
            // the first extension is considered the most popular and is
            // added first; we do not want to overwrite it later).
            //
            if (!mMimeTypeToExtensionMap.containsKey(mimeType)) {
                mMimeTypeToExtensionMap.put(mimeType, extension);
            }
            mExtensionToMimeTypeMap.put(extension, mimeType);
        }
    }

}
