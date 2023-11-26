package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.servicepointdirectory.ServicePointVersionsTimelineTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TimelineTest {

    @Test
    void shouldFailWhenMissingServicePoint() {
        Assertions.assertThrows(NullPointerException.class, () -> new Timeline(null, null));
    }

    @Test
    void shouldCreateTimelineElementsListWhenKilMasterTimelinesCombinationWithAndWithoutGaps() {
        List<ServicePointVersion> servicePointVersionList = new ArrayList<>();
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel2());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel3());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel4());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel5());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel6());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel7());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel8());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel9());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel10());

        Timeline timeline = new Timeline(servicePointVersionList, ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        List<DateRange> mergedTimeline = timeline.getKilometerMasterTimelineElements();
        assertThat(mergedTimeline).hasSize(6);
        assertThat(mergedTimeline.get(0).getFrom()).isEqualTo(LocalDate.of(2010, 12, 11));
        assertThat(mergedTimeline.get(0).getTo()).isEqualTo(LocalDate.of(2013, 8, 10));
        assertThat(mergedTimeline.get(1).getFrom()).isEqualTo(LocalDate.of(2013, 8, 14));
        assertThat(mergedTimeline.get(1).getTo()).isEqualTo(LocalDate.of(2014, 8, 14));
        assertThat(mergedTimeline.get(2).getFrom()).isEqualTo(LocalDate.of(2014, 8, 18));
        assertThat(mergedTimeline.get(2).getTo()).isEqualTo(LocalDate.of(2015, 8, 18));
        assertThat(mergedTimeline.get(3).getFrom()).isEqualTo(LocalDate.of(2015, 8, 20));
        assertThat(mergedTimeline.get(3).getTo()).isEqualTo(LocalDate.of(2018, 8, 20));
        assertThat(mergedTimeline.get(4).getFrom()).isEqualTo(LocalDate.of(2018, 8, 25));
        assertThat(mergedTimeline.get(4).getTo()).isEqualTo(LocalDate.of(2019, 8, 20));
        assertThat(mergedTimeline.get(5).getFrom()).isEqualTo(LocalDate.of(2019, 8, 25));
        assertThat(mergedTimeline.get(5).getTo()).isEqualTo(LocalDate.of(2020, 8, 20));
    }

    @Test
    void shouldCreateEmptyListWhenKilMasterTimelinesWithNull() {
        Timeline timeline = new Timeline(null, ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        List<DateRange> mergedTimeline = timeline.getKilometerMasterTimelineElements();
        assertThat(mergedTimeline).hasSize(0);
    }

    @Test
    void shouldCreateEmptyListWhenKilMasterTimelinesWithEmptyList() {
        List<ServicePointVersion> servicePointVersionList = new ArrayList<>();

        Timeline timeline = new Timeline(servicePointVersionList, ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        List<DateRange> mergedTimeline = timeline.getKilometerMasterTimelineElements();
        assertThat(mergedTimeline).hasSize(0);
    }

    @Test
    void shouldCreateKilMasterTimelineElementsWhenGapsBetweenKilMasterNumbers() {
        List<ServicePointVersion> servicePointVersionList = new ArrayList<>();
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel3());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel5());

        Timeline timeline = new Timeline(servicePointVersionList, ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        List<DateRange> mergedTimeline = timeline.getKilometerMasterTimelineElements();
        assertThat(mergedTimeline).hasSize(3);
        assertThat(mergedTimeline.get(0).getFrom()).isEqualTo(LocalDate.of(2010, 12, 11));
        assertThat(mergedTimeline.get(0).getTo()).isEqualTo(LocalDate.of(2011, 8, 10));
        assertThat(mergedTimeline.get(1).getFrom()).isEqualTo(LocalDate.of(2012, 8, 11));
        assertThat(mergedTimeline.get(1).getTo()).isEqualTo(LocalDate.of(2013, 8, 10));
        assertThat(mergedTimeline.get(2).getFrom()).isEqualTo(LocalDate.of(2014, 8, 18));
        assertThat(mergedTimeline.get(2).getTo()).isEqualTo(LocalDate.of(2015, 8, 18));
    }

    @Test
    void shouldCreateKilMasterTimelinesWhenGapAfterFirstKilMasterNumber() {
        List<ServicePointVersion> servicePointVersionList = new ArrayList<>();
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel0());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel2());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel3());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel4());

        Timeline timeline = new Timeline(servicePointVersionList, ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        List<DateRange> mergedTimeline = timeline.getKilometerMasterTimelineElements();
        assertThat(mergedTimeline).hasSize(3);
        assertThat(mergedTimeline.get(0).getFrom()).isEqualTo(LocalDate.of(2008, 12, 11));
        assertThat(mergedTimeline.get(0).getTo()).isEqualTo(LocalDate.of(2009, 8, 10));
        assertThat(mergedTimeline.get(1).getFrom()).isEqualTo(LocalDate.of(2010, 12, 11));
        assertThat(mergedTimeline.get(1).getTo()).isEqualTo(LocalDate.of(2013, 8, 10));
        assertThat(mergedTimeline.get(2).getFrom()).isEqualTo(LocalDate.of(2013, 8, 14));
        assertThat(mergedTimeline.get(2).getTo()).isEqualTo(LocalDate.of(2014, 8, 14));
    }

    @Test
    void shouldCreateKilMasterTimelinesWhenGapsBetweenKilMasterNumbers() {
        List<ServicePointVersion> servicePointVersionList = new ArrayList<>();
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel2());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel3());

        Timeline timeline = new Timeline(servicePointVersionList, ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        List<DateRange> mergedTimeline = timeline.getKilometerMasterTimelineElements();
        assertThat(mergedTimeline).hasSize(1);
        assertThat(mergedTimeline.get(0).getFrom()).isEqualTo(LocalDate.of(2010, 12, 11));
        assertThat(mergedTimeline.get(0).getTo()).isEqualTo(LocalDate.of(2013, 8, 10));
    }

}