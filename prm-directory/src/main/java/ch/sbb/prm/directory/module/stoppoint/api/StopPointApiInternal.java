package ch.sbb.prm.directory.module.stoppoint.api;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.model.stoppoint.RecordingObligationModel;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Person with Reduced Mobility")
@RequestMapping("internal/stop-points")
public interface StopPointApiInternal {

  @ResponseStatus(HttpStatus.OK)
  @PutMapping(path = "/recording-obligation/{sloid}")
  void updateRecordingObligation(@PathVariable String sloid, @RequestBody @Valid RecordingObligationModel recordingObligation);

  @ResponseStatus(HttpStatus.OK)
  @GetMapping(path = "/recording-obligation/{sloid}")
  RecordingObligationModel getRecordingObligation(@PathVariable String sloid);

}
