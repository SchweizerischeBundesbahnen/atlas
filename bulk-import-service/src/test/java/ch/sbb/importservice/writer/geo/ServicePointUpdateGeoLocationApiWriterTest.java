package ch.sbb.importservice.writer.geo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoLocationModel;
import ch.sbb.atlas.api.servicepoint.ServicePointSwissWithGeoLocationModel.Detail;
import ch.sbb.atlas.geoupdate.job.model.GeoUpdateItemResultModel;
import ch.sbb.atlas.imports.ItemProcessResponseStatus;
import ch.sbb.importservice.repository.GeoUpdateProcessItemRepository;
import ch.sbb.importservice.service.geo.ServicePointUpdateGeoLocationService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;

@ExtendWith(MockitoExtension.class)
class ServicePointUpdateGeoLocationApiWriterTest {

  @Mock
  private StepExecution stepExecution;

  @Mock
  private ServicePointUpdateGeoLocationService sePoDiClientService;

  @Mock
  private GeoUpdateProcessItemRepository geoUpdateProcessItemRepository;

  @InjectMocks
  private ServicePointUpdateGeoLocationApiWriter geoApiWriter;

  @Test
  void shouldWrite(){
    //given
    ServicePointSwissWithGeoLocationModel swissWithGeoModel1 = ServicePointSwissWithGeoLocationModel.builder()
        .sloid("ch:1:sloid:7000")
        .details(List.of(
            Detail.builder().id(1000L).validFrom(LocalDate.of(2000, 1, 1)).build(),
            Detail.builder().id(1002L).validFrom(LocalDate.of(2002, 1, 1)).build()
        )).build();
    ServicePointSwissWithGeoLocationModel swissWithGeoModel2 = ServicePointSwissWithGeoLocationModel.builder()
        .sloid("ch:1:sloid:7001")
        .details(List.of(
            Detail.builder().id(1003L).validFrom(LocalDate.of(2001, 1, 1)).build(),
            Detail.builder().id(1004L).validFrom(LocalDate.of(2003, 1, 1)).build()
        )).build();
    List<ServicePointSwissWithGeoLocationModel> servicePointCsvModels = List.of(swissWithGeoModel1, swissWithGeoModel2);
    GeoUpdateItemResultModel successResultModel = GeoUpdateItemResultModel.builder()
        .sloid(swissWithGeoModel1.getSloid())
        .id(swissWithGeoModel1.getDetails().getFirst().getId())
        .status(ItemProcessResponseStatus.SUCCESS)
        .message("Tutto Bene")
        .build();
    doReturn(successResultModel).when(sePoDiClientService).updateServicePointGeoLocation(swissWithGeoModel1.getSloid(),
        swissWithGeoModel1.getDetails().getFirst().getId());
    JobExecution jobExecution = new JobExecution(132L);
    jobExecution.setJobInstance(new JobInstance(123L, "MyJob"));
    when(stepExecution.getJobExecution()).thenReturn(jobExecution);
    //when
    geoApiWriter.doWrite(servicePointCsvModels);
    //then
    verify(sePoDiClientService).updateServicePointGeoLocation(swissWithGeoModel1.getSloid(),swissWithGeoModel1.getDetails().getFirst().getId());
    verify(sePoDiClientService).updateServicePointGeoLocation(swissWithGeoModel1.getSloid(),swissWithGeoModel1.getDetails().getLast().getId());

    verify(sePoDiClientService).updateServicePointGeoLocation(swissWithGeoModel2.getSloid(),
        swissWithGeoModel2.getDetails().getFirst().getId());
    verify(sePoDiClientService).updateServicePointGeoLocation(swissWithGeoModel2.getSloid(),
        swissWithGeoModel2.getDetails().getLast().getId());
    verify(geoUpdateProcessItemRepository, times(1)).saveAndFlush(any());

  }

}