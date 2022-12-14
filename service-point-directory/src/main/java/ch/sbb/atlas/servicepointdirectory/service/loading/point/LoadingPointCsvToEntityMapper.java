package ch.sbb.atlas.servicepointdirectory.service.loading.point;

import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

public class LoadingPointCsvToEntityMapper implements Function<LoadingPointCsvModel, LoadingPointVersion> {

  @Override
  public LoadingPointVersion apply(LoadingPointCsvModel loadingPointCsvModel) {
    return LoadingPointVersion.builder()
        .number(loadingPointCsvModel.getNumber())
        .designation(loadingPointCsvModel.getDesignation())
        .designationLong(loadingPointCsvModel.getDesignationLong())
        .connectionPoint(loadingPointCsvModel.getConnectionPoint())
        .servicePointNumber(loadingPointCsvModel.getServicePointNumber())
        .validFrom(loadingPointCsvModel.getValidFrom())
        .validTo(loadingPointCsvModel.getValidTo())
        .creator(loadingPointCsvModel.getCreatedBy())
        .creationDate(loadingPointCsvModel.getCreatedAt())
        .editor(loadingPointCsvModel.getEditedBy())
        .editionDate(loadingPointCsvModel.getEditedAt())
        .build();
  }
}
