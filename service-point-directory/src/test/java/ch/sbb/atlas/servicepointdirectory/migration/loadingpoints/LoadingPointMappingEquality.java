package ch.sbb.atlas.servicepointdirectory.migration.loadingpoints;

import static ch.sbb.atlas.imports.util.CsvReader.dateFromString;
import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.servicepoint.ServicePointNumber;

public record LoadingPointMappingEquality(LoadingPointDidokCsvModel didokCsvLine, LoadingPointAtlasCsvModel atlasCsvLine) {

  public void performCheck() {
    assertThat(atlasCsvLine.getNumber()).isEqualTo(didokCsvLine.getNumber());
    assertThat(atlasCsvLine.getDesignation()).isEqualTo(didokCsvLine.getDesignation());
    assertThat(atlasCsvLine.getDesignationLong()).isEqualTo(didokCsvLine.getDesignationLong());
    assertThat(atlasCsvLine.getConnectionPoint()).isEqualTo(didokCsvLine.getConnectionPoint());
    assertThat(atlasCsvLine.getServicePointNumber()).isEqualTo(didokCsvLine.getServicePointNumber());
    assertThat(dateFromString(atlasCsvLine.getValidFrom())).isEqualTo(didokCsvLine.getValidFrom());
    assertThat(dateFromString(atlasCsvLine.getValidTo())).isEqualTo(didokCsvLine.getValidTo());
    assertThat(atlasCsvLine.getCreationDate()).isEqualTo(didokCsvLine.getCreatedAt());
    assertThat(atlasCsvLine.getEditionDate()).isEqualTo(didokCsvLine.getEditedAt());

    if (atlasCsvLine.getParentSloidServicePoint() != null) {
      assertThat(getParentServicePointNumberFromParentSLOID()).isEqualTo(
          ServicePointNumber.ofNumberWithoutCheckDigit(didokCsvLine.getServicePointNumber()).getNumberShort());
    }
  }

  private Integer getParentServicePointNumberFromParentSLOID() {
    final int beginIndex = atlasCsvLine.getParentSloidServicePoint().lastIndexOf(":") + 1;
    return Integer.valueOf(atlasCsvLine.getParentSloidServicePoint().substring(beginIndex));
  }

}
