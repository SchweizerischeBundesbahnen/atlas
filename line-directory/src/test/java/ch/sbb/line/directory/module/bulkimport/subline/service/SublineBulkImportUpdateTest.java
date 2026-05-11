package ch.sbb.line.directory.module.bulkimport.subline.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.SublineVersionModelV2;
import ch.sbb.atlas.api.lidi.enumaration.SublineConcessionType;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.SublineUpdateCsvModel;
import ch.sbb.line.directory.module.subline.SublineTestData;
import ch.sbb.line.directory.module.subline.entity.SublineVersion;
import org.junit.jupiter.api.Test;

class SublineBulkImportUpdateTest {

  @Test
  void shouldMapConcessionTypeIfGivenInCsv() {
    BulkImportUpdateContainer<SublineUpdateCsvModel> updateContainer = BulkImportUpdateContainer.<SublineUpdateCsvModel>builder()
        .object(SublineUpdateCsvModel.builder()
            .slnid("ch:1:slnid:12345:1")
            .sublineConcessionType(SublineConcessionType.CANTONALLY_APPROVED_LINE)
            .build())
        .inNameOf("e123456")
        .build();

    SublineVersion currentVersion = SublineVersion.builder()
        .mainlineSlnid(SublineTestData.MAINLINE_SLNID)
        .build();

    SublineVersionModelV2 updateModel = SublineBulkImportUpdate.apply(updateContainer, currentVersion);
    assertThat(updateModel.getSublineConcessionType()).isEqualTo(SublineConcessionType.CANTONALLY_APPROVED_LINE);
    assertThat(updateModel.getMainlineSlnid()).isEqualTo(SublineTestData.MAINLINE_SLNID);
  }

  @Test
  void shouldKeepConcessionTypeOfEntityIfNotGivenInCsv() {
    BulkImportUpdateContainer<SublineUpdateCsvModel> updateContainer = BulkImportUpdateContainer.<SublineUpdateCsvModel>builder()
        .object(SublineUpdateCsvModel.builder()
            .slnid("ch:1:slnid:12345:1")
            .build())
        .inNameOf("e123456")
        .build();

    SublineVersion currentVersion = SublineVersion.builder().concessionType(SublineConcessionType.LINE_ABROAD).build();

    SublineVersionModelV2 updateModel = SublineBulkImportUpdate.apply(updateContainer, currentVersion);
    assertThat(updateModel.getSublineConcessionType()).isEqualTo(SublineConcessionType.LINE_ABROAD);
  }
}