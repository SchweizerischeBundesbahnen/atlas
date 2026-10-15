package ch.sbb.line.directory.module.tth.controller;

import ch.sbb.atlas.api.timetable.hearing.ExternalTimetableHearingStatementCreateModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementApiV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.line.directory.module.tth.exception.TimetableHearingMovedException;
import java.util.List;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Keeps the moved endpoint in the line-directory OpenAPI spec, marked deprecated. At runtime the gateway route
 * {@code tth-legacy-v2} forwards this path to timetable-hearing, so this controller is only reached by direct calls.
 */
@Deprecated
@RestController
public class TimetableHearingStatementControllerV2 implements TimetableHearingStatementApiV2 {

  @Override
  public TimetableHearingStatementModelV2 createStatementExternal(ExternalTimetableHearingStatementCreateModel statement,
      List<MultipartFile> documents) {
    throw new TimetableHearingMovedException();
  }

}
