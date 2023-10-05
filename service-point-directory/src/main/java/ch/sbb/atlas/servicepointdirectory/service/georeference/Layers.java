package ch.sbb.atlas.servicepointdirectory.service.georeference;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Layers {

  DISTRICT("ch.swisstopo.swissboundaries3d-bezirk-flaeche.fill"),
  MUNICIPALITY("ch.swisstopo.swissboundaries3d-gemeinde-flaeche.fill"),
  CANTON("ch.swisstopo.swissboundaries3d-kanton-flaeche.fill"),
  COUNTRY("ch.swisstopo.swissboundaries3d-land-flaeche.fill"),
  LOCALITY("ch.swisstopo-vd.ortschaftenverzeichnis_plz"),

  ;

  private final String layerBodId;

  public static String getLayersParam() {
    return "all:" + Stream.of(values()).map(Layers::getLayerBodId).collect(Collectors.joining(","));
  }
}
