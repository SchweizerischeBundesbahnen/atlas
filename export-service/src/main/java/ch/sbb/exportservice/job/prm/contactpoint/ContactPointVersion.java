package ch.sbb.exportservice.job.prm.contactpoint;

import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.job.prm.BasePrmEntity;
import java.time.LocalDate;
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
public class ContactPointVersion extends BasePrmEntity {

    private Long id;

    private String sloid;

    private String parentServicePointSloid;

    private ServicePointNumber parentServicePointNumber;

    private ContactPointType type;

    private String designation;

    private String additionalInformation;

    private StandardAttributeType inductionLoop;

    private String openingHours;

    private StandardAttributeType wheelchairAccess;

    private LocalDate validFrom;

    private LocalDate validTo;

}
