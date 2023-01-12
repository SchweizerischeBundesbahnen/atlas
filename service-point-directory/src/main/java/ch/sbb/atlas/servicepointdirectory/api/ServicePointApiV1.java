package ch.sbb.atlas.servicepointdirectory.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "ServicePoints")
@RequestMapping("v1/service-points")
public interface ServicePointApiV1 {

  @GetMapping("{servicePointNumber}")
  List<ServicePointVersionModel> getServicePointVersions(@PathVariable Integer servicePointNumber);

  @GetMapping("versions/{id}")
  ServicePointVersionModel getServicePointVersion(@PathVariable Long id);

}
