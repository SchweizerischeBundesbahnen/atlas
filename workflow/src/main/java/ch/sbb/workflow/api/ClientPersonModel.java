package ch.sbb.workflow.api;

import ch.sbb.atlas.base.service.model.api.AtlasCharacterSetsRegex;
import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.workflow.entity.Person;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Schema(name = "Client")
public class ClientPersonModel extends PersonModel {

  @Pattern(regexp = AtlasCharacterSetsRegex.EMAIL_ADDRESS)
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_255)
  @Schema(description = "mail", example = "mail@sbb.ch")
  @NotBlank
  private String mail;

  public static ClientPersonModel toModel(Person person) {
    return ClientPersonModel.builder()
        .firstName(person.getFirstName())
        .lastName(person.getLastName())
        .personFunction(person.getFunction())
        .mail(person.getMail())
        .creationDate(person.getCreationDate())
        .editionDate(person.getEditionDate())
        .build();
  }

  public static Person toEntity(ClientPersonModel model) {
    return Person.builder()
        .firstName(model.getFirstName())
        .lastName(model.getLastName())
        .function(model.getPersonFunction())
        .mail(model.getMail())
        .build();
  }

}
