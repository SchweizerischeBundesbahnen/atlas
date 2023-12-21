package ch.sbb.atlas.servicepointdirectory.service.georeference;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

import java.time.Year;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static ch.sbb.atlas.api.AtlasApiConstants.ZURICH_ZONE_ID;

@Data
public class GeoAdminResponse {

  private List<Result> results;

  @Data
  public static class Result {

    private String layerBodId;
    private String layerName;
    private String featureId;
    private String id;
    private Attributes attributes;
  }

  @Data
  @Getter
  public static class Attributes {
    private String name;

    @JsonProperty("gemname")
    private String municipalityName;

    @JsonProperty("gde_nr")
    private Integer municipalityNumber;

    @JsonProperty("langtext")
    private String longText;

    @JsonProperty("jahr")
    private Integer year;

  }

  public Optional<Result> getLatestResultByLayer(Layers layer) {
    if (layer.equals(Layers.MUNICIPALITY)) {
      return results.stream()
              .filter(l -> l.getLayerBodId().equals(layer.getLayerBodId()))
              .filter(l -> l.getAttributes().getYear().equals(Year.now(ZoneId.of(ZURICH_ZONE_ID)).getValue()))
              .findFirst();
    }
    return results.stream()
            .filter(i -> i.getLayerBodId().equals(layer.getLayerBodId()))
            .findFirst();
  }
}
