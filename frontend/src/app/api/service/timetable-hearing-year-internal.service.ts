import { Injectable } from '@angular/core';
import { AtlasApiService } from './atlasApi.service';
import { Observable } from 'rxjs';
import { TimetableHearingYear } from '../model/timetableHearingYear';
import { HearingStatus } from '../model/hearingStatus';

@Injectable({
  providedIn: 'root',
})
export class TimetableHearingYearInternalService extends AtlasApiService {

  public closeTimetableHearing(year: number): Observable<TimetableHearingYear> {
    this.validateParams({ year });
    return this.post(`/line-directory/internal/timetable-hearing/years/${encodeURIComponent(String(year))}/close`);
  }

  public createHearingYear(timetableHearingYear: TimetableHearingYear): Observable<TimetableHearingYear> {
    this.validateParams({ timetableHearingYear });
    return this.post(`/line-directory/internal/timetable-hearing/years`, timetableHearingYear);
  }

  public getHearingYear(year: number): Observable<TimetableHearingYear> {
    this.validateParams({ year });
    return this.get(`/line-directory/internal/timetable-hearing/years/${encodeURIComponent(String(year))}`, 'json');
  }

  public getHearingYears(statusChoices?: Array<HearingStatus>): Observable<TimetableHearingYear[]> {
    const httpParams = this.paramsOf({ statusChoices });
    return this.get(`/line-directory/internal/timetable-hearing/years`, 'json', httpParams);
  }

  public startHearingYear(year: number): Observable<TimetableHearingYear> {
    this.validateParams({ year });
    return this.post(`/line-directory/internal/timetable-hearing/years/${encodeURIComponent(String(year))}/start`);
  }

  public updateTimetableHearingSettings(year: number, timetableHearingYear: TimetableHearingYear): Observable<void> {
    this.validateParams({ year, timetableHearingYear });
    return this.put(`/line-directory/internal/timetable-hearing/years/${encodeURIComponent(String(year))}`, timetableHearingYear);
  }

}
