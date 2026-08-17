package ch.sbb.timetable.hearing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.timetable.hearing.repository.DossierRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DossierServiceUnitTest {

  @Mock
  private DossierRepository dossierRepository;

  @InjectMocks
  private DossierService dossierService;

  @Test
  void shouldGetStatementIdsFromDossierStatus() {
    // given
    when(dossierRepository.findStatementIdsByDossierStatusIn(
        List.of(DossierStatus.ADDED, DossierStatus.DOSSIER_CANTON_CHECK))).thenReturn(List.of(2L));
    // when & then
    assertThat(dossierService.getStatementIdsFromDossierStatus(List.of(DossierStatus.ADDED,
        DossierStatus.DOSSIER_CANTON_CHECK))).containsExactly(2L);
  }

  @Test
  void shouldUpdateDossierStatusClosingYear() {
    // given
    doNothing().when(dossierRepository).updateDossierStatus(any(DossierStatus.class), anyCollection());
    // when
    dossierService.updateDossierStatusClosingYear();
    // then
    verify(dossierRepository).updateDossierStatus(DossierStatus.CANCELED, List.of(DossierStatus.ADDED));
    verify(dossierRepository).updateDossierStatus(DossierStatus.DISSOLVED,
        List.of(DossierStatus.MOVED, DossierStatus.DOSSIER_BO_CHECK, DossierStatus.DOSSIER_CANTON_CHECK));
  }
}