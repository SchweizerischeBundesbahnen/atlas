package ch.sbb.prm.directory.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.converter.MeanOfTransportConverter;
import ch.sbb.atlas.servicepoint.converter.ServicePointNumberConverter;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.prm.directory.enumeration.StandardAttributeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Entity(name = "stop_place_version")
@AtlasVersionable
public class StopPlaceVersion extends BasePrmImportEntity implements Versionable {

  private static final String VERSION_SEQ = "stop_place_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @AtlasVersionableProperty
  private String sloid;

  @NotNull
  @AtlasVersionableProperty
  @Convert(converter = ServicePointNumberConverter.class)
  @Valid
  private ServicePointNumber number;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

  @AtlasVersionableProperty
  @ElementCollection(targetClass = MeanOfTransport.class, fetch = FetchType.EAGER)
  @Convert(converter = MeanOfTransportConverter.class)
  private Set<MeanOfTransport> meansOfTransport;

  @AtlasVersionableProperty
  private String freeText;

  @AtlasVersionableProperty
  private String address;

  @AtlasVersionableProperty
  private String zipCode;

  @AtlasVersionableProperty
  private String city;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType alternativeTransport;

  @AtlasVersionableProperty
  private String alternativeTransportCondition;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType assistanceAvailability;

  @AtlasVersionableProperty
  private String alternativeCondition;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType assistanceService;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType audioTicketMachine;

  @AtlasVersionableProperty
  private String additionalInfo;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType dynamicAudioSystem;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType dynamicOpticSystem;

  @AtlasVersionableProperty
  private String infoTicketMachine;

  @AtlasVersionableProperty
  private boolean interoperable;

  @AtlasVersionableProperty
  private String url;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType visualInfo;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType wheelchairTicketMachine;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType assistanceRequestFulfilled;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType ticketMachine;


}
