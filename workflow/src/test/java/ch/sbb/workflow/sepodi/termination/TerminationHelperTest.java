package ch.sbb.workflow.sepodi.termination;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TerminationHelperTest {

  @Test
  void getTerminationDateWhenAllTerminationDatesAreEqual() {
    //given
    TerminationStopPointWorkflow workflow = buildWorkflow();
    LocalDate terminationDate = LocalDate.of(2000, 1, 1);
    workflow.setBoTerminationDate(terminationDate);
    workflow.setInfoPlusTerminationDate(terminationDate);
    workflow.setNovaTerminationDate(terminationDate);
    //when
    LocalDate result = TerminationHelper.getTerminationDate(workflow);

    //then
    assertThat(result).isNotNull().isEqualTo(terminationDate);
  }

  @Test
  void getTerminationDateWhenNovaHasDifferentTerminationDate() {
    //given
    TerminationStopPointWorkflow workflow = buildWorkflow();
    LocalDate terminationDate = LocalDate.of(2000, 1, 1);
    workflow.setBoTerminationDate(terminationDate);
    workflow.setInfoPlusTerminationDate(terminationDate);
    LocalDate novaTerminationDate = LocalDate.of(2001, 1, 1);
    workflow.setNovaTerminationDate(novaTerminationDate);

    //when
    LocalDate result = TerminationHelper.getTerminationDate(workflow);

    //then
    assertThat(result).isNotNull().isEqualTo(novaTerminationDate);
  }

  @Test
  void getTerminationDateWhenAllTerminationDatesAreNotEqual() {
    //given
    TerminationStopPointWorkflow workflow = buildWorkflow();
    workflow.setBoTerminationDate(LocalDate.of(2000, 1, 1));
    workflow.setInfoPlusTerminationDate(LocalDate.of(2001, 1, 1));
    LocalDate novaTerminationDate = LocalDate.of(2002, 1, 1);
    workflow.setNovaTerminationDate(novaTerminationDate);

    //when
    LocalDate result = TerminationHelper.getTerminationDate(workflow);

    //then
    assertThat(result).isNotNull().isEqualTo(novaTerminationDate);
  }

  private static TerminationStopPointWorkflow buildWorkflow() {
    return TerminationStopPointWorkflow.builder()
        .sloid("ch:1:sloid:1")
        .versionId(1234L)
        .boTerminationDate(LocalDate.of(2000, 1, 1))
        .infoPlusTerminationDate(LocalDate.of(2000, 1, 2))
        .novaTerminationDate(LocalDate.of(2000, 1, 3))
        .applicantMail("a@b.com")
        .designationOfficial("Heimsiswil Zentrum")
        .sboid("ch:sboid:1")
        .status(TerminationWorkflowStatus.STARTED)
        .build();
  }

}