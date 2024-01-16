package ch.sbb.prm.directory.controller;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PARKING_LOT;
import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PLATFORM;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.relation.RelationVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.prm.directory.RelationTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.repository.RelationRepository;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RelationVersionControllerApiTest extends BaseControllerApiTest {

  private final RelationRepository relationRepository;
  private final StopPointRepository stopPointRepository;
  private final SharedServicePointRepository sharedServicePointRepository;

  @Autowired
  RelationVersionControllerApiTest(RelationRepository relationRepository,
                                   StopPointRepository stopPointRepository,
                                   SharedServicePointRepository sharedServicePointRepository) {
    this.relationRepository = relationRepository;
    this.stopPointRepository = stopPointRepository;
    this.sharedServicePointRepository = sharedServicePointRepository;
  }

  @AfterEach
  void cleanUp() {
    sharedServicePointRepository.deleteAll();
  }

  @Test
  void shouldGetRelationBySloid() throws Exception {
    //given
    String relation1Sloid = "ch:1:sloid:8507000:1";
    String relation2Sloid = "ch:1:sloid:8507000:2";
    String parentServicePointSloid = "ch:1:sloid:8507000";
    RelationVersion relation1 = RelationTestData.getRelation(parentServicePointSloid, relation1Sloid, PLATFORM);
    RelationVersion relation2 = RelationTestData.getRelation(parentServicePointSloid, relation2Sloid, PARKING_LOT);
    relationRepository.saveAndFlush(relation1);
    relationRepository.saveAndFlush(relation2);

    //when & then
    mvc.perform(get("/v1/relations/" +relation1Sloid))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].sloid", is(relation1Sloid)))
        .andExpect(jsonPath("$[0].referencePointElementType", is(PLATFORM.name())));
  }

  @Test
  void shouldGetRelationsBySloidAndReferenceType() throws Exception {
    //given
    String relation1Sloid = "ch:1:sloid:8507000:1";
    String relation2Sloid = "ch:1:sloid:8507000:2";
    String parentServicePointSloid = "ch:1:sloid:8507000";
    RelationVersion relation1 = RelationTestData.getRelation(parentServicePointSloid, relation1Sloid, PLATFORM);
    RelationVersion relation2 = RelationTestData.getRelation(parentServicePointSloid, relation2Sloid, PARKING_LOT);
    relationRepository.saveAndFlush(relation1);
    relationRepository.saveAndFlush(relation2);

    //when & then
    mvc.perform(get("/v1/relations/" +relation1Sloid + "/" + PLATFORM.name()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].sloid", is(relation1Sloid)))
        .andExpect(jsonPath("$[0].referencePointElementType", is(PLATFORM.name())));
  }

  @Test
  void shouldGetRelationsByParentServicePointSloidAndReferenceType() throws Exception {
    //given
    String relation1Sloid = "ch:1:sloid:8507000:1";
    String relation2Sloid = "ch:1:sloid:8507000:2";
    String parentServicePointSloid = "ch:1:sloid:8507000";
    RelationVersion relation1 = RelationTestData.getRelation(parentServicePointSloid, relation1Sloid, PLATFORM);
    RelationVersion relation2 = RelationTestData.getRelation(parentServicePointSloid, relation2Sloid, PARKING_LOT);
    RelationVersion relation3 = RelationTestData.getRelation(parentServicePointSloid, relation1Sloid, PLATFORM);
    relation3.setValidFrom(LocalDate.of(2001, 1, 1));
    relation3.setValidTo(LocalDate.of(2001, 12, 31));
    relation3.setContrastingAreas(StandardAttributeType.TO_BE_COMPLETED);
    relationRepository.saveAndFlush(relation1);
    relationRepository.saveAndFlush(relation2);
    relationRepository.saveAndFlush(relation3);

    //when & then
    mvc.perform(get("/v1/relations/parent-service-point-sloid/" + parentServicePointSloid + "/" + PLATFORM.name()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].sloid", is(relation1Sloid)))
        .andExpect(jsonPath("$[0].parentServicePointSloid", is(parentServicePointSloid)))
        .andExpect(jsonPath("$[0].referencePointElementType", is(PLATFORM.name())))
        .andExpect(jsonPath("$[1].sloid", is(relation1Sloid)))
        .andExpect(jsonPath("$[1].parentServicePointSloid", is(parentServicePointSloid)))
        .andExpect(jsonPath("$[1].referencePointElementType", is(PLATFORM.name())));
  }

  @Test
  void shouldGetRelationsByParentServicePointSloid() throws Exception {
    //given
    String relation1Sloid = "ch:1:sloid:8507000:1";
    String relation2Sloid = "ch:1:sloid:8507000:2";
    String parentServicePointSloid = "ch:1:sloid:8507000";
    RelationVersion relation1 = RelationTestData.getRelation(parentServicePointSloid, relation1Sloid, PLATFORM);
    RelationVersion relation2 = RelationTestData.getRelation(parentServicePointSloid, relation2Sloid, PARKING_LOT);
    RelationVersion relation3 = RelationTestData.getRelation(parentServicePointSloid, relation1Sloid, PLATFORM);
    relation3.setValidFrom(LocalDate.of(2001, 1, 1));
    relation3.setValidTo(LocalDate.of(2001, 12, 31));
    relation3.setContrastingAreas(StandardAttributeType.TO_BE_COMPLETED);
    relationRepository.saveAndFlush(relation1);
    relationRepository.saveAndFlush(relation2);
    relationRepository.saveAndFlush(relation3);

    //when & then
    mvc.perform(get("/v1/relations/parent-service-point-sloid/" + parentServicePointSloid))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)));
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
  void shouldUpdateRelation() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:8507000";
    stopPointRepository.save(StopPointTestData.builderVersion1().sloid(parentServicePointSloid).build());
    String referencePointSloid = "ch:1:sloid:8507000:1";
    RelationVersion version1 = RelationTestData.builderVersion1().build();
    version1.setParentServicePointSloid(parentServicePointSloid);
    version1.setReferencePointSloid(referencePointSloid);
    relationRepository.saveAndFlush(version1);
    RelationVersion version2 = RelationTestData.builderVersion2().build();
    version2.setParentServicePointSloid(parentServicePointSloid);
    version2.setReferencePointSloid(referencePointSloid);
    relationRepository.saveAndFlush(version2);

    RelationVersionModel editedVersionModel = new RelationVersionModel();
    editedVersionModel.setParentServicePointSloid(parentServicePointSloid);
    editedVersionModel.setSloid(version2.getSloid());
    editedVersionModel.setReferencePointSloid(version2.getReferencePointSloid());
    editedVersionModel.setValidFrom(version2.getValidFrom());
    editedVersionModel.setValidTo(version2.getValidTo().minusYears(1));
    editedVersionModel.setContrastingAreas(version2.getContrastingAreas());
    editedVersionModel.setTactileVisualMarks(version2.getTactileVisualMarks());
    editedVersionModel.setStepFreeAccess(version2.getStepFreeAccess());
    editedVersionModel.setCreationDate(version2.getCreationDate());
    editedVersionModel.setEditionDate(version2.getEditionDate());
    editedVersionModel.setCreator(version2.getCreator());
    editedVersionModel.setEditor(version2.getEditor());
    editedVersionModel.setEtagVersion(version2.getVersion());

    SharedServicePoint servicePoint = SharedServicePoint.builder()
            .servicePoint("{\"servicePointSloid\":\"ch:1:sloid:8507000\",\"sboids\":[\"ch:1:sboid:100602\"],"
                    + "\"trafficPointSloids\":[]}")
            .sloid("ch:1:sloid:8507000")
            .build();
    sharedServicePointRepository.saveAndFlush(servicePoint);

    //when & then
    mvc.perform(put("/v1/relations/" + version2.getId()).contentType(contentType)
            .content(mapper.writeValueAsString(editedVersionModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2000-01-01")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2000-12-31")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2001-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2001-12-31")));
  }

}
