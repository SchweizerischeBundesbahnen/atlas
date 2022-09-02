package ch.sbb.atlas.user.administration.entity;

import ch.sbb.atlas.user.administration.enums.ApplicationRole;
import ch.sbb.atlas.user.administration.enums.ApplicationType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

@Entity(name = "user_permission")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

}
