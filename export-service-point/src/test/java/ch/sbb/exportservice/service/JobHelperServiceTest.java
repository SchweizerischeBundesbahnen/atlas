package ch.sbb.exportservice.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import ch.sbb.exportservice.utils.JobDescriptionConstants;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.explore.JobExplorer;

public class JobHelperServiceTest {

  @Mock
  private JobExplorer jobExplorer;

  @Mock
  private JobInstance jobInstance;

  @Mock
  private JobParameters jobParameters;

  @Mock
  private JobExecution jobExecution;

  private JobHelperService jobHelperService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    jobHelperService = new JobHelperService(jobExplorer);
  }

  @Test
  public void shouldReturnMinDateWhenNoJobExecutionWasFound() {
    //when
    LocalDate result = jobHelperService.getDateForImportFileToDownload("myJob");
    //then
    assertThat(result).isEqualTo(JobHelperService.MIN_LOCAL_DATE);
  }

  @Test
  public void shouldReturnDateWhenJobExecutionWasFound() {
    //given
    Map<String, JobParameter<?>> parameters = new HashMap<>();
    parameters.put(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
    when(jobExplorer.findJobInstancesByJobName(any(), anyInt(), anyInt())).thenReturn(List.of(jobInstance));
    when(jobExplorer.getLastJobExecution(jobInstance)).thenReturn(jobExecution);
    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
    when(jobParameters.getParameters()).thenReturn(parameters);
    when(jobExecution.getStatus()).thenReturn(BatchStatus.COMPLETED);

    LocalDate successfullyJobExecutionLocalDate = LocalDate.of(2000, 1, 1);

    when(jobExecution.getCreateTime()).thenReturn(successfullyJobExecutionLocalDate.atStartOfDay());
    //when
    LocalDate result = jobHelperService.getDateForImportFileToDownload("myJob");
    //then
    assertThat(result).isEqualTo(successfullyJobExecutionLocalDate);
  }

  @Test
  public void shouldReturnTrueWhenMatchedDateIsBetweenTodayAndMatchingDate() {
    //given
    LocalDate matchingDate = LocalDate.now();
    LocalDate lastEditionDate = LocalDate.now();
    //when
    boolean result = jobHelperService.isDateMatchedBetweenTodayAndMatchingDate(matchingDate,
        lastEditionDate);

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnTrueWhenLastEditionDateIsBetweenTodayAndMatchingDate() {
    //given
    LocalDate now = LocalDate.now();
    LocalDate matchingDate = now.minusDays(2);
    LocalDate lastEditionDate = now.minusDays(1);
    //when
    boolean result = jobHelperService.isDateMatchedBetweenTodayAndMatchingDate(matchingDate,
        lastEditionDate);

    //then
    assertThat(result).isTrue();
  }

  @Test
  public void shouldReturnFalseWhenLastEditionDateIsNotBetweenTodayAndMatchingDate() {
    //given
    LocalDate now = LocalDate.now();
    LocalDate lastEditionDate = now.minusDays(1);
    //when
    boolean result = jobHelperService.isDateMatchedBetweenTodayAndMatchingDate(now,
        lastEditionDate);

    //then
    assertThat(result).isFalse();
  }

}
