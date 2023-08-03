package ch.sbb.atlas.servicepointdirectory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class BasePointUtilityTest {

  @Test
  void shouldFindVersionsExactlyIncludedBetweenEditedValidFromAndEditedValidTo() {
    //given
    ServicePointVersion version1 = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    ServicePointVersion version2 = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .build();

    //when
    List<ServicePointVersion> result = BasePointUtility.findVersionsExactlyIncludedBetweenEditedValidFromAndEditedValidTo(
        LocalDate.of(2000, 1, 1),
        LocalDate.of(2001, 12, 31),
        List.of(version1, version2)
    );

    //then
    assertThat(result).isNotNull();
    assertThat(result.isEmpty()).isFalse();
    assertThat(result.size()).isEqualTo(2);
  }

  @Test
  void shouldNotFindVersionsWhenNotExactlyIncludedBetweenEditedValidFromAndEditedValidTo() {
    //given
    ServicePointVersion version1 = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .build();
    ServicePointVersion version2 = ServicePointVersion.builder()
        .validFrom(LocalDate.of(2001, 1, 1))
        .validTo(LocalDate.of(2001, 12, 31))
        .build();

    //when
    List<ServicePointVersion> result = BasePointUtility.findVersionsExactlyIncludedBetweenEditedValidFromAndEditedValidTo(
        LocalDate.of(2000, 1, 2),
        LocalDate.of(2001, 12, 30),
        List.of(version1, version2)
    );

    //then
    assertThat(result).isNotNull();
    assertThat(result.isEmpty()).isTrue();
  }

}
