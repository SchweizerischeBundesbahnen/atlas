package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class Timeline {

    private final List<DateRange> kilometerMasterTimelineElements = new ArrayList<>();
    private final DateRange servicePointTimelineElement;

    public Timeline(List<ServicePointVersion> allKilometerMasterNumberVersions, ServicePointVersion servicePointVersion) {
        Objects.requireNonNull(servicePointVersion);
        servicePointTimelineElement = new DateRange(servicePointVersion.getValidFrom(), servicePointVersion.getValidTo());

        if (!CollectionUtils.isEmpty(allKilometerMasterNumberVersions)) {
            List<DateRange> list = allKilometerMasterNumberVersions.stream()
                    .map(version -> new DateRange(version.getValidFrom(), version.getValidTo()))
                    .toList();
            this.kilometerMasterTimelineElements.addAll(getMergedTimeline(list));
        }
    }

    static List<DateRange> getMergedTimeline(List<DateRange> elements) {
        List<DateRange> result = new ArrayList<>();

        for (DateRange current : elements) {
            // check if result has an element. if not, add current element.
            if (result.isEmpty()) {
                result.add(current);
            } else {
                // there is an element in result, check if we can append to it.
                var currentResultElement = result.get(result.size() - 1);
                if (noGapsBetweenTimelines(currentResultElement, current)) {
                    currentResultElement.setTo(current.getTo());
                } else {
                    result.add(current);
                }
            }
        }

        return result;
    }

    private static boolean noGapsBetweenTimelines(DateRange current, DateRange next) {
        return ChronoUnit.DAYS.between(current.getTo(), next.getFrom()) <= 1;
    }

    public boolean isSePoTimelineInsideOrEqToOneOfKilomMastTimelines() {
        return kilometerMasterTimelineElements.stream()
                .anyMatch(kilMasterTimelineElement -> isSePoTimelineInsideOrEqToKilomMastTimeline(servicePointTimelineElement, kilMasterTimelineElement));
    }

    private static boolean isSePoTimelineInsideOrEqToKilomMastTimeline(DateRange sePoTimelineElement, DateRange kilomMasterTimelineElement) {
        return (kilomMasterTimelineElement.getFrom().isBefore(sePoTimelineElement.getFrom()) || kilomMasterTimelineElement.getFrom().isEqual(sePoTimelineElement.getFrom()))
                && (kilomMasterTimelineElement.getTo().isAfter(sePoTimelineElement.getTo()) || kilomMasterTimelineElement.getTo().isEqual(sePoTimelineElement.getTo()));
    }

}