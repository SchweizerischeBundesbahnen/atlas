package ch.sbb.atlas.timetable.hearing.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.model.SwissCanton;
import ch.sbb.atlas.model.entity.BaseEntity;
import ch.sbb.atlas.timetable.hearing.enumeration.StatementStatus;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@Entity(name = "timetable_hearing_statement")
@FieldNameConstants
public class TimetableHearingStatement extends BaseEntity {

  private static final String VERSION_SEQ = "timetable_hearing_statement_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  // Information regarding subject
  @NotNull
  private Long timetableYear;

  @NotNull
  @Enumerated(EnumType.STRING)
  private StatementStatus statementStatus;

  private String ttfnid;

  @Enumerated(EnumType.STRING)
  private SwissCanton swissCanton;

  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String stopPlace;

  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  private Set<@Size(max = AtlasFieldLengths.LENGTH_50) String> responsibleTransportCompanies;

  // Statement giver information
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String firstName;
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String lastName;
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String organisation;
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String street;
  @Min(1000)
  @Max(99999)
  private Integer zip;
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String city;
  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String email;

  // Statement
  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_5000)
  private String statement;

  @Size(max = 3)
  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  private Set<@Size(max = AtlasFieldLengths.LENGTH_50) String> documents;

  // FoT Justification field for comments
  @Size(max = AtlasFieldLengths.LENGTH_5000)
  private String justification;

}
