package ch.sbb.atlas.versioning.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.versioning.model.Entity;
import ch.sbb.atlas.versioning.model.Property;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.model.VersioningAction;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the ATLAS-3374 fix: mutable collection fields on a Versionable must be snapshotted
 * during property extraction so that subsequent JPA mutations to the live collection (e.g. clearing
 * meansOfTransport when saving the UPDATE version) do not bleed into the remainder (NEW/NOT_TOUCHED) versions.
 */
class VersionableServiceCollectionSnapshotTest {

  private final VersionableService versionableService = new VersionableServiceImpl();

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @FieldNameConstants
  @AtlasVersionable
  static class VersionableWithSet implements Versionable {

    private LocalDate validFrom;
    private LocalDate validTo;
    private Long id;
    @AtlasVersionableProperty
    private String name;
    @AtlasVersionableProperty
    private Set<String> tags;
  }

  /**
   * Reproduces ATLAS-3374: shortening validTo while also clearing a Set property (e.g. meansOfTransport)
   * splits the timeline into an UPDATE version and a NEW (remainder) version. The remainder must keep
   * the original Set contents even after the live Set is mutated externally (simulating the JPA persistence
   * context clearing the Set when the UPDATE version is saved).
   *
   * <pre>
   * BEFORE: [2024-01-01 ─────────────────────── 9999-12-31]  tags={A,B}
   * EDIT:   [2024-01-01 ────── 2024-12-31]  tags={}
   *
   * RESULT (UPDATE): [2024-01-01 ── 2024-12-31]  tags={}
   * RESULT (NEW):    [2025-01-01 ─────────────── 9999-12-31]  tags={A,B}  ← must not be cleared
   * </pre>
   */
  @Test
  void remainderVersionShouldKeepOriginalSetContentsAfterLiveCollectionIsCleared() {
    Set<String> liveTags = new HashSet<>(Set.of("A", "B"));
    VersionableWithSet current = VersionableWithSet.builder()
        .id(1L)
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(9999, 12, 31))
        .name("original")
        .tags(liveTags)
        .build();

    VersionableWithSet edited = VersionableWithSet.builder()
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(2024, 12, 31))
        .name("updated")
        .tags(new HashSet<>())
        .build();

    List<VersionedObject> result = versionableService.versioningObjects(current, edited, List.of(current));

    // Simulate what JPA does when saving the UPDATE version: the persistence context clears the live Set
    liveTags.clear();

    VersionedObject updateVersion = findByAction(result, VersioningAction.UPDATE);
    VersionedObject newVersion = findByAction(result, VersioningAction.NEW);

    assertThat(updateVersion.getValidTo()).isEqualTo(LocalDate.of(2024, 12, 31));
    assertThat(getPropertyValue(updateVersion.getEntity(), VersionableWithSet.Fields.tags))
        .isInstanceOfSatisfying(Set.class, s -> assertThat(s).isEmpty());

    assertThat(newVersion.getValidFrom()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(newVersion.getValidTo()).isEqualTo(LocalDate.of(9999, 12, 31));
    assertThat(getPropertyValue(newVersion.getEntity(), VersionableWithSet.Fields.tags))
        .isInstanceOfSatisfying(Set.class, s -> assertThat(s).containsExactlyInAnyOrder("A", "B"));
  }

  /**
   * Ensures that when the same version object appears in both the currentVersion and currentVersions list
   * (the standard update call pattern), the two extracted entities contain independent copies of
   * their Set properties — so a clear of the live Set affects neither.
   */
  @Test
  void currentVersionAndCurrentVersionsListShouldYieldIndependentSetCopies() {
    Set<String> liveTags = new HashSet<>(Set.of("X", "Y", "Z"));
    VersionableWithSet current = VersionableWithSet.builder()
        .id(1L)
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(9999, 12, 31))
        .name("v1")
        .tags(liveTags)
        .build();

    // Edit does not change validFrom/validTo → UPDATE only, no split
    VersionableWithSet edited = VersionableWithSet.builder()
        .validFrom(LocalDate.of(2024, 1, 1))
        .validTo(LocalDate.of(9999, 12, 31))
        .name("v1-updated")
        .tags(new HashSet<>(Set.of("X")))
        .build();

    List<VersionedObject> result = versionableService.versioningObjects(current, edited, List.of(current));

    // Simulate JPA clearing the managed Set
    liveTags.clear();

    VersionedObject updateVersion = findByAction(result, VersioningAction.UPDATE);
    // The UPDATE entity should carry the edited value {X}, not the now-cleared live Set
    assertThat(getPropertyValue(updateVersion.getEntity(), VersionableWithSet.Fields.tags))
        .isInstanceOfSatisfying(Set.class, s -> assertThat(s).containsExactlyInAnyOrder("X"));
  }

  // ─── helpers ──────────────────────────────────────────────────────────────

  private static VersionedObject findByAction(List<VersionedObject> objects, VersioningAction action) {
    return objects.stream()
        .filter(v -> action == v.getAction())
        .findFirst()
        .orElseThrow(() -> new AssertionError("No versioned object with action " + action));
  }

  private static Object getPropertyValue(Entity entity, String key) {
    return entity.getProperties().stream()
        .filter(p -> key.equals(p.getKey()))
        .map(Property::getValue)
        .findFirst()
        .orElseThrow(() -> new AssertionError("Property not found: " + key));
  }
}
