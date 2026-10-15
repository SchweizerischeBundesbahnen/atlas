package ch.sbb.exportservice.job.prm.ersad.netex;

import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ATLAS_PARTICIPANT_REF;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.COMPOSITE_FRAME;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.COMPOSITE_FRAME_ID_PREFIX;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.DATA_OBJECTS;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.DESCRIPTION;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.FRAMES;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.FRAME_DATE_FORMAT_PATTERN;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.GML_NAMESPACE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.GML_NAMESPACE_PREFIX;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.NETEX_NAMESPACE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.PARKINGS;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.PARTICIPANT_REF;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.PUBLICATION_DELIVERY;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.PUBLICATION_DESCRIPTION;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.PUBLICATION_TIMESTAMP;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.SITE_FRAME;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.SITE_FRAME_ID_PREFIX;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.SIRI_NAMESPACE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.SIRI_NAMESPACE_PREFIX;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.STOP_PLACES;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.TIMESTAMP_FORMAT_PATTERN;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ATTR_VERSION;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.XML_VERSION;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ZURICH_ZONE_ID;

import ch.sbb.exportservice.job.prm.ersad.model.ErsadStation;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

public final class ErsadNetexFileWriter implements AutoCloseable {

  private static final ZoneId ZURICH = ZoneId.of(ZURICH_ZONE_ID);
  private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT_PATTERN);
  private static final DateTimeFormatter FRAME_DATE_FORMAT = DateTimeFormatter.ofPattern(FRAME_DATE_FORMAT_PATTERN);

  private enum Phase {
    STOP_PLACES, PARKINGS
  }

  private final Writer output;
  private final XmlWriterSupport xml;
  private final StopPlaceXmlWriter stopPlaceWriter;
  private final ParkingXmlWriter parkingWriter;

  private Phase phase = Phase.STOP_PLACES;
  private boolean containerOpen;
  private boolean closed;

  private ErsadNetexFileWriter(Writer output, XmlWriterSupport xml) {
    this.output = output;
    this.xml = xml;
    this.stopPlaceWriter = new StopPlaceXmlWriter(xml);
    this.parkingWriter = new ParkingXmlWriter(xml);
  }

  public static ErsadNetexFileWriter open(Path file, LocalDateTime publicationTimestamp) {
    BufferedWriter output = null;
    try {
      output = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
      XmlWriterSupport xml = new XmlWriterSupport(XMLOutputFactory.newFactory().createXMLStreamWriter(output));
      ErsadNetexFileWriter writer = new ErsadNetexFileWriter(output, xml);
      writer.writeHeader(publicationTimestamp);
      return writer;
    } catch (IOException e) {
      closeQuietly(output);
      throw new UncheckedIOException("Could not open ERSAD NeTEx file " + file, e);
    } catch (XMLStreamException | RuntimeException e) {
      closeQuietly(output);
      if (e instanceof RuntimeException runtimeException) {
        throw runtimeException;
      }
      throw new IllegalStateException("Could not create XML writer for " + file, e);
    }
  }

  private static void closeQuietly(Writer output) {
    if (output != null) {
      try {
        output.close();
      } catch (IOException ignored) {
        // best effort cleanup
      }
    }
  }

  public void writeStopPlaces(Stream<ErsadStation> stations) {
    enterPhase(Phase.STOP_PLACES);
    stations.forEach(station -> {
      openContainer(STOP_PLACES);
      stopPlaceWriter.write(station);
    });
  }

  public void writeParkings(Stream<ErsadStation> stations) {
    enterPhase(Phase.PARKINGS);
    stations.forEach(station -> station.parkings().forEach(parking -> {
      openContainer(PARKINGS);
      parkingWriter.write(station, parking);
    }));
  }

  @Override
  public void close() {
    if (closed) {
      return;
    }
    closed = true;
    try {
      closeContainer();
      xml.endElement(); // SiteFrame
      xml.endElement(); // frames
      xml.endElement(); // CompositeFrame
      xml.endElement(); // dataObjects
      xml.endElement(); // PublicationDelivery
      xml.endDocument();
      xml.close();
      output.close();
    } catch (IOException e) {
      throw new UncheckedIOException("Could not close ERSAD NeTEx file", e);
    } finally {
      if (output != null) {
        try {
          output.close();
        } catch (IOException ignored) {
        }
      }
    }
  }

  private void writeHeader(LocalDateTime publicationTimestamp) {
    String frameDate = publicationTimestamp.format(FRAME_DATE_FORMAT);
    xml.startDocument();
    xml.startElement(PUBLICATION_DELIVERY);
    xml.defaultNamespace(NETEX_NAMESPACE);
    xml.namespace(GML_NAMESPACE_PREFIX, GML_NAMESPACE);
    xml.namespace(SIRI_NAMESPACE_PREFIX, SIRI_NAMESPACE);
    xml.attribute(ATTR_VERSION, XML_VERSION);
    xml.element(PUBLICATION_TIMESTAMP, publicationTimestamp.atZone(ZURICH).format(TIMESTAMP_FORMAT));
    xml.element(PARTICIPANT_REF, ATLAS_PARTICIPANT_REF);
    xml.element(DESCRIPTION, PUBLICATION_DESCRIPTION);
    xml.startElement(DATA_OBJECTS);
    xml.startElement(COMPOSITE_FRAME, COMPOSITE_FRAME_ID_PREFIX + frameDate);
    xml.startElement(FRAMES);
    xml.startElement(SITE_FRAME, SITE_FRAME_ID_PREFIX + frameDate);
  }

  private void enterPhase(Phase next) {
    if (closed) {
      throw new IllegalStateException("ERSAD NeTEx writer is already closed");
    }
    if (next.ordinal() < phase.ordinal()) {
      throw new IllegalStateException("Cannot write " + next + " after " + phase);
    }
    if (next != phase) {
      closeContainer();
      phase = next;
    }
  }

  private void openContainer(String name) {
    if (!containerOpen) {
      xml.startElement(name);
      containerOpen = true;
    }
  }

  private void closeContainer() {
    if (containerOpen) {
      xml.endElement();
      containerOpen = false;
    }
  }

}
