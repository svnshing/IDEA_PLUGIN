package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by admin on 2018/1/31.
 */
public class FileUtil {
    public static void writeExportLog(String filepath, StringBuffer sb)
    {
        FileOutputStream out = null;
        try {
            createDir(filepath);
            File logFile = new File(filepath);
            if (!logFile.exists()) logFile.createNewFile();
            out = new FileOutputStream(logFile, true);
            out.write(sb.toString().getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyFile(String src, String dest)
            throws IOException
    {
        FileInputStream in = new FileInputStream(src);

        File file = new File(dest);

        createDir(dest);
        if (!file.exists()) file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);

        byte[] buffer = new byte[1024];
        int c;
        while ((c = in.read(buffer)) != -1) {
            for (int i = 0; i < c; i++)
                out.write(buffer[i]);
        }
        in.close();
        out.close();
    }

    public static void createDir(String dir) {
        File _dir = new File(dir.substring(0, dir.lastIndexOf("\\")));
        if (!_dir.isDirectory()) _dir.mkdirs();
    }
}
