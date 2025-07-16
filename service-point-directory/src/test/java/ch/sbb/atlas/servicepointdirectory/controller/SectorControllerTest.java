package ch.sbb.atlas.servicepointdirectory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.location.SloidType;
import ch.sbb.atlas.api.servicepoint.sector.SectorVersionModel;
import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.model.LocalDateTimeMatchers;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepointdirectory.SectorTestData;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion.Fields;
import ch.sbb.atlas.servicepointdirectory.entity.sector.SectorVersion;
import ch.sbb.atlas.servicepointdirectory.repository.SectorVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class SectorControllerTest extends BaseControllerApiTest {

  @MockitoBean
  private TrafficPointElementService trafficPointElementService;

  @MockitoBean
  private ServicePointService servicePointService;

  @MockitoBean
  private LocationService locationService;

  private final SectorVersionRepository sectorVersionRepository;

  private SectorVersion sectorVersion;

  @Autowired
  public SectorControllerTest(SectorVersionRepository sectorVersionRepository) {
    this.sectorVersionRepository = sectorVersionRepository;
  }

  @BeforeEach
  void createDefaultVersion() {
    sectorVersion = SectorTestData.getNewBasicSectorVersion();
    this.sectorVersion = sectorVersionRepository.save(sectorVersion);
  }

  @AfterEach
  void cleanUpDb() {
    sectorVersionRepository.deleteAll();
  }

  @Test
  void shouldGetSectors() throws Exception {
    mvc.perform(get("/v1/sectors")).andExpect(status().isOk())
        .andExpect(jsonPath("$[0]." + Fields.id, is(sectorVersion.getId().intValue())))
        .andExpect(jsonPath("$[0]." + Fields.sloid, is("ch:1:sloid:sector:1111")))
        .andExpect(jsonPath("$[0]." + Fields.designation, is("test")))
        .andExpect(jsonPath("$[0].creationDate", LocalDateTimeMatchers.stringDateTimeIsWithinOneHourOfNow()))
        .andExpect(jsonPath("$[0].creator", is("e123456")));
  }

  @Test
  void shouldGetSectorsBySloid() throws Exception {
    mvc.perform(get("/v1/sectors/" + sectorVersion.getSloid())).andExpect(status().isOk())
        .andExpect(jsonPath("$[0]." + Fields.id, is(sectorVersion.getId().intValue())))
        .andExpect(jsonPath("$[0]." + Fields.sloid, is("ch:1:sloid:sector:1111")))
        .andExpect(jsonPath("$[0]." + Fields.designation, is("test")))
        .andExpect(jsonPath("$[0].creationDate", LocalDateTimeMatchers.stringDateTimeIsWithinOneHourOfNow()))
        .andExpect(jsonPath("$[0].creator", is("e123456")));
  }

  @Test
  void shouldGetSectorVersionById() throws Exception {
    mvc.perform(get("/v1/sectors/versions/{id}", sectorVersion.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(sectorVersion.getId().intValue())))
        .andExpect(jsonPath("$.sloid", is(sectorVersion.getSloid())))
        .andExpect(jsonPath("$.designation", is(sectorVersion.getDesignation())));
  }

  @Test
  void shouldCreateSector() throws Exception {
    sectorVersionRepository.deleteAll();

    SectorVersionModel toCreate = SectorTestData.getCreateSectorVersion();

    doReturn("ch:1:sloid:sector:1:0:1").when(locationService).generateSloid(SloidType.SECTOR,
        toCreate.getTrafficPointSloid());

    when(trafficPointElementService.findBySloidOrderByValidFrom(toCreate.getTrafficPointSloid()))
        .thenReturn(List.of(TrafficPointTestData.getBasicTrafficPoint()));

    when(servicePointService.findAllByNumberOrderByValidFrom(any()))
        .thenReturn(List.of(ServicePointTestData.getBern()));

    mvc.perform(post("/v1/sectors")
            .contentType(contentType)
            .content(mapper.writeValueAsString(toCreate)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.sloid", is("ch:1:sloid:sector:1:0:1")))
        .andExpect(jsonPath("$.designation", is(toCreate.getDesignation())));
  }

  @Test
  void shouldUpdateSectorAndReturnTwoVersions() throws Exception {
    sectorVersionRepository.deleteAll();

    SectorVersion initial = sectorVersionRepository.save(
        SectorTestData.getBasicSectorVersion()
    );
    Long id = initial.getId();

    SectorVersionModel update = SectorVersionModel.builder()
        .etagVersion(initial.getVersion())
        .sloid(initial.getSloid())
        .trafficPointSloid(initial.getTrafficPointSloid())
        .validFrom(LocalDate.of(2023, 1, 1))
        .validTo(LocalDate.of(2033, 1, 2))
        .designation("jaja")
        .length(initial.getLength())
        .north(initial.getNorth())
        .east(initial.getEast())
        .spatialReference(initial.getSpatialReference())
        .height(initial.getHeight())
        .edgeHeight(initial.getEdgeHeight())
        .build();

    when(trafficPointElementService.findBySloidOrderByValidFrom(initial.getTrafficPointSloid()))
        .thenReturn(List.of(TrafficPointTestData.getBasicTrafficPoint()));

    when(servicePointService.findAllByNumberOrderByValidFrom(any()))
        .thenReturn(List.of(ServicePointTestData.getBern()));

    mvc.perform(put("/v1/sectors/{id}", id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(update)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(initial.getId().intValue())))
        .andExpect(jsonPath("$[0].designation", is(initial.getDesignation())))
        .andExpect(jsonPath("$[1].id", is(initial.getId().intValue() + 1)))
        .andExpect(jsonPath("$[1].designation", is("jaja")));
  }

}
