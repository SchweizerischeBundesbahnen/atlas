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

    private List<TimelineElement> elements;
    private TimelineElement servicePointTimelineElement;

    public Timeline(List<ServicePointVersion> servicePointVersions, ServicePointVersion servicePointVersion) {
        if (servicePointVersion == null) {
            throw new IllegalStateException("ServicePointVersion is required to instantiate Timeline.");
        }
        servicePointTimelineElement = new TimelineElement(servicePointVersion);

        if (servicePointVersions == null) {
            this.elements = new ArrayList<>();
        } else {
            this.elements = getMergedTimeline(servicePointVersions.stream()
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

    public boolean isBpkTimelineInsideOfOneBpsTimeline() {
        return elements.stream()
                .anyMatch(bps -> isOverlapping(servicePointTimelineElement, bps));
    }

    private static boolean isOverlapping(TimelineElement bpkTimeline, TimelineElement bpsTimeline) {
        return (bpsTimeline.startDate.isBefore(bpkTimeline.startDate) || bpsTimeline.startDate.isEqual(bpkTimeline.startDate))
                && (bpsTimeline.endDate.isAfter(bpkTimeline.endDate) || bpsTimeline.endDate.isEqual(bpkTimeline.endDate));
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