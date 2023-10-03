package ch.sbb.prm.directory.repository;

import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.PARKING_LOT;
import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.PLATFORM;
import static ch.sbb.prm.directory.enumeration.ReferencePointElementType.TOILETTE;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.RelationTestData;
import ch.sbb.prm.directory.entity.RelationVersion;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class RelationRepositoryTest {

  private final RelationRepository relationRepository;

  @Autowired
  RelationRepositoryTest(RelationRepository relationRepository) {
    this.relationRepository = relationRepository;
  }

  @AfterEach
  void tearDown() {
    relationRepository.deleteAll();
  }

  @Test
  void shouldReturnRelationsBySloid() {
    String relation1Sloid = "ch:1:sloid:8507000:1";
    String relation2Sloid = "ch:1:sloid:8507000:2";
    String parentServicePointSloid = "ch:1:sloid:8507000";
    RelationVersion relation1 = RelationTestData.getRelation(parentServicePointSloid, relation1Sloid, PLATFORM);
    RelationVersion relation2 = RelationTestData.getRelation(parentServicePointSloid, relation2Sloid, PARKING_LOT);
    RelationVersion relationVersion1 = relationRepository.saveAndFlush(relation1);
    RelationVersion relationVersion2 = relationRepository.saveAndFlush(relation2);

    //when
    List<RelationVersion> result = relationRepository.findAllBySloid(relation1Sloid);
    //then
    assertThat(result)
        .hasSize(1)
        .containsExactlyInAnyOrder(relationVersion1)
        .doesNotContain(relationVersion2);
  }

  @Test
  void shouldNotReturnRelationsBySloid() {
    String relation1Sloid = "ch:1:sloid:8507000:1";
    String relation2Sloid = "ch:1:sloid:8507000:2";
    String parentServicePointSloid = "ch:1:sloid:8507000";
    RelationVersion relation1 = RelationTestData.getRelation(parentServicePointSloid, relation1Sloid, PLATFORM);
    RelationVersion relation2 = RelationTestData.getRelation(parentServicePointSloid, relation2Sloid, PARKING_LOT);
    relationRepository.saveAndFlush(relation1);
    relationRepository.saveAndFlush(relation2);

    //when
    List<RelationVersion> result = relationRepository.findAllBySloid("ch:1:sloid:100000:1");
    //then
    assertThat(result).hasSize(0);
  }

  @Test
  void shouldReturnRelationsBySloidAndReferencePointElementType() {
    String relation1Sloid = "ch:1:sloid:8507000:1";
    String relation2Sloid = "ch:1:sloid:8507000:2";
    String parentServicePointSloid = "ch:1:sloid:8507000";
    RelationVersion relation1 = RelationTestData.getRelation(parentServicePointSloid, relation1Sloid, PLATFORM);
    RelationVersion relation2 = RelationTestData.getRelation(parentServicePointSloid, relation2Sloid, PARKING_LOT);
    RelationVersion relationVersion1 = relationRepository.saveAndFlush(relation1);
    RelationVersion relationVersion2 = relationRepository.saveAndFlush(relation2);

    //when
    List<RelationVersion> result = relationRepository.findAllBySloidAndReferencePointElementType(relation1Sloid, PLATFORM);
    //then
    assertThat(result)
        .hasSize(1)
        .containsExactlyInAnyOrder(relationVersion1)
        .doesNotContain(relationVersion2);
  }

  @Test
  void shouldNotReturnRelationsBySloidAndReferencePointElementType() {
    String relation1Sloid = "ch:1:sloid:8507000:1";
    String relation2Sloid = "ch:1:sloid:8507000:2";
    String parentServicePointSloid = "ch:1:sloid:8507000";
    RelationVersion relation1 = RelationTestData.getRelation(parentServicePointSloid, relation1Sloid, PLATFORM);
    RelationVersion relation2 = RelationTestData.getRelation(parentServicePointSloid, relation2Sloid, PARKING_LOT);
    relationRepository.saveAndFlush(relation1);
    relationRepository.saveAndFlush(relation2);

    //when
    List<RelationVersion> result = relationRepository.findAllBySloidAndReferencePointElementType(relation1Sloid, PARKING_LOT);
    //then
    assertThat(result)
        .hasSize(0);
  }

  @Test
  void shouldReturnRelationsByParentServicePointSloid() {
    String relation1Sloid = "ch:1:sloid:8507000:1";
    String relation2Sloid = "ch:1:sloid:8507000:2";
    String parentServicePointSloid = "ch:1:sloid:8507000";
    RelationVersion relation1 = RelationTestData.getRelation(parentServicePointSloid, relation1Sloid, PLATFORM);
    RelationVersion relation2 = RelationTestData.getRelation(parentServicePointSloid, relation2Sloid, PARKING_LOT);
    RelationVersion relationVersion1 = relationRepository.saveAndFlush(relation1);
    RelationVersion relationVersion2 = relationRepository.saveAndFlush(relation2);

    //when
    List<RelationVersion> result = relationRepository.findAllByParentServicePointSloid(parentServicePointSloid);
    //then
    assertThat(result)
        .hasSize(2)
        .containsExactlyInAnyOrder(relationVersion1,relationVersion2);
  }


  @Test
  void shouldNotReturnRelationsByParentServicePointSloid() {
    String relation1Sloid = "ch:1:sloid:8507000:1";
    String relation2Sloid = "ch:1:sloid:8507000:2";
    String parentServicePointSloid = "ch:1:sloid:8507000";
    RelationVersion relation1 = RelationTestData.getRelation(parentServicePointSloid, relation1Sloid, PLATFORM);
    RelationVersion relation2 = RelationTestData.getRelation(parentServicePointSloid, relation2Sloid, PARKING_LOT);
    relationRepository.saveAndFlush(relation1);
    relationRepository.saveAndFlush(relation2);

    //when
    List<RelationVersion> result = relationRepository.findAllByParentServicePointSloid("ch:1:sloid:100000");
    //then
    assertThat(result)
        .hasSize(0);
  }

  @Test
  void shouldReturnRelationsByParentServicePointSloidAndReferencePointElementType() {
    String relation1Sloid = "ch:1:sloid:8507000:1";
    String relation2Sloid = "ch:1:sloid:8507000:2";
    String parentServicePointSloid = "ch:1:sloid:8507000";
    RelationVersion relation1 = RelationTestData.getRelation(parentServicePointSloid, relation1Sloid, PLATFORM);
    RelationVersion relation2 = RelationTestData.getRelation(parentServicePointSloid, relation2Sloid, PARKING_LOT);
    RelationVersion relationVersion1 = relationRepository.saveAndFlush(relation1);
    relationRepository.saveAndFlush(relation2);

    //when
    List<RelationVersion> result =
        relationRepository.findAllByParentServicePointSloidAndReferencePointElementType(parentServicePointSloid, PLATFORM);
    //then
    assertThat(result)
        .hasSize(1)
        .containsExactlyInAnyOrder(relationVersion1);
  }

  @Test
  void shouldNotReturnRelationsByParentServicePointSloidWithWrongReferencePointElementType() {
    String relation1Sloid = "ch:1:sloid:8507000:1";
    String relation2Sloid = "ch:1:sloid:8507000:2";
    String parentServicePointSloid = "ch:1:sloid:8507000";
    RelationVersion relation1 = RelationTestData.getRelation(parentServicePointSloid, relation1Sloid, PLATFORM);
    RelationVersion relation2 = RelationTestData.getRelation(parentServicePointSloid, relation2Sloid, PARKING_LOT);
    relationRepository.saveAndFlush(relation1);
    relationRepository.saveAndFlush(relation2);

    //when
    List<RelationVersion> result =
        relationRepository.findAllByParentServicePointSloidAndReferencePointElementType(parentServicePointSloid, TOILETTE);
    //then
    assertThat(result)
        .hasSize(0);
  }

}