package ch.sbb.importservice.integration;

import static ch.sbb.importservice.utils.JobDescriptionConstants.FULL_PATH_FILENAME_JOB_PARAMETER;
import static ch.sbb.importservice.utils.JobDescriptionConstants.IMPORT_DIDOK_USER_CSV_JOB_NAME;
import static ch.sbb.importservice.utils.JobDescriptionConstants.START_AT_JOB_PARAMETER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.user.administration.CantonPermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.enumeration.UserAccountStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.importservice.client.UserClient;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.MailProducerService;
import ch.sbb.importservice.service.csv.DidokUserCsvService;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

@IntegrationTest
@AutoConfigureMockMvc(addFilters = false)
class ImportDidokUserIntegrationTest {

  @Autowired
  private JobLauncher jobLauncher;

  @Autowired
  @Qualifier(IMPORT_DIDOK_USER_CSV_JOB_NAME)
  private Job importDidokUserCsvJob;

  @MockBean
  private DidokUserCsvService didokUserCsvService;

  @MockBean
  private UserClient userClient;

  @MockBean
  private FileHelperService fileHelperService;

  @MockBean
  private MailProducerService mailProducerService;

  @Test
  void shouldExecuteImportSePoDiJobFromGivenFile() throws Exception {
    // given

    File file = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("SePoDi_Test.csv")).getFile());

    CantonPermissionRestrictionModel permissionRestrictionModel = CantonPermissionRestrictionModel.builder()
        .value(SwissCanton.AARGAU)
        .type(PermissionRestrictionType.CANTON).build();
    PermissionModel permissionModel = PermissionModel.builder()
        .permissionRestrictions(List.of(permissionRestrictionModel))
        .role(ApplicationRole.WRITER)
        .application(ApplicationType.SEPODI)
        .build();
    UserModel u123456 = UserModel.builder()
        .sbbUserId("u123456")
        .permissions(Set.of(permissionModel))
        .accountStatus(UserAccountStatus.ACTIVE)
        .build();

    doNothing().when(mailProducerService).produceMailNotification(any());
    when(userClient.userAlreadyExists(any())).thenReturn(u123456);
    when(userClient.createUser(any())).thenReturn(u123456);
    when(userClient.updateUser(any())).thenReturn(u123456);

    JobParameters jobParameters = new JobParametersBuilder()
        .addString(FULL_PATH_FILENAME_JOB_PARAMETER, file.getAbsolutePath())
        .addString("applicationType", ApplicationType.SEPODI.toString())
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    // when
    JobExecution jobExecution = jobLauncher.run(importDidokUserCsvJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(IMPORT_DIDOK_USER_CSV_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
  }

  @Test
  void shouldExecuteImportPrmJobFromGivenFile() throws Exception {
    // given

    File file = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("PRM_Test.csv")).getFile());

    CantonPermissionRestrictionModel permissionRestrictionModel = CantonPermissionRestrictionModel.builder()
        .value(SwissCanton.AARGAU)
        .type(PermissionRestrictionType.CANTON).build();
    PermissionModel permissionModel = PermissionModel.builder()
        .permissionRestrictions(List.of(permissionRestrictionModel))
        .role(ApplicationRole.WRITER)
        .application(ApplicationType.SEPODI)
        .build();
    UserModel u123456 = UserModel.builder()
        .sbbUserId("u123456")
        .permissions(Set.of(permissionModel))
        .accountStatus(UserAccountStatus.ACTIVE)
        .build();

    doNothing().when(mailProducerService).produceMailNotification(any());
    when(userClient.userAlreadyExists(any())).thenReturn(u123456);
    when(userClient.createUser(any())).thenReturn(u123456);
    when(userClient.updateUser(any())).thenReturn(u123456);

    JobParameters jobParameters = new JobParametersBuilder()
        .addString(FULL_PATH_FILENAME_JOB_PARAMETER, file.getAbsolutePath())
        .addString("applicationType", ApplicationType.PRM.toString())
        .addLong(START_AT_JOB_PARAMETER, System.currentTimeMillis()).toJobParameters();
    // when
    JobExecution jobExecution = jobLauncher.run(importDidokUserCsvJob, jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(actualJobInstance.getJobName()).isEqualTo(IMPORT_DIDOK_USER_CSV_JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());
  }

}
