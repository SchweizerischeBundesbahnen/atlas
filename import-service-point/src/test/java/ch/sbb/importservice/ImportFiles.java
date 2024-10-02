package ch.sbb.importservice;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.bulk.TrafficPointUpdateCsvModel;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.importservice.service.bulk.reader.BulkImportCsvReader;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ImportFiles {

  public static File getFileByPath(String path) {
    return new File(Objects.requireNonNull(ImportFiles.class.getClassLoader().getResource(path)).getFile());
  }

  public static void assertThatFileContainsExpectedServicePointUpdate(File csvFile){
    List<BulkImportUpdateContainer<ServicePointUpdateCsvModel>> servicePointUpdates =
        BulkImportCsvReader.readLinesFromFileWithNullingValue(
            csvFile, ServicePointUpdateCsvModel.class);

    assertThat(servicePointUpdates).hasSize(1);
    assertThat(servicePointUpdates.getFirst().getAttributesToNull()).containsExactly("height");

    ServicePointUpdateCsvModel expected = ImportFiles.getExpectedServicePointUpdateCsvModel();
    ServicePointUpdateCsvModel actual = servicePointUpdates.getFirst().getObject();
    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  public static ServicePointUpdateCsvModel getExpectedServicePointUpdateCsvModel() {
    return ServicePointUpdateCsvModel.builder()
        .sloid("ch:1:sloid:7000")
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .designationOfficial("Bern")
        .stopPointType(StopPointType.ORDERLY)
        .freightServicePoint(true)
        .meansOfTransport(Set.of(MeanOfTransport.BUS, MeanOfTransport.TRAIN))
        .sortCodeOfDestinationStation("70003")
        .businessOrganisation("ch:1:sboid:100001")
        .east(2600037.945)
        .north(1199749.812)
        .spatialReference(SpatialReference.LV95)
        .build();
  }

  public static TrafficPointUpdateCsvModel getExpectedTrafficPointUpdateCsvModel() {
    return TrafficPointUpdateCsvModel.builder()
        .sloid("ch:1:sloid:7000")
        .validFrom(LocalDate.of(2021, 4, 1))
        .validTo(LocalDate.of(2099, 12, 31))
        .designation("Bern")
        .east(2600037.945)
        .north(1199749.812)
        .spatialReference(SpatialReference.LV95)
        .height(540.2)
        .build();
  }

}
