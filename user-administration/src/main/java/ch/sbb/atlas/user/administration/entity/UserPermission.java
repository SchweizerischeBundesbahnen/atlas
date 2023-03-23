package ch.sbb.atlas.user.administration.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Entity(name = "user_permission")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class UserPermission extends BasePermission {

  private static final String ID_SEQ = "user_permission_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ)
  @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotEmpty
  private String sbbUserId;

  @Builder.Default
  @OneToMany(mappedBy = "userPermission", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<PermissionRestriction> permissionRestrictions = new HashSet<>();

}
