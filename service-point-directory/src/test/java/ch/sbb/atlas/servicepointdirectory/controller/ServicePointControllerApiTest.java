package ch.sbb.atlas.servicepointdirectory.controller;

import static ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference.LV95;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.imports.servicepoint.BaseDidokCsvModel;
import ch.sbb.atlas.imports.servicepoint.model.ServicePointImportReqModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.api.model.CreateServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.api.model.ReadServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.api.model.ServicePointVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointGeolocationMapper;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointImportService;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class ServicePointControllerApiTest extends BaseControllerApiTest {

  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  private final ServicePointVersionRepository repository;

  private final ServicePointController servicePointController;

  private ServicePointVersion servicePointVersion;

  @Autowired
  public ServicePointControllerApiTest(ServicePointVersionRepository repository, ServicePointController servicePointController) {
    this.repository = repository;
    this.servicePointController = servicePointController;
  }

  @BeforeEach
  void createDefaultVersion() {
    servicePointVersion = repository.save(ServicePointTestData.getBernWyleregg());
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
  }

  @Test
  void shouldGetServicePoint() throws Exception {
    mvc.perform(get("/v1/service-points/85890087")).andExpect(status().isOk())
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.id, is(servicePointVersion.getId().intValue())))
        .andExpect(jsonPath("$[0].number.number", is(8589008)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.designationOfficial, is("Bern, Wyleregg")))
        .andExpect(jsonPath("$[0].meansOfTransportInformation[0].code", is("B")))
        .andExpect(jsonPath("$[0].meansOfTransportInformation[0].designationDe", is("Bus")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.operatingPointRouteNetwork, is(false)))

        // IS_BETRIEBSPUNKT
        .andExpect(jsonPath("$[0].operatingPoint", is(true)))
        // IS_FAHRPLAN
        .andExpect(jsonPath("$[0].operatingPointWithTimetable", is(true)))
        // IS_HALTESTELLE
        .andExpect(jsonPath("$[0].stopPoint", is(true)))
        // IS_BEDIENPUNKT
        .andExpect(jsonPath("$[0].freightServicePoint", is(false)))
        // IS_VERKEHRSPUNKT
        .andExpect(jsonPath("$[0].trafficPoint", is(true)))
        // IS_GRENZPUNKT
        .andExpect(jsonPath("$[0].borderPoint", is(false)))
        // IS_VIRTUELL
        .andExpect(jsonPath("$[0].hasGeolocation", is(true)))

        .andExpect(jsonPath("$[0].operatingPointKilometer", is(false)))

        .andExpect(jsonPath("$[0].servicePointGeolocation.swissLocation.cantonInformation.fsoNumber", is(2)))
        .andExpect(jsonPath("$[0].creationDate", is("2021-03-22T09:26:29")))
        .andExpect(jsonPath("$[0].creator", is("fs45117")));
  }

  @Test
  void shouldGetServicePointVersions() throws Exception {
    mvc.perform(get("/v1/service-points")).andExpect(status().isOk())
        .andExpect(jsonPath("$.objects[0]." + ServicePointVersionModel.Fields.id, is(servicePointVersion.getId().intValue())))
        .andExpect(jsonPath("$.totalCount", is(1)));
  }

  @Test
  void shouldGetServicePointVersionById() throws Exception {
    mvc.perform(get("/v1/service-points/versions/" + servicePointVersion.getId())).andExpect(status().isOk());
  }

  @Test
  void shouldFindServicePointVersionByModifiedAfter() throws Exception {
    String modifiedAfterQueryString = servicePointVersion.getEditionDate().plusDays(1)
        .format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN));
    mvc.perform(get("/v1/service-points?modifiedAfter=" + modifiedAfterQueryString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(0)));

    modifiedAfterQueryString = servicePointVersion.getEditionDate().minusDays(1)
        .format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN));
    mvc.perform(get("/v1/service-points?modifiedAfter=" + modifiedAfterQueryString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)));
  }

  @Test
  void shouldFindServicePointVersionByFromAndToDate() throws Exception {
    String fromDate = servicePointVersion.getValidFrom().minusDays(1)
        .format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
    String toDate = servicePointVersion.getValidTo().plusDays(1)
        .format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
    mvc.perform(get("/v1/service-points?fromDate=" + fromDate + "&toDate=" + toDate))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)));
  }

  @Test
  void shouldFailOnInvalidServicePointNumber() throws Exception {
    mvc.perform(get("/v1/service-points/123"))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldImportServicePointsSuccessfully() throws Exception {
    try (InputStream csvStream = this.getClass().getResourceAsStream("/SERVICE_POINTS_VERSIONING.csv")) {
      // given
      List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(csvStream);
      List<ServicePointCsvModel> servicePointCsvModelsOrderedByValidFrom = servicePointCsvModels.stream()
          .sorted(Comparator.comparing(BaseDidokCsvModel::getValidFrom))
          .toList();
      int didokCode = servicePointCsvModels.get(0).getDidokCode();
      ServicePointImportReqModel importRequestModel = new ServicePointImportReqModel(
          List.of(
              ServicePointCsvModelContainer
                  .builder()
                  .servicePointCsvModelList(servicePointCsvModelsOrderedByValidFrom)
                  .didokCode(didokCode)
                  .build()
          )
      );
      String jsonString = mapper.writeValueAsString(importRequestModel);

      // when
      mvc.perform(post("/v1/service-points/import")
              .content(jsonString)
              .contentType(contentType))
          // then
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(5)));
    }
  }

  @Test
  void shouldReturnBadRequestOnEmptyListRequest() throws Exception {
    // given
    ServicePointImportReqModel importRequestModel = new ServicePointImportReqModel(
        Collections.emptyList()
    );
    String jsonString = mapper.writeValueAsString(importRequestModel);

    // when
    mvc.perform(post("/v1/service-points/import")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")));
  }

  @Test
  void shouldReturnBadRequestOnNullListRequest() throws Exception {
    // given
    ServicePointImportReqModel importRequestModel = new ServicePointImportReqModel();
    String jsonString = mapper.writeValueAsString(importRequestModel);

    // when
    mvc.perform(post("/v1/service-points/import")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")));
  }

  @Test
  void shouldReturnBadRequestOnNullImportRequestModel() throws Exception {
    // given & when
    mvc.perform(post("/v1/service-points/import")
            .contentType(contentType))
        // then
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldCreateServicePoint() throws Exception {

    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(ServicePointTestData.getAargauServicePointVersionModel())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.id, is(servicePointVersion.getId().intValue()+1)))
        .andExpect(jsonPath("$.number.number", is(8034510)))
        .andExpect(jsonPath("$.number.numberShort", is(34510)))
        .andExpect(jsonPath("$.number.checkDigit", is(8)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.designationOfficial, is("Aargau Strasse")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.sloid, is("ch:1:sloid:18771")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.designationLong, is("designation long 1")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.abbreviation, is("3")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.statusDidok3, is("IN_OPERATION")))
        .andExpect(jsonPath("$.statusDidok3Information.code", is("3")))
        .andExpect(jsonPath("$.statusDidok3Information.designationDe", is("In Betrieb")))
        .andExpect(jsonPath("$.statusDidok3Information.designationFr", is("En fonctionnement")))
        .andExpect(jsonPath("$.statusDidok3Information.designationIt", is("In funzione")))
        .andExpect(jsonPath("$.statusDidok3Information.designationEn", is("In operation")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.operatingPoint, is(true)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.operatingPointWithTimetable, is(false)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.freightServicePoint, is(true)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.sortCodeOfDestinationStation, is("39136")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.businessOrganisation, is("ch:1:sboid:100871")))
        .andExpect(jsonPath("$.categories[0]", is("POINT_OF_SALE")))
        .andExpect(jsonPath("$.categoriesInformation[0].code", is("6")))
        .andExpect(jsonPath("$.categoriesInformation[0].designationDe", is("Verkaufsstelle")))
        .andExpect(jsonPath("$.categoriesInformation[0].designationFr", is("Point de vente")))
        .andExpect(jsonPath("$.categoriesInformation[0].designationIt", is("Punto vendita")))
        .andExpect(jsonPath("$.categoriesInformation[0].designationEn", is("Point of sale")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.operatingPointType, is("INVENTORY_POINT")))
        .andExpect(jsonPath("$.operatingPointTypeInformation.code", is("30")))
        .andExpect(jsonPath("$.operatingPointTypeInformation.designationDe", is("Inventarpunkt")))
        .andExpect(jsonPath("$.operatingPointTypeInformation.designationFr", is("Point d'inventaire")))
        .andExpect(jsonPath("$.operatingPointTypeInformation.designationIt", is("punto di inventario")))
        .andExpect(jsonPath("$.operatingPointTypeInformation.designationEn", is("Inventory point")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.operatingPointTechnicalTimetableType, is("ASSIGNED_OPERATING_POINT")))
        .andExpect(jsonPath("$.operatingPointTechnicalTimetableTypeInformation.code", is("16")))
        .andExpect(jsonPath("$.operatingPointTechnicalTimetableTypeInformation.designationDe", is("Zugeordneter Betriebspunkt")))
        .andExpect(jsonPath("$.operatingPointTechnicalTimetableTypeInformation.designationFr", is("Point d’exploitation associé")))
        .andExpect(jsonPath("$.operatingPointTechnicalTimetableTypeInformation.designationIt", is("Punto d’esercizio associato")))
        .andExpect(jsonPath("$.operatingPointTechnicalTimetableTypeInformation.designationEn", is("Assigned operating point")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.operatingPointRouteNetwork, is(false)))
        .andExpect(jsonPath("$.operatingPointKilometerMaster.number", is(8034511)))
        .andExpect(jsonPath("$.operatingPointKilometerMaster.numberShort", is(34511)))
        .andExpect(jsonPath("$.operatingPointKilometerMaster.checkDigit", is(6)))
        .andExpect(jsonPath("$.meansOfTransport[0]", is("TRAIN")))
        .andExpect(jsonPath("$.meansOfTransportInformation[0].code", is("Z")))
        .andExpect(jsonPath("$.meansOfTransportInformation[0].designationDe", is("Zug")))
        .andExpect(jsonPath("$.meansOfTransportInformation[0].designationFr", is("Train")))
        .andExpect(jsonPath("$.meansOfTransportInformation[0].designationIt", is("Treno")))
        .andExpect(jsonPath("$.meansOfTransportInformation[0].designationEn", is("Train")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.stopPointType, is("ON_REQUEST")))
        .andExpect(jsonPath("$.stopPointTypeInformation.code", is("20")))
        .andExpect(jsonPath("$.stopPointTypeInformation.designationDe", is("Bedarfshaltestelle")))
        .andExpect(jsonPath("$.stopPointTypeInformation.designationFr", is("Arrêt sur demande")))
        .andExpect(jsonPath("$.stopPointTypeInformation.designationIt", is("Fermata facoltativa")))
        .andExpect(jsonPath("$.stopPointTypeInformation.designationEn", is("Request stop")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.fotComment, is("Bahnersatz")))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.cantonInformation.fsoNumber", is(2)))
        .andExpect(jsonPath("$.servicePointGeolocation.spatialReference", is(LV95.toString())))
        .andExpect(jsonPath("$.servicePointGeolocation.lv95.north", is(5935705.395163289)))
        .andExpect(jsonPath("$.servicePointGeolocation.lv95.east", is(829209.9504364047)))
        .andExpect(jsonPath("$.servicePointGeolocation.wgs84.north", is(77.40956063569155)))
        .andExpect(jsonPath("$.servicePointGeolocation.wgs84.east", is(-69.39756596147541)))
        .andExpect(jsonPath("$.servicePointGeolocation.wgs84web.east", is(-7725301.705124057)))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.canton", is("BERN")))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.cantonInformation.fsoNumber", is(2)))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.cantonInformation.name", is("Bern")))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.cantonInformation.abbreviation", is("BE")))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.district.fsoNumber", is(246)))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.district.districtName", is("Bern-Mittelland")))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.localityMunicipality.municipalityName", is("Bern")))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.localityMunicipality.localityName", is("Bern")))

        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.status, is("VALIDATED")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$.operatingPointKilometer", is(true)))
        .andExpect(jsonPath("$.validFreightServicePoint", is(true)))
        .andExpect(jsonPath("$.stopPoint", is(true)))
        .andExpect(jsonPath("$.fareStop", is(false)))
        .andExpect(jsonPath("$.borderPoint", is(false)))
        .andExpect(jsonPath("$.validType", is(true)))
        .andExpect(jsonPath("$.trafficPoint", is(true)))
        .andExpect(jsonPath("$.hasGeolocation", is(true)))
        .andExpect(jsonPath("$.creator", is("e123456")));
  }

  @Test
  public void shouldUpdateServicePointAndCreateMultipleVersions() throws Exception {
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(ServicePointTestData.getAargauServicePointVersionModel());
    Long id = servicePointVersionModel.getId();

    CreateServicePointVersionModel newServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    newServicePointVersionModel.setServicePointGeolocation(ServicePointGeolocationMapper.toModel(ServicePointTestData.getAargauServicePointGeolocation()));
    newServicePointVersionModel.setValidFrom(LocalDate.of(2011, 12, 11));
    newServicePointVersionModel.setValidTo(LocalDate.of(2012, 12, 11));

    mvc.perform(MockMvcRequestBuilders.put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(newServicePointVersionModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2011-12-10")))
        .andExpect(jsonPath("$[0].servicePointGeolocation.swissLocation.cantonInformation.fsoNumber", is(2)))
        .andExpect(jsonPath("$[0].servicePointGeolocation.spatialReference", is(LV95.toString())))
        .andExpect(jsonPath("$[0].servicePointGeolocation.lv95.north", is(5935705.395163289)))
        .andExpect(jsonPath("$[0].servicePointGeolocation.lv95.east", is(829209.9504364047)))
        .andExpect(jsonPath("$[0].servicePointGeolocation.wgs84.north", is(77.40956063569155)))
        .andExpect(jsonPath("$[0].servicePointGeolocation.wgs84.east", is(-69.39756596147541)))
        .andExpect(jsonPath("$[0].servicePointGeolocation.wgs84web.east", is(-7725301.705124057)))
        .andExpect(jsonPath("$[0].servicePointGeolocation.swissLocation.canton", is("BERN")))
        .andExpect(jsonPath("$[0].servicePointGeolocation.swissLocation.cantonInformation.fsoNumber", is(2)))
        .andExpect(jsonPath("$[0].servicePointGeolocation.swissLocation.cantonInformation.name", is("Bern")))
        .andExpect(jsonPath("$[0].servicePointGeolocation.swissLocation.cantonInformation.abbreviation", is("BE")))
        .andExpect(jsonPath("$[0].servicePointGeolocation.swissLocation.district.fsoNumber", is(246)))
        .andExpect(jsonPath("$[0].servicePointGeolocation.swissLocation.district.districtName", is("Bern-Mittelland")))
        .andExpect(jsonPath("$[0].servicePointGeolocation.swissLocation.localityMunicipality.municipalityName", is("Bern")))
        .andExpect(jsonPath("$[0].servicePointGeolocation.swissLocation.localityMunicipality.localityName", is("Bern")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2011-12-11")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2012-12-11")))
        .andExpect(jsonPath("$[1].servicePointGeolocation.swissLocation.cantonInformation.fsoNumber", is(19)))
        .andExpect(jsonPath("$[1].servicePointGeolocation.spatialReference", is(LV95.toString())))
        .andExpect(jsonPath("$[1].servicePointGeolocation.lv95.north", is(6362085.000118345)))
        .andExpect(jsonPath("$[1].servicePointGeolocation.lv95.east", is(938601.4185735969)))
        .andExpect(jsonPath("$[1].servicePointGeolocation.wgs84.north", is(78.98697271069213)))
        .andExpect(jsonPath("$[1].servicePointGeolocation.wgs84.east", is(-81.90234165643068)))
        .andExpect(jsonPath("$[1].servicePointGeolocation.wgs84web.east", is(-9117326.967970582)))
        .andExpect(jsonPath("$[1].servicePointGeolocation.swissLocation.canton", is("AARGAU")))
        .andExpect(jsonPath("$[1].servicePointGeolocation.swissLocation.cantonInformation.fsoNumber", is(19)))
        .andExpect(jsonPath("$[1].servicePointGeolocation.swissLocation.cantonInformation.name", is("Aargau")))
        .andExpect(jsonPath("$[1].servicePointGeolocation.swissLocation.cantonInformation.abbreviation", is("AG")))
        .andExpect(jsonPath("$[1].servicePointGeolocation.swissLocation.district.fsoNumber", is(1909)))
        .andExpect(jsonPath("$[1].servicePointGeolocation.swissLocation.district.districtName", is("Rheinfelden")))
        .andExpect(jsonPath("$[1].servicePointGeolocation.swissLocation.localityMunicipality.municipalityName", is("Hellikon")))
        .andExpect(jsonPath("$[1].servicePointGeolocation.swissLocation.localityMunicipality.localityName", is("Hellikon")))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validFrom, is("2012-12-12")))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$[2].servicePointGeolocation.swissLocation.cantonInformation.fsoNumber", is(2)))
        .andExpect(jsonPath("$[2].servicePointGeolocation.spatialReference", is(LV95.toString())))
        .andExpect(jsonPath("$[2].servicePointGeolocation.lv95.north", is(5935705.395163289)))
        .andExpect(jsonPath("$[2].servicePointGeolocation.lv95.east", is(829209.9504364047)))
        .andExpect(jsonPath("$[2].servicePointGeolocation.wgs84.north", is(77.40956063569155)))
        .andExpect(jsonPath("$[2].servicePointGeolocation.wgs84.east", is(-69.39756596147541)))
        .andExpect(jsonPath("$[2].servicePointGeolocation.wgs84web.east", is(-7725301.705124057)))
        .andExpect(jsonPath("$[2].servicePointGeolocation.swissLocation.canton", is("BERN")))
        .andExpect(jsonPath("$[2].servicePointGeolocation.swissLocation.cantonInformation.fsoNumber", is(2)))
        .andExpect(jsonPath("$[2].servicePointGeolocation.swissLocation.cantonInformation.name", is("Bern")))
        .andExpect(jsonPath("$[2].servicePointGeolocation.swissLocation.cantonInformation.abbreviation", is("BE")))
        .andExpect(jsonPath("$[2].servicePointGeolocation.swissLocation.district.fsoNumber", is(246)))
        .andExpect(jsonPath("$[2].servicePointGeolocation.swissLocation.district.districtName", is("Bern-Mittelland")))
        .andExpect(jsonPath("$[2].servicePointGeolocation.swissLocation.localityMunicipality.municipalityName", is("Bern")))
        .andExpect(jsonPath("$[2].servicePointGeolocation.swissLocation.localityMunicipality.localityName", is("Bern")));
  }

  @Test
  public void shouldUpdateServicePointAndNotCreateMultipleVersions() throws Exception {
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(ServicePointTestData.getAargauServicePointVersionModel());
    Long id = servicePointVersionModel.getId();

    CreateServicePointVersionModel newServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    newServicePointVersionModel.setServicePointGeolocation(ServicePointGeolocationMapper.toModel(ServicePointTestData.getAargauServicePointGeolocation()));

    mvc.perform(MockMvcRequestBuilders.put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(newServicePointVersionModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

}
