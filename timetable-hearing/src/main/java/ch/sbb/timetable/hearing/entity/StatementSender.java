package ch.sbb.timetable.hearing.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.redact.Redacted;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder(toBuilder = true)
@Embeddable
@Redacted
public class StatementSender {

  @Redacted
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String firstName;

  @Redacted
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String lastName;

  @Redacted
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String organisation;

  @Redacted
  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String street;

  @Redacted
  @Min(1000)
  @Max(99999)
  private Integer zip;

  @Redacted
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String city;

  @Redacted
  @NotEmpty
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_10)
  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  private List<String> emails;
}
