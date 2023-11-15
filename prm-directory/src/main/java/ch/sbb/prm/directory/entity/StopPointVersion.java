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
import ch.sbb.prm.directory.validation.PrmMeansOfTransportHelper;
import ch.sbb.prm.directory.validation.VariantsReducedCompleteRecordable;
import ch.sbb.prm.directory.validation.annotation.NotForReducedVariant;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Entity(name = "stop_point_version")
@AtlasVersionable
public class StopPointVersion extends BasePrmImportEntity implements PrmVersionable, VariantsReducedCompleteRecordable, PrmSharedVersion {

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

  @Size(max = AtlasFieldLengths.LENGTH_2000)
  @AtlasVersionableProperty
  private String freeText;

  @Size(max = AtlasFieldLengths.LENGTH_2000)
  @NotForReducedVariant
  @AtlasVersionableProperty
  private String address;

  @Size(max = AtlasFieldLengths.LENGTH_50)
  @NotForReducedVariant
  @AtlasVersionableProperty
  private String zipCode;

  @Size(max = AtlasFieldLengths.LENGTH_75)
  @NotForReducedVariant
  @AtlasVersionableProperty
  private String city;

  @NotForReducedVariant(nullable = false)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType alternativeTransport;

  @Size(max = AtlasFieldLengths.LENGTH_2000)
  @NotForReducedVariant
  @AtlasVersionableProperty
  private String alternativeTransportCondition;

  @NotForReducedVariant(nullable = false)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType assistanceAvailability;

  @Size(max = AtlasFieldLengths.LENGTH_2000)
  @NotForReducedVariant
  @AtlasVersionableProperty
  private String assistanceCondition;

  @NotForReducedVariant(nullable = false)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType assistanceService;

  @NotForReducedVariant(nullable = false)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType audioTicketMachine;

  @Size(max = AtlasFieldLengths.LENGTH_2000)
  @NotForReducedVariant
  @AtlasVersionableProperty
  private String additionalInformation;

  @NotForReducedVariant(nullable = false)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType dynamicAudioSystem;

  @NotForReducedVariant(nullable = false)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType dynamicOpticSystem;

  @NotForReducedVariant
  @AtlasVersionableProperty
  private String infoTicketMachine;

  @NotForReducedVariant
  @AtlasVersionableProperty
  private Boolean interoperable;

  @Size(max = AtlasFieldLengths.LENGTH_500)
  @NotForReducedVariant
  @AtlasVersionableProperty
  private String url;

  @NotForReducedVariant(nullable = false)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType visualInfo;

  @NotForReducedVariant(nullable = false)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType wheelchairTicketMachine;

  @NotForReducedVariant(nullable = false)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType assistanceRequestFulfilled;

  @NotForReducedVariant(nullable = false)
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

  @Override
  public String getParentServicePointSloid() {
    return this.sloid;
  }
}
