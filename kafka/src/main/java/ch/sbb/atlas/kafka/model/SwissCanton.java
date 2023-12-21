package ch.sbb.atlas.kafka.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.stream.Stream;

@Schema(enumAsRef = true, example = "BERN")
@RequiredArgsConstructor
@Getter
public enum SwissCanton {

  ZURICH(1, "Zürich", "ZH"),
  BERN(2, "Bern", "BE"),
  LUCERNE(3, "Luzern", "LU"),
  URI(4, "Uri", "UR"),
  SCHWYZ(5, "Schwyz", "SZ"),
  OBWALDEN(6, "Obwalden", "OW"),
  NIDWALDEN(7, "Nidwalden", "NW"),
  GLARUS(8, "Glarus", "GL"),
  ZUG(9, "Zug", "ZG"),
  FRIBOURG(10, "Fribourg", "FR"),
  SOLOTHURN(11, "Solothurn", "SO"),
  BASEL_CITY(12, "Basel-Stadt", "BS"),
  BASEL_COUNTRY(13, "Basel-Landschaft", "BL"),
  SCHAFFHAUSEN(14, "Schaffhausen", "SH"),
  APPENZELL_AUSSERRHODEN(15, "Appenzell Ausserrhoden", "AR"),
  APPENZELL_INNERRHODEN(16, "Appenzell Innerrhoden", "AI"),
  ST_GALLEN(17, "St. Gallen", "SG"),
  GRAUBUNDEN(18, "Graubünden", "GR"),
  AARGAU(19, "Aargau", "AG"),
  THURGAU(20, "Thurgau", "TG"),
  TICINO(21, "Ticino", "TI"),
  VAUD(22, "Vaud", "VD"),
  VALAIS(23, "Valais", "VS"),
  NEUCHATEL(24, "Neuchâtel", "NE"),
  GENEVE(25, "Genève", "GE"),
  JURA(26, "Jura", "JU"),

  ;

  private final Integer number;
  private final String name;
  private final String abbreviation;

  public static SwissCanton fromCantonNumber(Integer swissCantonNumber) {
    return Stream.of(SwissCanton.values()).filter(swissCanton -> Objects.equals(swissCanton.getNumber(),
        swissCantonNumber)).findFirst().orElse(null);
  }

  public static SwissCanton fromCantonName(String swissCantonName) {
    return Stream.of(SwissCanton.values()).filter(swissCanton -> Objects.equals(swissCanton.getName(),
        swissCantonName)).findFirst().orElse(null);
  }
}
