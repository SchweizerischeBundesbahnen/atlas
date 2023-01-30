package ch.sbb.importservice.entitiy;

import ch.sbb.atlas.base.service.imports.servicepoint.model.ItemImportResponseStatus;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
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
@Entity(name = "import_process_item")
public class ImportProcessItem {

  private static final String VERSION_SEQ = "import_process_item_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  private Long stepExecutionId;

  private String jobExecutionName;

  private Integer itemNumber;

  @Enumerated(EnumType.STRING)
  private ItemImportResponseStatus responseStatus;

  private String responseMessage;
}
