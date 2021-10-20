package ch.sbb.timetable.field.number.entity;

import ch.sbb.timetable.field.number.enumaration.Status;
import ch.sbb.timetable.field.number.versioning.model.Versionable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
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
public class Version implements Versionable {

  public static List<String> VERSIONABLE_ATTRIBUTES = List.of(Fields.name, Fields.number,
      Fields.swissTimetableFieldNumber, Fields.ttfnid, Fields.businessOrganisation, Fields.comment, Fields.nameCompact);

  private static final String VERSION_SEQ = "timetable_field_number_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @Builder.Default
  @OneToMany(mappedBy = "version", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private Set<LineRelation> lineRelations = new HashSet<>();

  private String ttfnid;

  private String name;

  private String number;

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

  private String businessOrganisation;

  private String comment;

  private String nameCompact;

}
