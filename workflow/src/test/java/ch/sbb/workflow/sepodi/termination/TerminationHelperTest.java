package ch.sbb.workflow.sepodi.termination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.sepodi.termination.entity.TerminationDecision;
import ch.sbb.workflow.sepodi.termination.entity.TerminationDecisionPerson;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import ch.sbb.workflow.sepodi.termination.model.TerminationInfoModel;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TerminationHelperTest {

  @Test
  void shouldGetTerminationDateWhenAllTerminationDatesAreEqual() {
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
  void shouldGetTerminationDateWhenNovaHasDifferentTerminationDate() {
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
  void shouldGetTerminationDateWhenAllTerminationDatesAreNotEqual() {
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

  @Test
  void shouldGetInfoPlusTerminationDateWhenInfoPlusAndNovaVoted() {
    //given
    TerminationStopPointWorkflow workflow = buildWorkflow();
    TerminationDecision infoPlusDecision = TerminationDecision.builder()
        .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
        .judgement(JudgementType.YES)
        .build();
    workflow.setInfoPlusDecision(infoPlusDecision);
    TerminationDecision novaDecision = TerminationDecision.builder()
        .terminationDecisionPerson(TerminationDecisionPerson.NOVA)
        .judgement(JudgementType.YES)
        .build();
    workflow.setNovaDecision(novaDecision);
    workflow.setBoTerminationDate(LocalDate.of(2000, 1, 1));
    LocalDate infoPlusTerminationDate = LocalDate.of(2001, 1, 1);
    workflow.setInfoPlusTerminationDate(infoPlusTerminationDate);
    LocalDate novaTerminationDate = LocalDate.of(2002, 1, 1);
    workflow.setNovaTerminationDate(novaTerminationDate);

    //when
    TerminationInfoModel result = TerminationHelper.calculateTerminationDate(workflow);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getTerminationDate()).isNotNull().isEqualTo(workflow.getInfoPlusTerminationDate());
  }

  @Test
  void shouldReturnBoTerminationDateWhenNotOneVoted() {
    //given
    TerminationStopPointWorkflow workflow = buildWorkflow();
    //when
    TerminationInfoModel result = TerminationHelper.calculateTerminationDate(workflow);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getTerminationDate()).isNotNull().isEqualTo(workflow.getBoTerminationDate());
  }

  @Test
  void shouldThrowExceptionWhenInfoPlusVotedNo() {
    //given
    TerminationStopPointWorkflow workflow = buildWorkflow();
    TerminationDecision infoPlusDecision = TerminationDecision.builder()
        .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
        .judgement(JudgementType.NO)
        .build();
    workflow.setInfoPlusDecision(infoPlusDecision);
    //when & then
    assertThrows(IllegalStateException.class, () -> TerminationHelper.calculateTerminationDate(workflow));
  }

  @Test
  void shouldReturnInfoPlusTerminationDateWhenInfoPlusVotedYes() {
    //given
    TerminationStopPointWorkflow workflow = buildWorkflow();
    TerminationDecision infoPlusDecision = TerminationDecision.builder()
        .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
        .judgement(JudgementType.YES)
        .build();
    workflow.setInfoPlusDecision(infoPlusDecision);
    //when
    TerminationInfoModel result = TerminationHelper.calculateTerminationDate(workflow);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getTerminationDate()).isNotNull().isEqualTo(workflow.getInfoPlusTerminationDate());
  }

  @Test
  void shouldReturnInfoPlusTerminationDateWhenNovaVotedYes() {
    //given
    TerminationStopPointWorkflow workflow = buildWorkflow();
    TerminationDecision infoPlusDecision = TerminationDecision.builder()
        .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
        .judgement(JudgementType.YES)
        .build();
    workflow.setInfoPlusDecision(infoPlusDecision);
    TerminationDecision novaDecision = TerminationDecision.builder()
        .terminationDecisionPerson(TerminationDecisionPerson.NOVA)
        .judgement(JudgementType.YES)
        .build();
    workflow.setNovaDecision(novaDecision);
    //when
    TerminationInfoModel result = TerminationHelper.calculateTerminationDate(workflow);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getTerminationDate()).isNotNull().isEqualTo(workflow.getInfoPlusTerminationDate());
  }

  @Test
  void shouldReturnInfoPlusTerminationDateWhenNovaVotedNo() {
    //given
    TerminationStopPointWorkflow workflow = buildWorkflow();
    TerminationDecision infoPlusDecision = TerminationDecision.builder()
        .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
        .judgement(JudgementType.YES)
        .build();
    workflow.setInfoPlusDecision(infoPlusDecision);
    TerminationDecision novaDecision = TerminationDecision.builder()
        .terminationDecisionPerson(TerminationDecisionPerson.NOVA)
        .judgement(JudgementType.NO)
        .build();
    workflow.setNovaDecision(novaDecision);
    //when
    TerminationInfoModel result = TerminationHelper.calculateTerminationDate(workflow);
    //then
    assertThat(result).isNotNull();
    assertThat(result.getTerminationDate()).isNotNull().isEqualTo(workflow.getInfoPlusTerminationDate());
  }

  @Test
  void shouldThrowExceptionWhenInfoPlusAndNovaVotedNo() {
    //given
    TerminationStopPointWorkflow workflow = buildWorkflow();
    TerminationDecision infoPlusDecision = TerminationDecision.builder()
        .terminationDecisionPerson(TerminationDecisionPerson.INFO_PLUS)
        .judgement(JudgementType.NO)
        .build();
    workflow.setInfoPlusDecision(infoPlusDecision);
    TerminationDecision novaDecision = TerminationDecision.builder()
        .terminationDecisionPerson(TerminationDecisionPerson.NOVA)
        .judgement(JudgementType.NO)
        .build();
    workflow.setNovaDecision(novaDecision);
    //when & then
    assertThrows(IllegalStateException.class, () -> TerminationHelper.calculateTerminationDate(workflow));
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