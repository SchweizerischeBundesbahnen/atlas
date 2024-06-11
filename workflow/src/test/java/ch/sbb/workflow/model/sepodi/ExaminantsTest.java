package ch.sbb.workflow.model.sepodi;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@EnableAutoConfiguration
@IntegrationTest
class ExaminantsTest {

  @Autowired
  private Examinants examinants;

  @Test
  void shouldGetExaminantByCantonWhenProfileIsProd() {
    //given
    examinants.setActiveProfile("prod");

    //when
    ClientPersonModel result = examinants.getExaminantPersonByCanton(SwissCanton.BERN);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getMail()).isEqualTo("TechSupport-ATLAS@sbb.ch");
  }

  @Test
  void shouldGetExaminantByCantonWhenProfileIsNotProd() {
    //given
    examinants.setActiveProfile("local");

    //when
    ClientPersonModel result = examinants.getExaminantPersonByCanton(SwissCanton.BERN);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getMail()).isEqualTo("TechSupport-ATLAS@sbb.ch");
  }

  @Test
  void shouldGetExaminantSpecialistOffice() {
    //when
    ClientPersonModel result = examinants.getExaminantSpecialistOffice();

    //then
    assertThat(result).isNotNull();
    assertThat(result.getMail()).isEqualTo("TechSupport-ATLAS@sbb.ch");
  }

}