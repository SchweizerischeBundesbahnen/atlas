package ch.sbb.exportservice.recovery;

public class RecoveryJobsRunnerTest {

  //  private RecoveryJobsRunner recoveryJobsRunner;
  //  @Mock
  //  private JobExplorer jobExplorer;
  //
  //  @Mock
  //  private JobLauncher jobLauncher;
  //
  //  @Mock
  //  private JobRepository jobRepository;
  //
  //  @Mock
  //  private JobInstance jobInstance;
  //
  //  @Mock
  //  private JobParameters jobParameters;
  //
  //  @Mock
  //  private JobExecution jobExecution;
  //
  //  @Mock
  //  private FileService fileService;
  //
  //  @Mock
  //  @Qualifier(JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME)
  //  private Job exportServicePointCsvJob;
  //  @Mock
  //  @Qualifier(JobDescriptionConstants.EXPORT_SERVICE_POINT_JSON_JOB_NAME)
  //  private Job exportServicePointJsonJob;
  //
  //  @BeforeEach
  //  public void setUp() {
  //    MockitoAnnotations.openMocks(this);
  //    recoveryJobsRunner = new RecoveryJobsRunner(jobExplorer, jobLauncher, jobRepository, exportServicePointCsvJob,
  //        exportServicePointJsonJob, fileService);
  //  }
  //
  //  @Test
  //  public void shouldRecoverExportServicePointCsvJob()
  //      throws Exception {
  //    //given
  //    StepExecution stepExecution = new StepExecution("myStep", jobExecution);
  //    stepExecution.setId(132L);
  //    Map<String, JobParameter<?>> parameters = new HashMap<>();
  //    parameters.put(JobDescriptionConstants.EXECUTION_TYPE_PARAMETER, new JobParameter<>("BATCH", String.class));
  //    when(jobParameters.getParameters()).thenReturn(parameters);
  //    when(jobExecution.getStatus()).thenReturn(BatchStatus.STARTING);
  //    when(jobExecution.getJobParameters()).thenReturn(jobParameters);
  //    when(jobExecution.getStepExecutions()).thenReturn(List.of(stepExecution));
  //    when(jobExplorer.getLastJobInstance(JobDescriptionConstants.EXPORT_SERVICE_POINT_CSV_JOB_NAME)).thenReturn(jobInstance);
  //    when(jobExplorer.getLastJobExecution(jobInstance)).thenReturn(jobExecution);
  //    when(jobLauncher.run(any(), any())).thenReturn(jobExecution);
  //    //when
  //    recoveryJobsRunner.run(new DefaultApplicationArguments());
  //    //then
  //    verify(jobLauncher).run(eq(exportServicePointCsvJob), any());
  //    verify(fileService).clearDir();
  //  }
  //
  //  @Test
  //  public void shouldNotRecoverAnyJob() throws Exception {
  //    //when
  //    recoveryJobsRunner.run(new DefaultApplicationArguments());
  //    //then
  //    verify(jobLauncher, never()).run(eq(exportServicePointCsvJob), any());
  //    verify(fileService).clearDir();
  //  }

}