package ch.sbb.line.directory.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public final class PdfFiles {

  public static List<MultipartFile> multipartFiles = multipartFiles();

  public static List<File> files = getFiles();

  private PdfFiles() {
  }

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
    MultipartFile multipartFile = getMultipartFile("src/test/resources/pdf/dummy.pdf");
    MultipartFile multipartFile1 = getMultipartFile("src/test/resources/pdf/dummy1.pdf");
    MultipartFile multipartFile2 = getMultipartFile("src/test/resources/pdf/dummy2.pdf");
    MultipartFile multipartFile3 = getMultipartFile("src/test/resources/pdf/dummy3.pdf");
    multipartFiles.add(multipartFile);
    multipartFiles.add(multipartFile1);
    multipartFiles.add(multipartFile2);
    multipartFiles.add(multipartFile3);
    return multipartFiles;
  }

  private static List<File> getFiles() {
    List<File> files = new ArrayList<>();
    File file = new File("src/test/resources/pdf/dummy.pdf");
    File file1 = new File("src/test/resources/pdf/dummy1.pdf");
    File file2 = new File("src/test/resources/pdf/dummy2.pdf");
    File file3 = new File("src/test/resources/pdf/dummy3.pdf");
    File file4 = new File("src/test/resources/pdf/test.txt");
    File file5 = new File("src/test/resources/pdf/test1.txt");
    files.add(file);
    files.add(file1);
    files.add(file2);
    files.add(file3);
    files.add(file4);
    files.add(file5);
    return files;
  }
}
