package ch.sbb.atlas.user.administration.repository;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.searching.specification.EnumSpecification;
import ch.sbb.atlas.searching.specification.IsMemberSpecification;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import ch.sbb.atlas.user.administration.entity.UserPermission_;
import java.util.List;
import java.util.Set;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class CustomUserPermissionRepositoryImpl implements CustomUserPermissionRepository {

  private final EntityManager entityManager;

  @Override
  public Page<String> getFilteredUsers(Pageable pageable, @NotNull Set<ApplicationType> applicationTypes,
      @NotNull Set<String> sboids) {
    EnumSpecification<UserPermission> applicationTypesSpec = new EnumSpecification<>(applicationTypes.stream().toList(),
        UserPermission_.application);
    IsMemberSpecification<UserPermission, Set<String>, String> sboidsSpec = new IsMemberSpecification<>(sboids,
        UserPermission_.sboid);
    EnumSpecification<UserPermission> applicationRoleSpec = new EnumSpecification<>(List.of(ApplicationRole.READER),
        UserPermission_.role, true);
    Specification<UserPermission> specification = applicationTypesSpec.and(sboidsSpec).and(applicationRoleSpec);

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<String> query = criteriaBuilder.createQuery(String.class);
    Root<UserPermission> root = query.from(UserPermission.class);

    query.where(specification.toPredicate(root, query, criteriaBuilder));
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

}
