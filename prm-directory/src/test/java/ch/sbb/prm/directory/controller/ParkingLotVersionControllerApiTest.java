package ch.sbb.prm.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.prm.directory.ParkingLotTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPlaceTestData;
import ch.sbb.atlas.api.prm.model.parkinglot.CreateParkingLotVersionModel;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.prm.directory.repository.ParkingLotRepository;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import ch.sbb.prm.directory.service.RelationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ParkingLotVersionControllerApiTest extends BaseControllerApiTest {

  private final ParkingLotRepository parkingLotRepository;
  private final StopPlaceRepository stopPlaceRepository;
  private final ReferencePointRepository referencePointRepository;

  @MockBean
  private final RelationService relationService;

  @Autowired
  ParkingLotVersionControllerApiTest(ParkingLotRepository parkingLotRepository, StopPlaceRepository stopPlaceRepository,
      ReferencePointRepository referencePointRepository, RelationService relationService){
    this.parkingLotRepository = parkingLotRepository;
    this.stopPlaceRepository = stopPlaceRepository;
    this.referencePointRepository = referencePointRepository;
    this.relationService = relationService;
  }

  @Test
  void shouldGetParkingLotVersion() throws Exception {
    //given
    parkingLotRepository.save(ParkingLotTestData.getParkingLotVersion());
    //when & then
    mvc.perform(get("/v1/parking-lots"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void shouldCreateParkingLot() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    StopPlaceVersion stopPlaceVersion = StopPlaceTestData.getStopPlaceVersion();
    stopPlaceVersion.setSloid(parentServicePointSloid);
    stopPlaceRepository.save(stopPlaceVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);

    CreateParkingLotVersionModel model = ParkingLotTestData.getCreateParkingLotVersionModel();
    model.setParentServicePointSloid(parentServicePointSloid);

    //when && then
    mvc.perform(post("/v1/parking-lots")
            .contentType(contentType)
            .content(mapper.writeValueAsString(model)))
        .andExpect(status().isCreated());
    verify(relationService, times(1)).save(any(RelationVersion.class));

  }

  @Test
  void shouldNotCreateParkingLotWhenStopPlaceDoesNotExists() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);

    CreateParkingLotVersionModel model = ParkingLotTestData.getCreateParkingLotVersionModel();
    model.setParentServicePointSloid(parentServicePointSloid);

    //when && then
    mvc.perform(post("/v1/parking-lots")
            .contentType(contentType)
            .content(mapper.writeValueAsString(model)))
        .andExpect(status().isPreconditionFailed())
        .andExpect(jsonPath("$.message", is("The stop place with sloid ch:1:sloid:7000 does not exists.")));
    verify(relationService, times(0)).save(any(RelationVersion.class));

  }

  /**
   * Szenario 8a: Letzte Version terminieren wenn nur validTo ist updated
   * NEU:      |______________________|
   * IST:      |-------------------------------------------------------
   * Version:                            1
   *
   * RESULTAT: |----------------------| Version wird per xx aufgehoben
   * Version:         1
   */
  @Test
  void shouldUpdateParkingLot() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:70000";
    StopPlaceVersion stopPlaceVersion = StopPlaceTestData.getStopPlaceVersion();
    stopPlaceVersion.setSloid(parentServicePointSloid);
    stopPlaceRepository.save(stopPlaceVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);
    ParkingLotVersion version1 = ParkingLotTestData.builderVersion1().build();
    version1.setParentServicePointSloid(parentServicePointSloid);
    parkingLotRepository.saveAndFlush(version1);
    ParkingLotVersion version2 = ParkingLotTestData.builderVersion2().build();
    version2.setParentServicePointSloid(parentServicePointSloid);
    parkingLotRepository.saveAndFlush(version2);

    CreateParkingLotVersionModel editedVersionModel = new CreateParkingLotVersionModel();
    editedVersionModel.setParentServicePointSloid(parentServicePointSloid);
    editedVersionModel.setSloid(version2.getSloid());
    editedVersionModel.setValidFrom(version2.getValidFrom());
    editedVersionModel.setValidTo(version2.getValidTo().minusYears(1));
    editedVersionModel.setNumberWithoutCheckDigit(version2.getNumber().getNumber());
    editedVersionModel.setDesignation(version2.getDesignation());
    editedVersionModel.setInfo(version2.getInfo());
    editedVersionModel.setPlacesAvailable(version2.getPlacesAvailable());
    editedVersionModel.setPrmPlacesAvailable(version2.getPrmPlacesAvailable());
    editedVersionModel.setCreationDate(version2.getCreationDate());
    editedVersionModel.setEditionDate(version2.getEditionDate());
    editedVersionModel.setCreator(version2.getCreator());
    editedVersionModel.setEditor(version2.getEditor());
    editedVersionModel.setEtagVersion(version2.getVersion());

    //when & then
    mvc.perform(put("/v1/parking-lots/" + version2.getId()).contentType(contentType)
            .content(mapper.writeValueAsString(editedVersionModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2000-01-01")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2000-12-31")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2001-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2001-12-31")));

  }
}
