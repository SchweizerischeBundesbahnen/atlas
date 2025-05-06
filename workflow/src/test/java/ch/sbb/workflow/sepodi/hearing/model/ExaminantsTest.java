package ch.sbb.workflow.sepodi.hearing.model;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.Examinants;
import ch.sbb.workflow.sepodi.hearing.model.sepodi.StopPointClientPersonModel;
import java.util.List;
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
    StopPointClientPersonModel result = examinants.getExaminantPersonByCanton(SwissCanton.BERN);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getMail()).isEqualTo("TechSupport-ATLAS@sbb.ch");
  }

  @Test
  void shouldGetExaminantByCantonWhenProfileIsNotProd() {
    //given
    examinants.setActiveProfile("local");

    //when
    StopPointClientPersonModel result = examinants.getExaminantPersonByCanton(SwissCanton.BERN);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getMail()).isEqualTo("TechSupport-ATLAS@sbb.ch");
  }

  @Test
  void shouldGetExaminantSpecialistOfficeNonProd() {
    //when
    examinants.setActiveProfile("local");
    StopPointClientPersonModel result = examinants.getExaminantSpecialistOffice();

    //then
    assertThat(result).isNotNull();
    assertThat(result.getMail()).isEqualTo("testuser-atlas@sbb.ch");
  }

  @Test
  void shouldGetExaminantSpecialistOfficeProd() {
    //when
    examinants.setActiveProfile("prod");
    StopPointClientPersonModel result = examinants.getExaminantSpecialistOffice();

    //then
    assertThat(result).isNotNull();
    assertThat(result.getMail()).isEqualTo("atlas@sbb.ch");
  }

  @Test
  void shouldGetExaminants() {
    //when
    List<StopPointClientPersonModel> result = examinants.getExaminants(SwissCanton.BERN);

    //then
    assertThat(result)
        .hasSize(2)
        .map(StopPointClientPersonModel::getMail)
        .containsExactlyInAnyOrder("testuser-atlas@sbb.ch", "TechSupport-ATLAS@sbb.ch");
  }

}
