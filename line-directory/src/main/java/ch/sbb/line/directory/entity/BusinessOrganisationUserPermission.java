package ch.sbb.line.directory.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity(name = "business_organisation_user_permission")
public class BusinessOrganisationUserPermission {

  private static final String ID_SEQ = "business_organisation_user_permission_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ID_SEQ)
  @SequenceGenerator(name = ID_SEQ, sequenceName = ID_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotNull
  @Size(max = 32)
  private String sboid;

  @ManyToOne
  @JoinColumn(name = "user_permission_id")
  private UserPermission userPermission;

}
