import { Injectable } from '@angular/core';
import { AtlasApiService } from '../atlasApi.service';
import { Status } from '../../model/status';
import { Observable } from 'rxjs';
import { ContainerTimetableFieldNumber } from '../../model/containerTimetableFieldNumber';
import { TimetableFieldNumberVersion } from '../../model/timetableFieldNumberVersion';

@Injectable({
  providedIn: 'root',
})
export class TimetableFieldNumberInternalService extends AtlasApiService {

  public getOverview(searchCriteria?: Array<string>, number?: string,
                     businessOrganisation?: string, validOn?: Date,
                     statusChoices?: Array<Status>, page?: number, size?: number, sort?: Array<string>,
  ): Observable<ContainerTimetableFieldNumber> {
    const httpParams = this.paramsOf({ searchCriteria, number, businessOrganisation, validOn, statusChoices, page, size, sort });
    return this.get('/line-directory/internal/field-numbers', 'json', httpParams);
  }

  public revokeTimetableFieldNumber(ttfnId: string): Observable<TimetableFieldNumberVersion[]> {
    this.validateParams({ ttfnId });
    return this.post(`/line-directory/internal/field-numbers/${encodeURIComponent(String(ttfnId))}/revoke`);
  }

  public deleteVersions(ttfnId: string): Observable<void> {
    this.validateParams({ ttfnId });
    return this.delete(`/line-directory/internal/field-numbers/${encodeURIComponent(String(ttfnId))}`);
  }

}
