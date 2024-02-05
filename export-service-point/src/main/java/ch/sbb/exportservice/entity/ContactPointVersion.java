package ch.sbb.exportservice.entity;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
public class ContactPointVersion extends BaseEntity {

    private Long id;

    private String sloid;

    private String parentServicePointSloid;

    private ServicePointNumber parentServicePointNumber;

    private String type;

    private String designation;

    private String additionalInformation;

    private String inductionLoop;

    private String openingHours;

    private String wheelchairAccess;

    private LocalDate validFrom;

    private LocalDate validTo;

}
