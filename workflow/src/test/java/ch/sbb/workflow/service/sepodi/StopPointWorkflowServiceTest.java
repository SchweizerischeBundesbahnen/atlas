package ch.sbb.workflow.service.sepodi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.exception.StopPointWorkflowStatusMustBeAddedException;
import ch.sbb.workflow.mapper.StopPointClientPersonMapper;
import ch.sbb.workflow.model.search.StopPointWorkflowSearchRestrictions;
import ch.sbb.workflow.model.sepodi.EditStopPointWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointWorkflowRequestParams;
import ch.sbb.workflow.repository.StopPointWorkflowRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

@IntegrationTest
class StopPointWorkflowServiceTest {

  @Autowired
  private StopPointWorkflowRepository workflowRepository;

  @Autowired
  private StopPointWorkflowService workflowService;

  @BeforeEach
  void setUp() {
    StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
        .sloid("ch:1:sloid:8000")
        .sboid("ch:1:sboid:10")
        .status(WorkflowStatus.ADDED)
        .designationOfficial("Heimsiswil Zentrum")
        .versionId(1L)
        .localityName("Heimiswil")
        .build();
    workflowRepository.save(stopPointWorkflow);
  }

  @AfterEach
  void tearDown() {
    workflowRepository.deleteAll();
  }

  @Test
  void shouldFindWorkflowByLocalityCaseInsensitive() {
    List<StopPointWorkflow> searchResult = workflowService.getWorkflows(
        StopPointWorkflowSearchRestrictions.builder().pageable(Pageable.unpaged()).stopPointWorkflowRequestParams(
            StopPointWorkflowRequestParams.builder()
                .localityName("heimiswil")
                .build()).build()).getContent();

    assertThat(searchResult).hasSize(1);
  }

  @Test
  void shouldNotFindWorkflowByLocalityCaseInsensitive() {
    List<StopPointWorkflow> searchResult = workflowService.getWorkflows(
        StopPointWorkflowSearchRestrictions.builder().pageable(Pageable.unpaged()).stopPointWorkflowRequestParams(
            StopPointWorkflowRequestParams.builder()
                .localityName("notexisting")
                .build()).build()).getContent();

    assertThat(searchResult).isEmpty();
  }
  @Test
  void testEditWorkflow_Success() {
    StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
            .sloid("ch:1:sloid:8000")
            .sboid("ch:1:sboid:10")
            .status(WorkflowStatus.ADDED)
            .designationOfficial("Heimsiswil Zentrum")
            .versionId(1L)
            .localityName("Heimiswil")
            .build();

    StopPointWorkflow saved = workflowRepository.save(stopPointWorkflow);
    Long id = saved.getId();

    EditStopPointWorkflowModel workflowModel = EditStopPointWorkflowModel.builder()
            .workflowComment("New Comment")
            .designationOfficial("Heimsiswil Zentrum")
            .build();

    workflowService.editWorkflow(id, workflowModel);

    StopPointWorkflow foundWorkflow = workflowRepository.findById(id).get();

    assertEquals("Heimsiswil Zentrum", foundWorkflow.getDesignationOfficial());
    assertEquals("New Comment", foundWorkflow.getWorkflowComment());
  }

  @Test
  void testEditWorkflow_StopPointWorkflowStatusMustBeAddedException() {
    StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
            .sloid("ch:1:sloid:8000")
            .sboid("ch:1:sboid:10")
            .status(WorkflowStatus.APPROVED)
            .designationOfficial("Heimsiswil Zentrum")
            .versionId(1L)
            .localityName("Heimiswil")
            .build();

    StopPointWorkflow saved = workflowRepository.save(stopPointWorkflow);
    Long id = saved.getId();

    EditStopPointWorkflowModel workflowModel = EditStopPointWorkflowModel.builder()
            .workflowComment("New Comment")
            .designationOfficial("Heimsiswil Zentrum")
            .build();

    assertThrows(StopPointWorkflowStatusMustBeAddedException.class, () -> {
      workflowService.editWorkflow(id, workflowModel);
    });
  }

  @Test
  void testEditWorkflow_UpdateExaminants() {
    Person person = Person.builder()
            .firstName("Marek")
            .lastName("Hamsik")
            .function("Centrocampista")
            .mail("test@test.com").build();

    StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
            .sloid("ch:1:sloid:8000")
            .sboid("ch:1:sboid:10")
            .status(WorkflowStatus.ADDED)
            .designationOfficial("Heimsiswil Zentrum")
            .versionId(1L)
            .localityName("Heimiswil")
            .examinants(Set.of(person))
            .build();

    StopPointWorkflow saved = workflowRepository.save(stopPointWorkflow);

    Long id = saved.getId();
    Long examinantId = saved.getExaminants().stream().findFirst().get().getId();

    Person personEdited = Person.builder()
            .id(examinantId)
            .firstName("Neue Person")
            .lastName("Person")
            .function("SchönesWetterHeute")
            .mail("test@test.com").build();

    EditStopPointWorkflowModel workflowModel = EditStopPointWorkflowModel.builder()
            .workflowComment("New Comment")
            .designationOfficial("Heimsiswil Zentrum")
            .examinants(List.of(personEdited).stream().map(StopPointClientPersonMapper::toModel).toList())
            .build();

    workflowService.editWorkflow(id, workflowModel);

    StopPointWorkflow stopPointWorkflowInDb = workflowRepository.findById(id).get();


    assertFalse(stopPointWorkflowInDb.getExaminants().isEmpty());
    assertThat(stopPointWorkflowInDb.getExaminants()).hasSize(1);
    assertThat(stopPointWorkflowInDb.getExaminants()).extracting(examinant -> examinant.getId()).contains(examinantId);
  }

  @Test
  void testEditWorkflow_ExistingExaminantsUpdate() {
    Person person = Person.builder()
            .firstName("Marek")
            .lastName("Hamsik")
            .function("Centrocampista")
            .mail("test@test.com").build();

    StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
            .sloid("ch:1:sloid:8000")
            .sboid("ch:1:sboid:10")
            .status(WorkflowStatus.ADDED)
            .designationOfficial("Heimsiswil Zentrum")
            .versionId(1L)
            .examinants(Set.of(person))
            .localityName("Heimiswil")
            .build();

    StopPointWorkflow saved = workflowRepository.save(stopPointWorkflow);
    Long id = saved.getId();

    person.setMail("neueMail@mail.neu");
    List<Person> examinant = new ArrayList<>();
    examinant.add(person);

    EditStopPointWorkflowModel workflowModel = EditStopPointWorkflowModel.builder()
        .workflowComment("test")
        .designationOfficial("Heimsiswil Zentrum")
        .examinants(examinant.stream().map(StopPointClientPersonMapper::toModel).toList())
        .build();

    workflowService.editWorkflow(id, workflowModel);
    StopPointWorkflow stopPointWorkflow1 = workflowRepository.findById(id).get();
    assertFalse(stopPointWorkflow1.getExaminants().isEmpty());
    assertThat(stopPointWorkflow1.getExaminants()).extracting("mail").contains("neueMail@mail.neu");
  }

  @Test
  void shouldNotFindExpiredWorkflowWhenWorkflowEndsIn31Days() {
    //given
    workflowRepository.deleteAll();
    StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
        .sloid("ch:1:sloid:8000")
        .sboid("ch:1:sboid:10")
        .status(WorkflowStatus.HEARING)
        .designationOfficial("Heimsiswil Zentrum")
        .versionId(1L)
        .startDate(LocalDate.now().minusDays(61))
        .endDate(LocalDate.now().minusDays(31))
        .localityName("Heimiswil")
        .build();
    workflowRepository.saveAndFlush(stopPointWorkflow);

    //when
    List<StopPointWorkflow> result = workflowService.findExpiredWorkflow();

    //then
    assertThat(result).isEmpty();
  }

  @Test
  void shouldFindExpiredWorkflowWhenWorkflowEndsAfter31Days() {
    //given
    workflowRepository.deleteAll();
    StopPointWorkflow stopPointWorkflow = StopPointWorkflow.builder()
        .sloid("ch:1:sloid:8000")
        .sboid("ch:1:sboid:10")
        .status(WorkflowStatus.HEARING)
        .designationOfficial("Heimsiswil Zentrum")
        .versionId(1L)
        .startDate(LocalDate.now().minusDays(61))
        .endDate(LocalDate.now().minusDays(32))
        .localityName("Heimiswil")
        .build();
    workflowRepository.saveAndFlush(stopPointWorkflow);

    //when
    List<StopPointWorkflow> result = workflowService.findExpiredWorkflow();

    //then
    assertThat(result).hasSize(1);
  }

}