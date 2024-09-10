package ch.sbb.importservice.entity;

import ch.sbb.atlas.imports.ItemProcessResponseStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldNameConstants
@Entity(name = "geo_update_import_process_item")
public class GeoUpdateProcessItem {

  private static final String VERSION_SEQ = "geo_update_import_process_item_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  private Long stepExecutionId;

  private String jobExecutionName;

  private String sloid;

  private Long servicePointId;

  @Enumerated(EnumType.STRING)
  private ItemProcessResponseStatus responseStatus;

  private String responseMessage;
}
