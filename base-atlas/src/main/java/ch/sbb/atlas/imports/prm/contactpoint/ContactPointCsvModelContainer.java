package ch.sbb.atlas.imports.prm.contactpoint;

import ch.sbb.atlas.api.prm.model.contactpoint.ContactPointVersionModel;
import ch.sbb.atlas.imports.prm.BasePrmCsvModelContainer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactPointCsvModelContainer extends BasePrmCsvModelContainer<ContactPointCsvModel> {
    @JsonIgnore
    public List<ContactPointVersionModel> getCreateModels() {
        return getCsvModels().stream().map(ContactPointCsvToModelMapper::toModel).toList();
    }
}
