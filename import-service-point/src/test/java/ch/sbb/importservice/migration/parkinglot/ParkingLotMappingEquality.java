package ch.sbb.importservice.migration.parkinglot;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.export.model.prm.ParkingLotVersionCsvModel;
import ch.sbb.atlas.imports.prm.parkinglot.ParkingLotCsvModel;
import ch.sbb.importservice.migration.MigrationUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record ParkingLotMappingEquality(ParkingLotCsvModel didokCsvLine, ParkingLotVersionCsvModel atlasCsvLine) {

    public void performCheck() {
        assertThat(atlasCsvLine.getParentNumberServicePoint()).isEqualTo(MigrationUtil.removeCheckDigit(didokCsvLine.getDidokCode()));
        assertThat(atlasCsvLine.getSloid()).isEqualTo(didokCsvLine.getSloid());
        if (atlasCsvLine.getParentSloidServicePoint() != null && didokCsvLine.getDsSloid() != null) {
            assertThat(atlasCsvLine.getParentSloidServicePoint()).isEqualTo(
                    didokCsvLine.getDsSloid());
        }
        if (atlasCsvLine.getDesignation() != null && didokCsvLine.getDescription() != null) {
            assertThat(atlasCsvLine.getDesignation()).isEqualTo((didokCsvLine.getDescription()));
        }

        assertThat(atlasCsvLine.getPlacesAvailable().getRank()).isEqualTo(didokCsvLine.getPlacesAvailable());
        assertThat(atlasCsvLine.getPrmPlacesAvailable().getRank()).isEqualTo(didokCsvLine.getPrmPlacesAvailable());

        assertThat(localDateFromString(atlasCsvLine.getCreationDate())).isEqualTo(didokCsvLine.getCreatedAt());
        assertThat(localDateFromString(atlasCsvLine.getEditionDate())).isEqualTo(didokCsvLine.getModifiedAt());

    }

    public LocalDateTime localDateFromString(String string) {
        return LocalDateTime.parse(string, DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN));
    }
}
