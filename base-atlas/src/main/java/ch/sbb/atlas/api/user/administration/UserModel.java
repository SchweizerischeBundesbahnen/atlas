package ch.sbb.atlas.api.user.administration;

import ch.sbb.atlas.api.user.administration.enumeration.UserAccountStatus;
import ch.sbb.atlas.redact.Redacted;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.lang3.StringUtils;

@Builder(toBuilder = true)
@Data
@FieldNameConstants
@Redacted
@Schema(name = "User")
@AllArgsConstructor
@NoArgsConstructor
public class UserModel implements UserAdministrationEvent {

  @NotNull
  @Schema(description = "SBB User Id", example = "u111111")
  private String sbbUserId;

  @Redacted
  @Schema(description = "User lastname", example = "Mustermann")
  private String lastName;

  @Redacted
  @Schema(description = "User firstname", example = "Max")
  private String firstName;

  @Redacted
  @Schema(description = "User E-Mail address (from Azure)", example = "example@sbb.ch")
  private String mail;

  @Redacted
  @Schema(description = "Manually maintained E-Mail address. Overrides the Azure E-Mail address "
      + "(mail) when set, both for mail delivery and for display.", example = "example@sbb.ch")
  private String manualMail;

  @Redacted
  @Schema(description = "User display name (azure)", example = "Example User (IT-PTR-CEN2-YPT)")
  private String displayName;

  @Schema(description = "User account status", example = "ACTIVE")
  private UserAccountStatus accountStatus;

  @NotNull
  @Schema(description = "User permissions")
  private Set<PermissionModel> permissions;

  @Override
  public String getUserId() {
    return getSbbUserId();
  }

  /**
   * The manually maintained mail address always takes precedence over the Azure mail address.
   * Not serialized on purpose (see {@link ch.sbb.atlas.redact.RedactAspect}): redaction works by
   * reflecting over annotated fields, so a transported derived getter would silently bypass it.
   * Consumers derive the effective mail from the two transported fields ({@link #mail} and
   * {@link #manualMail}) themselves.
   */
  @JsonIgnore
  @Schema(hidden = true)
  @Redacted
  public String getEffectiveMail() {
    return StringUtils.isNotBlank(manualMail) ? manualMail : mail;
  }
}
