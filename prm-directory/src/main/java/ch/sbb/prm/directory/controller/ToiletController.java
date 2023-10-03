package ch.sbb.prm.directory.controller;

import ch.sbb.prm.directory.mapper.ToiletVersionMapper;
import ch.sbb.prm.directory.service.ToiletService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ToiletController implements ToiletApiV1 {

  private final ToiletService toiletService;

  @Override
  public List<ToiletVersionModel> getToilets() {
    return toiletService.getAllToilets().stream().map(ToiletVersionMapper::toModel).sorted().toList();
  }
}
