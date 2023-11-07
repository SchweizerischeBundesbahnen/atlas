package ch.sbb.prm.directory.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.converter.MeanOfTransportConverter;
import ch.sbb.atlas.servicepoint.converter.ServicePointNumberConverter;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.prm.directory.service.PrmVersionable;
import ch.sbb.prm.directory.validation.NotForReduced;
import ch.sbb.prm.directory.validation.PrmMeansOfTransportHelper;
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
import jakarta.persistence.Transient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
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
@Entity(name = "stop_point_version")
@AtlasVersionable
public class StopPointVersion extends BasePrmImportEntity implements PrmVersionable {

  private static final String VERSION_SEQ = "stop_point_version_seq";

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
  @NotEmpty
  @ElementCollection(targetClass = MeanOfTransport.class, fetch = FetchType.EAGER)
  @Convert(converter = MeanOfTransportConverter.class)
  private Set<MeanOfTransport> meansOfTransport;

  @AtlasVersionableProperty
  private String freeText;

  @NotForReduced
  @AtlasVersionableProperty
  private String address;

  @NotForReduced
  @AtlasVersionableProperty
  private String zipCode;

  @NotForReduced
  @AtlasVersionableProperty
  private String city;

  @NotForReduced
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType alternativeTransport;

  @NotForReduced
  @AtlasVersionableProperty
  private String alternativeTransportCondition;

  @NotForReduced
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType assistanceAvailability;

  @NotForReduced
  @AtlasVersionableProperty
  private String assistanceCondition;

  @NotForReduced
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType assistanceService;

  @NotForReduced
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType audioTicketMachine;

  @NotForReduced
  @AtlasVersionableProperty
  private String additionalInformation;

  @NotForReduced
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType dynamicAudioSystem;

  @NotForReduced
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType dynamicOpticSystem;

  @NotForReduced
  @AtlasVersionableProperty
  private String infoTicketMachine;

  @NotForReduced
  @AtlasVersionableProperty
  private boolean interoperable;

  @NotForReduced
  @AtlasVersionableProperty
  private String url;

  @NotForReduced
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType visualInfo;

  @NotForReduced
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType wheelchairTicketMachine;

  @NotForReduced
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType assistanceRequestFulfilled;

  @NotForReduced
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType ticketMachine;

  public Set<MeanOfTransport> getMeansOfTransport() {
    if (meansOfTransport == null) {
      return new HashSet<>();
    }
    return meansOfTransport;
  }
  @Transient
  public boolean isReduced(){
    return PrmMeansOfTransportHelper.isReduced(meansOfTransport);
  }

}
