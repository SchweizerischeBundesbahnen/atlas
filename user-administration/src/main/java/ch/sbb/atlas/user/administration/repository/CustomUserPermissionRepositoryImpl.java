package ch.sbb.atlas.user.administration.repository;

import ch.sbb.atlas.api.user.administration.enumeration.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.searching.specification.EnumSpecification;
import ch.sbb.atlas.user.administration.entity.BasePermission;
import ch.sbb.atlas.user.administration.entity.PermissionRestriction;
import ch.sbb.atlas.user.administration.entity.PermissionRestriction_;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import ch.sbb.atlas.user.administration.entity.UserPermission_;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.SetJoin;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class CustomUserPermissionRepositoryImpl implements CustomUserPermissionRepository {

  private final EntityManager entityManager;

  @Override
  public Page<String> getFilteredUsers(Pageable pageable, Set<ApplicationType> applicationTypes,
      Set<String> permissionRestrictions, PermissionRestrictionType type) {
    EnumSpecification<BasePermission> applicationTypesSpec = new EnumSpecification<>(applicationTypes.stream().toList(),
        UserPermission_.application);
    EnumSpecification<BasePermission> applicationRoleSpec = new EnumSpecification<>(List.of(ApplicationRole.READER),
        UserPermission_.role, true);
    Specification<BasePermission> specification = applicationTypesSpec.and(applicationRoleSpec);

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<String> query = criteriaBuilder.createQuery(String.class);
    Root<BasePermission> baseRoot = query.from(BasePermission.class);
    Root<UserPermission> root = query.from(UserPermission.class);

    Predicate permissionRestrictionPredicate = getPermissionRestrictionPredicate(permissionRestrictions, type,
        criteriaBuilder, root);

    Predicate restriction = specification.toPredicate(baseRoot, query, criteriaBuilder);
    query.where(criteriaBuilder.and(restriction, permissionRestrictionPredicate));
    query.groupBy(root.get(UserPermission_.sbbUserId));
    Expression<Long> count = criteriaBuilder.count(root.get(UserPermission_.sbbUserId));
    query.having(criteriaBuilder.greaterThanOrEqualTo(count, Long.valueOf(applicationTypes.size())));

    CriteriaQuery<String> select = query.select(root.get(UserPermission_.sbbUserId));
    TypedQuery<String> typedQuery = entityManager.createQuery(select);

    long totalElements = typedQuery.getResultStream().count();
    List<String> pagedElements = typedQuery.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize())
        .getResultList();
    return new PageImpl<>(pagedElements, pageable, totalElements);
  }

  private static Predicate getPermissionRestrictionPredicate(Set<String> permissionRestrictions, PermissionRestrictionType type,
      CriteriaBuilder criteriaBuilder, Root<UserPermission> root) {
      List<Predicate> predicates = new ArrayList<>();
      for (String permissionRestriction : permissionRestrictions) {
        SetJoin<UserPermission, PermissionRestriction> permissionJoin = root.join(UserPermission_.permissionRestrictions);
        predicates.add(criteriaBuilder.equal(permissionJoin.get(PermissionRestriction_.type), type));
        predicates.add(permissionJoin.get(PermissionRestriction_.restriction).in(permissionRestriction));
      }
      return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
  }

}
