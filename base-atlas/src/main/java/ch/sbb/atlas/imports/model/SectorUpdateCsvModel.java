package ch.sbb.atlas.imports.model;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.deserializer.LocalDateDeserializer;
import ch.sbb.atlas.imports.annotation.CopyFromCurrentVersion;
import ch.sbb.atlas.imports.annotation.CopyFromCurrentVersion.Mapping;
import ch.sbb.atlas.imports.annotation.DefaultMapping;
import ch.sbb.atlas.imports.annotation.Nulling;
import ch.sbb.atlas.imports.bulk.BulkImportErrors;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.UpdateGeolocationModel;
import ch.sbb.atlas.imports.bulk.Validatable;
import ch.sbb.atlas.imports.model.SectorUpdateCsvModel.Fields;
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
@JsonPropertyOrder({Fields.sloid, Fields.validFrom, Fields.validTo, Fields.designation,
    Fields.east, Fields.north, Fields.spatialReference, Fields.height, Fields.length,
    Fields.edgeHeight})
@CopyFromCurrentVersion({
    @Mapping(target = "id", current = "id"),
    @Mapping(target = "etagVersion", current = "version"),
    @Mapping(target = "sloid", current = "sloid"),
    @Mapping(target = "trafficPointSloid", current = "trafficPointSloid"),
})
public class SectorUpdateCsvModel implements Validatable<SectorUpdateCsvModel>, UpdateGeolocationModel {

  private String sloid;

  @DefaultMapping
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validFrom;

  @DefaultMapping
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validTo;

  @DefaultMapping
  private String designation;

  private Double east;

  private Double north;

  private SpatialReference spatialReference;

  @Nulling(property = "sectorGeolocation.height")
  private Double height;

  @DefaultMapping
  @Nulling
  private Double length;

  @DefaultMapping
  @Nulling
  private Double edgeHeight;

  @Override
  public List<BulkImportError> validate() {
    return BulkImportErrors.notNullForFields(this,
        List.of(Fields.sloid, Fields.validFrom, Fields.validTo));
  }

  @Override
  public List<UniqueField<SectorUpdateCsvModel>> uniqueFields() {
    return List.of(new UniqueField<>(Fields.sloid, SectorUpdateCsvModel::getSloid));
  }

}
