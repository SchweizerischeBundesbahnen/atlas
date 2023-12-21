package ch.sbb.atlas.servicepointdirectory.service.georeference;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

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
  }

  public Optional<Result> getLatestResultByLayer(Layers layer) {
    long count = results.stream().filter(i -> i.getLayerBodId().equals(layer.getLayerBodId())).count();
    return count != 0 ? results.stream().filter(i -> i.getLayerBodId().equals(layer.getLayerBodId())).skip(count-1).findFirst() : Optional.empty();
  }
}
