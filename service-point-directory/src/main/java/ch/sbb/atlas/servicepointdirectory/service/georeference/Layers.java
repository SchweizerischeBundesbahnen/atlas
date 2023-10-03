package ch.sbb.atlas.servicepointdirectory.service.georeference;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Layers {

  BEZIRK("ch.swisstopo.swissboundaries3d-bezirk-flaeche.fill"),
  GEMEINDE("ch.swisstopo.swissboundaries3d-gemeinde-flaeche.fill"),
  KANTON("ch.swisstopo.swissboundaries3d-kanton-flaeche.fill"),
  LAND("ch.swisstopo.swissboundaries3d-land-flaeche.fill"),
  ORTSCHAFT("ch.swisstopo-vd.ortschaftenverzeichnis_plz"),

  ;

  private final String layerBodId;

  public static String getLayersParam() {
    return "all:" + Stream.of(values()).map(Layers::getLayerBodId).collect(Collectors.joining(","));
  }
}
