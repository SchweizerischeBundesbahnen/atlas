package ch.sbb.atlas.user.administration.service;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.searching.specification.EnumSpecification;
import ch.sbb.atlas.searching.specification.IsMemberSpecification;
import ch.sbb.atlas.user.administration.api.UserPermissionCreateModel;
import ch.sbb.atlas.user.administration.api.UserPermissionModel;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import ch.sbb.atlas.user.administration.entity.UserPermission_;
import ch.sbb.atlas.user.administration.exception.UserPermissionConflictException;
import ch.sbb.atlas.user.administration.repository.UserPermissionRepository;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAdministrationService {

  private final UserPermissionRepository userPermissionRepository;
  private final EntityManager em;

  public Page<String> getUserPage(Pageable pageable, Set<String> sboids,
      Set<ApplicationType> applicationTypes) {
    sboids = Optional.ofNullable(sboids).orElse(new HashSet<>());
    applicationTypes = Optional.ofNullable(applicationTypes).orElse(new HashSet<>());
    TypedQuery<String> query = this.getFilteredUserQuery(applicationTypes, sboids);
    List<String> resultList = query.getResultList();
    return new PageImpl<>(
        resultList.stream().skip(pageable.getOffset()).limit(pageable.getPageSize()).toList(),
        pageable,
        resultList.size());
  }

  public List<UserPermission> getUserPermissions(String sbbUserId) {
    return userPermissionRepository.findBySbbUserIdIgnoreCase(sbbUserId);
  }

  private void validatePermissionExistence(String sbbUserId) {
    boolean exists = userPermissionRepository.existsBySbbUserIdIgnoreCase(sbbUserId);
    if (exists) {
      throw new UserPermissionConflictException(sbbUserId);
    }
  }

  public void save(UserPermissionCreateModel userPermissionCreate) {
    validatePermissionExistence(userPermissionCreate.getSbbUserId());
    final List<UserPermission> toSave = userPermissionCreate.toEntityList();
    userPermissionRepository.saveAll(toSave);
  }

  public void updateUser(UserPermissionCreateModel editedPermissions) {
    editedPermissions.getPermissions().forEach(editedPermission -> {
      Optional<UserPermission> existingPermissions = getCurrentUserPermission(
          editedPermissions.getSbbUserId(),
          editedPermission.getApplication());
      existingPermissions.ifPresent(updateExistingPermissions(editedPermission));
    });
  }

  private Consumer<UserPermission> updateExistingPermissions(UserPermissionModel editedPermission) {
    return userPermission -> {
      userPermission.setRole(editedPermission.getRole());
      userPermission.setSboid(new HashSet<>(editedPermission.getSboids()));
    };
  }

  Optional<UserPermission> getCurrentUserPermission(String sbbuid,
      ApplicationType applicationType) {
    return userPermissionRepository.findBySbbUserIdIgnoreCase(sbbuid)
                                   .stream()
                                   .filter(userPermission -> userPermission.getApplication()
                                       == applicationType)
                                   .findFirst();
  }

  private TypedQuery<String> getFilteredUserQuery(
      @NonNull Set<ApplicationType> applicationTypes,
      @NonNull Set<String> sboids) {
    // Specification for where clause
    Specification<UserPermission> specification = new EnumSpecification<>(
        applicationTypes.stream().toList(), UserPermission_.application).and(
        new IsMemberSpecification<>(sboids, UserPermission_.sboid)
    ).and(new EnumSpecification<>(List.of(ApplicationRole.READER), UserPermission_.role, true));

    CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
    CriteriaQuery<String> query = criteriaBuilder.createQuery(String.class);
    Root<UserPermission> root = query.from(UserPermission.class);

    query.where(specification.toPredicate(root, query, criteriaBuilder));
    query.groupBy(root.get(UserPermission_.sbbUserId));
    Expression<Long> count = criteriaBuilder.count(
        root.get(UserPermission_.sbbUserId));
    query.having(
        criteriaBuilder.greaterThanOrEqualTo(count,
            (long) sboids.size() * applicationTypes.size()));

    CriteriaQuery<String> select = query.select(root.get(UserPermission_
        .sbbUserId));
    return em.createQuery(select);
  }
}
