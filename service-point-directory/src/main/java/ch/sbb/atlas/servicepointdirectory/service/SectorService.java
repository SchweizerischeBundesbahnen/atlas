package ch.sbb.atlas.servicepointdirectory.service;

import ch.sbb.atlas.api.servicepoint.CreateSectorVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadSectorVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.SectorVersion;
import ch.sbb.atlas.servicepointdirectory.mapper.SectorMapper;
import ch.sbb.atlas.servicepointdirectory.repository.SectorVersionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SectorService {

  private final SectorVersionRepository sectorVersionRepository;

  public List<SectorVersion> getAllSectorVersions() {
    return sectorVersionRepository.findAll();
  }

  public ReadSectorVersionModel createSector(CreateSectorVersionModel createSectorVersionModel) {
    SectorVersion sectorVersion = SectorMapper.toEntity(createSectorVersionModel);
    SectorVersion saved = sectorVersionRepository.saveAndFlush(sectorVersion);
    return SectorMapper.toModel(saved);
  }

}
