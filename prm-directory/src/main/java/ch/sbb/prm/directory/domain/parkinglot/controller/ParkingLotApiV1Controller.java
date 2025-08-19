package ch.sbb.prm.directory.domain.parkinglot.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.prm.model.parkinglot.ParkingLotVersionModel;
import ch.sbb.atlas.api.prm.model.parkinglot.ReadParkingLotVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.prm.directory.search.model.PrmObjectRequestParams;
import ch.sbb.prm.directory.domain.parkinglot.api.ParkingLotApiV1;
import ch.sbb.prm.directory.domain.parkinglot.entity.ParkingLotVersion;
import ch.sbb.prm.directory.domain.parkinglot.mapper.ParkingLotVersionMapper;
import ch.sbb.prm.directory.domain.parkinglot.service.ParkingLotService;
import ch.sbb.prm.directory.domain.parkinglot.search.ParkingLotSearchRestrictions;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ParkingLotApiV1Controller implements ParkingLotApiV1 {

  private final ParkingLotService parkingLotService;

  @Override
  public Container<ReadParkingLotVersionModel> getParkingLots(Pageable pageable,
      PrmObjectRequestParams prmObjectRequestParams) {
    ParkingLotSearchRestrictions searchRestrictions = ParkingLotSearchRestrictions.builder()
        .pageable(pageable)
        .prmObjectRequestParams(prmObjectRequestParams)
        .build();

    Page<ParkingLotVersion> platformVersions = parkingLotService.findAll(searchRestrictions);

    return Container.<ReadParkingLotVersionModel>builder()
        .objects(platformVersions.stream().map(ParkingLotVersionMapper::toModel).toList())
        .totalCount(platformVersions.getTotalElements())
        .build();
  }

  @Override
  public List<ReadParkingLotVersionModel> getParkingLotVersions(String sloid) {
    return parkingLotService.getAllVersions(sloid).stream().map(ParkingLotVersionMapper::toModel).toList();
  }

  @Override
  public ReadParkingLotVersionModel createParkingLot(ParkingLotVersionModel model) {
    ParkingLotVersion parkingLotVersion = parkingLotService.createParkingLot(ParkingLotVersionMapper.toEntity(model));
    return ParkingLotVersionMapper.toModel(parkingLotVersion);
  }

  @Override
  public List<ReadParkingLotVersionModel> updateParkingLot(Long id, ParkingLotVersionModel model) {
    ParkingLotVersion parkingLotVersion =
        parkingLotService.getPlatformVersionById(id).orElseThrow(() -> new IdNotFoundException(id));

    ParkingLotVersion editedVersion = ParkingLotVersionMapper.toEntity(model);
    parkingLotService.updateParkingLotVersion(parkingLotVersion, editedVersion);

    return parkingLotService.getAllVersions(parkingLotVersion.getSloid()).stream()
        .map(ParkingLotVersionMapper::toModel).toList();
  }

}
