package ch.sbb.importservice.service.csv;

import ch.sbb.atlas.exception.CsvException;
import ch.sbb.atlas.imports.DidokCsvMapper;
import ch.sbb.atlas.imports.user.UserCsvModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DidokUserCsvService {

  public List<UserCsvModel> getUserCsvModels(File file, ApplicationType applicationType) {
    log.info("Starting file import process");
    log.info("CSV File to import: {}", file.getName());
    List<UserCsvModel> csvModelsToUpdate = getCsvModelsToUpdate(file);
    csvModelsToUpdate.forEach(userCsvModel -> userCsvModel.setApplicationType(applicationType));
    log.info("Found {} Csv Models to send to ServicePointDirectory", csvModelsToUpdate.size());
    return csvModelsToUpdate;
  }

  public List<UserCsvModel> getCsvModelsToUpdate(File importFile) {
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(importFile, StandardCharsets.UTF_8))) {

      List<String> csvLinesToProcess = new ArrayList<>(getLines(bufferedReader));
      MappingIterator<UserCsvModel> mappingIterator = DidokCsvMapper.CSV_MAPPER.readerFor(UserCsvModel.class)
          .with(DidokCsvMapper.CSV_SCHEMA)
          .readValues(String.join("\n", csvLinesToProcess));
      List<UserCsvModel> mappedCsvModels = mapObjects(mappingIterator);
      log.info("Found {} lines to update", mappedCsvModels.size());
      return mappedCsvModels;
    } catch (IOException e) {
      throw new CsvException(e);
    }
  }

  private List<UserCsvModel> mapObjects(MappingIterator<UserCsvModel> mappingIterator) {
    final List<UserCsvModel> mappedObjects = new ArrayList<>();
    while (mappingIterator.hasNext()) {
      mappedObjects.add(mappingIterator.next());
    }
    return mappedObjects;
  }

  private List<String> getLines(BufferedReader bufferedReader)
      throws IOException {
    List<String> lines = new ArrayList<>();
    String line;
    while ((line = bufferedReader.readLine()) != null) {
          lines.add(line);
    }
    return lines;
  }

}
