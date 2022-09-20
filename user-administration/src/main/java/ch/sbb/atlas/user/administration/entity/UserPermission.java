package ch.sbb.atlas.user.administration.entity;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity(name = "user_permission")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class UserPermission {

  private static final String ID_SEQ = "user_permission_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ)
  @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotNull
  @Enumerated(EnumType.STRING)
  private ApplicationRole role;

  @NotNull
  @Enumerated(EnumType.STRING)
  private ApplicationType application;

  @NotNull
  @Size(min = 7, max = 7)
  private String sbbUserId;

  // TODO: integration-test
  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  @CollectionTable(name = "business_organisation_user_permission")
  private Set<String> sboid;

}
