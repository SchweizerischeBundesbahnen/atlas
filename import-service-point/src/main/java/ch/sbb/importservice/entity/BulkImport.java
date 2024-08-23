package ch.sbb.importservice.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.model.BulkImportConfig;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.CreationTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldNameConstants
@Entity(name = "bulk_import")
public class BulkImport {

  private static final String BULK_IMPORT_SEQ = "bulk_import_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = BULK_IMPORT_SEQ)
  @SequenceGenerator(name = BULK_IMPORT_SEQ, sequenceName = BULK_IMPORT_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotNull
  private ApplicationType application;

  @NotNull
  private BusinessObjectType objectType;

  @NotNull
  private ImportType importType;

  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String importFileUrl;

  @Size(max = AtlasFieldLengths.LENGTH_100)
  private String logFileUrl;

  @NotNull
  private String creator;

  private String inNameOf;

  @CreationTimestamp
  private LocalDateTime creationDate;

  @AssertTrue
  boolean isObjectTypeAllowedForApplication() {
    return switch (application) {
      case SEPODI -> BusinessObjectType.SEPODI_BUSINESS_OBJECTS.contains(objectType);
      case PRM -> BusinessObjectType.PRM_BUSINESS_OBJECTS.contains(objectType);
      default -> throw new IllegalStateException("Unexpected value: " + application);
    };
  }

  public BulkImportConfig getBulkImportConfig() {
    return BulkImportConfig.builder()
        .application(application)
        .objectType(objectType)
        .importType(importType)
        .build();
  }
}
