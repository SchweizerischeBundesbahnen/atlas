package ch.sbb.workflow.sepodi.termination.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import ch.sbb.workflow.sepodi.termination.repository.TerminationStopPointWorkflowRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TerminationStopPointWorkflowApiTest extends BaseControllerApiTest {

  private final TerminationStopPointWorkflowRepository repository;

  @Autowired
  TerminationStopPointWorkflowApiTest(TerminationStopPointWorkflowRepository repository) {
    this.repository = repository;
  }

  @Test
  void shouldReturnFilteredSortedPagedListOfWorkflows() throws Exception {
    // given
    TerminationStopPointWorkflow workflowOne = TerminationStopPointWorkflow.builder()
        .sboid("ch:1:sboid:1")
        .versionId(50L)
        .sloid("ch:1:sloid:1")
        .boTerminationDate(LocalDate.of(2000, 1, 1))
        .infoPlusTerminationDate(LocalDate.of(2000, 1, 2))
        .novaTerminationDate(LocalDate.of(2000, 1, 3))
        .designationOfficial("Bern")
        .status(TerminationWorkflowStatus.STARTED)
        .build();
    TerminationStopPointWorkflow workflowTwo = TerminationStopPointWorkflow.builder()
        .sboid("ch:1:sboid:2")
        .versionId(55L)
        .sloid("ch:1:sloid:2")
        .boTerminationDate(LocalDate.of(2000, 1, 1))
        .infoPlusTerminationDate(LocalDate.of(2000, 1, 2))
        .novaTerminationDate(LocalDate.of(2000, 1, 3))
        .designationOfficial("Züri")
        .status(TerminationWorkflowStatus.TERMINATION_APPROVED)
        .build();

    repository.save(workflowOne);
    repository.save(workflowTwo);

    // when
    mvc.perform(get("/internal/termination-stop-point/workflows"
            + "?searchCriterias=bern"
            + "&searchCriterias=ch:1:sloid:1"
            //+ "&workflowIds=50" // todo: check if it works this way, else get dynamic from save operation
            + "&status=TERMINATION_APPROVED"
            + "&status=STARTED"
            + "&sboids=ch:1:sboid:1"
            + "&sboids=ch:1:sboid:100157"
            + "&page=0&size=10&sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)))
        .andExpect(jsonPath("$.objects", hasSize(1)));

    // then
  }
}
