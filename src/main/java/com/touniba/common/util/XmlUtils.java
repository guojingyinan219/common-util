package com.touniba.common.util;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * TODO: to optimize
 */
public class XmlUtils {
    private static final DocumentBuilder BUILDER;
    private static final Transformer TRANSFORMER;

    static {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // ignore all comments
            factory.setIgnoringComments(true);
            // allow includes
            factory.setNamespaceAware(true);
            factory.setXIncludeAware(true);
            BUILDER = factory.newDocumentBuilder();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    static {
        try {
            TRANSFORMER = TransformerFactory.newInstance().newTransformer();
            TRANSFORMER.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            TRANSFORMER.setOutputProperty(OutputKeys.INDENT, "yes");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static Document read(InputStream in) throws IOException {
        synchronized (BUILDER) {
            try {
                return BUILDER.parse(in);
            } catch (Throwable e) {
                throw new IOException(e);
            }
        }
    }

    /**
     * Read by file.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static Document read(File file) throws IOException {
        synchronized (BUILDER) {
            try {
                return BUILDER.parse(file);
            } catch (Throwable e) {
                throw new IOException(e);
            }
        }
    }

    /**
     * Write
     *
     * @param out
     * @param document
     * @throws IOException
     */
    public static void write(OutputStream out, Document document) throws IOException {
        synchronized (TRANSFORMER) {
            try {
                DOMSource source = new DOMSource(document);
                StreamResult result = new StreamResult(out);
                TRANSFORMER.transform(source, result);
            } catch (Throwable e) {
                throw new IOException(e);
            }
        }
    }
}
