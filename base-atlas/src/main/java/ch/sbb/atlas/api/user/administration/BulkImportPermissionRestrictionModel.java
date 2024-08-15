package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class BulkImportPermissionRestrictionModel extends PermissionRestrictionModel<Boolean> {

    public BulkImportPermissionRestrictionModel() {
        super(PermissionRestrictionType.BULK_IMPORT);
    }

    public BulkImportPermissionRestrictionModel(Boolean value) {
        super(PermissionRestrictionType.BULK_IMPORT);
        this.value = value;
    }

    @NotNull
    private Boolean value;

    @Override
    public String getValueAsString() {
        return String.valueOf(getValue());
    }

    @Override
    public void setValueAsString(String value) {
        setValue(Boolean.valueOf(value));
    }
}
