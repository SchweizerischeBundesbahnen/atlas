package ch.sbb.workflow.sepodi.termination.model;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@EnableAutoConfiguration
@IntegrationTest
class TerminationExaminantsTest {

  @Autowired
  private TerminationExaminants terminationExaminants;

  @Test
  void shouldGetTerminationExaminantsWhenProfileIsProd() {
    //given
    terminationExaminants.setActiveProfile("prod");

    //when and then
    assertThat(terminationExaminants.getNova()).isNotNull().isEqualTo("nova@sbb.ch");
    assertThat(terminationExaminants.getInfoPlus()).isNotNull().isEqualTo("info@sbb.ch");
  }

  @Test
  void shouldGetTerminationExaminantsWhenProfileNotIsProd() {
    //given
    terminationExaminants.setActiveProfile("int");

    //when and then
    assertThat(terminationExaminants.getNova()).isNotNull().isEqualTo("TechSupport-ATLAS@sbb.ch");
    assertThat(terminationExaminants.getInfoPlus()).isNotNull().isEqualTo("TechSupport-ATLAS@sbb.ch");
  }

}