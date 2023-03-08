package ch.sbb.line.directory.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@SuperBuilder
@Embeddable
public class StatementSender {

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
}
