package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import lombok.Data;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Timeline {

    private List<TimelineElement> kilometerMasterTimelineElements;
    private TimelineElement servicePointTimelineElement;

    public Timeline(List<ServicePointVersion> allKilometerMasterNumberVersions, ServicePointVersion servicePointVersion) {
        if (servicePointVersion == null) {
            throw new IllegalStateException("ServicePointVersion is required to instantiate Timeline.");
        }
        servicePointTimelineElement = new TimelineElement(servicePointVersion);

        if (allKilometerMasterNumberVersions == null) {
            this.kilometerMasterTimelineElements = new ArrayList<>();
        } else {
            this.kilometerMasterTimelineElements = getMergedTimeline(allKilometerMasterNumberVersions.stream()
                    .map(TimelineElement::new)
                    .collect(Collectors.toList()));
        }
    }

    static List<TimelineElement> getMergedTimeline(List<TimelineElement> elements) {
        List<TimelineElement> result = new ArrayList<>();

        for (TimelineElement current : elements) {
            // check if result has an element. if not, add current element.
            if (result.isEmpty()) {
                result.add(current);
            } else {
                // there is an element in result, check if we can append to it.
                var currentResultElement = result.get(result.size() - 1);
                if (noGapsBetweenTimelines(currentResultElement, current)) {
                    currentResultElement.setEndDate(current.getEndDate());
                } else {
                    result.add(current);
                }
            }
        }

        return result;
    }

    private static boolean noGapsBetweenTimelines(TimelineElement current, TimelineElement next) {
        return ChronoUnit.DAYS.between(current.endDate, next.startDate) <= 1;
    }

    public boolean isSePoTimelineInsideOrEqOfOneOfKilomMasterTimelines() {
        return kilometerMasterTimelineElements.stream()
                .anyMatch(kilMasterTimelineElement -> isSePoTimelineInsideOrEqOfKilomMasterTimeline(servicePointTimelineElement, kilMasterTimelineElement));
    }

    private static boolean isSePoTimelineInsideOrEqOfKilomMasterTimeline(TimelineElement sePoTimelineElement, TimelineElement kilomMasterTimelineElement) {
        return (kilomMasterTimelineElement.startDate.isBefore(sePoTimelineElement.startDate) || kilomMasterTimelineElement.startDate.isEqual(sePoTimelineElement.startDate))
                && (kilomMasterTimelineElement.endDate.isAfter(sePoTimelineElement.endDate) || kilomMasterTimelineElement.endDate.isEqual(sePoTimelineElement.endDate));
    }

    @Data
    static class TimelineElement {
        private final LocalDate startDate;
        private LocalDate endDate;

        public TimelineElement(ServicePointVersion servicePointVersion) {
            this.startDate = servicePointVersion.getValidFrom();
            this.endDate = servicePointVersion.getValidTo();
        }
    }

}