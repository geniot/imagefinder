package com.imagefinder;

import java.io.*;
import java.nio.charset.Charset;

public class Utils {
    public static boolean isValidISOLatin1 (String s) {
        return Charset.forName("US-ASCII").newEncoder().canEncode(s);
    } // or "ISO-8859-1" for ISO Latin 1

    public static byte[] serialize(Object yourObject) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(yourObject);
            byte[] yourBytes = bos.toByteArray();
            return yourBytes;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return new byte[]{};
    }

    public static Object deserialize(byte[] yourBytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            Object o = in.readObject();
            return o;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                bis.close();
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return null;
    }

    public static byte[] getImageBytes(String fileName) {
        try {
            InputStream is = AppMain.class.getClassLoader().getResourceAsStream(fileName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipUtils.copyLarge(is, baos);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new byte[]{};
    }

    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    public static String getExtension(String s) {
        String ext = null;
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

}
