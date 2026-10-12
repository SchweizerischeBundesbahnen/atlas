package ch.sbb.exportservice.job.prm.ersad.netex;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadParking;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadPathLink;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadStation;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadStationEquipment;
import ch.sbb.exportservice.job.prm.ersad.model.NetexTriState;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class ErsadNetexFileWriterTest {

  @TempDir
  private Path tempDir;

  @Test
  void shouldMatchTavannesGoldenFile() throws Exception {
    // Given
    List<ErsadStation> stations = List.of(ErsadGoldenFixtures.tavannes());

    // When
    Path file = write(stations);

    // Then
    assertThat(normalize(parse(file))).isEqualTo(normalize(parseResource("/ersad/tavannes-8500100.xml")));
  }

  @Test
  void shouldMatchThreeStationGoldenFile() throws Exception {
    // Given
    List<ErsadStation> stations = ErsadGoldenFixtures.burgdorfYverdonBellinzona();

    // When
    Path file = write(stations);

    // Then
    assertThat(normalize(parse(file)))
        .isEqualTo(normalize(parseResource("/ersad/burgdorf-yverdon-bellinzona.xml")));
  }

  @Test
  void shouldProduceWellFormedXml() throws Exception {
    // Given
    List<ErsadStation> stations = new ArrayList<>(ErsadGoldenFixtures.burgdorfYverdonBellinzona());
    stations.add(ErsadGoldenFixtures.tavannes());

    // When
    Path file = write(stations);

    // Then
    assertThatCode(() -> parse(file)).doesNotThrowAnyException();
    Document document = parse(file);
    assertThat(document.getDocumentElement().getLocalName()).isEqualTo("PublicationDelivery");
    assertThat(document.getDocumentElement().getNamespaceURI()).isEqualTo("http://www.netex.org.uk/netex");
    assertThat(document.getElementsByTagNameNS("*", "StopPlace").getLength()).isEqualTo(4);
    assertThat(Files.readString(file, StandardCharsets.UTF_8)).startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
  }

  @Test
  void shouldProduceWellFormedXmlWhenNoStations() throws Exception {
    // Given
    List<ErsadStation> stations = List.of();

    // When
    Path file = write(stations);

    // Then
    Document document = parse(file);
    assertThat(document.getElementsByTagNameNS("*", "SiteFrame").getLength()).isEqualTo(1);
    assertThat(document.getElementsByTagNameNS("*", "stopPlaces").getLength()).isZero();
  }

  @Test
  void shouldOmitElementWhenValueToBeCompleted() throws Exception {
    // Given
    ErsadStation tavannes = ErsadGoldenFixtures.tavannes();
    ErsadStation station = withEquipment(tavannes, new ErsadStationEquipment(
        StandardAttributeType.TO_BE_COMPLETED, StandardAttributeType.TO_BE_COMPLETED,
        StandardAttributeType.NOT_APPLICABLE, BooleanOptionalAttributeType.TO_BE_COMPLETED,
        StandardAttributeType.TO_BE_COMPLETED, StandardAttributeType.TO_BE_COMPLETED));

    // When
    String xml = Files.readString(write(List.of(station)), StandardCharsets.UTF_8);

    // Then
    assertThat(xml)
        .doesNotContain("id=\"ch:1:sloid:100:aa\"")
        .doesNotContain("<TicketMachines>")
        .doesNotContain("<AudibleSignalsAvailable>")
        .doesNotContain("TO_BE_COMPLETED")
        .doesNotContain("NOT_APPLICABLE")
        .doesNotContainPattern("<(\\w+)(\\s[^>]*)?></\\1>")
        .doesNotContainPattern("<(\\w+)(\\s[^>]*)?>\\s*</\\1>");
    Document document = parse(write(List.of(station)));
    assertThat(document.getElementsByTagNameNS("*", "VisualSignsAvailable").getLength())
        .as("VisualSignsAvailable is no longer exported anywhere (8.3 known limitation)").isZero();
  }

  @Test
  void shouldWriteStationLevelTicketMachinesFromStopPointWithoutTicketCounter() throws Exception {
    // Given
    ErsadStation tavannes = ErsadGoldenFixtures.tavannes();
    ErsadStation station = withEquipment(tavannes, new ErsadStationEquipment(
        StandardAttributeType.TO_BE_COMPLETED, StandardAttributeType.TO_BE_COMPLETED,
        StandardAttributeType.TO_BE_COMPLETED, BooleanOptionalAttributeType.YES,
        StandardAttributeType.TO_BE_COMPLETED, StandardAttributeType.TO_BE_COMPLETED));
    ErsadStation withoutContactPoints = new ErsadStation(station.sloid(), station.number(), station.name(),
        station.longitude(), station.latitude(), station.address(), station.url(), station.meansOfTransport(),
        station.accessibleStation(), station.assistance(), station.equipment(), station.quays(), List.of(),
        List.of(), station.parkings(), station.pathLinks(), station.version());

    // When
    String xml = Files.readString(write(List.of(withoutContactPoints)), StandardCharsets.UTF_8)
        .replaceAll(">\\s+<", "><");

    // Then
    assertThat(xml).contains("<TicketingEquipment id=\"" + station.sloid() + ":ticketing\" version=\""
        + station.version() + "\"><TicketMachines>true</TicketMachines></TicketingEquipment>");
  }

  @Test
  void shouldNotWriteTicketMachinesOnTicketCounterTicketingEquipment() throws Exception {
    // Given
    ErsadStation station = ErsadGoldenFixtures.tavannes();

    // When
    String xml = Files.readString(write(List.of(station)), StandardCharsets.UTF_8).replaceAll(">\\s+<", "><");

    // Then
    assertThat(xml).contains("<TicketingEquipment id=\"ch:1:sloid:100:1234\" version=\"1\"><TicketOffice>true</TicketOffice>");
  }

  @Test
  void shouldOmitWholeAccessibilityAssessmentWhenAllValuesMissing() throws Exception {
    // Given
    ErsadStation tavannes = ErsadGoldenFixtures.tavannes();
    ErsadStation station = new ErsadStation(tavannes.sloid(), tavannes.number(), tavannes.name(), null, null, null,
        null, List.of(), null, null, null, List.of(), List.of(), List.of(), List.of(), List.of(), 1);

    // When
    Document document = parse(write(List.of(station)));

    // Then
    assertThat(document.getElementsByTagNameNS("*", "AccessibilityAssessment").getLength()).isZero();
    assertThat(document.getElementsByTagNameNS("*", "Centroid").getLength()).isZero();
    assertThat(document.getElementsByTagNameNS("*", "PostalAddress").getLength()).isZero();
    assertThat(document.getElementsByTagNameNS("*", "localServices").getLength()).isZero();
    assertThat(document.getElementsByTagNameNS("*", "placeEquipments").getLength()).isZero();
    assertThat(document.getElementsByTagNameNS("*", "quays").getLength()).isZero();
    assertThat(document.getElementsByTagNameNS("*", "Name").item(0).getTextContent()).isEqualTo("Tavannes");
  }

  @Test
  void shouldNotWriteEmptyContainerWhenNoParkings() throws Exception {
    // Given
    List<ErsadStation> stations = ErsadGoldenFixtures.burgdorfYverdonBellinzona();

    // When
    String xml = Files.readString(write(stations), StandardCharsets.UTF_8);

    // Then
    assertThat(xml).doesNotContain("<parkings").doesNotContain("<pathLinks").contains("<stopPlaces>");
  }

  @Test
  void shouldWriteParkingAreaOnlyWhenPrmPlacesAvailableYes() throws Exception {
    // Given
    ErsadStation tavannes = ErsadGoldenFixtures.tavannes();
    ErsadStation station = withParkings(tavannes, List.of(
        new ErsadParking("ch:1:sloid:100:parking:1", "Mit PRM", BooleanOptionalAttributeType.YES,
            BooleanOptionalAttributeType.YES, 1),
        new ErsadParking("ch:1:sloid:100:parking:2", "Ohne PRM", BooleanOptionalAttributeType.YES,
            BooleanOptionalAttributeType.NO, 1),
        new ErsadParking("ch:1:sloid:100:parking:3", "Unbekannt", BooleanOptionalAttributeType.YES,
            BooleanOptionalAttributeType.TO_BE_COMPLETED, 1)));

    // When
    Document document = parse(write(List.of(station)));

    // Then
    assertThat(document.getElementsByTagNameNS("*", "Parking").getLength()).isEqualTo(3);
    NodeList parkingAreas = document.getElementsByTagNameNS("*", "ParkingArea");
    assertThat(parkingAreas.getLength()).isEqualTo(1);
    assertThat(parkingAreas.item(0).getAttributes().getNamedItem("id").getNodeValue())
        .isEqualTo("ch:1:sloid:100:parking:1:parkingArea");
    assertThat(document.getElementsByTagNameNS("*", "parkingAreas").getLength()).isEqualTo(1);
  }

  @Test
  void shouldNeverWriteTotalCapacity() throws Exception {
    // Given
    List<ErsadStation> stations = List.of(ErsadGoldenFixtures.tavannes());

    // When
    String xml = Files.readString(write(stations), StandardCharsets.UTF_8);

    // Then
    assertThat(xml).contains("<Parking ").doesNotContain("TotalCapacity");
  }

  @Test
  void shouldEscapeSpecialCharactersInNames() throws Exception {
    // Given
    ErsadStation tavannes = ErsadGoldenFixtures.tavannes();
    String name = "Zürich <HB> & \"Gare\" 'Löwenstrasse'";
    ErsadStation station = new ErsadStation(tavannes.sloid(), tavannes.number(), name, tavannes.longitude(),
        tavannes.latitude(), tavannes.address(), tavannes.url(), tavannes.meansOfTransport(),
        tavannes.accessibleStation(), tavannes.assistance(), tavannes.equipment(), tavannes.quays(),
        tavannes.contactPoints(), tavannes.toilets(), tavannes.parkings(), tavannes.pathLinks(), tavannes.version());

    // When
    Path file = write(List.of(station));

    // Then
    assertThat(Files.readString(file, StandardCharsets.UTF_8)).contains("&amp;").contains("&lt;HB").contains("Zürich");
    Document document = parse(file);
    assertThat(document.getElementsByTagNameNS("*", "Name").item(0).getTextContent()).isEqualTo(name);
  }

  @Test
  void shouldWriteWgs84CoordinatesWithElevenDecimals() throws Exception {
    // Given
    ErsadStation tavannes = ErsadGoldenFixtures.tavannes();
    ErsadStation station = new ErsadStation(tavannes.sloid(), tavannes.number(), tavannes.name(), 7.2,
        47.219844660314567, null, null, tavannes.meansOfTransport(), null, null, null, List.of(), List.of(), List.of(),
        List.of(), List.of(), 1);

    // When
    Document document = parse(write(List.of(station)));

    // Then
    assertThat(document.getElementsByTagNameNS("*", "Longitude").item(0).getTextContent()).isEqualTo("7.20000000000");
    assertThat(document.getElementsByTagNameNS("*", "Latitude").item(0).getTextContent()).isEqualTo("47.21984466031");
  }

  @Test
  void shouldWriteTransportModeRailAndRailStationForTrainStation() throws Exception {
    // Given
    ErsadStation tavannes = ErsadGoldenFixtures.tavannes();
    ErsadStation station = new ErsadStation(tavannes.sloid(), tavannes.number(), tavannes.name(), null, null, null,
        null, List.of("BUS", "TRAIN"), NetexTriState.UNKNOWN, null, null, List.of(), List.of(), List.of(), List.of(),
        List.of(), 1);

    // When
    Document document = parse(write(List.of(station)));

    // Then
    assertThat(document.getElementsByTagNameNS("*", "TransportMode").item(0).getTextContent()).isEqualTo("rail");
    assertThat(document.getElementsByTagNameNS("*", "StopPlaceType").item(0).getTextContent())
        .isEqualTo("railStation");
    assertThat(document.getElementsByTagNameNS("*", "MobilityImpairedAccess").item(0).getTextContent())
        .isEqualTo("unknown");
  }

  @Test
  void shouldBeIdempotentOnClose() {
    // Given
    Path file = tempDir.resolve("ersad.xml");
    ErsadNetexFileWriter writer = ErsadNetexFileWriter.open(file, ErsadGoldenFixtures.PUBLICATION_TIMESTAMP);

    // When
    writer.close();

    // Then
    assertThatCode(writer::close).doesNotThrowAnyException();
    assertThatCode(() -> parse(file)).doesNotThrowAnyException();
  }

  private Path write(List<ErsadStation> stations) {
    Path file = tempDir.resolve("ersad-" + System.nanoTime() + ".xml");
    try (ErsadNetexFileWriter writer = ErsadNetexFileWriter.open(file, ErsadGoldenFixtures.PUBLICATION_TIMESTAMP)) {
      writer.writeStopPlaces(stations.stream());
      writer.writeParkings(stations.stream());
    }
    return file;
  }

  @Test
  void shouldWriteTactileWarningStripContrastOnSitePathLinkFromContrastingAreas() throws Exception {
    // Given
    ErsadStation tavannes = ErsadGoldenFixtures.tavannes();
    ErsadStation station = withPathLinks(tavannes, List.of(
        new ErsadPathLink("ref:1", "to:1", ReferencePointElementType.PLATFORM, null, null, StandardAttributeType.YES, 1),
        new ErsadPathLink("ref:1", "to:2", ReferencePointElementType.PLATFORM, null, null, StandardAttributeType.NO, 1),
        new ErsadPathLink("ref:1", "to:3", ReferencePointElementType.PLATFORM, null, null,
            StandardAttributeType.TO_BE_COMPLETED, 1)));

    // When
    String xml = Files.readString(write(List.of(station)), StandardCharsets.UTF_8).replaceAll(">\\s+<", "><");

    // Then
    assertThat(xml)
        .contains("<To><PlaceRef ref=\"to:1\"/></To><TactileWarningStripContrast>true</TactileWarningStripContrast>")
        .contains("<To><PlaceRef ref=\"to:2\"/></To><TactileWarningStripContrast>false</TactileWarningStripContrast>");
    assertThat(xml.split("<TactileWarningStripContrast>", -1)).hasSize(3);
  }

  private static ErsadStation withPathLinks(ErsadStation s, List<ErsadPathLink> pathLinks) {
    return new ErsadStation(s.sloid(), s.number(), s.name(), s.longitude(), s.latitude(), s.address(), s.url(),
        s.meansOfTransport(), s.accessibleStation(), s.assistance(), s.equipment(), s.quays(), s.contactPoints(),
        s.toilets(), s.parkings(), pathLinks, s.version());
  }

  private static ErsadStation withEquipment(ErsadStation s, ErsadStationEquipment equipment) {
    return new ErsadStation(s.sloid(), s.number(), s.name(), s.longitude(), s.latitude(), s.address(), s.url(),
        s.meansOfTransport(), null, s.assistance(), equipment, s.quays(), s.contactPoints(), s.toilets(),
        s.parkings(), s.pathLinks(), s.version());
  }

  private static ErsadStation withParkings(ErsadStation s, List<ErsadParking> parkings) {
    return new ErsadStation(s.sloid(), s.number(), s.name(), s.longitude(), s.latitude(), s.address(), s.url(),
        s.meansOfTransport(), s.accessibleStation(), s.assistance(), s.equipment(), s.quays(), s.contactPoints(),
        s.toilets(), parkings, s.pathLinks(), s.version());
  }

  private static DocumentBuilder documentBuilder() throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    factory.setNamespaceAware(true);
    factory.setIgnoringComments(true);
    return factory.newDocumentBuilder();
  }

  private static Document parse(Path file) throws Exception {
    return documentBuilder().parse(file.toFile());
  }

  private static Document parseResource(String resource) throws Exception {
    try (InputStream inputStream = ErsadNetexFileWriterTest.class.getResourceAsStream(resource)) {
      assertThat(inputStream).as(resource).isNotNull();
      return documentBuilder().parse(inputStream);
    }
  }

  private static String normalize(Document document) throws Exception {
    removeWhitespaceOnlyTextNodes(document.getDocumentElement());
    document.normalizeDocument();
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    StringWriter out = new StringWriter();
    transformer.transform(new DOMSource(document), new StreamResult(out));
    return out.toString().replace("\r\n", "\n");
  }

  private static void removeWhitespaceOnlyTextNodes(Node node) {
    NodeList children = node.getChildNodes();
    for (int i = children.getLength() - 1; i >= 0; i--) {
      Node child = children.item(i);
      if (child.getNodeType() == Node.TEXT_NODE && child.getTextContent().isBlank()) {
        node.removeChild(child);
      } else if (child.getNodeType() == Node.ELEMENT_NODE) {
        removeWhitespaceOnlyTextNodes(child);
      }
    }
  }

}
