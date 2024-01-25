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

    public File unzipFile(File zippedFile, String destinationDirectoryPath) throws IOException {
        File destinationDirectory = new File(destinationDirectoryPath);

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zippedFile))) {
            byte[] buffer = new byte[BUFFER_SIZE];
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            if (zipEntry != null) {
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
                zipInputStream.getNextEntry();
                return newFile;
            }
        }
        return null;
    }

}
