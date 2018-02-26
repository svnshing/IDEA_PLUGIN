package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by admin on 2018/1/31.
 */
public class ZipUtil {
    private File folderToCompress = null;
    private String zipFilePath = null;
    private List<String> fileList = new ArrayList();

    public ZipUtil(File folderToCompress, String zipFilePath) {
        this.folderToCompress = folderToCompress;
        this.zipFilePath = zipFilePath;
    }

    public void zipIt() {
        generateFileList(this.folderToCompress);

        byte[] buffer = new byte[1024];
        String absolutePath = this.folderToCompress.getAbsolutePath();

        ZipOutputStream zos = null;
        try {
            FileOutputStream fos = new FileOutputStream(new File(this.zipFilePath));
            zos = new ZipOutputStream(fos);

            System.out.println("Output to Zip : " + this.zipFilePath);
            FileInputStream in = null;

            List files = new ArrayList();
            for (String fileRelativePath : this.fileList) {
                System.out.println("File Added : " + fileRelativePath);
                ZipEntry ze = new ZipEntry(fileRelativePath);
                zos.putNextEntry(ze);
                try {
                    File file = new File(absolutePath + File.separator + fileRelativePath);
                    files.add(file);
                    in = new FileInputStream(absolutePath + File.separator + fileRelativePath);
                    int len;
                    while ((len = in.read(buffer)) > 0)
                        zos.write(buffer, 0, len);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            }

            zos.closeEntry();
            System.out.println("Folder successfully compressed");

            for (Iterator<File> it = files.iterator(); it.hasNext(); ) {
                forceDelete(it.next());
            }
            File[] subFolders = this.folderToCompress.listFiles();
            int ze = subFolders.length;
            for (int i = 0; i < ze; i++) {
                File subFolder = subFolders[i];
                if ((subFolder.isDirectory()) || (!subFolder.getPath().endsWith(".zip")))
                    deleteDirectory(subFolder);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void generateFileList(File node) {
        if (node.isFile()) {
            this.fileList.add(generateZipEntryPath(node));
        }

        if (node.isDirectory()) {
            String[] subNote = node.list();
            for (String filename : subNote)
                generateFileList(new File(node, filename));
        }
    }

    private String generateZipEntryPath(File fileToAdd) {
        String absolutePathOfFileToAdd = fileToAdd.getAbsolutePath();
        return absolutePathOfFileToAdd.substring(this.folderToCompress.getAbsolutePath().length() + 1, absolutePathOfFileToAdd.length());
    }

    public static void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }
                String message = "Unable to delete file: " + file;

                throw new IOException(message);
            }
        }
    }

    public static void deleteDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            return;
        }

        cleanDirectory(directory);

        if (!directory.delete()) {
            String message = "Unable to delete directory " + directory + ".";

            throw new IOException(message);
        }
    }

    public static void cleanDirectory(File directory) throws IOException {
        if (!directory.exists()) {
            String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        File[] files = directory.listFiles();
        if (files == null) {
            throw new IOException("Failed to list contents of " + directory);
        }

        IOException exception = null;
        for (File file : files) {
            try {
                forceDelete(file);
            } catch (IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception)
            throw exception;
    }
}
