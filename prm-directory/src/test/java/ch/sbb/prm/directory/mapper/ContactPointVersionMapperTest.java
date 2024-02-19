package ch.sbb.prm.directory.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import ch.sbb.atlas.api.prm.enumeration.RecordingStatus;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.contactpoint.ReadContactPointVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.ContactPointTestData;
import ch.sbb.prm.directory.entity.ContactPointVersion;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ContactPointVersionMapperTest {

  @Test
  void shouldMapToModelCorrectly() {
    // Given
    ContactPointVersion contactPointVersion = ContactPointTestData.getContactPointVersion();

    // When
    ReadContactPointVersionModel contactPointVersionModel = ContactPointVersionMapper.toModel(contactPointVersion);

    ReadContactPointVersionModel expected = ReadContactPointVersionModel
        .builder()
        .sloid("ch:1:sloid:12345:1")
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(1234567))
        .validFrom(LocalDate.of(2000, 1, 1))
        .validTo(LocalDate.of(2000, 12, 31))
        .parentServicePointSloid("ch:1:sloid:12345")
        .designation("Designation")
        .additionalInformation("Additional information")
        .inductionLoop(StandardAttributeType.NOT_APPLICABLE)
        .openingHours("10:00-22:00")
        .wheelchairAccess(StandardAttributeType.YES)
        .type(ContactPointType.INFORMATION_DESK)
        .build();

    assertThat(contactPointVersionModel).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  void shouldHaveRecordingStatusComplete() {
    ContactPointVersion contactPointVersion = ContactPointTestData.getContactPointVersion();

    RecordingStatus result = ContactPointVersionMapper.toOverviewModel(contactPointVersion).getRecordingStatus();

    assertThat(result).isEqualTo(RecordingStatus.COMPLETE);
  }

  @Test
  void shouldHaveRecordingStatusIncomplete() {
    ContactPointVersion contactPointVersion = ContactPointTestData.getContactPointVersion();
    contactPointVersion.setInductionLoop(StandardAttributeType.TO_BE_COMPLETED);

    RecordingStatus result = ContactPointVersionMapper.toOverviewModel(contactPointVersion).getRecordingStatus();

    assertThat(result).isEqualTo(RecordingStatus.INCOMPLETE);
  }
}
