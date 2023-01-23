package ch.sbb.atlas.base.service.imports.servicepoint.servicepoint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServicePointCsvModelContainer {

  private Integer didokCode;

  private List<ServicePointCsvModel> servicePointCsvModelList;

}
