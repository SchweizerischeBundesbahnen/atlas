import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlasApi.service';
import { Observable } from 'rxjs';
import { TimetableHearingYear } from '../../model/timetableHearingYear';
import { HearingStatus } from '../../model/hearingStatus';

@Injectable({
  providedIn: 'root',
})
export class TimetableHearingYearInternalService {

  private readonly YEARS = '/line-directory/internal/timetable-hearing/years';

  private readonly atlasApiService = inject(AtlasApiService);

  public closeTimetableHearing(year: number): Observable<TimetableHearingYear> {
    this.atlasApiService.validateParams({ year });
    return this.atlasApiService.post(`${this.YEARS}/${encodeURIComponent(String(year))}/close`);
  }

  public createHearingYear(timetableHearingYear: TimetableHearingYear): Observable<TimetableHearingYear> {
    this.atlasApiService.validateParams({ timetableHearingYear });
    return this.atlasApiService.post(this.YEARS, timetableHearingYear);
  }

  public getHearingYear(year: number): Observable<TimetableHearingYear> {
    this.atlasApiService.validateParams({ year });
    return this.atlasApiService.get(`${this.YEARS}/${encodeURIComponent(String(year))}`);
  }

  public getHearingYears(statusChoices?: Array<HearingStatus>): Observable<TimetableHearingYear[]> {
    const httpParams = this.atlasApiService.paramsOf({ statusChoices });
    return this.atlasApiService.get(this.YEARS, httpParams);
  }

  public startHearingYear(year: number): Observable<TimetableHearingYear> {
    this.atlasApiService.validateParams({ year });
    return this.atlasApiService.post(`${this.YEARS}/${encodeURIComponent(String(year))}/start`);
  }

  public updateTimetableHearingSettings(year: number, timetableHearingYear: TimetableHearingYear): Observable<void> {
    this.atlasApiService.validateParams({ year, timetableHearingYear });
    return this.atlasApiService.put(`${this.YEARS}/${encodeURIComponent(String(year))}`, timetableHearingYear);
  }

}
