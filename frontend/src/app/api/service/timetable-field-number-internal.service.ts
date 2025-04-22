import { Injectable } from '@angular/core';
import { AtlasApiService } from './atlasApi.service';
import { Status } from '../model/status';
import { Observable } from 'rxjs';
import { ContainerTimetableFieldNumber } from '../model/containerTimetableFieldNumber';
import { HttpParams } from '@angular/common/http';
import { TimetableFieldNumberVersion } from '../model/timetableFieldNumberVersion';

@Injectable({
  providedIn: 'root',
})
export class TimetableFieldNumberInternalService extends AtlasApiService {

  public getOverview(searchCriteria?: Array<string>, number?: string,
                     businessOrganisation?: string, validOn?: Date,
                     statusChoices?: Array<Status>, page?: number, size?: number, sort?: Array<string>,
  ): Observable<ContainerTimetableFieldNumber> {
    let queryParameters = new HttpParams();
    if (searchCriteria) {
      searchCriteria.forEach((element) => {
        queryParameters = this.addToHttpParams(queryParameters, element, 'searchCriteria');
      });
    }
    if (number !== undefined && number !== null) {
      queryParameters = this.addToHttpParams(queryParameters, number, 'number');
    }
    if (businessOrganisation !== undefined && businessOrganisation !== null) {
      queryParameters = this.addToHttpParams(queryParameters, businessOrganisation, 'businessOrganisation');
    }
    if (validOn !== undefined && validOn !== null) {
      queryParameters = this.addToHttpParams(queryParameters, validOn, 'validOn');
    }
    if (statusChoices) {
      statusChoices.forEach((element) => {
        queryParameters = this.addToHttpParams(queryParameters, element, 'statusChoices');
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

    return this.httpClient.get<ContainerTimetableFieldNumber>(`${this.basePath}/line-directory/internal/field-numbers`,
      {
        params: queryParameters,
      },
    );
  }

  public revokeTimetableFieldNumber(ttfnId: string): Observable<Array<TimetableFieldNumberVersion>> {
    if (ttfnId === null || ttfnId === undefined) {
      throw new Error('Required parameter ttfnId was null or undefined when calling revokeTimetableFieldNumber.');
    }
    return this.httpClient.post<Array<TimetableFieldNumberVersion>>(`${this.basePath}/line-directory/internal/field-numbers/${encodeURIComponent(String(ttfnId))}/revoke`,
      null,
    );
  }

  public deleteVersions(ttfnid: string): Observable<any> {
    if (ttfnid === null || ttfnid === undefined) {
      throw new Error('Required parameter ttfnid was null or undefined when calling deleteVersions.');
    }
    return this.httpClient.delete<any>(`${this.basePath}/line-directory/internal/field-numbers/${encodeURIComponent(String(ttfnid))}`);
  }

}
