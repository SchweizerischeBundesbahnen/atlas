import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlasApi.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class TimetableYearChangeInternalService {

  private readonly TTY_CHANGE = '/line-directory/internal/timetable-year-change';

  private readonly atlasApiService = inject(AtlasApiService);

  public getNextTimetablesYearChange(count: number): Observable<Date[]> {
    this.atlasApiService.validateParams({ count });
    return this.atlasApiService.get(`${this.TTY_CHANGE}/next-years/${encodeURIComponent(String(count))}`);
  }

  public getTimetableYearChange(year: number): Observable<Date> {
    this.atlasApiService.validateParams({ year });
    return this.atlasApiService.get(`${this.TTY_CHANGE}/${encodeURIComponent(String(year))}`);
  }

}
