import { Injectable } from '@angular/core';
import { AtlasApiService } from './atlasApi.service';
import { Observable } from 'rxjs';
import { TimetableHearingYear } from '../model/timetableHearingYear';
import { HearingStatus } from '../model/hearingStatus';
import { HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class TimetableHearingYearInternalService extends AtlasApiService {

  public closeTimetableHearing(year: number): Observable<TimetableHearingYear> {
    if (year === null || year === undefined) {
      throw new Error('Required parameter year was null or undefined when calling closeTimetableHearing.');
    }
    return this.httpClient.post<TimetableHearingYear>(`${this.basePath}/line-directory/internal/timetable-hearing/years/${encodeURIComponent(String(year))}/close`,
      null,
    );
  }

  public createHearingYear(timetableHearingYear: TimetableHearingYear): Observable<TimetableHearingYear> {
    if (timetableHearingYear === null || timetableHearingYear === undefined) {
      throw new Error('Required parameter timetableHearingYear was null or undefined when calling createHearingYear.');
    }
    return this.httpClient.post<TimetableHearingYear>(`${this.basePath}/line-directory/internal/timetable-hearing/years`,
      timetableHearingYear,
    );
  }

  public getHearingYear(year: number): Observable<TimetableHearingYear> {
    if (year === null || year === undefined) {
      throw new Error('Required parameter year was null or undefined when calling getHearingYear.');
    }
    return this.httpClient.get<TimetableHearingYear>(`${this.basePath}/line-directory/internal/timetable-hearing/years/${encodeURIComponent(String(year))}`,
    );
  }

  public getHearingYears(statusChoices?: Array<HearingStatus>): Observable<Array<TimetableHearingYear>> {
    let queryParameters = new HttpParams();
    if (statusChoices) {
      statusChoices.forEach((element) => {
        queryParameters = this.addToHttpParams(queryParameters, element, 'statusChoices');
      });
    }
    return this.httpClient.get<Array<TimetableHearingYear>>(`${this.basePath}/line-directory/internal/timetable-hearing/years`,
      {
        params: queryParameters,
      },
    );
  }

  public startHearingYear(year: number): Observable<TimetableHearingYear> {
    if (year === null || year === undefined) {
      throw new Error('Required parameter year was null or undefined when calling startHearingYear.');
    }
    return this.httpClient.post<TimetableHearingYear>(`${this.basePath}/line-directory/internal/timetable-hearing/years/${encodeURIComponent(String(year))}/start`,
      null,
    );
  }

  public updateTimetableHearingSettings(year: number, timetableHearingYear: TimetableHearingYear): Observable<any> {
    if (year === null || year === undefined) {
      throw new Error('Required parameter year was null or undefined when calling updateTimetableHearingSettings.');
    }
    if (timetableHearingYear === null || timetableHearingYear === undefined) {
      throw new Error('Required parameter timetableHearingYear was null or undefined when calling updateTimetableHearingSettings.');
    }
    return this.httpClient.put<any>(`${this.basePath}/line-directory/internal/timetable-hearing/years/${encodeURIComponent(String(year))}`,
      timetableHearingYear,
    );
  }

}
