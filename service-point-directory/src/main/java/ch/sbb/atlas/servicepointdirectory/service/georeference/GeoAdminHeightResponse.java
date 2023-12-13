package ch.sbb.atlas.servicepointdirectory.service.georeference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoAdminHeightResponse {
    Double height;
}
