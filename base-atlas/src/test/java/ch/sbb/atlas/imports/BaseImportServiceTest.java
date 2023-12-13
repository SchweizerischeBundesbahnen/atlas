package ch.sbb.atlas.imports;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.imports.ItemImportResult.ItemImportResultBuilder;
import ch.sbb.atlas.imports.servicepoint.enumeration.ItemImportResponseStatus;
import ch.sbb.atlas.versioning.model.Versionable;
import java.time.LocalDate;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

class BaseImportServiceTest {

    private final DummyImportService baseImportService = new DummyImportService();

    @Test
    void shouldBuildSuccess() {
        DummyVersionable element = DummyVersionable.builder().validFrom(LocalDate.now()).validTo(LocalDate.now()).id(1L).build();

        ItemImportResult result = baseImportService.buildSuccessImportResult(element);
        assertThat(result.getStatus()).isEqualTo(ItemImportResponseStatus.SUCCESS);
    }

    @Test
    void shouldBuildWarning() {
        DummyVersionable element = DummyVersionable.builder().validFrom(LocalDate.now()).validTo(LocalDate.now()).id(1L).build();

        ItemImportResult result = baseImportService.buildWarningImportResult(element, Collections.emptyList());
        assertThat(result.getStatus()).isEqualTo(ItemImportResponseStatus.WARNING);
    }

    @Test
    void shouldBuildFailed() {
        DummyVersionable element = DummyVersionable.builder().validFrom(LocalDate.now()).validTo(LocalDate.now()).id(1L).build();

        ItemImportResult result = baseImportService.buildFailedImportResult(element, new IllegalStateException());
        assertThat(result.getStatus()).isEqualTo(ItemImportResponseStatus.FAILED);
    }

    private static class DummyImportService extends BaseImportService<DummyVersionable> {

        @Override
        protected void save(DummyVersionable element) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected ItemImportResult addInfoToItemImportResult(ItemImportResultBuilder itemImportResultBuilder, DummyVersionable element) {
            return itemImportResultBuilder
                .validFrom(element.getValidFrom())
                .validTo(element.getValidTo())
                .itemNumber(String.valueOf(element.getId()))
                .build();
        }
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    private static class DummyVersionable implements Versionable {

        private Long id;
        private LocalDate validFrom;
        private LocalDate validTo;
    }
}