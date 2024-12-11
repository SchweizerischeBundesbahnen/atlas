package ch.sbb.line.directory.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.CreateSublineVersionModelV2;
import ch.sbb.atlas.api.lidi.ReadSublineVersionModelV2;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.api.lidi.enumaration.PaymentType;
import ch.sbb.atlas.api.lidi.enumaration.SublineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.SublineVersion;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class SublineVersionMapperTest {

  @Test
  void shouldMapToReadModel() {
    SublineVersion sublineVersion = SublineVersion.builder()
        .id(1L)
        .status(Status.VALIDATED)
        .sublineType(SublineType.TECHNICAL)
        .paymentType(PaymentType.INTERNATIONAL)
        .number("number")
        .longName("longName")
        .description("description")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .businessOrganisation("businessOrganisation")
        .mainlineSlnid("ch:1:mainlineslnid")
        .swissSublineNumber("swissSublineNumber")
        .version(5)
        .creator("creator")
        .creationDate(LocalDateTime.of(2020, 1, 1, 15, 10))
        .editor("editor")
        .editionDate(LocalDateTime.of(2020, 1, 1, 15, 10))
        .build();
    LineVersion lineVersion = LineVersion.builder()
        .swissLineNumber("mainSwissLineNumber")
        .number("mainNumber")
        .longName("mainLongName")
        .shortNumber("mainShortNumber")
        .offerCategory(OfferCategory.IC)
        .build();

    ReadSublineVersionModelV2 expected = ReadSublineVersionModelV2.builder()
        .id(1L)
        .status(Status.VALIDATED)
        .sublineType(SublineType.TECHNICAL)
        .longName("longName")
        .description("description")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .businessOrganisation("businessOrganisation")
        .mainlineSlnid("ch:1:mainlineslnid")
        .swissSublineNumber("swissSublineNumber")
        .etagVersion(5)
        .creator("creator")
        .creationDate(LocalDateTime.of(2020, 1, 1, 15, 10))
        .editor("editor")
        .editionDate(LocalDateTime.of(2020, 1, 1, 15, 10))
        .mainSwissLineNumber("mainSwissLineNumber")
        .mainLineNumber("mainNumber")
        .mainShortNumber("mainShortNumber")
        .mainLineOfferCategory(OfferCategory.IC)
        .build();

    ReadSublineVersionModelV2 actual = SublineMapper.toModel(sublineVersion, lineVersion);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void shouldMapToEntityForCreate() {
    CreateSublineVersionModelV2 model = CreateSublineVersionModelV2.builder()
        .id(1L)
        .status(Status.VALIDATED)
        .mainlineSlnid("ch:1:mainlineslnid")
        .sublineType(SublineType.TECHNICAL)
        .sublineConcessionType(SublineConcessionType.CANTONALLY_APPROVED_LINE)
        .description("description")
        .swissSublineNumber("swissSublineNumber")
        .longName("longName")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .businessOrganisation("businessOrganisation")
        .creator("creator")
        .creationDate(LocalDateTime.of(2020, 1, 1, 15, 10))
        .editor("editor")
        .editionDate(LocalDateTime.of(2020, 1, 1, 15, 10))
        .etagVersion(5)
        .build();


    SublineVersion expected = SublineVersion.builder()
        .id(1L)
        .status(Status.VALIDATED)
        .sublineType(SublineType.TECHNICAL)
        .concessionType(SublineConcessionType.CANTONALLY_APPROVED_LINE)
        .longName("longName")
        .description("description")
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .businessOrganisation("businessOrganisation")
        .mainlineSlnid("ch:1:mainlineslnid")
        .swissSublineNumber("swissSublineNumber")
        .version(5)
        .creator("creator")
        .creationDate(LocalDateTime.of(2020, 1, 1, 15, 10))
        .editor("editor")
        .editionDate(LocalDateTime.of(2020, 1, 1, 15, 10))
        .build();

    SublineVersion actual = SublineMapper.toEntity(model);
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }
}