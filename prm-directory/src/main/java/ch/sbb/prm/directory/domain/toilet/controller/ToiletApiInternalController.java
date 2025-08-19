package ch.sbb.prm.directory.domain.toilet.controller;

import ch.sbb.atlas.api.prm.model.toilet.ToiletOverviewModel;
import ch.sbb.prm.directory.domain.toilet.service.ToiletService;
import ch.sbb.prm.directory.domain.toilet.api.ToiletApiInternal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ToiletApiInternalController implements ToiletApiInternal {

  private final ToiletService toiletService;

  @Override
  public List<ToiletOverviewModel> getToiletOverview(String parentServicePointSloid) {
    return toiletService.buildOverview(toiletService.findByParentServicePointSloid(parentServicePointSloid));
  }

}
