package ch.sbb.atlas.servicepointdirectory.migration.servicepoints;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@UtilityClass
public class MigrationTestsUtilityClass {

    private final int BUFFER_SIZE = 8192; // 8 KB


//    public static String unzipFile1(String zipFilePath, String outputDirectory) {
//        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
//            ZipEntry entry = zipInputStream.getNextEntry();
//            if (entry != null) {
//                String unzippedFilePath = outputDirectory + File.separator + entry.getName();
//                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(unzippedFilePath))) {
//                    byte[] buffer = new byte[1024];
//                    int bytesRead;
//                    while ((bytesRead = zipInputStream.read(buffer)) != -1) {
//                        bos.write(buffer, 0, bytesRead);
//                    }
//                    return unzippedFilePath;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

//    public void unzipFile2(String fileZipPath, String destinationDirectoryPath) throws IOException {
//        File destinationDirectory = new File(destinationDirectoryPath);
//
//        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(fileZipPath))) {
//            byte[] buffer = new byte[BUFFER_SIZE];
//            ZipEntry zipEntry = zipInputStream.getNextEntry();
//            while (zipEntry != null) {
//                File newFile = newFile(destinationDirectory, zipEntry);
//                if (zipEntry.isDirectory()) {
//                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
//                        throw new IOException("Failed to create directory " + newFile);
//                    }
//                } else {
//                    File parent = newFile.getParentFile();
//                    if (!parent.isDirectory() && !parent.mkdirs()) {
//                        throw new IOException("Failed to create directory " + parent);
//                    }
//
//                    try (FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
//                        int length;
//                        while ((length = zipInputStream.read(buffer)) > 0) {
//                            fileOutputStream.write(buffer, 0, length);
//                        }
//                    }
//                }
//                zipEntry = zipInputStream.getNextEntry();
//            }
//        }
//    }

    public File unzipFileWithInputFileAndOutputFile(File zippedFile, String destinationDirectoryPath) throws IOException {
        File destinationDirectory = new File(destinationDirectoryPath);

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zippedFile))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
//                File newFile = newFile(destinationDirectory, zipEntry);
                File newFile = new File(destinationDirectory, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    try (FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
                        int length;
                        while ((length = zipInputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, length);
                        }
                    }
                }
                zipEntry = zipInputStream.getNextEntry();
                return newFile;
            }
        }
        return null;
    }

    public void unzipFileWithInputPathAndOutputPath(String fileZipPath, String destinationDirectoryPath) throws IOException {
        File destinationDirectory = new File(destinationDirectoryPath);

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(fileZipPath))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
//                File newFile = newFile(destinationDirectory, zipEntry);
                File newFile = new File(destinationDirectory, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    try (FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
                        int length;
                        while ((length = zipInputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, length);
                        }
                    }
                }
                zipEntry = zipInputStream.getNextEntry();
            }
        }
    }

//    public File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
//        File destFile = new File(destinationDir, zipEntry.getName());
//        String destDirPath = destinationDir.getCanonicalPath();
//        String destFilePath = destFile.getCanonicalPath();
//        if (!destFilePath.startsWith(destDirPath + File.separator)) {
//            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
//        }
//        return destFile;
//    }

}
