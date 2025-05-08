package ch.sbb.workflow.sepodi.hearing.model.search;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.workflow.sepodi.hearing.enity.Decision;
import ch.sbb.workflow.sepodi.hearing.enity.DecisionType;
import ch.sbb.workflow.sepodi.hearing.enity.JudgementType;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.sepodi.hearing.enity.StopPointWorkflow;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.io.Serial;
import org.springframework.data.jpa.domain.Specification;

public class NoDecisionSpecification implements Specification<StopPointWorkflow> {

  @Serial
  private static final long serialVersionUID = 1;

  @Override
  public Predicate toPredicate(Root<StopPointWorkflow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
    Subquery<Long> decisionSubquery = query.subquery(Long.class);
    Root<Decision> decisionRoot = decisionSubquery.from(Decision.class);

    Join<Decision, Person> personJoin = decisionRoot.join("examinant", JoinType.INNER);

    Join<Person, StopPointWorkflow> workflowJoin = personJoin.join("stopPointWorkflow", JoinType.INNER);

    decisionSubquery.select(workflowJoin.get("id"))
        .where(cb.and(
            cb.equal(workflowJoin.get("status"), WorkflowStatus.HEARING),
            cb.equal(decisionRoot.get("judgement"), JudgementType.NO),
            cb.equal(decisionRoot.get("decisionType"), DecisionType.VOTED),
            cb.isNull(decisionRoot.get("fotJudgement"))
        ));

    return cb.in(root.get("id")).value(decisionSubquery);
  }

}
