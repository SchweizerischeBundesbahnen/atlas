package ch.sbb.atlas.timetable.hearing.entity;

import ch.sbb.atlas.model.entity.BaseEntity;
import ch.sbb.atlas.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.timetable.hearing.enumeration.SwissCanton;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;
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

  @NotNull
  private String ttfnid;

  @NotNull
  @Enumerated(EnumType.STRING)
  private SwissCanton swissCanton;

  @NotNull
  private String stopPlace;

  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  private Set<String> responsibleTransportCompanies;

  // Statement giver information
  @NotNull
  private String firstName;
  @NotNull
  private String lastName;
  @NotNull
  private String organisation;
  @NotNull
  private String street;
  @NotNull
  private Integer zip;
  @NotNull
  private String city;
  @NotNull
  private String email;

  // Statement
  @NotNull
  private String statement;

  @NotNull
  private String justification;

  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  private Set<String> documents;

}
