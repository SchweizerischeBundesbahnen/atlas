package ch.sbb.timetable.field.number.entity;

import ch.sbb.timetable.field.number.enumaration.Status;
import ch.sbb.timetable.field.number.service.UserService;
import ch.sbb.timetable.field.number.versioning.annotation.AtlasVersionable;
import ch.sbb.timetable.field.number.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import ch.sbb.timetable.field.number.versioning.model.VersionableProperty.RelationType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity(name = "timetable_field_number_version")
@FieldNameConstants
@AtlasVersionable
public class Version implements Versionable {

  private static final String VERSION_SEQ = "timetable_field_number_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @Builder.Default
  @OneToMany(mappedBy = "version", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @AtlasVersionableProperty(relationType = RelationType.ONE_TO_MANY,
      relationsFields = {LineRelation.Fields.slnid}
  )
  private Set<LineRelation> lineRelations = new HashSet<>();

  @AtlasVersionableProperty
  private String ttfnid;

  @AtlasVersionableProperty
  private String name;

  @AtlasVersionableProperty
  private String number;

  @AtlasVersionableProperty
  private String swissTimetableFieldNumber;

  @Enumerated(EnumType.STRING)
  private Status status;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime creationDate;

  private String creator;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime editionDate;

  private String editor;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

  @AtlasVersionableProperty
  private String businessOrganisation;

  @AtlasVersionableProperty
  private String comment;

  @AtlasVersionableProperty
  private String nameCompact;

  @PrePersist
  public void onPrePersist(){
    String sbbUid = UserService.getSbbUid();
    setCreator(sbbUid);
    setEditor(sbbUid);
  }

  @PreUpdate
  public void onPreUpdate(){
    setEditor(UserService.getSbbUid());
  }
}
