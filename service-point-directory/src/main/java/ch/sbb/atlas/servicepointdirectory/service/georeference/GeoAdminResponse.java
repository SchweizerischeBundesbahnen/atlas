package ch.sbb.atlas.servicepointdirectory.service.georeference;

import java.util.List;
import java.util.Optional;
import lombok.Data;
import lombok.Getter;

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
    private String label;
    private Double flaeche;
    private String gemname;
    private String kanton;
    private String langtext;
  }

  public Optional<Result> getResultByLayer(Layers layer) {
    return results.stream().filter(i -> i.getLayerBodId().equals(layer.getLayerBodId())).findFirst();
  }
}
