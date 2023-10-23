package ch.sbb.atlas.servicepointdirectory.service.servicepoint;


import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.servicepointdirectory.ServicePointVersionsTimelineTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.util.Assert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        Assert.equals(6, mergedTimeline.size());
        Assert.equals(LocalDate.of(2010, 12, 11), mergedTimeline.get(0).getFrom());
        Assert.equals(LocalDate.of(2013, 8, 10), mergedTimeline.get(0).getTo());
        Assert.equals(LocalDate.of(2013, 8, 14), mergedTimeline.get(1).getFrom());
        Assert.equals(LocalDate.of(2014, 8, 14), mergedTimeline.get(1).getTo());
        Assert.equals(LocalDate.of(2014, 8, 18), mergedTimeline.get(2).getFrom());
        Assert.equals(LocalDate.of(2015, 8, 18), mergedTimeline.get(2).getTo());
        Assert.equals(LocalDate.of(2015, 8, 20), mergedTimeline.get(3).getFrom());
        Assert.equals(LocalDate.of(2018, 8, 20), mergedTimeline.get(3).getTo());
        Assert.equals(LocalDate.of(2018, 8, 25), mergedTimeline.get(4).getFrom());
        Assert.equals(LocalDate.of(2019, 8, 20), mergedTimeline.get(4).getTo());
        Assert.equals(LocalDate.of(2019, 8, 25), mergedTimeline.get(5).getFrom());
        Assert.equals(LocalDate.of(2020, 8, 20), mergedTimeline.get(5).getTo());
    }

    @Test
    void shouldCreateEmptyListWhenKilMasterTimelinesWithNull() {
        Timeline timeline = new Timeline(null, ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        List<DateRange> mergedTimeline = timeline.getKilometerMasterTimelineElements();
        Assert.equals(0, mergedTimeline.size());
    }

    @Test
    void shouldCreateEmptyListWhenKilMasterTimelinesWithEmptyList() {
        List<ServicePointVersion> servicePointVersionList = new ArrayList<>();

        Timeline timeline = new Timeline(servicePointVersionList, ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        List<DateRange> mergedTimeline = timeline.getKilometerMasterTimelineElements();
        Assert.equals(0, mergedTimeline.size());
    }

    @Test
    void shouldCreateKilMasterTimelineElementsWhenGapsBetweenKilMasterNumbers() {
        List<ServicePointVersion> servicePointVersionList = new ArrayList<>();
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel3());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel5());

        Timeline timeline = new Timeline(servicePointVersionList, ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        List<DateRange> mergedTimeline = timeline.getKilometerMasterTimelineElements();
        Assert.equals(3, mergedTimeline.size());
        Assert.equals(LocalDate.of(2010, 12, 11), mergedTimeline.get(0).getFrom());
        Assert.equals(LocalDate.of(2011, 8, 10), mergedTimeline.get(0).getTo());
        Assert.equals(LocalDate.of(2012, 8, 11), mergedTimeline.get(1).getFrom());
        Assert.equals(LocalDate.of(2013, 8, 10), mergedTimeline.get(1).getTo());
        Assert.equals(LocalDate.of(2014, 8, 18), mergedTimeline.get(2).getFrom());
        Assert.equals(LocalDate.of(2015, 8, 18), mergedTimeline.get(2).getTo());
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
        Assert.equals(3, mergedTimeline.size());
        Assert.equals(LocalDate.of(2008, 12, 11), mergedTimeline.get(0).getFrom());
        Assert.equals(LocalDate.of(2009, 8, 10), mergedTimeline.get(0).getTo());
        Assert.equals(LocalDate.of(2010, 12, 11), mergedTimeline.get(1).getFrom());
        Assert.equals(LocalDate.of(2013, 8, 10), mergedTimeline.get(1).getTo());
        Assert.equals(LocalDate.of(2013, 8, 14), mergedTimeline.get(2).getFrom());
        Assert.equals(LocalDate.of(2014, 8, 14), mergedTimeline.get(2).getTo());
    }

    @Test
    void shouldCreateKilMasterTimelinesWhenGapsBetweenKilMasterNumbers() {
        List<ServicePointVersion> servicePointVersionList = new ArrayList<>();
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel2());
        servicePointVersionList.add(ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel3());

        Timeline timeline = new Timeline(servicePointVersionList, ServicePointVersionsTimelineTestData.getAargauServicePointVersionModel1());
        List<DateRange> mergedTimeline = timeline.getKilometerMasterTimelineElements();
        Assert.equals(1, mergedTimeline.size());
        Assert.equals(LocalDate.of(2010, 12, 11), mergedTimeline.get(0).getFrom());
        Assert.equals(LocalDate.of(2013, 8, 10), mergedTimeline.get(0).getTo());
    }

}