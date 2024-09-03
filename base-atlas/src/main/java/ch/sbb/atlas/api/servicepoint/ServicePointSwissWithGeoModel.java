package ch.sbb.atlas.api.servicepoint;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldNameConstants
@Schema(name = "servicePointSwissWithGeo")
public class ServicePointSwissWithGeoModel {

  @NotNull
  private String sloid;

  @NotEmpty
  private List<Detail> details;

  @AllArgsConstructor
  @Builder
  @Data
  public static class Detail {

    @NotNull
    private Long id;

    @NotNull
    private LocalDate validFrom;

  }

}
