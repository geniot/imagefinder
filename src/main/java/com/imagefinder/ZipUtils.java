package com.imagefinder;


import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Author: Vitaly Sazanovich
 * Email: vitaly.sazanovich@gmail.com
 * Date: 08/04/14
 * Time: 13:52
 */
public class ZipUtils {
    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static void fillFileNames(File selectedFile, List<String> fileNames, List<String> imageExtensions) throws IOException {
        ZipFile zipFile = new ZipFile(selectedFile);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String ext = Utils.getExtension(entry.getName());
            if (ext != null) {
                ext = ext.toLowerCase();
                if (imageExtensions.contains(ext)) {
                    fileNames.add(entry.getName());
                }
            }
        }
    }

    public static byte[] getFileFromArchive(File selectedFile, String fileName) throws IOException {
        ZipFile zipFile = new ZipFile(selectedFile);
        ZipEntry zipEntry = zipFile.getEntry(fileName);
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = zipFile.getInputStream(zipEntry);
            out = new ByteArrayOutputStream();
            copy(in, out);
        } finally {
            closeQuietly(in);
            closeQuietly(out);
        }

        return out.toByteArray();
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }


    public static long copyLarge(InputStream input, OutputStream output)
            throws IOException {
        return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
    }

    public static long copyLarge(InputStream input, OutputStream output, byte[] buffer)
            throws IOException {
        long count = 0;
        int n = 0;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static void closeQuietly(InputStream input) {
        closeQuietly((Closeable) input);
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ioe) {
            // ignore
        }
    }
}