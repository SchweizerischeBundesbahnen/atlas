package ch.sbb.workflow.model.search;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.DecisionType;
import ch.sbb.workflow.entity.JudgementType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public class NoDecisionSpecification implements Specification<StopPointWorkflow> {

//  @Override
//  public Predicate toPredicate(Root<StopPointWorkflow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//    // Join StopPointWorkflow with Person
//    Join<StopPointWorkflow, Person> personJoin = root.join("examinants", JoinType.INNER);
//
//    // Join Person with Decision through the examinant field in Decision
//    Join<Person, Decision> decisionJoin = personJoin.join("examinant", JoinType.INNER);
//
//    // Conditions
//    Predicate statusCondition = cb.equal(root.get("status"), WorkflowStatus.HEARING);
//    Predicate judgementCondition = cb.equal(decisionJoin.get("judgement"), JudgementType.NO);
//    Predicate decisionTypeCondition = cb.equal(decisionJoin.get("decisionType"), DecisionType.VOTED);
//    Predicate fotJudgementCondition = cb.or(
//        cb.isNull(decisionJoin.get("fotJudgement")),
//        cb.notEqual(decisionJoin.get("fotJudgement"), JudgementType.YES)
//    );
//
//    // Combine conditions
//    return cb.and(statusCondition, judgementCondition, decisionTypeCondition, fotJudgementCondition);
//  }

  @Override
  public Predicate toPredicate(Root<StopPointWorkflow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
    // Create a subquery for Decision
    Subquery<Long> decisionSubquery = query.subquery(Long.class);
    Root<Decision> decisionRoot = decisionSubquery.from(Decision.class);

    // Join Decision with Person
    Join<Decision, Person> personJoin = decisionRoot.join("examinant", JoinType.INNER);

    // Join Person with StopPointWorkflow
    Join<Person, StopPointWorkflow> workflowJoin = personJoin.join("stopPointWorkflow", JoinType.INNER);

    // Select the workflow IDs that meet the criteria
    decisionSubquery.select(workflowJoin.get("id"))
        .where(cb.and(
            cb.equal(workflowJoin.get("status"), WorkflowStatus.HEARING),
            cb.equal(decisionRoot.get("judgement"), JudgementType.NO),
            cb.equal(decisionRoot.get("decisionType"), DecisionType.VOTED),
            cb.or(
                cb.isNull(decisionRoot.get("fotJudgement")),
                cb.notEqual(decisionRoot.get("fotJudgement"), JudgementType.YES)
            )
        ));

    // The main query selects workflows whose IDs are in the subquery
    return cb.in(root.get("id")).value(decisionSubquery);
  }


}
