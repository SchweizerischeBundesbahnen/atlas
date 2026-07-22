package ch.sbb.atlas.imports.model;

import ch.sbb.atlas.api.lidi.enumaration.LineConcessionType;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import ch.sbb.atlas.api.lidi.enumaration.OfferCategory;
import ch.sbb.atlas.deserializer.LocalDateDeserializer;
import ch.sbb.atlas.imports.annotation.DefaultMapping;
import ch.sbb.atlas.imports.bulk.BulkImportErrors;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.Validatable;
import ch.sbb.atlas.imports.model.LineCreateCsvModel.Fields;
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

@EqualsAndHashCode
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@FieldNameConstants
@JsonPropertyOrder({Fields.linienId, Fields.validFrom, Fields.validTo, Fields.description,
    Fields.number, Fields.swissLineNumber, Fields.lineConcessionType, Fields.lineType, Fields.shortNumber,
    Fields.offerCategory, Fields.longName, Fields.businessOrganisation, Fields.comment})
public class LineCreateCsvModel implements Validatable<LineCreateCsvModel> {

  @DefaultMapping
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
  private String number;

  @DefaultMapping
  private String swissLineNumber;

  private LineConcessionType lineConcessionType;

  @DefaultMapping
  private LineType lineType;

  @DefaultMapping
  private String shortNumber;

  @DefaultMapping
  private OfferCategory offerCategory;

  @DefaultMapping
  private String longName;

  @DefaultMapping
  private String businessOrganisation;

  @DefaultMapping
  private String comment;

  @Override
  public List<BulkImportError> validate() {
    return BulkImportErrors.notNullForFields(this,
        List.of(Fields.validFrom, Fields.validTo,
            Fields.lineType, Fields.description, Fields.number, Fields.offerCategory, Fields.businessOrganisation));
  }

  @Override
  public List<UniqueField<LineCreateCsvModel>> uniqueFields() {
    return List.of(new UniqueField<>(Fields.swissLineNumber, LineCreateCsvModel::getSwissLineNumber));
  }

}
