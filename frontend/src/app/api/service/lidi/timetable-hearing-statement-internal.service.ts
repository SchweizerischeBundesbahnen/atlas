import {inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {AtlasApiService} from '../atlasApi.service';
import {UpdateHearingStatementStatus} from '../../model/updateHearingStatementStatus';
import {UpdateHearingCanton} from '../../model/updateHearingCanton';
import {ContainerTimetableHearingStatementV2} from '../../model/containerTimetableHearingStatementV2';
import {SwissCanton} from '../../model/swissCanton';
import {StatementStatus} from '../../model/statementStatus';
import {TimetableHearingStatementV2} from '../../model/timetableHearingStatementV2';
import {TimetableHearingStatementAlternating} from '../../model/timetableHearingStatementAlternating';
import {TransportCompany} from '../../model/transportCompany';

@Injectable({
  providedIn: 'root',
})
export class TimetableHearingStatementInternalService {

  private readonly STATEMENTS = '/line-directory/internal/timetable-hearing/statements';

  private readonly atlasApiService = inject(AtlasApiService);

  public updateHearingStatementStatus(updateHearingStatementStatus: UpdateHearingStatementStatus): Observable<void> {
    return this.atlasApiService.put(`${this.STATEMENTS}/update-statement-status`, updateHearingStatementStatus);
  }

  public updateHearingCanton(updateHearingCantonModel: UpdateHearingCanton): Observable<void> {
    return this.atlasApiService.put(`${this.STATEMENTS}/update-canton`, updateHearingCantonModel);
  }

  public getStatements(
    timetableHearingYear?: number, canton?: SwissCanton, searchCriterias?: Array<string>, statusRestrictions?: Array<StatementStatus>,
    ttfnid?: string, transportCompanies?: Array<number>, page?: number, size?: number, sort?: Array<string>,
  ): Observable<ContainerTimetableHearingStatementV2> {
    const httpParams = this.atlasApiService.paramsOf({
      timetableHearingYear,
      canton,
      searchCriterias,
      statusRestrictions,
      ttfnid,
      transportCompanies,
      page,
      size,
      sort,
    });
    return this.atlasApiService.get(this.STATEMENTS, httpParams);
  }

  public getStatementsAsCsv(
    language: string, timetableHearingYear?: number, canton?: SwissCanton, searchCriterias?: Array<string>,
    statusRestrictions?: Array<StatementStatus>, ttfnid?: string, transportCompanies?: Array<number>,
  ): Observable<Blob> {
    this.atlasApiService.validateParams({ language });
    const httpParams = this.atlasApiService.paramsOf({
      timetableHearingYear,
      canton,
      searchCriterias,
      statusRestrictions,
      ttfnid,
      transportCompanies,
    });
    return this.atlasApiService.getBlob(`${this.STATEMENTS}/csv/${encodeURIComponent(String(language))}`, httpParams);
  }

  public getStatement(id: number): Observable<TimetableHearingStatementV2> {
    this.atlasApiService.validateParams({ id });
    return this.atlasApiService.get(`${this.STATEMENTS}/${encodeURIComponent(String(id))}`);
  }

  public getPreviousStatement(
    id: number, timetableHearingYear?: number, canton?: SwissCanton, searchCriterias?: Array<string>, statusRestrictions?: Array<StatementStatus>,
    ttfnid?: string, transportCompanies?: Array<number>, page?: number, size?: number, sort?: Array<string>,
  ): Observable<TimetableHearingStatementAlternating> {
    this.atlasApiService.validateParams({ id });
    const httpParams = this.atlasApiService.paramsOf({
      timetableHearingYear,
      canton,
      searchCriterias,
      statusRestrictions,
      ttfnid,
      transportCompanies,
      page,
      size,
      sort,
    });
    return this.atlasApiService.get(`${this.STATEMENTS}/${encodeURIComponent(String(id))}/previous`, httpParams);
  }

  public getNextStatement(
    id: number, timetableHearingYear?: number, canton?: SwissCanton, searchCriterias?: Array<string>, statusRestrictions?: Array<StatementStatus>,
    ttfnid?: string, transportCompanies?: Array<number>, page?: number, size?: number, sort?: Array<string>,
  ): Observable<TimetableHearingStatementAlternating> {
    this.atlasApiService.validateParams({ id });
    const httpParams = this.atlasApiService.paramsOf({
      timetableHearingYear,
      canton,
      searchCriterias,
      statusRestrictions,
      ttfnid,
      transportCompanies,
      page,
      size,
      sort,
    });
    return this.atlasApiService.get(`${this.STATEMENTS}/${encodeURIComponent(String(id))}/next`, httpParams);
  }

  public getStatementDocument(id: number, filename: string): Observable<Blob> {
    this.atlasApiService.validateParams({ id, filename });
    return this.atlasApiService.getBlob(`${this.STATEMENTS}/${encodeURIComponent(String(id))}/documents/${encodeURIComponent(String(filename))}`);
  }

  public createStatement(statement: TimetableHearingStatementV2, documents?: Array<Blob>): Observable<TimetableHearingStatementV2> {
    this.atlasApiService.validateParams({ statement });
    return this.atlasApiService.post(this.STATEMENTS,
      this.atlasApiService.createFormData({statement, documents}),
      { responseType: 'json' },
    );
  }

  public updateHearingStatement(id: number, statement: TimetableHearingStatementV2, documents?: Array<Blob>): Observable<TimetableHearingStatementV2> {
    this.atlasApiService.validateParams({ statement, id });
    return this.atlasApiService.put(`${this.STATEMENTS}/${encodeURIComponent(String(id))}`,
      this.atlasApiService.createFormData({statement, documents}),
      { responseType: 'json' },
    );
  }

  public getResponsibleTransportCompanies(ttfnid: string, year: number): Observable<TransportCompany[]> {
    this.atlasApiService.validateParams({ year, ttfnid });
    return this.atlasApiService.get(`${this.STATEMENTS}/responsible-transport-companies/${encodeURIComponent(String(ttfnid))}/${encodeURIComponent(String(year))}`);
  }
}
