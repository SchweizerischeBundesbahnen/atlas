import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AtlasApiService } from '../atlas-api.service';
import { TthDossier } from '../../model/tthDossier';
import { DossierStatus } from '../../model/dossierStatus';
import { SwissCanton } from '../../model/swissCanton';
import { BoAnswer } from '../../model/boAnswer';
import { ContainerTthDossier } from '../../model/containerTthDossier';
import { StatementStatus } from '../../model/statementStatus';
import { HearingStatus } from '../../model/hearingStatus';
import { Language } from '../../model/language';

@Injectable({
  providedIn: 'root',
})
export class DossierInternalService {
  private readonly BASE_PATH = '/workflow/internal/tth/dossier';

  private readonly atlasApiService = inject(AtlasApiService);

  public getOverview(
    timetableYear?: number,
    hearingStatus?: HearingStatus,
    canton?: SwissCanton,
    boContactSbbuid?: string,
    searchCriterias?: Array<string>,
    statusRestrictions?: Array<DossierStatus>,
    page?: number,
    size?: number,
    sort?: Array<string>
  ): Observable<ContainerTthDossier> {
    const httpParams = this.atlasApiService.paramsOf({
      'timetableHearingYear.timetableYear': timetableYear,
      'timetableHearingYear.hearingStatus': hearingStatus,
      canton,
      boContactSbbuid,
      searchCriterias,
      statusRestrictions,
      page,
      size,
      sort,
    });
    return this.atlasApiService.get(this.BASE_PATH, httpParams);
  }

  public getDossier(id: number): Observable<TthDossier> {
    this.atlasApiService.validateParams({ id });
    return this.atlasApiService.get(`${this.BASE_PATH}/${encodeURIComponent(String(id))}`);
  }

  public createDossier(tthDossier: TthDossier): Observable<TthDossier> {
    this.atlasApiService.validateParams({ tthDossier });
    return this.atlasApiService.post(`${this.BASE_PATH}`, tthDossier);
  }

  public updateDossier(tthDossier: TthDossier): Observable<TthDossier> {
    this.atlasApiService.validateParams({ tthDossier });
    return this.atlasApiService.put(`${this.BASE_PATH}/${tthDossier.id}`, tthDossier);
  }

  public sendDossierToBo(dossierId: number): Observable<void> {
    return this.atlasApiService.post(`${this.BASE_PATH}/${dossierId}/send-to-bo`);
  }

  public completeDossier(dossierId: number, status: DossierStatus): Observable<void> {
    return this.atlasApiService.post(`${this.BASE_PATH}/${dossierId}/complete/${status}`);
  }

  public answerQuestion(dossierId: number, boAnswer: BoAnswer): Observable<void> {
    return this.atlasApiService.post(`${this.BASE_PATH}/answer/${dossierId}`, boAnswer);
  }

  public getDossiersAsCsv(
    lang: Language,
    timetableYear?: number,
    hearingStatus?: HearingStatus,
    canton?: SwissCanton,
    boContactSbbuid?: string,
    searchCriterias?: Array<string>,
    statusRestrictions?: Array<StatementStatus>
  ) {
    const httpParams = this.atlasApiService.paramsOf({
      'timetableHearingYear.timetableYear': timetableYear,
      'timetableHearingYear.hearingStatus': hearingStatus,
      canton,
      boContactSbbuid,
      searchCriterias,
      statusRestrictions,
      lang,
    });
    return this.atlasApiService.getBlob(`${this.BASE_PATH}/csv`, httpParams);
  }
}
