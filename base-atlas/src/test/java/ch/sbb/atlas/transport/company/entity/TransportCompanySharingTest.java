package ch.sbb.atlas.transport.company.entity;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.transport.company.SharedTransportCompanyModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Test;

class TransportCompanySharingTest {

  @Test
  void shouldSetAllPropertiesFromModel() {
    SharedTransportCompanyModel model = SharedTransportCompanyModel.builder()
        .id(1L)
        .number("1")
        .abbreviation("SBB")
        .description("Schweizerische Bundesbahn")
        .businessRegisterName("SBB")
        .businessRegisterNumber("1")
        .build();

    SharedTransportCompany sharedTransportCompany = new SharedTransportCompany();
    sharedTransportCompany.setPropertiesFromModel(model);

    assertThat(sharedTransportCompany).usingRecursiveComparison().isEqualTo(model);
  }

  @Test
  void shouldSetAllPropertiesToModel() {
    SharedTransportCompany transportCompany = SharedTransportCompany.builder()
        .id(1L)
        .number("1")
        .abbreviation("SBB")
        .description("Schweizerische Bundesbahn")
        .businessRegisterName("SBB")
        .businessRegisterNumber("1")
        .build();

    SharedTransportCompanyModel model = transportCompany.toModel();

    assertThat(transportCompany).usingRecursiveComparison().isEqualTo(model);
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Getter
  @Setter
  @Builder
  private static class SharedTransportCompany implements TransportCompanySharing {

    private Long id;

    private String number;

    private String abbreviation;

    private String description;

    private String businessRegisterName;

    private String businessRegisterNumber;
  }

}