package ch.sbb.exportservice.job.prm.ersad.service;

import ch.sbb.atlas.s3.service.FileService;
import ch.sbb.exportservice.job.prm.contactpoint.entity.ContactPointVersion;
import ch.sbb.exportservice.job.prm.ersad.assembler.ErsadStationAssembler;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadStation;
import ch.sbb.exportservice.job.prm.ersad.netex.ErsadNetexFileWriter;
import ch.sbb.exportservice.job.prm.ersad.reader.ErsadPrmReader;
import ch.sbb.exportservice.job.prm.ersad.reader.ErsadServicePointReader;
import ch.sbb.exportservice.job.prm.parkinglot.entity.ParkingLotVersion;
import ch.sbb.exportservice.job.prm.platform.entity.PlatformVersion;
import ch.sbb.exportservice.job.prm.relation.entity.RelationVersion;
import ch.sbb.exportservice.job.prm.stoppoint.entity.StopPointVersion;
import ch.sbb.exportservice.job.prm.toilet.entity.ToiletVersion;
import ch.sbb.exportservice.job.sepodi.servicepoint.entity.ServicePointVersion;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ErsadNetexGenerationTasklet implements Tasklet {

  private final ErsadPrmReader ersadPrmReader;
  private final ErsadServicePointReader ersadServicePointReader;
  private final ErsadStationAssembler ersadStationAssembler;
  private final Clock clock;
  private final FileService fileService;

  @Override
  public RepeatStatus execute(@NonNull StepContribution contribution, @NonNull ChunkContext chunkContext) throws Exception {
    LocalDate exportDate = LocalDate.now(clock);

    Map<String, StopPointVersion> stopPointsBySloid = ersadPrmReader.readValidatedTrainStopPoints();
    Set<String> stopPointSloids = stopPointsBySloid.keySet();

    Map<String, ServicePointVersion> servicePointsBySloid = ersadServicePointReader.readServicePoints(stopPointSloids);
    Map<String, List<PlatformVersion>> platformsByParentSloid = ersadPrmReader.readPlatforms(stopPointSloids);
    Set<String> platformSloids = platformsByParentSloid.values().stream()
        .flatMap(List::stream)
        .map(PlatformVersion::getSloid)
        .collect(Collectors.toSet());
    Map<String, String> trafficPointDesignations = ersadServicePointReader.readTrafficPointDesignations(platformSloids);
    Map<String, List<ContactPointVersion>> contactPointsByParentSloid = ersadPrmReader.readContactPoints(stopPointSloids);
    Map<String, List<ToiletVersion>> toiletsByParentSloid = ersadPrmReader.readToilets(stopPointSloids);
    Map<String, List<RelationVersion>> relationsByParentSloid = ersadPrmReader.readRelations(stopPointSloids);
    Map<String, List<ParkingLotVersion>> parkingLotsByParentSloid = ersadPrmReader.readParkingLots(stopPointSloids);

    List<ErsadStation> stations = ersadStationAssembler.assembleStations(
        stopPointsBySloid, servicePointsBySloid, platformsByParentSloid, trafficPointDesignations,
        contactPointsByParentSloid, toiletsByParentSloid, relationsByParentSloid, parkingLotsByParentSloid, exportDate);
    log.info("Assembled {} ERSAD stations for NeTEx export", stations.size());

    Path file = Path.of(exportFilePath());
    try (ErsadNetexFileWriter writer = ErsadNetexFileWriter.open(file, LocalDateTime.now(clock))) {
      writer.writeStopPlaces(stations.stream());
      writer.writeParkings(stations.stream());
      writer.writePathLinks(stations.stream());
    }

    return RepeatStatus.FINISHED;
  }

  private String exportFilePath() {
    ExportFilePathV2 filePath = ExportFilePathV2.getV2Builder(ExportObjectV2.ERSAD_NETEX, ExportTypeV2.ACTUAL)
        .extension(ExportExtensionFileType.XML_EXTENSION.getExtension())
        .systemDir(fileService.getDir())
        .build();
    return filePath.actualDateFilePath();
  }
}
