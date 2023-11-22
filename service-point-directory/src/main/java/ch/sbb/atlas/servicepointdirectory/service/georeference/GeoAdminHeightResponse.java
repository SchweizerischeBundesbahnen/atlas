package ch.sbb.atlas.servicepointdirectory.service.georeference;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "GeoReference")
public class GeoAdminHeightResponse {
    String height;
}
