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

    public Timeline(List<ServicePointVersion> servicePointVersions) {
        if (servicePointVersions == null) {
            this.elements = new ArrayList<>();
        } else {
            this.elements = servicePointVersions.stream()
                    .map(TimelineElement::new)
                    .collect(Collectors.toList());
        }
    }

    public List<TimelineElement> getMergedTimeline() {
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

    public TimelineElement getBpkTimelineElement(ServicePointVersion servicePointVersion) {
        return new TimelineElement(servicePointVersion);
    }

    private boolean noGapsBetweenTimelines(TimelineElement current, TimelineElement next) {
        return ChronoUnit.DAYS.between(current.endDate, next.startDate) <= 1;
    }

    public boolean isBpkTimelineInsideOfOneBpsTimeline(List<TimelineElement> bpsTimelines, TimelineElement bpkTimeline) {
        return bpsTimelines.stream()
                .anyMatch(bps -> isOverlapping(bpkTimeline, bps));
    }

    public static boolean isOverlapping(TimelineElement bpkTimeline, TimelineElement bpsTimeline) {
        return (bpsTimeline.startDate.isBefore(bpkTimeline.startDate) || bpsTimeline.startDate.isEqual(bpkTimeline.startDate))
                && (bpsTimeline.endDate.isAfter(bpkTimeline.endDate) || bpsTimeline.endDate.isEqual(bpkTimeline.endDate));
    }

    @Data
    public class TimelineElement {
        private final LocalDate startDate;
        private LocalDate endDate;

        public TimelineElement(TimelineElement current, TimelineElement next) {
            this.startDate = current.getStartDate();
            this.endDate = next.getEndDate();
        }

        public TimelineElement(ServicePointVersion servicePointVersion) {
            this.startDate = servicePointVersion.getValidFrom();
            this.endDate = servicePointVersion.getValidTo();
        }
    }

}