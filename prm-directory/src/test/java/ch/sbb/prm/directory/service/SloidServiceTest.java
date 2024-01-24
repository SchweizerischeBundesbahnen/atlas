package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.prm.directory.entity.ReferencePointVersion;
import org.junit.jupiter.api.Test;

class SloidServiceTest {

  private final SloidService sloidService = new SloidService();

  @Test
  void shouldGenerateSomeSloidForNew() {
    ReferencePointVersion version = ReferencePointVersion.builder().parentServicePointSloid("ch:1:sloid:7000").build();
    sloidService.generateNewSloidIfNotGiven(version);

    assertThat(version.getSloid()).isNotBlank().startsWith("ch:1:sloid:7000");
  }

  @Test
  void shouldNotOverrideManuallyChosenSloid() {
    ReferencePointVersion version = ReferencePointVersion.builder()
        .sloid("ch:1:sloid:7000:1")
        .parentServicePointSloid("ch:1:sloid:7000")
        .build();
    sloidService.generateNewSloidIfNotGiven(version);

    assertThat(version.getSloid()).isNotBlank().isEqualTo("ch:1:sloid:7000:1");
  }
}