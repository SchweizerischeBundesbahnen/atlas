import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlasApi.service';
import { TimetableFieldNumberVersion } from '../../model/timetableFieldNumberVersion';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class TimetableFieldNumberService {

  private readonly FIELD_NUMBER_VERSIONS = '/line-directory/v1/field-numbers/versions';

  private readonly atlasApiService = inject(AtlasApiService);

  public createVersion(timetableFieldNumberVersion: TimetableFieldNumberVersion): Observable<TimetableFieldNumberVersion> {
    this.atlasApiService.validateParams({ timetableFieldNumberVersion });
    return this.atlasApiService.post(
      this.FIELD_NUMBER_VERSIONS,
      timetableFieldNumberVersion);
  }

  public getAllVersionsVersioned(ttfnId: string): Observable<TimetableFieldNumberVersion[]> {
    this.atlasApiService.validateParams({ ttfnId });
    return this.atlasApiService.get(
      `${this.FIELD_NUMBER_VERSIONS}/${encodeURIComponent(String(ttfnId))}`);
  }

  public updateVersionWithVersioning(id: number, timetableFieldNumberVersion: TimetableFieldNumberVersion): Observable<TimetableFieldNumberVersion[]> {
    this.atlasApiService.validateParams({ id, timetableFieldNumberVersion });
    return this.atlasApiService.post(
      `${this.FIELD_NUMBER_VERSIONS}/${encodeURIComponent(String(id))}`,
      timetableFieldNumberVersion);
  }

}
