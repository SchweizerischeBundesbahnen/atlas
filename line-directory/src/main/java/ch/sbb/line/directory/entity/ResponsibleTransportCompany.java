package ch.sbb.line.directory.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;
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
@Entity(name = "timetable_hearing_statement_responsible_transport_companies")
@FieldNameConstants
public class ResponsibleTransportCompany {

  private static final String VERSION_SEQ = "timetable_hearing_statement_responsible_transport_companies_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "timetable_hearing_statement_id", referencedColumnName = "id")
  @NotNull
  private TimetableHearingStatement statement;

  @NotNull
  private Long transportCompanyId;

  private String number;

  private String abbreviation;

  private String businessRegisterName;

}
