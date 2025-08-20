package ch.sbb.prm.directory.module.toilet;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.prm.model.toilet.ReadToiletVersionModel;
import ch.sbb.atlas.api.prm.model.toilet.ToiletVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.prm.directory.module.toilet.api.ToiletApiV1;
import ch.sbb.prm.directory.module.toilet.entity.ToiletVersion;
import ch.sbb.prm.directory.module.toilet.mapper.ToiletVersionMapper;
import ch.sbb.prm.directory.module.toilet.search.ToiletSearchRestrictions;
import ch.sbb.prm.directory.module.toilet.service.ToiletService;
import ch.sbb.prm.directory.search.model.PrmObjectRequestParams;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ToiletApiV1Controller implements ToiletApiV1 {

  private final ToiletService toiletService;

  @Override
  public Container<ReadToiletVersionModel> getToilets(Pageable pageable, PrmObjectRequestParams prmObjectRequestParams) {
    ToiletSearchRestrictions searchRestrictions = ToiletSearchRestrictions.builder()
        .pageable(pageable)
        .prmObjectRequestParams(prmObjectRequestParams)
        .build();
    Page<ToiletVersion> toiletVersions = toiletService.findAll(searchRestrictions);
    return Container.<ReadToiletVersionModel>builder()
        .objects(toiletVersions.stream().map(ToiletVersionMapper::toModel).toList())
        .totalCount(toiletVersions.getTotalElements())
        .build();
  }

  @Override
  public ReadToiletVersionModel createToiletVersion(ToiletVersionModel model) {
    ToiletVersion toiletVersion = toiletService.createToilet(ToiletVersionMapper.toEntity(model));
    return ToiletVersionMapper.toModel(toiletVersion);
  }

  @Override
  public List<ReadToiletVersionModel> updateToiletVersion(Long id, ToiletVersionModel model) {
    ToiletVersion toiletVersion =
        toiletService.getToiletVersionById(id).orElseThrow(() -> new IdNotFoundException(id));
    ToiletVersion editedVersion = ToiletVersionMapper.toEntity(model);
    toiletService.updateToiletVersion(toiletVersion, editedVersion);

    return toiletService.getAllVersions(toiletVersion.getSloid()).stream()
        .map(ToiletVersionMapper::toModel).toList();
  }

  @Override
  public List<ReadToiletVersionModel> getToiletVersions(String sloid) {
    return toiletService.getAllVersions(sloid).stream().map(ToiletVersionMapper::toModel).toList();
  }

}
