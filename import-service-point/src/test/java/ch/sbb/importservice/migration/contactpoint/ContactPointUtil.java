package ch.sbb.importservice.migration.contactpoint;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ContactPointUtil {

  public static InputStream removeFirstNLines(InputStream inputStream, int linesToSkip) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

    try {
      // Skip the first linesToSkip lines
      for (int i = 0; i < linesToSkip; i++) {
        reader.readLine();
      }

      // Return the modified InputStream
      return new ByteArrayInputStream(reader.readLine().getBytes()); // just an example, adjust as needed
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }




  InputStream getInputStream(InputStream inputStream1, InputStream inputStream2) throws IOException{
//    InputStream inputStream1 = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + didokFileName);

    //        String outputFile = removeFirstLines(CsvReader.BASE_PATH + DIDOK_CSV_FILE_INFO_DESK, CsvReader.BASE_PATH + DIDOK_CSV_FILE_INFO_DESK_MOD);
    //        FileInputStream inputStream2 = new FileInputStream(outputFile);
    //        InputStream inputStream2 = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_CSV_FILE_INFO_DESK_MOD);



//    InputStream inputStream2 = this.getClass().getResourceAsStream(CsvReader.BASE_PATH + DIDOK_CSV_FILE_INFO_DESK);
    InputStream modifiedInputStream = removeFirstNLines(inputStream2, 7);

    // Concatenate input streams
    SequenceInputStream sequenceInputStream = new SequenceInputStream(inputStream1, modifiedInputStream);

    // Convert the sequenceInputStream to a regular InputStream
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int bytesRead;
    while ((bytesRead = sequenceInputStream.read(buffer)) != -1) {
      outputStream.write(buffer, 0, bytesRead);
    }
    InputStream concatenatedInputStream = new ByteArrayInputStream(outputStream.toByteArray());

    // Use the concatenated input stream as needed

    // Close the streams
    sequenceInputStream.close();
    concatenatedInputStream.close();
    return concatenatedInputStream;
  }

}
