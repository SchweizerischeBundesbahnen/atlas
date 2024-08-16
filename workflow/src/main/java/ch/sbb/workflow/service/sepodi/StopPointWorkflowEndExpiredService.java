package ch.sbb.workflow.service.sepodi;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.kafka.StopPointWorkflowNotificationService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class StopPointWorkflowEndExpiredService {

  private final DecisionService decisionService;
  private final SePoDiClientService sePoDiClientService;
  private final StopPointWorkflowNotificationService notificationService;
  private final StopPointWorkflowService stopPointWorkflowService;

  static final int WORKFLOW_EXPIRATION_IN_DAYS = StopPointWorkflowTransitionService.WORKFLOW_DURATION_IN_DAYS + 1;

  /**
   * Authorization for this method is delegated to ServicePointService#update()
   */
  public void endExpiredWorkflows() {
    log.info("###### Start close expired workflows in Hearing ######");
    List<StopPointWorkflow> expiredWorkflows = getExpiredWorkflows();
    expiredWorkflows.forEach(stopPointWorkflow -> {
      Set<Decision> decisions = new HashSet<>(decisionService.findDecisionByWorkflowId(stopPointWorkflow.getId()));
      if (decisions.stream().noneMatch(Decision::hasWeightedJudgementTypeNo)) {
        log.info("#### Found Expired workflow without JudgmentType.NO to close: [id:{},startDate:{},endDate:{},status:{}]",
            stopPointWorkflow.getId(), stopPointWorkflow.getStartDate(), stopPointWorkflow.getEndDate(),
            stopPointWorkflow.getStatus());
        List<Person> examiants = getExamiantsToVote(stopPointWorkflow, decisions);
        examiants.forEach(stopPointWorkflowService::voteExpiredWorkflowDecision);

        sePoDiClientService.updateStopPointStatusToValidatedAsAdmin(stopPointWorkflow);
        notificationService.sendApprovedStopPointWorkflowMail(stopPointWorkflow);
        stopPointWorkflow.setStatus(WorkflowStatus.APPROVED);
        stopPointWorkflowService.save(stopPointWorkflow);
        log.info("#### Expired workflow without JudgmentType.NO successfully closed: [id:{},startDate:{},endDate:{},status:{}]",
            stopPointWorkflow.getId(), stopPointWorkflow.getStartDate(), stopPointWorkflow.getEndDate(),
            stopPointWorkflow.getStatus());
      }
    });
    log.info("###### End close expired workflows in Hearing! ######");
  }

  private List<StopPointWorkflow> getExpiredWorkflows() {
    List<StopPointWorkflow> workflowsInHearing = stopPointWorkflowService.findWorkflowsInHearing();
    log.info("## Found {} in Hearing...", workflowsInHearing.size());
    List<StopPointWorkflow> expiredWorkflows = workflowsInHearing.stream()
        .filter(stopPointWorkflow -> stopPointWorkflow.getStartDate().plusDays(WORKFLOW_EXPIRATION_IN_DAYS).equals(
            LocalDate.now())).toList();
    log.info("## Found {} workflow(s) expired ...", expiredWorkflows.size());
    return expiredWorkflows;
  }

  private List<Person> getExamiantsToVote(StopPointWorkflow stopPointWorkflow, Set<Decision> decisions) {
    List<Person> examiants = new ArrayList<>(stopPointWorkflow.getExaminants().stream().toList());
    List<Person> examinantVotedPersonIds = new ArrayList<>(decisions.stream().map(Decision::getExaminant).toList());
    List<Person> overriderVotedPersonIds = new ArrayList<>(decisions.stream().map(Decision::getFotOverrider).toList());
    examiants.removeIf(examinantVotedPersonIds::contains);
    examiants.removeIf(overriderVotedPersonIds::contains);
    examiants.forEach(person ->
        log.info("### Found following examinant without vote: id:{}, mail:{}", person.getId(), person.getMail()));
    return examiants;
  }

}
