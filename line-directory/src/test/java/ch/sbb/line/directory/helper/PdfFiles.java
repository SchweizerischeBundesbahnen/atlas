package ch.sbb.line.directory.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@UtilityClass
public final class PdfFiles {

  public static final List<MultipartFile> MULTIPART_FILES = multipartFiles();

  public static final List<File> FILES = getFiles();

  private static MultipartFile getMultipartFile(String pathName) {
    File file = new File(pathName);
    FileInputStream input;
    try {
      input = new FileInputStream(file);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    try {
      return new MockMultipartFile("documents",
        file.getName(), "application/pdf", IOUtils.toByteArray(input));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static List<MultipartFile> multipartFiles() {
    List<MultipartFile> multipartFiles = new ArrayList<>();
    multipartFiles.add(getMultipartFile("src/test/resources/pdf/dummy.pdf"));
    multipartFiles.add(getMultipartFile("src/test/resources/pdf/dummy1.pdf"));
    multipartFiles.add(getMultipartFile("src/test/resources/pdf/dummy2.pdf"));
    multipartFiles.add(getMultipartFile("src/test/resources/pdf/dummy3.pdf"));
    return multipartFiles;
  }

  private static List<File> getFiles() {
    List<File> files = new ArrayList<>();
    files.add(new File("src/test/resources/pdf/dummy.pdf"));
    files.add(new File("src/test/resources/pdf/dummy1.pdf"));
    files.add(new File("src/test/resources/pdf/dummy2.pdf"));
    files.add(new File("src/test/resources/pdf/dummy3.pdf"));
    files.add(new File("src/test/resources/pdf/test.txt"));
    files.add(new File("src/test/resources/pdf/test1.txt"));
    return files;
  }
}
