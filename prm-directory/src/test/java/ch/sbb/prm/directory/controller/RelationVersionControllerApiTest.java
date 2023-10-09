package ch.sbb.prm.directory.controller;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.PARKING_LOT;
import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.PLATFORM;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.prm.directory.RelationTestData;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.enumeration.StandardAttributeType;
import ch.sbb.prm.directory.repository.RelationRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class RelationVersionControllerApiTest extends BaseControllerApiTest {

  private final RelationRepository relationRepository;

  @Autowired
  RelationVersionControllerApiTest(RelationRepository relationRepository){
    this.relationRepository = relationRepository;
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

}
