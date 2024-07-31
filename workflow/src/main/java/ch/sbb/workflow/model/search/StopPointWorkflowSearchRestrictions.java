package ch.sbb.workflow.model.search;

import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.workflow.entity.Decision;
import ch.sbb.workflow.entity.Person;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.model.sepodi.StopPointWorkflowRequestParams;
import ch.sbb.workflow.specification.ValidFromAndCreatedAtSpecification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
//@RequiredArgsConstructor
public class StopPointWorkflowSearchRestrictions {

  private final Pageable pageable;
  private final StopPointWorkflowRequestParams stopPointWorkflowRequestParams;
//  private final EntityManager entityManager;

  public Specification<StopPointWorkflow> getSpecification() {
    Specification<StopPointWorkflow> specification =
        specificationBuilder().searchCriteriaSpecification(stopPointWorkflowRequestParams.getSearchCriterias())
        .and(specificationBuilder().inSpecification(stopPointWorkflowRequestParams.getSloids(), StopPointWorkflow.Fields.sloid))
        .and(specificationBuilder().inSpecification(stopPointWorkflowRequestParams.getWorkflowIds(), StopPointWorkflow.Fields.id))
        .and(specificationBuilder().inSpecification(stopPointWorkflowRequestParams.getStatus(), StopPointWorkflow.Fields.status))
        .and(specificationBuilder().inSpecification(stopPointWorkflowRequestParams.getSboids(), StopPointWorkflow.Fields.sboid))
        .and(specificationBuilder().singleStringSpecification(
            Optional.ofNullable(stopPointWorkflowRequestParams.getLocalityName()), StopPointWorkflow.Fields.localityName))
        .and(specificationBuilder().inSpecification(stopPointWorkflowRequestParams.getDesignationOfficial(),
            StopPointWorkflow.Fields.designationOfficial))
        .and(new ValidFromAndCreatedAtSpecification<>(
            stopPointWorkflowRequestParams.getVersionValidFrom(),
            stopPointWorkflowRequestParams.getCreatedAt()
        ));
    if (stopPointWorkflowRequestParams.isFilterByNoDecision()) {
//      specification = specification.and(getDecisionSpecification());
      specification = specification.and(new NoDecisionSpecification());
    }

    return specification;
  }

  private Specification<StopPointWorkflow> getDecisionSpecification() {

    return (root, query, cb) -> {
      query.distinct(true);

      Join<Person, StopPointWorkflow> stopPointWorkflowJoin =
          root.join("id");

      getPersonSpecification();
      Join<Decision, Person> decisionJoin = stopPointWorkflowJoin.join("examinant_id");

      Predicate statusPredicate = cb.equal(root.get("status"), "HEARING");
      Predicate judgementPredicate = cb.equal(decisionJoin.get("judgement"), "NO");
      Predicate decisionTypePredicate = cb.equal(decisionJoin.get("decisionType"), "VOTED");
      Predicate fotJudgementPredicate = cb.or(
          cb.isNull(decisionJoin.get("fotJudgement")),
          cb.notEqual(decisionJoin.get("fotJudgement"), "YES")
      );

      return cb.and(statusPredicate, judgementPredicate, decisionTypePredicate, fotJudgementPredicate);
    };
  }

  private Specification<Person> getPersonSpecification() {
    return (root, query, cb) -> {
      query.distinct(true);
      Join<Person, StopPointWorkflow> stopPointWorkflowJoin = root.join("stop_point_workflow_id");
      return cb.and(stopPointWorkflowJoin.getOn());
    };
  }

  protected SpecificationBuilder<StopPointWorkflow> specificationBuilder() {
    return SpecificationBuilder.<StopPointWorkflow>builder()
        .stringAttributes(
            List.of(StopPointWorkflow.Fields.sloid,
                StopPointWorkflow.Fields.designationOfficial))
        .build();
  }




//  private Specification<StopPointWorkflow> getDecisionSpecification() {
//    //    Session session = HibernateUtil.getHibernateSession();
//    //    CriteriaBuilder cb = session.getCriteriaBuilder();
//    //    CriteriaQuery<StopPointWorkflow> query = cb.createQuery(StopPointWorkflow.class);
//    //    Root<Person> root = query.from(Person.class);
//
//
//    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//    CriteriaQuery<String> query = cb.createQuery(String.class);
//    Root<Person> root = query.from(Person.class);
//
//    //    return (root, query, cb) -> {
//    query.distinct(true);
//
//    //      Join<StopPointWorkflow, Person> personJoin = root.join("examinants");
//    //
//    //      Join<Decision, Person> decisionJoin = personJoin.join("examinant");
//
//    Join<Person, StopPointWorkflow> stopPointWorkflowJoin = root.join("stop_point_workflow_id");
//
//    Join<Decision, Person> decisionJoin = stopPointWorkflowJoin.join("examinant_id");
//
//    Predicate statusPredicate = cb.equal(root.get("status"), "HEARING");
//    Predicate judgementPredicate = cb.equal(decisionJoin.get("judgement"), "NO");
//    Predicate decisionTypePredicate = cb.equal(decisionJoin.get("decisionType"), "VOTED");
//    Predicate fotJudgementPredicate = cb.or(
//        cb.isNull(decisionJoin.get("fotJudgement")),
//        cb.notEqual(decisionJoin.get("fotJudgement"), "YES")
//    );
//
//    return (Specification<StopPointWorkflow>) cb.and(statusPredicate, judgementPredicate, decisionTypePredicate, fotJudgementPredicate);
//    //    };
//  }

}
