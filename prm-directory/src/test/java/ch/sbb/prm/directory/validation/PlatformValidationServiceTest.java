package ch.sbb.prm.directory.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.exception.RecordingVariantException;
import java.util.SortedSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.MockitoAnnotations;

class PlatformValidationServiceTest {

  private PlatformValidationService platformValidationService;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    this.platformValidationService = new PlatformValidationService();
  }

  @Test
  void shouldNotValidateWhenCompletePlatformContainsAllFields() {
    //given
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();

    //when
    RecordingVariantException result = Assertions.assertThrows(
        RecordingVariantException.class,
        () -> platformValidationService.validateRecordingVariants(platformVersion, true));

    //then
    assertThat(result).isNotNull();
    ErrorResponse errorResponse = result.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(400);
    assertThat(errorResponse.getMessage()).isEqualTo(
        "PlatformVersion with sloid [ch:1:sloid:12345:1] cannot be save: Attempting to save a Reduced object with wrong "
            + "properties population!");
    assertThat(errorResponse.getDetails()).hasSize(9);
  }

  @Test
  void shouldNotAllowCompletePlatformWithInfoOpportunities() {
    //given
    PlatformVersion platformVersion = PlatformTestData.getPlatformVersion();
    assertThat(platformVersion.getInfoOpportunities()).isNotEmpty();

    //when
    RecordingVariantException result = Assertions.assertThrows(
        RecordingVariantException.class,
        () -> platformValidationService.validateRecordingVariants(platformVersion, false));

    //then
    assertThat(result).isNotNull();
    assertThat(result.getErrorConstraintMap()).containsKey("infoOpportunities");
  }

  @Test
  void shouldNotValidateWhenCompletePlatformDoesNotContainsAllMandatoryFields() {
    //given
    PlatformVersion platformVersion = PlatformTestData.getCompletePlatformVersion();
    platformVersion.setBoardingDevice(null);

    //when
    RecordingVariantException result = Assertions.assertThrows(
        RecordingVariantException.class,
        () -> platformValidationService.validateRecordingVariants(platformVersion, false));

    //then
    assertThat(result).isNotNull();
    ErrorResponse errorResponse = result.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(400);
    assertThat(errorResponse.getMessage()).isEqualTo(
        "PlatformVersion with sloid [ch:1:sloid:12345:1] cannot be save: Attempting to save a Complete object with wrong "
            + "properties population!");
    SortedSet<Detail> errorResponseDetails = errorResponse.getDetails();
    assertThat(errorResponseDetails).hasSize(1);
    Detail detail = errorResponseDetails.stream().toList().get(0);
    assertThat(detail.getMessage()).isEqualTo("Must not be null for Completed Object. At least a default value is mandatory");
    assertThat(detail.getField()).isEqualTo("boardingDevice");
  }

  @Test
  void shouldValidateWhenCompleteContainsAllDeclaredCompleteFields() {
    //given
    PlatformVersion platformVersion = PlatformTestData.getCompletePlatformVersion();

    //when
    Executable executable = () -> platformValidationService.validateRecordingVariants(platformVersion, false);

    //then
    assertDoesNotThrow(executable);
  }

  @Test
  void shouldValidateWhenReducedContainsAllDeclaredReducedFields() {
    //given
    PlatformVersion platformVersion = PlatformTestData.getReducedPlatformVersion();

    //when
    Executable executable = () -> platformValidationService.validateRecordingVariants(platformVersion, true);

    //then
    assertDoesNotThrow(executable);
  }

}