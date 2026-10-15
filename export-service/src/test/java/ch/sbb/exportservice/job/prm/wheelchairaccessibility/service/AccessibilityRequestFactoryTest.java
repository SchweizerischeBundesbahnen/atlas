package ch.sbb.exportservice.job.prm.wheelchairaccessibility.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRequest;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityVersion;
import ch.sbb.exportservice.job.prm.platform.entity.PlatformVersion;
import ch.sbb.exportservice.job.prm.relation.entity.RelationVersion;
import ch.sbb.exportservice.job.prm.stoppoint.entity.StopPointVersion;
import java.util.List;
import org.junit.jupiter.api.Test;

class AccessibilityRequestFactoryTest {

  private static final StopPointVersion STOP_POINT = StopPointVersion.builder().sloid("ch:1:sloid:1").build();
  private static final PlatformVersion PLATFORM_1 = PlatformVersion.builder().sloid("ch:1:sloid:1:0:1").build();
  private static final PlatformVersion PLATFORM_2 = PlatformVersion.builder().sloid("ch:1:sloid:1:0:2").build();
  private static final RelationVersion RELATION_1 = RelationVersion.builder().sloid("ch:1:sloid:1:0:1").build();
  private static final RelationVersion RELATION_2 = RelationVersion.builder().sloid("ch:1:sloid:1:0:2").build();

  @Test
  void shouldBuildStopPointRequestWithAllPlatformsAndRelations() {
    // Given
    // When
    AccessibilityRequest request = AccessibilityRequestFactory.forStopPoint(List.of(STOP_POINT),
        List.of(PLATFORM_1, PLATFORM_2), List.of(RELATION_1, RELATION_2));

    // Then
    assertThat(request.getStopPoint()).extracting(AccessibilityVersion::getSloid)
        .containsExactly(STOP_POINT.getSloid());
    assertThat(request.getPlatform()).extracting(AccessibilityVersion::getSloid)
        .containsExactly(PLATFORM_1.getSloid(), PLATFORM_2.getSloid());
    assertThat(request.getRelations()).extracting(AccessibilityVersion::getSloid)
        .containsExactly(RELATION_1.getSloid(), RELATION_2.getSloid());
  }

  @Test
  void shouldBuildPlatformRequestWithOnlyRelationsOfThatPlatform() {
    // Given
    // When
    AccessibilityRequest request = AccessibilityRequestFactory.forPlatform(List.of(STOP_POINT), PLATFORM_1.getSloid(),
        List.of(PLATFORM_1), List.of(RELATION_1, RELATION_2));

    // Then
    assertThat(request.getStopPoint()).extracting(AccessibilityVersion::getSloid)
        .containsExactly(STOP_POINT.getSloid());
    assertThat(request.getPlatform()).extracting(AccessibilityVersion::getSloid)
        .containsExactly(PLATFORM_1.getSloid());
    assertThat(request.getRelations()).extracting(AccessibilityVersion::getSloid)
        .containsExactly(RELATION_1.getSloid());
  }
}
