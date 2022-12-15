package ch.sbb.atlas.servicepointdirectory.service;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ServicePointImportService {

  private final ServicePointVersionRepository servicePointVersionRepository;

  public static List<ServicePointCsvModel> parseServicePoints(InputStream inputStream)
      throws IOException {
    CsvMapper mapper = new CsvMapper().enable(Feature.EMPTY_STRING_AS_NULL);
    CsvSchema csvSchema = CsvSchema.emptySchema()
        .withHeader()
        .withColumnSeparator(';')
        .withEscapeChar('\\');

    MappingIterator<ServicePointCsvModel> mappingIterator = mapper.readerFor(
        ServicePointCsvModel.class).with(csvSchema).readValues(inputStream);
    int counter = 0;
    List<ServicePointCsvModel> servicePoints = new ArrayList<>();
    while (mappingIterator.hasNext() && counter < 10) {
      servicePoints.add(mappingIterator.next());
      counter++;
    }
    log.info("Parsed {} servicePoints", servicePoints.size());
    return servicePoints;
  }

  public void importSPCsvModel(List<ServicePointCsvModel> csvModels) {
    for (ServicePointCsvModel csvModel : csvModels) {
      // GeoLocation
      ServicePointGeolocation servicePointGeolocation = ServicePointGeolocation.builder()
          .source_spatial_ref(1) // TODO: no attribute in Dienststellen_ALL
          .lv03east(csvModel.getE_LV03())
          .lv03north(csvModel.getN_LV03())
          .lv95east(csvModel.getE_LV95())
          .lv95north(csvModel.getN_LV95())
          .wgs84east(csvModel.getE_WGS84())
          .wgs84north(csvModel.getN_WGS84())
          .height(csvModel.getHEIGHT())
          .isoCountryCode(Country.from(csvModel.getLAENDERCODE()).getIsoCode()) // TODO: check with Marek
          .swissCantonFsoNumber(5) // TODO: Marek
          .swissCantonName("Bern") // TODO: Marek
          .swissCantonNumber(5) // TODO: Marek
          .swissDistrictName("Bern") // TODO: Marek
          .swissDistrictNumber(5) // TODO: Marek
          .swissMunicipalityName("Bern") // TODO: Marek
          .swissLocalityName("Bern") // TODO: Marek
          .creationDate(LocalDateTime.parse(csvModel.getERSTELLT_AM(),
              new DateTimeFormatterBuilder()
                  .parseCaseInsensitive()
                  .append(ISO_LOCAL_DATE)
                  .appendLiteral(' ')
                  .append(ISO_LOCAL_TIME)
                  .toFormatter()
          ))
          .creator(csvModel.getERSTELLT_VON())
          .editionDate(LocalDateTime.parse(csvModel.getGEAENDERT_AM(),
              new DateTimeFormatterBuilder()
                  .parseCaseInsensitive()
                  .append(ISO_LOCAL_DATE)
                  .appendLiteral(' ')
                  .append(ISO_LOCAL_TIME)
                  .toFormatter()
          ))
          .editor(csvModel.getGEAENDERT_VON())
          .build();
      // ServicePoint
      ServicePointVersion servicePoint = ServicePointVersion.builder()
          .number(csvModel.getDIDOK_CODE())
          .sloid(csvModel.getSLOID())
          .checkDigit(csvModel.getDIDOK_CODE() % 10)
          .numberShort(csvModel.getNUMMER())
          .country(Country.from(csvModel.getLAENDERCODE()))
          .designationLong(csvModel.getBEZEICHNUNG_LANG())
          .designationOfficial(csvModel.getBEZEICHNUNG_OFFIZIELL())
          .abbreviation(csvModel.getABKUERZUNG())
          .statusDidok3(ServicePointStatus.from(csvModel.getSTATUS()))
          .businessOrganisation(csvModel.getGO_NUMMER().toString()) // TODO: map to sboid GO_Export.csv
          .hasGeolocation(!csvModel.getIS_VIRTUELL())
          .status(Status.VALIDATED)
          .validFrom(LocalDate.parse(csvModel.getGUELTIG_VON()))
          .validTo(LocalDate.parse(csvModel.getGUELTIG_BIS()))
          .categories(
              Arrays.stream(Objects.nonNull(csvModel.getDS_KATEGORIEN_IDS()) ? csvModel.getDS_KATEGORIEN_IDS().split("\\|") :
                      new String[]{})
                  .map(categoryIdStr -> Category.from(Integer.parseInt(categoryIdStr)))
                  .filter(Objects::nonNull).collect(Collectors.toSet())
          )
          .operatingPointType(OperatingPointType.from(csvModel.getBPVB_BETRIEBSPUNKT_ART_ID()))
          .servicePointGeolocation(servicePointGeolocation)
          .creationDate(LocalDateTime.parse(csvModel.getERSTELLT_AM(),
              new DateTimeFormatterBuilder()
                  .parseCaseInsensitive()
                  .append(ISO_LOCAL_DATE)
                  .appendLiteral(' ')
                  .append(ISO_LOCAL_TIME)
                  .toFormatter()
          ))
          .creator(csvModel.getERSTELLT_VON())
          .editionDate(LocalDateTime.parse(csvModel.getGEAENDERT_AM(),
              new DateTimeFormatterBuilder()
                  .parseCaseInsensitive()
                  .append(ISO_LOCAL_DATE)
                  .appendLiteral(' ')
                  .append(ISO_LOCAL_TIME)
                  .toFormatter()
          ))
          .editor(csvModel.getGEAENDERT_VON())
          .build();
      servicePointGeolocation.setServicePointVersion(servicePoint);
      servicePointVersionRepository.save(servicePoint);
    }
  }

}
