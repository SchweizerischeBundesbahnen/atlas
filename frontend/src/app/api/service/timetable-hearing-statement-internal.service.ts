import { Injectable } from '@angular/core';
import { HttpHeaders, HttpParams } from '@angular/common/http';
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
    return this.httpClient.request<void>('put', `${this.basePath}${path}`,
      {
        body: updateHearingStatementStatus,
        responseType: 'json',
        headers: new HttpHeaders({
          'Content-Type': 'application/json',
        }),
      },
    );
  }

  public updateHearingCanton(updateHearingCantonModel: UpdateHearingCanton): Observable<void> {
    const path = `/line-directory/internal/timetable-hearing/statements/update-canton`;
    return this.httpClient.put<void>(`${this.basePath}${path}`, updateHearingCantonModel, {
      responseType: 'json',
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
    });
  }

  public getStatements(
    timetableHearingYear?: number, canton?: SwissCanton, searchCriterias?: Array<string>, statusRestrictions?: Array<StatementStatus>,
    ttfnid?: string, transportCompanies?: Array<number>, page?: number, size?: number, sort?: Array<string>,
  ): Observable<ContainerTimetableHearingStatementV2> {

    let queryParameters = new HttpParams();

    if (timetableHearingYear !== undefined && timetableHearingYear !== null) {
      queryParameters = this.addToHttpParams(queryParameters, timetableHearingYear, 'timetableHearingYear');
    }
    if (canton !== undefined && canton !== null) {
      queryParameters = this.addToHttpParams(queryParameters, canton, 'canton');
    }
    if (searchCriterias) {
      searchCriterias.forEach((element) => {
        queryParameters = this.addToHttpParams(queryParameters, element, 'searchCriterias');
      });
    }
    if (statusRestrictions) {
      statusRestrictions.forEach((element) => {
        queryParameters = this.addToHttpParams(queryParameters, element, 'statusRestrictions');
      });
    }
    if (ttfnid !== undefined && ttfnid !== null) {
      queryParameters = this.addToHttpParams(queryParameters, ttfnid, 'ttfnid');
    }
    if (transportCompanies) {
      transportCompanies.forEach((element) => {
        queryParameters = this.addToHttpParams(queryParameters, element, 'transportCompanies');
      });
    }
    if (page !== undefined && page !== null) {
      queryParameters = this.addToHttpParams(queryParameters, page, 'page');
    }
    if (size !== undefined && size !== null) {
      queryParameters = this.addToHttpParams(queryParameters, size, 'size');
    }
    if (sort) {
      sort.forEach((element) => {
        queryParameters = this.addToHttpParams(queryParameters, element, 'sort');
      });
    }

    const path = `/line-directory/internal/timetable-hearing/statements`;

    return this.httpClient.get<ContainerTimetableHearingStatementV2>(`${this.basePath}${path}`,
      {
        params: queryParameters,
      },
    );
  }

  public getStatementsAsCsv(
    language: string, timetableHearingYear?: number, canton?: SwissCanton, searchCriterias?: Array<string>,
    statusRestrictions?: Array<StatementStatus>, ttfnid?: string, transportCompanies?: Array<number>,
  ): Observable<Blob> {

    if (language === null || language === undefined) {
      throw new Error('Required parameter language was null or undefined when calling getStatementsAsCsv.');
    }

    let queryParameters = new HttpParams();
    if (timetableHearingYear !== undefined && timetableHearingYear !== null) {
      queryParameters = this.addToHttpParams(queryParameters, timetableHearingYear, 'timetableHearingYear');
    }
    if (canton !== undefined && canton !== null) {
      queryParameters = this.addToHttpParams(queryParameters, canton, 'canton');
    }
    if (searchCriterias) {
      searchCriterias.forEach((element) => {
        queryParameters = this.addToHttpParams(queryParameters, element, 'searchCriterias');
      });
    }
    if (statusRestrictions) {
      statusRestrictions.forEach((element) => {
        queryParameters = this.addToHttpParams(queryParameters, element, 'statusRestrictions');
      });
    }
    if (ttfnid !== undefined && ttfnid !== null) {
      queryParameters = this.addToHttpParams(queryParameters, ttfnid, 'ttfnid');
    }
    if (transportCompanies) {
      transportCompanies.forEach((element) => {
        queryParameters = this.addToHttpParams(queryParameters, element, 'transportCompanies');
      });
    }

    return this.httpClient.get(`${this.basePath}/line-directory/internal/timetable-hearing/statements/csv/${encodeURIComponent(String(language))}`,
      {
        params: queryParameters,
        responseType: 'blob',
      },
    );
  }

  public getStatement(id: number): Observable<TimetableHearingStatementV2> {
    if (id === null || id === undefined) {
      throw new Error('Required parameter id was null or undefined when calling getStatement.');
    }
    return this.httpClient.get<TimetableHearingStatementV2>(`${this.basePath}/line-directory/internal/timetable-hearing/statements/${encodeURIComponent(String(id))}`);
  }

  public getPreviousStatement(
    id: number, timetableHearingYear?: number, canton?: SwissCanton, searchCriterias?: Array<string>, statusRestrictions?: Array<StatementStatus>,
    ttfnid?: string, transportCompanies?: Array<number>, page?: number, size?: number, sort?: Array<string>,
  ): Observable<TimetableHearingStatementAlternating> {
    if (id === null || id === undefined) {
      throw new Error('Required parameter id was null or undefined when calling getPreviousStatement.');
    }

    let queryParameters = new HttpParams();
    if (timetableHearingYear !== undefined && timetableHearingYear !== null) {
      queryParameters = this.addToHttpParams(queryParameters, timetableHearingYear, 'timetableHearingYear');
    }
    if (canton !== undefined && canton !== null) {
      queryParameters = this.addToHttpParams(queryParameters, canton, 'canton');
    }
    if (searchCriterias) {
      searchCriterias.forEach((element) => {
        queryParameters = this.addToHttpParams(queryParameters, element, 'searchCriterias');
      });
    }
    if (statusRestrictions) {
      statusRestrictions.forEach((element) => {
        queryParameters = this.addToHttpParams(queryParameters, element, 'statusRestrictions');
      });
    }
    if (ttfnid !== undefined && ttfnid !== null) {
      queryParameters = this.addToHttpParams(queryParameters, ttfnid, 'ttfnid');
    }
    if (transportCompanies) {
      transportCompanies.forEach((element) => {
        queryParameters = this.addToHttpParams(queryParameters, element, 'transportCompanies');
      });
    }
    if (page !== undefined && page !== null) {
      queryParameters = this.addToHttpParams(queryParameters, page, 'page');
    }
    if (size !== undefined && size !== null) {
      queryParameters = this.addToHttpParams(queryParameters, size, 'size');
    }
    if (sort) {
      sort.forEach((element) => {
        queryParameters = this.addToHttpParams(queryParameters, element, 'sort');
      });
    }

    return this.httpClient.get<TimetableHearingStatementAlternating>(`${this.basePath}/line-directory/internal/timetable-hearing/statements/${encodeURIComponent(String(id))}/previous`,
      {
        params: queryParameters,
      },
    );
  }

  public getNextStatement(
    id: number, timetableHearingYear?: number, canton?: SwissCanton, searchCriterias?: Array<string>, statusRestrictions?: Array<StatementStatus>,
    ttfnid?: string, transportCompanies?: Array<number>, page?: number, size?: number, sort?: Array<string>,
  ): Observable<TimetableHearingStatementAlternating> {
    if (id === null || id === undefined) {
      throw new Error('Required parameter id was null or undefined when calling getNextStatement.');
    }

    let queryParameters = new HttpParams();
    if (timetableHearingYear !== undefined && timetableHearingYear !== null) {
      queryParameters = this.addToHttpParams(queryParameters, timetableHearingYear, 'timetableHearingYear');
    }
    if (canton !== undefined && canton !== null) {
      queryParameters = this.addToHttpParams(queryParameters, canton, 'canton');
    }
    if (searchCriterias) {
      searchCriterias.forEach((element) => {
        queryParameters = this.addToHttpParams(queryParameters, element, 'searchCriterias');
      });
    }
    if (statusRestrictions) {
      statusRestrictions.forEach((element) => {
        queryParameters = this.addToHttpParams(queryParameters, element, 'statusRestrictions');
      });
    }
    if (ttfnid !== undefined && ttfnid !== null) {
      queryParameters = this.addToHttpParams(queryParameters, ttfnid, 'ttfnid');
    }
    if (transportCompanies) {
      transportCompanies.forEach((element) => {
        queryParameters = this.addToHttpParams(queryParameters, element, 'transportCompanies');
      });
    }
    if (page !== undefined && page !== null) {
      queryParameters = this.addToHttpParams(queryParameters, page, 'page');
    }
    if (size !== undefined && size !== null) {
      queryParameters = this.addToHttpParams(queryParameters, size, 'size');
    }
    if (sort) {
      sort.forEach((element) => {
        queryParameters = this.addToHttpParams(queryParameters, element, 'sort');
      });
    }

    return this.httpClient.get<TimetableHearingStatementAlternating>(`${this.basePath}/line-directory/internal/timetable-hearing/statements/${encodeURIComponent(String(id))}/next`,
      {
        params: queryParameters,
      },
    );
  }

  public getStatementDocument(id: number, filename: string): Observable<Blob> {
    if (id === null || id === undefined) {
      throw new Error('Required parameter id was null or undefined when calling getStatementDocument.');
    }
    if (filename === null || filename === undefined) {
      throw new Error('Required parameter filename was null or undefined when calling getStatementDocument.');
    }
    return this.httpClient.get(`${this.basePath}/line-directory/internal/timetable-hearing/statements/${encodeURIComponent(String(id))}/documents/${encodeURIComponent(String(filename))}`,
      {
        responseType: 'blob',
      },
    );
  }

  public deleteStatementDocument(id: number, filename: string): Observable<void> {
    if (id === null || id === undefined) {
      throw new Error('Required parameter id was null or undefined when calling deleteStatementDocument.');
    }
    if (filename === null || filename === undefined) {
      throw new Error('Required parameter filename was null or undefined when calling deleteStatementDocument.');
    }
    return this.httpClient.delete<void>(`${this.basePath}/line-directory/internal/timetable-hearing/statements/${encodeURIComponent(String(id))}/documents/${encodeURIComponent(String(filename))}`);
  }

  public createStatement(statement: TimetableHearingStatementV2, documents?: Array<Blob>): Observable<TimetableHearingStatementV2> {
    if (statement === null || statement === undefined) {
      throw new Error('Required parameter statement was null or undefined when calling createStatement.');
    }

    let formParams: { append(param: string, value: any): any; } = new FormData();
    formParams = formParams.append('statement', new Blob([JSON.stringify(statement)], {type: 'application/json'})) || formParams;
    if (documents) {
      documents.forEach((element) => {
        formParams = formParams.append('documents', element) || formParams;
      });
    }

    return this.httpClient.post<TimetableHearingStatementV2>(`${this.basePath}/line-directory/internal/timetable-hearing/statements`,
      formParams,
    );
  }

  public updateHearingStatement(id: number, statement: TimetableHearingStatementV2, documents?: Array<Blob>): Observable<TimetableHearingStatementV2> {
    if (id === null || id === undefined) {
      throw new Error('Required parameter id was null or undefined when calling updateHearingStatement.');
    }
    if (statement === null || statement === undefined) {
      throw new Error('Required parameter statement was null or undefined when calling updateHearingStatement.');
    }

    let formParams: { append(param: string, value: any): any; } = new FormData();
    formParams = formParams.append('statement', new Blob([JSON.stringify(statement)], {type: 'application/json'})) || formParams;
    if (documents) {
      documents.forEach((element) => {
        formParams = formParams.append('documents', element) || formParams;
      });
    }

    return this.httpClient.put<TimetableHearingStatementV2>(`${this.basePath}/line-directory/internal/timetable-hearing/statements/${encodeURIComponent(String(id))}`,
      formParams,
    );
  }

  public getResponsibleTransportCompanies(ttfnid: string, year: number): Observable<TransportCompany[]> {
    if (ttfnid === null || ttfnid === undefined) {
      throw new Error('Required parameter ttfnid was null or undefined when calling getResponsibleTransportCompanies.');
    }
    if (year === null || year === undefined) {
      throw new Error('Required parameter year was null or undefined when calling getResponsibleTransportCompanies.');
    }

    return this.httpClient.get<Array<TransportCompany>>(`${this.basePath}/line-directory/internal/timetable-hearing/statements/responsible-transport-companies/${encodeURIComponent(String(ttfnid))}/${encodeURIComponent(String(year))}`);
  }

}
