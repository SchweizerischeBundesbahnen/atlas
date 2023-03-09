package ch.sbb.atlas.servicepointdirectory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Schema(enumAsRef = true, example = "SWITZERLAND")
public enum Country {

  AZERBAIJAN("AZ", 57, "Aserbaidschan", "Azerbaïdjan", " Azerbaigian", "Azerbaijan"),
  BELGIUM("BE", 88, "Belgien", "Belgique", " Belgio", "Belgium"),
  BELARUS("BY", 21, "Weißrussland", "Bélarus", " Bielorussia", "Belarus"),
  SERB_BOSNIA_AND_HERZEGOVINA("BA", 44, "Bosnien und Herzegowinas, serbische Republik", "Bosnie-Herzégovine, République serbe de",
      " Bosnia ed Erzegovina", "Bosnia and Herzegovina, Serb Republic of "),
  BOSNIA_AND_HERZEGOVINA("BA", 49, "Bosnien und Herzegowina", "Bosnie-Herzégovine", " Bosnia ed Erzegovina",
      "Bosnia and Herzegovina"),
  CROAT_BOSNIA_AND_HERZEGOVINA("BA", 50, "Bosnien und Herzegowinas, kroatisch-moslemische Föderation",
      "Bosnie-Herzégovine, Fédération croato-musulmane de", " Bosnia ed Erzegovina",
      "Bosnia and Herzegovina, Muslim-Croat Federation of "),
  BULGARIA("BG", 52, "Bulgarien", "Bulgarie", " Bulgaria", "Bulgaria"),
  CANADA("CA", null, "Kanada", "Canada", " Canada", "Canada"),
  CHINA("CN", 33, "China", "Chine", " Cina", "China"),
  NORTH_KOREA("KP", 30, "Koreas Demokratische Volksrepublik", "Corée, République populaire démocratique de", " Corea del Nord",
      "Korea, Democratic People's Republic of"),
  SOUTH_KOREA("KR", 61, "Koreanische Republik", "Corée, République de", " Corea del Sud", "Korea, Republic of"),
  CROATIA("HR", 78, "Kroatien", "Croatie", " Croazia", "Croatia"),
  CUBA("CU", 40, "Kuba", "Cuba", " Cuba", "Cuba"),
  DENMARK("DK", 86, "Dänemark", "Danemark", " Danimarca", "Denmark"),
  EGYPT("EG", 90, "Aegypten", "Egypte", " Egitto", "Egypt"),
  ESTONIA("EE", 26, "Estland", "Estonie", " Estonia", "Estonia"),
  FINLAND("FI", 10, "Finnland", "Finlande", " Finlandia", "Finland"),
  FRANCE("FR", 87, "Frankreich", "France", " Francia", "France"),
  GEORGIA("GE", 28, "Georgien", "Géorgie", " Georgia", "Georgia"),
  GERMANY("DE", 80, "Deutschland", "Allemagne", " Germania", "Germany"),
  JAPAN("JP", 42, "Japan", "Japon", " Giappone", "日本"),
  GREECE("GR", 73, "Griechenland", "Grèce", " Grecia", "Greece"),
  IRAN("IR", 96, "Iran, Islamische Republik", "Iran, République islamique d'", " Iran", "Iran, Islamic Republic of"),
  IRAQ("IQ", 99, "Irak", "Iraq", " Iraq", "Iraq"),
  IRELAND("IE", 60, "Irland", "Irlande", " Irlanda", "Ireland"),
  ISRAEL("IL", 95, "Israel", "Israël", " Israele", "Israel"),
  ITALY("IT", 83, "Italien", "Italie", " Italia", "Italy"),
  KAZAKHSTAN("KZ", 27, "Kasachstan", "Kazakhstan", " Kazakistan", "Kazakhstan"),
  KYRGYZSTAN("KG", 59, "Kirgisistan", "Kirghizistan", " Kirghizistan", "Kyrgyzstan"),
  LATVIA("LV", 25, "Lettland", "Lettonie", " Lettonia", "Latvia"),
  LEBANON("LB", 98, "Libanon", "Liban", " Libano", "Lebanon"),
  LITHUANIA("LT", 24, "Litauen", "Lituanie", " Lituania", "Lithuania"),
  LUXEMBOURG("LU", 82, "Luxemburg", "Luxembourg", " Lussemburgo", "Luxembourg"),
  MACEDONIA("MK", 65, "Mazedonien, Ehemalige jugoslawische Republik", "Macédoine, L'Ex-République Yougoslave de",
      " Macedonia del Nord", "Macedonia, The former Yugoslav Republic of"),
  MOROCCO("MA", 93, "Marokko", "Maroc", " Marocco", "Morocco"),
  MOLDOVA("MD", 23, "Moldawien, Republik", "Moldava, République de", " Moldavia", "Moldova, Republic of"),
  MONGOLIA("MN", 31, "Mongolei", "Mongolie", " Mongolia", "Mongolia"),
  MONTENEGRO("ME", 62, "Montenegro", "Montenegro", " Montenegro", "Montenegro"),
  NORWAY("NO", 76, "Norwegen", "Norvège", " Norvegia", "Norway"),
  NETHERLANDS("NL", 84, "Niederlande", "Pays-Bas", " Paesi Bassi", "Netherlands"),
  POLAND("PL", 51, "Polen", "Pologne", " Polonia", "Poland"),
  PORTUGAL("PT", 94, "Portugal", "Portugal", " Portogallo", "Portugal"),
  NORTHERN_IRELAND("GB", 70, "Vereinigtes Königreich von Großbritannien und von Nordirland",
      "Royaume-Uni de Grande-Bretagne et d''Irlande du Nord", " Regno Unito",
      "United Kingdom of Great Britain and Northern Ireland"),
  CONGO("CD", null, "Kongo, Demokratische Republik", "République démocratique du Congo", " RD del Congo",
      "Congo, the Democratic Republic of the"),
  CZECH_REPUBLIC("CZ", 54, "Tschechische Republik", "Tchèque, République", " Rep. Ceca", "Czech Republic"),
  ROMANIA("RO", 53, "Rumänien", "Roumanie", " Romania", "Romania"),
  RUSSIA("RU", 20, "Russische Föderation", "Russie, Fédération de", " Russia", "Russian Federation"),
  SERBIA("RS", 72, "Serbien", "Serbie", " Serbia", "Serbia"),
  SYRIA("SY", 97, "Syrien, Arabische Republik", "Syrienne, République arabe", " Siria", "Syrian Arab Republic"),
  SLOVAKIA("SK", 56, "Slowakei", "Slovaquie", " Slovacchia", "Slovakia"),
  SLOVENIA("SI", 79, "Slowenien", "Slovénie", " Slovenia", "Slovenia"),
  SPAIN("ES", 71, "Spanien", "Espagne", " Spagna", "Spain"),
  UNITED_STATES("US", 46, "Vereinigte Staaten", "États-Unis", " Stati Uniti", "United States"),
  SOUTH_AFRICA("ZA", null, "Südafrika", "Afrique du Sud", " Sudafrica", "South Africa"),
  SWEDEN("SE", 74, "Schweden", "Suède", " Svezia", "Sweden"),
  SWITZERLAND("CH", 85, "Schweiz", "Suisse", " Svizzera", "Switzerland"),
  TAJIKISTAN("TJ", 66, "Tadschikistan", "Tadjikistan", " Tagikistan", "Tajikistan"),
  TUNISIA("TN", 91, "Tunesien", "Tunisie", " Tunisia", "Tunisia"),
  TURKEY("TR", 75, "Türkei", "Turquie", " Turchia", "Turkey"),
  TURKMENISTAN("TM", 67, "Turkmenistan", "Turkménistan", " Turkmenistan", "Turkmenistan"),
  UKRAINE("UA", 22, "Ukraine", "Ukraine", " Ucraina", "Ukraine"),
  HUNGARY("HU", 55, "Ungarn", "Hongrie", " Ungheria", "Hungary"),
  UZBEKISTAN("UZ", 29, "Usbekistan", "Ouzbékistan", " Uzbekistan", "Uzbekistan"),
  VIETNAM("VN", 32, "Vietnam", "Viet Nam", " Vietnam", "Vietnam"),
  GERMANY_BUS("DE", 11, " Deutschland (Bus)", "Allemagne (bus)", "Germania (bus)", "Germany (bus)"),
  AUSTRIA_BUS("AT", 12, " Österreich (Bus)", "Autriche (bus)", "Austria (bus)", "Austria (bus)"),
  ITALY_BUS("IT", 13, " Italien (Bus)", "Italie (bus)", "Italia (bus)", "Italy (bus)"),
  FRANCE_BUS("FR", 14, " Frankreich (Bus)", "France (bus)", "Francia (bus)", "France (bus)"),
  AUSTRIA_HUNGARY("GYSEV/ROEE", 43, "Österreich-Ungarn", "Austria-Hungary", null, "Raab-Ödenburg-Eberfurter Eisenbahn AG"),
  BOSNIA_AND_HERZEGOVINA_RAILWAY("ZBH", 89, "Bosnien-Herzegowina", "Bosnia-Herzegowina", null,
      "Eisenbahn Bosnien - Herzegowinas"),
  AFGHANISTAN("AF", 68, " Afghanistan", "Afghanistan", " Afghanistan", " Afghanistan"),
  ALBANIA("AL", 41, "Albanien", "Albanie", " Albania", "Albania"),
  ALGERIA("DZ", 92, "Algerien", "Algérie", " Algeria", "Algeria"),
  ARMENIA("AM", 58, "Armenien", "Arménie", " Armenia", "Armenia"),
  AUSTRALIA("AU", null, "Australien", "Australie", " Australia", "Australia"),
  AUSTRIA("AT", 81, "Österreich", "Autriche", " Austria", "Austria");

  private final String isoCode;
  private final Integer uicCode;
  private final String nameDe;
  private final String nameFr;
  private final String nameIt;
  private final String nameEn;

  public static Country from(Integer uicCode) {
    return Arrays.stream(Country.values()).filter(country -> Objects.equals(country.getUicCode(), uicCode)).findFirst()
        .orElse(null);
  }

  public static Country filterCountriesWithSpecifiedIsoCode(String isoCode) {
    return Arrays.stream(Country.values()).filter(country -> Objects.equals(country.getIsoCode(), isoCode)).findFirst()
        .orElse(null);
  }
}
