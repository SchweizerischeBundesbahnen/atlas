package ch.sbb.workflow.api;

import ch.sbb.workflow.entity.Person;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(name = "Person")
public class PersonModel {

  @Schema(description = "Firstname", example = "John")
  @NotBlank
  private String firstName;

  @Schema(description = "Second", example = "Doe")
  @NotBlank
  private String lastName;

  @Schema(description = "Function", example = "Officer")
  @NotBlank
  private String function;

  @Schema(description = "mail", example = "mail@sbb.ch")
  @NotBlank
  private String mail;

  @Schema(description = "Object creation date", example = "01.01.2000", accessMode = AccessMode.READ_ONLY)
  private LocalDateTime creationDate;

  @Schema(description = "Last edition date", example = "01.01.2000", accessMode = AccessMode.READ_ONLY)
  private LocalDateTime editionDate;

  public static PersonModel toModel(Person person) {
    return PersonModel.builder()
        .firstName(person.getFirstName())
        .lastName(person.getLastName())
        .function(person.getFunction())
        .mail(person.getMail())
        .creationDate(person.getCreationDate())
        .editionDate(person.getEditionDate())
        .build();
  }

  public static Person toEntity(PersonModel model) {
    return Person.builder()
        .firstName(model.getFirstName())
        .lastName(model.getLastName())
        .function(model.getFunction())
        .mail(model.getMail())
        .build();
  }

}
