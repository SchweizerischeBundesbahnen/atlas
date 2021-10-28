package ch.sbb.line.directory.swiss.number;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
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
@FieldNameConstants
@Entity(name = "swiss_number")
public class SwissNumberTestEntity implements SwissNumber {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String swissLineNumber;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

  @Override
  public SwissNumberDescriptor getSwissNumberDescriptor() {
    return new SwissNumberDescriptor(Fields.swissLineNumber, this::getSwissLineNumber);
  }
}
