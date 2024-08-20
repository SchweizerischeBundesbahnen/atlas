package ch.sbb.importservice.entity;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class BulkImportRequest {
    private ApplicationType applicationType;
    private BusinessObjectType objectType;
    private ImportType importType;
    private String inNameOf;
    private List<String> emails;
}
