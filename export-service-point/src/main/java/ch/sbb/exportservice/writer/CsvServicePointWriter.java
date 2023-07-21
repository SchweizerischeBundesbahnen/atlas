package ch.sbb.exportservice.writer;

import static ch.sbb.exportservice.model.ServicePointVersionCsvModel.Fields.numberShort;

import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ServicePointExportType;
import ch.sbb.exportservice.model.ServicePointVersionCsvModel;
import ch.sbb.exportservice.model.ServicePointVersionCsvModel.Fields;
import ch.sbb.exportservice.service.FileExportService;
import java.nio.charset.StandardCharsets;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Component;

@Component
public class CsvServicePointWriter {

  private static final String[] CSV_HEADER = new String[]{numberShort, Fields.uicCountryCode,
      Fields.sloid, Fields.number, Fields.checkDigit, Fields.validFrom, Fields.validTo, Fields.designationOfficial,
      Fields.designationLong, Fields.abbreviation, Fields.operatingPoint, Fields.operatingPointWithTimetable, Fields.stopPoint,
      Fields.stopPointType, Fields.freightServicePoint, Fields.trafficPoint,
      Fields.borderPoint, Fields.hasGeolocation, Fields.isoCountryCode,
      Fields.cantonName, Fields.cantonFsoNumber, Fields.cantonAbbreviation,
      Fields.districtName, Fields.districtFsoNumber, Fields.municipalityName, Fields.fsoNumber,
      Fields.localityName, Fields.operatingPointType, Fields.operatingPointTechnicalTimetableType,
      Fields.meansOfTransport, Fields.categories, Fields.operatingPointTrafficPointType,
      Fields.operatingPointRouteNetwork, Fields.operatingPointKilometer, Fields.operatingPointKilometerMasterNumber,
      Fields.sortCodeOfDestinationStation, Fields.businessOrganisation, Fields.businessOrganisationNumber,
      Fields.businessOrganisationAbbreviationDe, Fields.businessOrganisationAbbreviationFr,
      Fields.businessOrganisationAbbreviationIt, Fields.businessOrganisationAbbreviationEn,
      Fields.businessOrganisationDescriptionDe, Fields.businessOrganisationDescriptionFr,
      Fields.businessOrganisationDescriptionIt, Fields.businessOrganisationDescriptionEn, Fields.fotComment, Fields.lv95East,
      Fields.lv95North, Fields.wgs84East, Fields.wgs84North, Fields.wgs84WebEast, Fields.wgs84WebNorth,
      Fields.height, Fields.creationDate, Fields.editionDate, Fields.status
  };
  private static final String DELIMITER = ";";
  @Autowired
  private FileExportService fileExportService;

  public FlatFileItemWriter<ServicePointVersionCsvModel> csvWriter(ServicePointExportType exportType) {
    WritableResource outputResource = new FileSystemResource(
        fileExportService.createFileNamePath(ExportExtensionFileType.CSV_EXTENSION,
            exportType));
    FlatFileItemWriter<ServicePointVersionCsvModel> writer = new FlatFileItemWriter<>();
    writer.setResource(outputResource);
    writer.setAppendAllowed(true);
    writer.setLineAggregator(new DelimitedLineAggregator<>() {
      {
        setDelimiter(DELIMITER);
        setFieldExtractor(new BeanWrapperFieldExtractor<>() {{
          setNames(CSV_HEADER);
        }});
      }
    });
    writer.setHeaderCallback(new CsvFlatFileHeaderCallback(CSV_HEADER));
    writer.setEncoding(StandardCharsets.UTF_8.name());
    return writer;
  }

}
