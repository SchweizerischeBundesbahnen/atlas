package ch.sbb.atlas.model.entity;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class BaseEntityTest {

  @Autowired
  private DummyBaseEntityRepository dummyBaseEntityRepository;

  @Test
  void shouldSaveCreatorAndEditorOnPersist() {
    DummyBaseEntity dummyBaseEntity = dummyBaseEntityRepository.save(DummyBaseEntity.builder().id(1L).build());
    assertThat(dummyBaseEntity.getCreator()).isNotNull();
    assertThat(dummyBaseEntity.getCreationDate()).isNotNull();
    assertThat(dummyBaseEntity.getEditor()).isNotNull();
    assertThat(dummyBaseEntity.getEditionDate()).isNotNull();
  }

  @Test
  void shouldSetCreatorAndEditorOnCreate() {
    DummyBaseEntity baseEntity = DummyBaseEntity.builder().id(1L).build();
    baseEntity.onPrePersist();
    assertThat(baseEntity.getCreator()).isNotNull();
    assertThat(baseEntity.getEditor()).isNotNull();
  }

  @Test
  void shouldSetEditorOnEdit() {
    DummyBaseEntity baseEntity = DummyBaseEntity.builder().id(1L).build();
    baseEntity.onPreUpdate();
    assertThat(baseEntity.getEditor()).isNotNull();
  }

}