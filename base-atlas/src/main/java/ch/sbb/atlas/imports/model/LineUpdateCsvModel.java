package ch.sbb.atlas.imports.model;

import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.deserializer.LocalDateDeserializer;
import ch.sbb.atlas.imports.annotation.CopyFromCurrentVersion;
import ch.sbb.atlas.imports.annotation.CopyFromCurrentVersion.Mapping;
import ch.sbb.atlas.imports.annotation.DefaultMapping;
import ch.sbb.atlas.imports.annotation.Nulling;
import ch.sbb.atlas.imports.bulk.BulkImportErrors;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.Validatable;
import ch.sbb.atlas.imports.model.LineUpdateCsvModel.Fields;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDate;
import java.util.ArrayList;
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
@JsonPropertyOrder({Fields.slnid, Fields.validFrom, Fields.validTo, Fields.description,
    Fields.number, Fields.swissLineNumber, Fields.lineConcessionType, Fields.shortNumber,
    Fields.offerCategory, Fields.longName, Fields.businessOrganisation, Fields.comment})
@CopyFromCurrentVersion({
    @Mapping(target = "id", current = "id"),
    @Mapping(target = "etagVersion", current = "version"),
})
public class LineUpdateCsvModel implements Validatable<LineUpdateCsvModel> {

  private String slnid;

  @DefaultMapping
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validFrom;

  @DefaultMapping
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validTo;

  @DefaultMapping
  private String description;

  @DefaultMapping
  private String number;

  @DefaultMapping
  private String swissLineNumber;

  private LineConcessionType lineConcessionType;

  @DefaultMapping
  @Nulling
  private String shortNumber;

  @DefaultMapping
  private OfferCategory offerCategory;

  @DefaultMapping
  @Nulling
  private String longName;

  @DefaultMapping
  private String businessOrganisation;

  @DefaultMapping
  @Nulling
  private String comment;

  @Override
  public List<BulkImportError> validate() {
    List<BulkImportError> errors = new ArrayList<>();
    if (slnid == null) {
      errors.add(BulkImportErrors.notNull(Fields.validFrom));
    }
    if (validFrom == null) {
      errors.add(BulkImportErrors.notNull(Fields.validFrom));
    }
    if (validTo == null) {
      errors.add(BulkImportErrors.notNull(Fields.validTo));
    }
    return errors;
  }

  @Override
  public List<UniqueField<LineUpdateCsvModel>> uniqueFields() {
    return List.of(new UniqueField<>(Fields.slnid, LineUpdateCsvModel::getSlnid));
  }
}
