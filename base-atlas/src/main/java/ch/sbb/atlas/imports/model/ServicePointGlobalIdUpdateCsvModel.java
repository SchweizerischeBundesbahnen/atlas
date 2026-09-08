package ch.sbb.atlas.imports.model;

import ch.sbb.atlas.imports.annotation.Nulling;
import ch.sbb.atlas.imports.bulk.BulkImportErrors;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.Validatable;
import ch.sbb.atlas.imports.model.ServicePointGlobalIdUpdateCsvModel.Fields;
import ch.sbb.atlas.imports.model.base.BulkImportValidationHelper;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * Assigns the official international Global-ID of a foreign stop to a service point.
 *
 * <p>Unlike every other SEPODI bulk import this scenario is <b>not versioned</b>: a Global-ID is a 1:1 mapping onto the stable
 * {@link ch.sbb.atlas.servicepoint.ServicePointNumber}, so there is no validFrom/validTo and no current version to copy from.
 *
 * <p>The mapping is stored per service point number, which is why {@code number} is the primary identifier here and
 * {@code sloid} is merely an optional alternative. Roughly a quarter of the foreign service points carry no sloid at all, so a
 * sloid-only file would be unable to address them.
 *
 * <p>An empty {@code globalId} is rejected. Removing a mapping is expressed with the explicit nulling value {@code <null>} so
 * that an accidentally blank column can never silently unassign a Global-ID.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@FieldNameConstants
@EqualsAndHashCode
@JsonPropertyOrder({Fields.sloid, Fields.number, Fields.globalId})
public class ServicePointGlobalIdUpdateCsvModel implements Validatable<ServicePointGlobalIdUpdateCsvModel> {

  private String sloid;

  private Integer number;

  @Nulling
  private String globalId;

  @Override
  public List<BulkImportError> validate() {
    List<BulkImportError> errors = new ArrayList<>();

    if (StringUtils.isBlank(sloid) && number == null) {
      errors.add(BulkImportErrors.sloidOrNumber());
    }

    errors.addAll(BulkImportValidationHelper.validateServicePointNumber(number));

    return errors;
  }

  @Override
  public List<UniqueField<ServicePointGlobalIdUpdateCsvModel>> uniqueFields() {
    return List.of(
        new UniqueField<>(Fields.sloid, ServicePointGlobalIdUpdateCsvModel::getSloid),
        new UniqueField<>(Fields.number, ServicePointGlobalIdUpdateCsvModel::getNumber),
        new UniqueField<>(Fields.globalId, ServicePointGlobalIdUpdateCsvModel::getGlobalId));
  }
}
