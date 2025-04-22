import { Injectable } from '@angular/core';
import { AtlasApiService } from './atlasApi.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class TimetableYearChangeInternalService extends AtlasApiService {

  public getNextTimetablesYearChange(count: number): Observable<Array<Date>> {
    if (count === null || count === undefined) {
      throw new Error('Required parameter count was null or undefined when calling getNextTimetablesYearChange.');
    }
    return this.httpClient.get<Array<Date>>(`${this.basePath}/line-directory/internal/timetable-year-change/next-years/${encodeURIComponent(String(count))}`);
  }

  public getTimetableYearChange(year: number): Observable<Date> {
    if (year === null || year === undefined) {
      throw new Error('Required parameter year was null or undefined when calling getTimetableYearChange.');
    }
    return this.httpClient.get<Date>(`${this.basePath}/line-directory/internal/timetable-year-change/${encodeURIComponent(String(year))}`);
  }

}
