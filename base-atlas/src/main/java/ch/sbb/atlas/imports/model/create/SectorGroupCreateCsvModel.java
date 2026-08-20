package ch.sbb.atlas.imports.model.create;

import ch.sbb.atlas.deserializer.LocalDateDeserializer;
import ch.sbb.atlas.imports.annotation.DefaultMapping;
import ch.sbb.atlas.imports.bulk.BulkImportErrors;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.Validatable;
import ch.sbb.atlas.imports.model.create.SectorGroupCreateCsvModel.Fields;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
@JsonPropertyOrder({Fields.trafficPointSloid, Fields.validFrom, Fields.validTo, Fields.designation,
    Fields.length, Fields.sectorSloids})
public class SectorGroupCreateCsvModel implements Validatable<SectorGroupCreateCsvModel> {

  public static final int MINIMUM_SECTOR_SLOIDS = 2;

  @DefaultMapping
  private String trafficPointSloid;

  @DefaultMapping
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validFrom;

  @DefaultMapping
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validTo;

  @DefaultMapping
  private String designation;

  @DefaultMapping
  private Double length;

  @DefaultMapping
  private Set<String> sectorSloids;

  @Override
  public List<BulkImportError> validate() {
    List<BulkImportError> errors = new ArrayList<>(BulkImportErrors.notNullForFields(this,
        List.of(Fields.trafficPointSloid, Fields.validFrom, Fields.validTo, Fields.designation,
            Fields.sectorSloids)));

    if (sectorSloids != null && sectorSloids.size() < MINIMUM_SECTOR_SLOIDS) {
      errors.add(BulkImportErrors.minSize(Fields.sectorSloids, MINIMUM_SECTOR_SLOIDS));
    }
    return errors;
  }

  @Override
  public List<UniqueField<SectorGroupCreateCsvModel>> uniqueFields() {
    return List.of();
  }

}
