import { Injectable } from '@angular/core';
import { AtlasApiService } from './atlasApi.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class TimetableYearChangeInternalService extends AtlasApiService {

  public getNextTimetablesYearChange(count: number): Observable<Date[]> {
    this.validateParams({ count });
    return this.get(`/line-directory/internal/timetable-year-change/next-years/${encodeURIComponent(String(count))}`, 'json');
  }

  public getTimetableYearChange(year: number): Observable<Date> {
    this.validateParams({ year });
    return this.get(`/line-directory/internal/timetable-year-change/${encodeURIComponent(String(year))}`, 'json');
  }

}
