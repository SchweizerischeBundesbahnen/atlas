package ch.sbb.workflow.module.sepodi.hearing.model.sepodi;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@Schema(name = "OtpResponse")
public class OtpResponseModel {

  @Schema(description = "True if a new one-time code was generated and sent by mail. False if the previously sent "
      + "one-time code is still valid and therefore has to be reused")
  private boolean mailSent;

  @Schema(description = "Seconds until the currently valid one-time code expires. A new one-time code can only be "
      + "obtained after this period")
  private long expiresInSeconds;

}
