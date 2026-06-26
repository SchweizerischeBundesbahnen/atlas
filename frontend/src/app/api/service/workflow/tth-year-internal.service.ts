import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AtlasApiService } from '../atlas-api.service';
import { TimetableHearingYear } from '../../model/timetableHearingYear';

@Injectable({
  providedIn: 'root',
})
export class TthYearInternalService {
  private readonly BASE_PATH = '/workflow/internal/tth/year';

  private readonly atlasApiService = inject(AtlasApiService);

  startTimetableHearingYear(year: number): Observable<TimetableHearingYear> {
    this.atlasApiService.validateParams({ year });
    return this.atlasApiService.post(`${this.BASE_PATH}/${encodeURIComponent(String(year))}/start`);
  }

  closeTimetableHearingYear(year: number): Observable<TimetableHearingYear> {
    this.atlasApiService.validateParams({ year });
    return this.atlasApiService.post(`${this.BASE_PATH}/${encodeURIComponent(String(year))}/close`);
  }
}
