package ch.sbb.exportservice.job.prm.ersad.netex;

import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ACCESSIBILITY_ASSESSMENT;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ACCESSIBILITY_LIMITATION;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ATTR_ID;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ATTR_REF;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ATTR_VERSION;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.COMMENT;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.LIMITATIONS;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.MOBILITY_IMPAIRED_ACCESS;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.NETEX_OBJECT_VERSION;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.UNKNOWN;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.WHEELCHAIR_ACCESS;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.XML_ENCODING;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.XML_VERSION;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class XmlWriterSupport {

  static final String VERSION = NETEX_OBJECT_VERSION;
  private static final String INDENT = "  ";

  private final XMLStreamWriter writer;
  private int depth;

  public record Limitation(String name, Optional<String> value) {

  }

  public void startDocument() {
    call(() -> writer.writeStartDocument(XML_ENCODING, XML_VERSION));
  }

  public void endDocument() {
    call(() -> {
      writer.writeCharacters("\n");
      writer.writeEndDocument();
      writer.flush();
    });
  }

  public void close() {
    call(writer::close);
  }

  public void startElement(String name) {
    indent();
    call(() -> writer.writeStartElement(name));
    depth++;
  }

  public void startElement(String name, String id) {
    startElement(name);
    attribute(ATTR_ID, id);
    attribute(ATTR_VERSION, VERSION);
  }

  public void attribute(String name, String value) {
    call(() -> writer.writeAttribute(name, value));
  }

  public void defaultNamespace(String namespaceUri) {
    call(() -> writer.writeDefaultNamespace(namespaceUri));
  }

  public void namespace(String prefix, String namespaceUri) {
    call(() -> writer.writeNamespace(prefix, namespaceUri));
  }

  public void endElement() {
    depth--;
    indent();
    call(writer::writeEndElement);
  }

  public void element(String name, String text) {
    indent();
    call(() -> {
      writer.writeStartElement(name);
      writer.writeCharacters(text);
      writer.writeEndElement();
    });
  }

  public void element(String name, String attributeName, String attributeValue, String text) {
    indent();
    call(() -> {
      writer.writeStartElement(name);
      writer.writeAttribute(attributeName, attributeValue);
      writer.writeCharacters(text);
      writer.writeEndElement();
    });
  }

  public void optionalElement(String name, Optional<String> value) {
    value.ifPresent(text -> element(name, text));
  }

  public void reference(String name, String ref) {
    indent();
    call(() -> {
      writer.writeEmptyElement(name);
      writer.writeAttribute(ATTR_REF, ref);
      writer.writeAttribute(ATTR_VERSION, VERSION);
    });
  }

  public void referenceWithoutVersion(String name, String ref) {
    indent();
    call(() -> {
      writer.writeEmptyElement(name);
      writer.writeAttribute(ATTR_REF, ref);
    });
  }

  public void accessibilityAssessment(String id, List<Limitation> limitations, Optional<String> comment) {
    List<Limitation> present = limitations.stream().filter(limitation -> limitation.value().isPresent()).toList();
    if (present.isEmpty() && comment.isEmpty()) {
      return;
    }
    String mobilityImpairedAccess = present.stream()
        .filter(limitation -> limitation.name().equals(WHEELCHAIR_ACCESS))
        .findFirst()
        .flatMap(Limitation::value)
        .orElse(UNKNOWN);
    List<Limitation> limitationsWithWheelchairAccess = present.stream()
        .anyMatch(limitation -> limitation.name().equals(WHEELCHAIR_ACCESS))
        ? present
        : withLeadingUnknownWheelchairAccess(present);
    startElement(ACCESSIBILITY_ASSESSMENT, id);
    element(MOBILITY_IMPAIRED_ACCESS, mobilityImpairedAccess);
    if (!limitationsWithWheelchairAccess.isEmpty()) {
      startElement(LIMITATIONS);
      startElement(ACCESSIBILITY_LIMITATION);
      limitationsWithWheelchairAccess.forEach(limitation -> optionalElement(limitation.name(), limitation.value()));
      endElement();
      endElement();
    }
    optionalElement(COMMENT, comment);
    endElement();
  }

  private static List<Limitation> withLeadingUnknownWheelchairAccess(List<Limitation> limitations) {
    List<Limitation> withFallback = new ArrayList<>();
    withFallback.add(new Limitation(WHEELCHAIR_ACCESS, Optional.of(UNKNOWN)));
    withFallback.addAll(limitations);
    return withFallback;
  }

  public static boolean anyPresent(List<Optional<String>> values) {
    return values.stream().anyMatch(Optional::isPresent);
  }

  private void indent() {
    call(() -> writer.writeCharacters("\n" + INDENT.repeat(depth)));
  }

  private static void call(XmlAction action) {
    try {
      action.run();
    } catch (XMLStreamException e) {
      throw new IllegalStateException("Could not write ERSAD NeTEx XML", e);
    }
  }

  @FunctionalInterface
  private interface XmlAction {

    void run() throws XMLStreamException;
  }
}
