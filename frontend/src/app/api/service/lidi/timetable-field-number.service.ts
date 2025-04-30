import { Injectable } from '@angular/core';
import { AtlasApiService } from '../atlasApi.service';
import { TimetableFieldNumberVersion } from '../../model/timetableFieldNumberVersion';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class TimetableFieldNumberService extends AtlasApiService {

  public createVersion(timetableFieldNumberVersion: TimetableFieldNumberVersion): Observable<TimetableFieldNumberVersion> {
    this.validateParams({ timetableFieldNumberVersion });
    return this.post(`/line-directory/v1/field-numbers/versions`, timetableFieldNumberVersion);
  }

  public getAllVersionsVersioned(ttfnId: string): Observable<TimetableFieldNumberVersion[]> {
    this.validateParams({ ttfnId });
    return this.get(`/line-directory/v1/field-numbers/versions/${encodeURIComponent(String(ttfnId))}`, 'json');
  }

  public updateVersionWithVersioning(id: number, timetableFieldNumberVersion: TimetableFieldNumberVersion): Observable<TimetableFieldNumberVersion[]> {
    this.validateParams({ id, timetableFieldNumberVersion });
    return this.post(`/line-directory/v1/field-numbers/versions/${encodeURIComponent(String(id))}`, timetableFieldNumberVersion);
  }

}
