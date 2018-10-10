package com.touniba.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Magoo on 2016/7/21.
 */
public class FileUtils {

    /**
     * List all sub files
     *
     * @param parent
     * @param regex
     * @return
     */
    public static List<File> listAllFiles(File parent, String regex) {
        List<File> list = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        parent.listFiles(file -> {
            if (file.isDirectory()) {
                list.addAll(listAllFiles(file, regex));
            } else {
                if (pattern.matcher(file.getName()).matches()) {
                    list.add(file);
                }
            }
            return false;
        });
        return list;
    }

    /**
     * Create a dir O
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static boolean createDir(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            return file.mkdirs();
        } else if (!file.isDirectory()) {
            throw new IOException("Cannot create dir '" + path + "': File exists");
        }
        return file.isDirectory();
    }

    /**
     * Create a empty file
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static boolean createFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            return file.createNewFile();
        } else if (!file.isFile()) {
            throw new IOException("Cannot create file '" + path + "': Dir exists");
        }
        return file.isFile();
    }

    /**
     * Create a file with content
     *
     * @param fileName
     * @param content
     * @param override
     * @throws IOException
     */
    public static void createFile(String fileName, byte[] content, boolean override) throws IOException {
        if (null == content) {
            throw new IOException("Content must not be null");
        }
        File file = new File(fileName);
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File is a directory: " + file.getAbsolutePath());
            }
            if (!override) {
                throw new IOException("File already exists: " + file.getAbsolutePath());
            }
        }
        try (FileOutputStream fos = new FileOutputStream(new File(fileName), false)) {
            fos.write(content, 0, content.length);
        }
    }

    /**
     * Append content to file.
     *
     * @param fileName
     * @param content
     * @throws IOException
     */
    public static void appendFile(String fileName, byte[] content) throws IOException {
        if (null == content) {
            throw new IOException("Content must not be null");
        }
        File file = new File(fileName);
        if (!file.exists()) {
            createFile(fileName, content, true);
        } else {
            if (file.isDirectory()) {
                throw new IOException("Cannot append to a directory: " + file.getAbsolutePath());
            }
            try (FileOutputStream fos = new FileOutputStream(new File(fileName), true)) {
                fos.write(content, 0, content.length);
            }
        }
    }
}
