package ch.sbb.importservice.entitiy;

import ch.sbb.atlas.imports.servicepoint.model.ItemImportResponseStatus;
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
