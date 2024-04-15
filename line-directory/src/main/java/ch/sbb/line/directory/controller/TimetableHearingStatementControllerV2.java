package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementApiV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TimetableHearingStatementControllerV2 implements TimetableHearingStatementApiV2 {
  @Override
  public TimetableHearingStatementModel createStatementV2(TimetableHearingStatementModel statement,
      List<MultipartFile> documents) {
    return null;
  }

}
