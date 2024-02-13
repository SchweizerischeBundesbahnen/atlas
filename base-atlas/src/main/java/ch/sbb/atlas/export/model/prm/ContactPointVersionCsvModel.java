package ch.sbb.atlas.export.model.prm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@FieldNameConstants
@EqualsAndHashCode
public class ContactPointVersionCsvModel {

    private String sloid;

    private String parentSloidServicePoint;

    private Integer parentNumberServicePoint;

    private String type;

    private String designation;

    private String additionalInformation;

    private String inductionLoop;

    private String openingHours;

    private String wheelchairAccess;

    private String validFrom;

    private String validTo;

    private String creationDate;

    private String editionDate;

}
