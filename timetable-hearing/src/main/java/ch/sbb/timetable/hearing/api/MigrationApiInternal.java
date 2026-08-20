package ch.sbb.timetable.hearing.api;

import ch.sbb.atlas.annotation.AdminOnly;
import ch.sbb.atlas.api.AtlasApiConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = AtlasApiConstants.INTERNAL_API_TAG_PREFIX + "Migration")
@RequestMapping(MigrationApiInternal.BASE_PATH)
public interface MigrationApiInternal {

  String BASE_PATH = "/internal/migrate-from-lidi";

  @AdminOnly
  @PostMapping
  void copyFromLidi();
}
