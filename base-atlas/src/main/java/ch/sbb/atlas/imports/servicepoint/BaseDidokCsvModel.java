package ch.sbb.atlas.imports.servicepoint;

import ch.sbb.atlas.imports.servicepoint.deserializer.LocalDateTimeDeserializer;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseDidokCsvModel {

  private static final int WGS84_EAST_MAX = 180;
  private static final int WGS84_NORTH_MAX = 90;

  // Coordinates

  @JsonProperty("SOURCE_SPATIAL_REF")
  private SpatialReference spatialReference;

  @JsonProperty("E_LV95")
  private Double eLv95;
  @JsonProperty("N_LV95")
  private Double nLv95;

  @JsonProperty("E_LV03")
  private Double eLv03;
  @JsonProperty("N_LV03")
  private Double nLv03;

  @JsonProperty("E_WGS84")
  private Double eWgs84;
  @JsonProperty("N_WGS84")
  private Double nWgs84;

  @JsonProperty("E_WGS84WEB")
  private Double eWgs84web;
  @JsonProperty("N_WGS84WEB")
  private Double nWgs84web;

  @JsonProperty("Z_LV95")
  private Double zLv95;

  @JsonProperty("HEIGHT")
  private Double height;

  // Validity

  @EqualsAndHashCode.Exclude
  @JsonProperty("GUELTIG_VON")
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validFrom;

  @EqualsAndHashCode.Exclude
  @JsonProperty("GUELTIG_BIS")
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validTo;

  // Create/Edit Info
  @EqualsAndHashCode.Exclude
  @JsonProperty("ERSTELLT_AM")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime createdAt;

  @EqualsAndHashCode.Exclude
  @JsonProperty("ERSTELLT_VON")
  private String createdBy;

  @EqualsAndHashCode.Exclude
  @JsonProperty("GEAENDERT_VON")
  private String editedBy;

  @EqualsAndHashCode.Exclude
  @JsonProperty("GEAENDERT_AM")
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime editedAt;

  private static Double getCoordinateBySpatialReference(
      SpatialReference spatialReference,
      Double wgs84,
      Double wgs84Web,
      Double lv95,
      Double lv03) {
    return switch (spatialReference) {
      case WGS84WEB -> wgs84Web;
      case LV95 -> lv95;
      case LV03 -> lv03;
      case WGS84 -> wgs84;
    };
  }

  public Double getHeight() {
    return ObjectUtils.firstNonNull(height, zLv95);
  }

  public Double getOriginalEast() {
    if (eWgs84 == null || eWgs84 < -WGS84_EAST_MAX || eWgs84 > WGS84_EAST_MAX) {
      return null;
    }
    return getCoordinateBySpatialReference(spatialReference, eWgs84, eWgs84web, eLv95, eLv03);
  }

  public Double getOriginalNorth() {
    if (nWgs84 == null || nWgs84 < -WGS84_NORTH_MAX || nWgs84 > WGS84_NORTH_MAX) {
      return null;
    }
    return getCoordinateBySpatialReference(spatialReference, nWgs84, nWgs84web, nLv95, nLv03);
  }
}
