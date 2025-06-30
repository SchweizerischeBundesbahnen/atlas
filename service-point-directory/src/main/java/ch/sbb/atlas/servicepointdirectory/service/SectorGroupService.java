package ch.sbb.atlas.servicepointdirectory.service;

import ch.sbb.atlas.api.servicepoint.CreateSectorGroupVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadSectorGroupVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.SectorGroupVersion;
import ch.sbb.atlas.servicepointdirectory.mapper.SectorGroupMapper;
import ch.sbb.atlas.servicepointdirectory.repository.SectorGroupVersionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SectorGroupService {

  private final SectorGroupVersionRepository sectorGroupVersionRepository;

  public List<SectorGroupVersion> getSectorGroups() {
    return sectorGroupVersionRepository.findAll();
  }

  public ReadSectorGroupVersionModel createSectorGroup(CreateSectorGroupVersionModel createSectorGroupVersionModel) {
    SectorGroupVersion sectorGroupVersion = SectorGroupMapper.toEntity(createSectorGroupVersionModel);
    SectorGroupVersion saved = sectorGroupVersionRepository.saveAndFlush(sectorGroupVersion);
    return SectorGroupMapper.toModel(saved);
  }

}
