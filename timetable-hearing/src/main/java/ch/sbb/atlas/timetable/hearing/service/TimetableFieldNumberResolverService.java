package ch.sbb.atlas.timetable.hearing.service;

import ch.sbb.atlas.timetable.hearing.model.TimetableFieldNumberInformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimetableFieldNumberResolverService {

  public String resolveTtfnid(TimetableFieldNumberInformation information) {
    // TODO: implement

    // Fallback to nothing
    return null;
  }

}
