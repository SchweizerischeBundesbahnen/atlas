import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AtlasApiService } from './atlasApi.service';
import { UpdateHearingStatementStatus } from '../model/updateHearingStatementStatus';
import { UpdateHearingCanton } from '../model/updateHearingCanton';
import { ContainerTimetableHearingStatementV2 } from '../model/containerTimetableHearingStatementV2';
import { SwissCanton } from '../model/swissCanton';
import { StatementStatus } from '../model/statementStatus';
import { TimetableHearingStatementV2 } from '../model/timetableHearingStatementV2';
import { TimetableHearingStatementAlternating } from '../model/timetableHearingStatementAlternating';
import { TransportCompany } from '../model/transportCompany';

@Injectable({
  providedIn: 'root',
})
export class TimetableHearingStatementInternalService extends AtlasApiService {

  public updateHearingStatementStatus(updateHearingStatementStatus: UpdateHearingStatementStatus): Observable<void> {
    const path = `/line-directory/internal/timetable-hearing/statements/update-statement-status`;
    return this.put(path, updateHearingStatementStatus);
  }

  public updateHearingCanton(updateHearingCantonModel: UpdateHearingCanton): Observable<void> {
    const path = `/line-directory/internal/timetable-hearing/statements/update-canton`;
    return this.put(path, updateHearingCantonModel);
  }

  public getStatements(
    timetableHearingYear?: number, canton?: SwissCanton, searchCriterias?: Array<string>, statusRestrictions?: Array<StatementStatus>,
    ttfnid?: string, transportCompanies?: Array<number>, page?: number, size?: number, sort?: Array<string>,
  ): Observable<ContainerTimetableHearingStatementV2> {
    const httpParams = this.paramsOf({
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
    const path = `/line-directory/internal/timetable-hearing/statements`;
    return this.get(path, httpParams);
  }

  public getStatementsAsCsv(
    language: string, timetableHearingYear?: number, canton?: SwissCanton, searchCriterias?: Array<string>,
    statusRestrictions?: Array<StatementStatus>, ttfnid?: string, transportCompanies?: Array<number>,
  ): Observable<Blob> {
    this.validateParams({ language });
    const httpParams = this.paramsOf({
      timetableHearingYear,
      canton,
      searchCriterias,
      statusRestrictions,
      ttfnid,
      transportCompanies,
    });
    return this.getBlob(`/line-directory/internal/timetable-hearing/statements/csv/${encodeURIComponent(String(language))}`, httpParams);
  }

  public getStatement(id: number): Observable<TimetableHearingStatementV2> {
    this.validateParams({ id });
    return this.get(`/line-directory/internal/timetable-hearing/statements/${encodeURIComponent(String(id))}`);
  }

  public getPreviousStatement(
    id: number, timetableHearingYear?: number, canton?: SwissCanton, searchCriterias?: Array<string>, statusRestrictions?: Array<StatementStatus>,
    ttfnid?: string, transportCompanies?: Array<number>, page?: number, size?: number, sort?: Array<string>,
  ): Observable<TimetableHearingStatementAlternating> {
    this.validateParams({ id });
    const httpParams = this.paramsOf({
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
    return this.get(`/line-directory/internal/timetable-hearing/statements/${encodeURIComponent(String(id))}/previous`, httpParams);
  }

  public getNextStatement(
    id: number, timetableHearingYear?: number, canton?: SwissCanton, searchCriterias?: Array<string>, statusRestrictions?: Array<StatementStatus>,
    ttfnid?: string, transportCompanies?: Array<number>, page?: number, size?: number, sort?: Array<string>,
  ): Observable<TimetableHearingStatementAlternating> {
    this.validateParams({ id });
    const httpParams = this.paramsOf({
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
    return this.get(`/line-directory/internal/timetable-hearing/statements/${encodeURIComponent(String(id))}/next`, httpParams);
  }

  public getStatementDocument(id: number, filename: string): Observable<Blob> {
    this.validateParams({ id, filename });
    return this.getBlob(`/line-directory/internal/timetable-hearing/statements/${encodeURIComponent(String(id))}/documents/${encodeURIComponent(String(filename))}`);
  }

  public deleteStatementDocument(id: number, filename: string): Observable<void> {
    this.validateParams({ id, filename });
    return this.delete(`/line-directory/internal/timetable-hearing/statements/${encodeURIComponent(String(id))}/documents/${encodeURIComponent(String(filename))}`);
  }

  public createStatement(statement: TimetableHearingStatementV2, documents?: Array<Blob>): Observable<TimetableHearingStatementV2> {
    this.validateParams({ statement });

    let formParams: { append(param: string, value: any): any; } = new FormData();
    formParams = formParams.append('statement', new Blob([JSON.stringify(statement)], { type: 'application/json' })) || formParams;
    if (documents) {
      documents.forEach((element) => {
        formParams = formParams.append('documents', element) || formParams;
      });
    }

    return this.httpClient.post<TimetableHearingStatementV2>(`${this.basePath}/line-directory/internal/timetable-hearing/statements`,
      formParams,
    );
  } // todo: test

  public updateHearingStatement(id: number, statement: TimetableHearingStatementV2, documents?: Array<Blob>): Observable<TimetableHearingStatementV2> {
    this.validateParams({ statement, id });

    let formParams: { append(param: string, value: any): any; } = new FormData();
    formParams = formParams.append('statement', new Blob([JSON.stringify(statement)], { type: 'application/json' })) || formParams;
    if (documents) {
      documents.forEach((element) => {
        formParams = formParams.append('documents', element) || formParams;
      });
    }

    return this.httpClient.put<TimetableHearingStatementV2>(`${this.basePath}/line-directory/internal/timetable-hearing/statements/${encodeURIComponent(String(id))}`,
      formParams,
    );
  } // todo: test

  public getResponsibleTransportCompanies(ttfnid: string, year: number): Observable<TransportCompany[]> {
    this.validateParams({ year, ttfnid });
    return this.get(`/line-directory/internal/timetable-hearing/statements/responsible-transport-companies/${encodeURIComponent(String(ttfnid))}/${encodeURIComponent(String(year))}`);
  }

}
