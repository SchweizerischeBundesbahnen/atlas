package ch.sbb.workflow.service.sepodi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.mapper.StopPointClientPersonMapper;
import ch.sbb.workflow.model.search.StopPointWorkflowSearchRestrictions;
import ch.sbb.workflow.model.sepodi.EditStopPointWorkflowModel;
import ch.sbb.workflow.model.sepodi.StopPointWorkflowRequestParams;
import ch.sbb.workflow.repository.StopPointWorkflowRepository;

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
    Long id = 4581L;
    EditStopPointWorkflowModel workflowModel = EditStopPointWorkflowModel.builder()
            .workflowComment("New Comment")
            .designationOfficial("New Official")
            .build();

    workflowService.editWorkflow(id, workflowModel);

    StopPointWorkflow foundWorkflow = workflowRepository.findById(id).get();

    assertEquals("New Official", foundWorkflow.getDesignationOfficial());
    assertEquals("New Comment", foundWorkflow.getWorkflowComment());
  }

  @Test
  void testEditWorkflow_ThrowsIllegalStateException() {
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
            .designationOfficial("New Official")
            .build();

    assertThrows(IllegalStateException.class, () -> {
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

    List<Person> examinant = new ArrayList<>();
    examinant.add(person);

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
            .designationOfficial("New Official")
            .examinants(examinant.stream().map(StopPointClientPersonMapper::toModel).toList())
            .build();

    workflowService.editWorkflow(id, workflowModel);

    StopPointWorkflow stopPointWorkflowInDb = workflowRepository.findById(id).get();


    assertFalse(stopPointWorkflowInDb.getExaminants().isEmpty());
    assertThat(stopPointWorkflowInDb.getExaminants()).hasSize(1);
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
            .designationOfficial("test")
            .examinants(examinant.stream().map(StopPointClientPersonMapper::toModel).toList())
            .build();

    workflowService.editWorkflow(id, workflowModel);
    StopPointWorkflow stopPointWorkflow1 = workflowRepository.findById(id).get();
    assertFalse(stopPointWorkflow1.getExaminants().isEmpty());
    assertThat(stopPointWorkflow1.getExaminants()).extracting("mail").contains("neueMail@mail.neu");
  }
}