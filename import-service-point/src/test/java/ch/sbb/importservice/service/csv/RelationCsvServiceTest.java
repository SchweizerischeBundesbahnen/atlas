package ch.sbb.importservice.service.csv;

import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModel;
import ch.sbb.atlas.imports.prm.contactpoint.ContactPointCsvModelContainer;
import ch.sbb.atlas.imports.prm.relation.RelationCsvModel;
import ch.sbb.atlas.imports.prm.relation.RelationCsvModelContainer;
import ch.sbb.atlas.testdata.prm.ContactPointCsvTestData;
import ch.sbb.atlas.testdata.prm.RelationCsvTestData;
import ch.sbb.importservice.service.FileHelperService;
import ch.sbb.importservice.service.JobHelperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.openMocks;

public class RelationCsvServiceTest {
    private RelationCsvService relationCsvService;

    @Mock
    private FileHelperService fileHelperService;

    @Mock
    private JobHelperService jobHelperService;

    @BeforeEach
    void setUp() {
        openMocks(this);
        relationCsvService = new RelationCsvService(fileHelperService, jobHelperService);
    }

    @Test
    void shouldHaveCorrectFileNameRelation() {
        CsvFileNameModel csvFileName = relationCsvService.csvFileNameModel();
        assertThat(csvFileName.getFileName()).startsWith("PRM_INFO_DESKS");
    }


    @Test
    void shouldMergeSequentialEqualsRelations() {
        // given
        RelationCsvModel relationCsvModel1 = RelationCsvTestData.getCsvModel();
        RelationCsvModel relationCsvModel2 = RelationCsvTestData.getCsvModel();
        relationCsvModel2.setValidFrom(LocalDate.of(2026, 1, 1));
        relationCsvModel2.setValidTo(LocalDate.of(2026, 12, 31));
        List<RelationCsvModel> csvModels = List.of(relationCsvModel1, relationCsvModel2);

        // when
        List<RelationCsvModelContainer> result = relationCsvService.mapToRelationCsvModelContainers(csvModels);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCsvModels()).hasSize(1);
        assertThat(result.get(0).getCsvModels().get(0).getValidFrom()).isEqualTo(relationCsvModel1.getValidFrom());
        assertThat(result.get(0).getCsvModels().get(0).getValidTo()).isEqualTo(relationCsvModel2.getValidTo());
    }

    @Test
    void shouldMergeEqualsRelations() {
        // given
        RelationCsvModel relationCsvModel1 = RelationCsvTestData.getCsvModel();
        RelationCsvModel relationCsvModel2 = RelationCsvTestData.getCsvModel();
        List<RelationCsvModel> csvModels = List.of(relationCsvModel1, relationCsvModel2);

        // when
        List<RelationCsvModelContainer> result = relationCsvService.mapToRelationCsvModelContainers(csvModels);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCsvModels()).hasSize(1);
        assertThat(result.get(0).getCsvModels().get(0).getValidFrom()).isEqualTo(relationCsvModel1.getValidFrom());
        assertThat(result.get(0).getCsvModels().get(0).getValidTo()).isEqualTo(relationCsvModel2.getValidTo());
    }

    @Test
    void shouldReturnOnlyActiveRelations() {
        // given
        RelationCsvModel relationCsvModel1 = RelationCsvTestData.getCsvModel();
        relationCsvModel1.setStatus(1);
        RelationCsvModel relationCsvModel2 = RelationCsvTestData.getCsvModel();
        relationCsvModel2.setValidFrom(relationCsvModel1.getValidTo().plusDays(1));
        relationCsvModel2.setValidTo(relationCsvModel1.getValidTo().plusYears(1));
        relationCsvModel2.setStatus(0);
        List<RelationCsvModel> csvModels = List.of(relationCsvModel1,relationCsvModel2);

        // when
        List<RelationCsvModelContainer> result = relationCsvService.mapToRelationCsvModelContainers(csvModels);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCsvModels()).hasSize(1);
        assertThat(result.get(0).getCsvModels().get(0).getStatus()).isEqualTo(1);
    }
}
