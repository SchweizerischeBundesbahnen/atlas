package ch.sbb.atlas.imports.model;

import ch.sbb.atlas.api.lidi.enumaration.SublineConcessionType;
import ch.sbb.atlas.deserializer.LocalDateDeserializer;
import ch.sbb.atlas.imports.annotation.CopyFromCurrentVersion;
import ch.sbb.atlas.imports.annotation.CopyFromCurrentVersion.Mapping;
import ch.sbb.atlas.imports.annotation.DefaultMapping;
import ch.sbb.atlas.imports.annotation.Nulling;
import ch.sbb.atlas.imports.bulk.BulkImportErrors;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.Validatable;
import ch.sbb.atlas.imports.model.SublineUpdateCsvModel.Fields;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@FieldNameConstants
@EqualsAndHashCode
@JsonPropertyOrder({Fields.slnid, Fields.linienId, Fields.validFrom, Fields.validTo,
    Fields.description, Fields.swissSublineNumber, Fields.sublineConcessionType, Fields.longName,
    Fields.businessOrganisation})
@CopyFromCurrentVersion({
    @Mapping(target = "id", current = "id"),
    @Mapping(target = "etagVersion", current = "version"),
})
public class SublineUpdateCsvModel implements Validatable<SublineUpdateCsvModel> {

  private String slnid;

  @DefaultMapping
  @Nulling
  private String linienId;

  @DefaultMapping
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validFrom;

  @DefaultMapping
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validTo;

  @DefaultMapping
  private String description;

  @DefaultMapping
  @Nulling
  private String swissSublineNumber;

  private SublineConcessionType sublineConcessionType;

  @DefaultMapping
  @Nulling
  private String longName;

  @DefaultMapping
  private String businessOrganisation;

  @Override
  public List<BulkImportError> validate() {
    return BulkImportErrors.notNullForFields(this,
        List.of(Fields.slnid, Fields.validFrom, Fields.validTo));
  }

  @Override
  public List<UniqueField<SublineUpdateCsvModel>> uniqueFields() {
    return List.of(new UniqueField<>(Fields.slnid, SublineUpdateCsvModel::getSlnid));
  }
}

