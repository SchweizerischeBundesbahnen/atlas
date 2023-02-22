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
  @Min(2010)
  @Max(2099)
  private Long timetableYear;

  @NotNull
  @Enumerated(EnumType.STRING)
  private StatementStatus statementStatus;

  private String ttfnid;

  @NotNull
  @Enumerated(EnumType.STRING)
  private SwissCanton swissCanton;

  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String stopPlace;

  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  private Set<@Size(max = AtlasFieldLengths.LENGTH_50) String> responsibleTransportCompanies;

  // Statement giver information
  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String firstName;
  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String lastName;
  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String organisation;
  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String street;
  @NotNull
  @Min(1000)
  @Max(99999)
  private Integer zip;
  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String city;
  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String email;

  // Statement
  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_2000)
  private String statement;

  // TODO: ist des vom Bürger oder was ist das?
  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_2000)
  private String justification;

  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  private Set<@Size(max = AtlasFieldLengths.LENGTH_50) String> documents;

}
