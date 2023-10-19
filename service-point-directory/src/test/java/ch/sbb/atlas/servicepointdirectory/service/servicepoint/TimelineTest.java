package ch.sbb.atlas.servicepointdirectory.service.servicepoint;


import ch.sbb.atlas.servicepointdirectory.ServicePointVersionsTimelineTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.util.Assert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TimelineTest {

    @Test
    void shouldFailWhenMissingServicePoint() {
        var exception = Assertions.assertThrows(IllegalStateException.class, () -> new Timeline(null, null));
        assertThat(exception.getMessage(), is(equalTo("ServicePointVersion is required to instantiate Timeline.")));
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
        List<Timeline.TimelineElement> mergedTimeline = timeline.getKilometerMasterTimelineElements();
        Assert.equals(6, mergedTimeline.size());
        Assert.equals(LocalDate.of(2010, 12, 11), mergedTimeline.get(0).getStartDate());
        Assert.equals(LocalDate.of(2013, 8, 10), mergedTimeline.get(0).getEndDate());
        Assert.equals(LocalDate.of(2013, 8, 14), mergedTimeline.get(1).getStartDate());
        Assert.equals(LocalDate.of(2014, 8, 14), mergedTimeline.get(1).getEndDate());
        Assert.equals(LocalDate.of(2014, 8, 18), mergedTimeline.get(2).getStartDate());
        Assert.equals(LocalDate.of(2015, 8, 18), mergedTimeline.get(2).getEndDate());
        Assert.equals(LocalDate.of(2015, 8, 20), mergedTimeline.get(3).getStartDate());
        Assert.equals(LocalDate.of(2018, 8, 20), mergedTimeline.get(3).getEndDate());
        Assert.equals(LocalDate.of(2018, 8, 25), mergedTimeline.get(4).getStartDate());
        Assert.equals(LocalDate.of(2019, 8, 20), mergedTimeline.get(4).getEndDate());
        Assert.equals(LocalDate.of(2019, 8, 25), mergedTimeline.get(5).getStartDate());
        Assert.equals(LocalDate.of(2020, 8, 20), mergedTimeline.get(5).getEndDate());
    }

    @Test
    void shouldCreateEmptyListWhenKilMasterTimelinesWithNull() {
        Timeline timeline = new Timeline(null, ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        List<Timeline.TimelineElement> mergedTimeline = timeline.getKilometerMasterTimelineElements();
        Assert.equals(0, mergedTimeline.size());
    }

    @Test
    void shouldCreateEmptyListWhenKilMasterTimelinesWithEmptyList() {
        List<ServicePointVersion> servicePointVersionList = new ArrayList<>();

        Timeline timeline = new Timeline(servicePointVersionList, ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        List<Timeline.TimelineElement> mergedTimeline = timeline.getKilometerMasterTimelineElements();
        Assert.equals(0, mergedTimeline.size());
    }

    @Test
    void shouldCreateKilMasterTimelineElementsWhenGapsBetweenKilMasterNumbers() {
        List<ServicePointVersion> servicePointVersionList = new ArrayList<>();
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel3());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel5());

        Timeline timeline = new Timeline(servicePointVersionList, ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        List<Timeline.TimelineElement> mergedTimeline = timeline.getKilometerMasterTimelineElements();
        Assert.equals(3, mergedTimeline.size());
        Assert.equals(LocalDate.of(2010, 12, 11), mergedTimeline.get(0).getStartDate());
        Assert.equals(LocalDate.of(2011, 8, 10), mergedTimeline.get(0).getEndDate());
        Assert.equals(LocalDate.of(2012, 8, 11), mergedTimeline.get(1).getStartDate());
        Assert.equals(LocalDate.of(2013, 8, 10), mergedTimeline.get(1).getEndDate());
        Assert.equals(LocalDate.of(2014, 8, 18), mergedTimeline.get(2).getStartDate());
        Assert.equals(LocalDate.of(2015, 8, 18), mergedTimeline.get(2).getEndDate());
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
        List<Timeline.TimelineElement> mergedTimeline = timeline.getKilometerMasterTimelineElements();
        Assert.equals(3, mergedTimeline.size());
        Assert.equals(LocalDate.of(2008, 12, 11), mergedTimeline.get(0).getStartDate());
        Assert.equals(LocalDate.of(2009, 8, 10), mergedTimeline.get(0).getEndDate());
        Assert.equals(LocalDate.of(2010, 12, 11), mergedTimeline.get(1).getStartDate());
        Assert.equals(LocalDate.of(2013, 8, 10), mergedTimeline.get(1).getEndDate());
        Assert.equals(LocalDate.of(2013, 8, 14), mergedTimeline.get(2).getStartDate());
        Assert.equals(LocalDate.of(2014, 8, 14), mergedTimeline.get(2).getEndDate());
    }

    @Test
    void shouldCreateKilMasterTimelinesWhenGapsBetweenKilMasterNumbers() {
        List<ServicePointVersion> servicePointVersionList = new ArrayList<>();
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel2());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel3());

        Timeline timeline = new Timeline(servicePointVersionList, ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        List<Timeline.TimelineElement> mergedTimeline = timeline.getKilometerMasterTimelineElements();
        Assert.equals(1, mergedTimeline.size());
        Assert.equals(LocalDate.of(2010, 12, 11), mergedTimeline.get(0).getStartDate());
        Assert.equals(LocalDate.of(2013, 8, 10), mergedTimeline.get(0).getEndDate());
    }

}