package ch.sbb.atlas.api.lidi;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Lines")
@RequestMapping("v2/lines")
public interface LineApiV2 {

  @GetMapping("versions/{slnid}")
  List<LineVersionModelV2> getLineVersions(@PathVariable String slnid);

}
