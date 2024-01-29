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
public class ReferencePointVersionCsvModel {

  private String sloid;

  private String parentSloidServicePoint;

  private Integer parentNumberServicePoint;

  private String designation;

  private boolean mainReferencePoint;

  private String additionalInformation;

  private String rpType;

  private String validFrom;

  private String validTo;

  private String creationDate;

  private String creator;

  private String editionDate;

  private String editor;

}
