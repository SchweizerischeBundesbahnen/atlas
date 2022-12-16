package ch.sbb.atlas.servicepointdirectory.service;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.servicepointdirectory.entity.LocationTypes;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointGeolocation;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.Category;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepointdirectory.enumeration.ServicePointStatus;
import ch.sbb.atlas.servicepointdirectory.enumeration.StopPlaceType;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
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

  /*@PostConstruct
  void init() throws IOException {
    InputStream csvStream = this.getClass().getResourceAsStream("/DienststellenV3.csv");
    List<ServicePointCsvModel> servicePointCsvModels = parseServicePoints(csvStream, 50000);
    importSPCsvModel(servicePointCsvModels);
  }*/

  public static List<ServicePointCsvModel> parseServicePoints(InputStream inputStream, Integer numberOfRowsToParse)
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
    while (mappingIterator.hasNext() && counter < numberOfRowsToParse) {
      servicePoints.add(mappingIterator.next());
      counter++;
    }
    log.info("Parsed {} servicePoints", servicePoints.size());
    return servicePoints;
  }

  private ServicePointVersion mapServicePointVersionFromCsvModel(ServicePointCsvModel csvModel) {
    ServicePointGeolocation servicePointGeolocation = null;
    if (!csvModel.getIS_VIRTUELL()) {
      servicePointGeolocation = ServicePointGeolocation.builder()
          .locationTypes(LocationTypes
              .builder()
              .spatialReference(csvModel.getSpatialReference())
              .lv03east(csvModel.getE_LV03())
              .lv03north(csvModel.getN_LV03())
              .lv95east(csvModel.getE_LV95())
              .lv95north(csvModel.getN_LV95())
              .wgs84east(csvModel.getE_WGS84())
              .wgs84north(csvModel.getN_WGS84())
              .wgs84webEast(csvModel.getE_WGS84WEB())
              .wgs84webNorth(csvModel.getN_WGS84WEB())
              .height(csvModel.getHEIGHT())
              .build())
          .country(Country.from(csvModel.getLAENDERCODE()))
          .swissCantonFsoNumber(csvModel.getBFS_NUMMER())
          .swissCantonName(csvModel.getKANTONSNAME())
          .swissCantonNumber(csvModel.getKANTONSNUM())
          .swissDistrictName(csvModel.getBEZIRKSNAME())
          .swissDistrictNumber(csvModel.getBEZIRKSNUM())
          .swissMunicipalityName(csvModel.getGEMEINDENAME())
          .swissLocalityName(csvModel.getORTSCHAFTSNAME())
          .creationDate(csvModel.getERSTELLT_AM())
          .creator(csvModel.getERSTELLT_VON())
          .editionDate(csvModel.getGEAENDERT_AM())
          .editor(csvModel.getGEAENDERT_VON())
          .build();
      if (!servicePointGeolocation.isValid()) {
        servicePointGeolocation = null;
      }
    }

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
        .businessOrganisation(
            csvModel.getGO_NUMMER().toString()) // TODO: map to sboid with IDENTIFIKATION = said, is it always ch:sboid:1?
        .hasGeolocation(!csvModel.getIS_VIRTUELL())
        .status(Status.VALIDATED)
        .validFrom(LocalDate.parse(csvModel.getGUELTIG_VON()))
        .validTo(LocalDate.parse(csvModel.getGUELTIG_BIS()))
        .categories(
            Arrays.stream(Objects.nonNull(csvModel.getDS_KATEGORIEN_IDS()) ? csvModel.getDS_KATEGORIEN_IDS().split("\\|") :
                    new String[]{})
                .map(categoryIdStr -> Category.from(Integer.parseInt(categoryIdStr)))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet())
        )
        .meansOfTransport(
            Arrays.stream(Objects.nonNull(csvModel.getBPVH_VERKEHRSMITTEL()) ? csvModel.getBPVH_VERKEHRSMITTEL().split("~") :
                    new String[]{})
                .map(MeanOfTransport::from)
                .collect(Collectors.toSet())
        )
        .stopPlaceType(StopPlaceType.from(csvModel.getHTYP_ID()))
        .operatingPointType(OperatingPointType.from(csvModel.getBPVB_BETRIEBSPUNKT_ART_ID()))
        .servicePointGeolocation(servicePointGeolocation)
        .creationDate(csvModel.getERSTELLT_AM())
        .creator(csvModel.getERSTELLT_VON())
        .editionDate(csvModel.getGEAENDERT_AM())
        .editor(csvModel.getGEAENDERT_VON())
        .build();

    if (Objects.nonNull(servicePointGeolocation)) {
      servicePointGeolocation.setServicePointVersion(servicePoint);
    }

    return servicePoint;
  }

  public void importSPCsvModel(List<ServicePointCsvModel> csvModels) {
    for (ServicePointCsvModel csvModel : csvModels) {
      ServicePointVersion servicePointVersion = mapServicePointVersionFromCsvModel(csvModel);
      servicePointVersionRepository.save(servicePointVersion);
    }
  }

}
