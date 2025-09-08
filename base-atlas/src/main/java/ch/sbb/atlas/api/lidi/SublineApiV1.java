package ch.sbb.atlas.api.lidi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Sublines")
@RequestMapping("v1/sublines")
public interface SublineApiV1 {

  /**
   * @deprecated
   */
  @Deprecated(forRemoval = true, since = "2.328.0")
  @GetMapping("versions/{slnid}")
  @Operation(deprecated = true, summary = "Use /v2/sublines/versions/{slnid} instead. Will be removed by 09.10.2025.")
  List<SublineVersionModel> getSublineVersion(@PathVariable String slnid);

}
