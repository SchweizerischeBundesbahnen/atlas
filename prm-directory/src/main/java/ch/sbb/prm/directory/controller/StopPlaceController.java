package ch.sbb.prm.directory.controller;

import ch.sbb.prm.directory.mapper.StopPlaceVersionMapper;
import ch.sbb.prm.directory.service.StopPlaceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StopPlaceController implements StopPlaceApiV1 {

  private final StopPlaceService stopPlaceService;
  @Override
  public List<StopPlaceVersionModel> getAllStopPaces() {
    return stopPlaceService.getAllStopPlaces().stream().map(StopPlaceVersionMapper::toModel).toList() ;
  }
}
