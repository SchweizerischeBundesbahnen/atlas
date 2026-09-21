package ch.sbb.workflow.otp.entity;

import ch.sbb.workflow.entity.Person;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
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
@FieldNameConstants
@Entity(name = "otp")
public class Otp {

  public static final Duration OTP_LIFESPAN = Duration.ofMinutes(10);

  private static final String VERSION_SEQ = "otp_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotNull
  private String code;

  @OneToOne
  @JoinColumn(name = "person_id", referencedColumnName = "id")
  private Person person;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime creationTime;

  public LocalDateTime getExpirationTime() {
    return creationTime.plus(OTP_LIFESPAN);
  }

  public boolean isStillValid() {
    return LocalDateTime.now().isBefore(getExpirationTime());
  }

  public long getExpiresInSeconds() {
    return Math.max(0, Duration.between(LocalDateTime.now(), getExpirationTime()).toSeconds());
  }

}
