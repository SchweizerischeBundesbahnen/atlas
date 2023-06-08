package ch.sbb.line.directory.service;

import ch.sbb.atlas.kafka.model.Status;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Documentation at LineStatusDecision.puml
 */
@Slf4j
@Service
public class LineStatusDecider {

  public Status getStatusForLine(LineVersion newLineVersion, Optional<LineVersion> currentLineVersion, List<LineVersion> currentLineVersions) {
    if (newLineVersion.getId() != null
        && currentLineVersion.isPresent()
        && currentLineVersion.get().getStatus() == Status.IN_REVIEW) {
      return Status.IN_REVIEW;
    }

    // Only for ORDERLY Lines there may be a workflow
    if (newLineVersion.getLineType() != LineType.ORDERLY) {
      return Status.VALIDATED;
    }

    Optional<LineVersion> previousVersionOnSameTimeslot = findPreviousVersionOnSameTimeslot(newLineVersion, currentLineVersions);
    log.debug("Deciding on Line.Status with previousVersionOnSameTimeslot={} and newLineVersion={}",
        previousVersionOnSameTimeslot, newLineVersion);

    // If there was no version previously on the timeline of the saving version, a workflow is mandatory
    if (previousVersionOnSameTimeslot.isEmpty()) {
      return Status.DRAFT;
    }

    return getStatusWithPreviousVersion(newLineVersion, previousVersionOnSameTimeslot.orElseThrow());
  }

  /**
   * Pre-Save Versions: |------||------||------|
   *                               ^
   * Saving Version             |------|
   */
  private Optional<LineVersion> findPreviousVersionOnSameTimeslot(LineVersion newLineVersion,
      List<LineVersion> currentLineVersions) {
    return currentLineVersions.stream().filter(currentLineVersion ->
            !currentLineVersion.getValidTo().isBefore(newLineVersion.getValidFrom()) &&
                !currentLineVersion.getValidFrom().isAfter(newLineVersion.getValidFrom()))
        .filter(lineVersion -> lineVersion.getStatus() == Status.VALIDATED).findFirst();
  }

  private Status getStatusWithPreviousVersion(LineVersion newLineVersion, LineVersion previousLineVersion) {
    boolean attributeChange = new LineRelevantAttributeChange().test(newLineVersion, previousLineVersion);
    boolean prolongation = new LineProlongation().test(newLineVersion, previousLineVersion);
    log.debug("Deciding on Line.Status with previousVersion. attributeChange={}, prolongation={}", attributeChange, prolongation);
    return attributeChange || prolongation ? Status.DRAFT : Status.VALIDATED;
  }

  private static class LineRelevantAttributeChange implements BiPredicate<LineVersion, LineVersion> {

    private final List<Function<LineVersion, Object>> worflowRelevantAttributes = new ArrayList<>();

    LineRelevantAttributeChange() {
      worflowRelevantAttributes.add(LineVersion::getSwissLineNumber);
      worflowRelevantAttributes.add(LineVersion::getNumber);
      worflowRelevantAttributes.add(LineVersion::getDescription);
      worflowRelevantAttributes.add(LineVersion::getLongName);
      worflowRelevantAttributes.add(LineVersion::getAlternativeName);
      worflowRelevantAttributes.add(LineVersion::getCombinationName);
    }

    @Override
    public boolean test(LineVersion newLineVersion, LineVersion previousLineVersion) {
      for (Function<LineVersion, Object> attribute : worflowRelevantAttributes) {
        if (!Objects.equals(attribute.apply(previousLineVersion), attribute.apply(newLineVersion))) {
          return true;
        }
      }
      if (previousLineVersion.getLineType() != LineType.ORDERLY
          && newLineVersion.getLineType() == LineType.ORDERLY) {
        return true;
      }
      return false;
    }
  }

  private static class LineProlongation implements BiPredicate<LineVersion, LineVersion> {

    @Override
    public boolean test(LineVersion newLineVersion, LineVersion previousLineVersion) {
      return newLineVersion.getValidFrom().isBefore(previousLineVersion.getValidFrom())
          || newLineVersion.getValidTo().isAfter(previousLineVersion.getValidTo());
    }
  }

}
