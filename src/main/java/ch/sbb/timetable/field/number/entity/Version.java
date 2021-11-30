package ch.sbb.timetable.field.number.entity;

import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.atlas.versioning.model.VersionableProperty.RelationType;
import ch.sbb.timetable.field.number.enumaration.Status;
import ch.sbb.timetable.field.number.service.UserService;
import ch.sbb.timetable.field.number.validation.DatesValidator;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity(name = "timetable_field_number_version")
@FieldNameConstants
@AtlasVersionable
public class Version implements Versionable, DatesValidator {

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
  @GeneratorType(type = TtfnidGenerator.class, when = GenerationTime.INSERT)
  @Column(updatable = false)
  private String ttfnid;

  @AtlasVersionableProperty
  @Size(max = 255)
  private String name;

  @AtlasVersionableProperty
  @NotNull
  @Size(min = 1, max = 50)
  private String number;

  @AtlasVersionableProperty
  @Size(min = 1, max = 50)
  @NotNull
  private String swissTimetableFieldNumber;

  @Enumerated(EnumType.STRING)
  @NotNull
  private Status status;

  @AtlasVersionableProperty(ignoreDiff = true)
  @CreationTimestamp
  @Column(columnDefinition = "TIMESTAMP", updatable = false)
  private LocalDateTime creationDate;

  @AtlasVersionableProperty
  @Column(updatable = false)
  @NotNull
  private String creator;

  @AtlasVersionableProperty
  @javax.persistence.Version
  @NotNull
  private Integer version;

  @Column(columnDefinition = "TIMESTAMP")
  @NotNull
  private LocalDateTime editionDate;

  @NotNull
  private String editor;

  @Column(columnDefinition = "TIMESTAMP")
  @NotNull
  private LocalDate validFrom;

  @Column(columnDefinition = "TIMESTAMP")
  @NotNull
  private LocalDate validTo;

  @AtlasVersionableProperty
  @Size(min = 1, max = 50)
  @NotNull
  private String businessOrganisation;

  @AtlasVersionableProperty
  @Size(max = 250)
  private String comment;

  @PrePersist
  public void onPrePersist() {
    String sbbUid = UserService.getSbbUid();
    setCreator(sbbUid);
    setEditor(sbbUid);
    setEditionDate(LocalDateTime.now());
  }

  @PreUpdate
  public void onPreUpdate() {
    setEditor(UserService.getSbbUid());
    setEditionDate(LocalDateTime.now());
  }
}
