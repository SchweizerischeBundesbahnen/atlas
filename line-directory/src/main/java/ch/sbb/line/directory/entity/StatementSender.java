package ch.sbb.line.directory.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
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
//
//  @NotNull
//  @Size(max = AtlasFieldLengths.LENGTH_100)
//  private String email;

  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_1000)
//  @Convert(converter = StringSetConverter.class)
//  @Column(name = "emails", nullable = false)
//  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)

//  @ElementCollection
//  @CollectionTable(name = "timetable_hearing_statement_emails", joinColumns = @JoinColumn(name = "timetable_hearing_statement_id"))
//  @Column(name = "email")


//  @Convert(converter = StringSetConverter.class)
  @Column(name = "email", nullable = false)
  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
  private Set<String> emails = new HashSet<>();
}
