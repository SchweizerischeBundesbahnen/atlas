import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlasApi.service';
import { Status } from '../../model/status';
import { Observable } from 'rxjs';
import { ContainerTimetableFieldNumber } from '../../model/containerTimetableFieldNumber';
import { TimetableFieldNumberVersion } from '../../model/timetableFieldNumberVersion';

@Injectable({
  providedIn: 'root',
})
export class TimetableFieldNumberInternalService {

  private readonly INTERNAL_FIELD_NUMBERS = '/line-directory/internal/field-numbers';

  private readonly atlasApiService = inject(AtlasApiService);

  public getOverview(searchCriteria?: Array<string>, number?: string,
                     businessOrganisation?: string, validOn?: Date,
                     statusChoices?: Array<Status>, page?: number, size?: number, sort?: Array<string>,
  ): Observable<ContainerTimetableFieldNumber> {
    const httpParams = this.atlasApiService.paramsOf({
      searchCriteria,
      number,
      businessOrganisation,
      validOn,
      statusChoices,
      page,
      size,
      sort,
    });
    return this.atlasApiService.get(
      this.INTERNAL_FIELD_NUMBERS,
      httpParams);
  }

  public revokeTimetableFieldNumber(ttfnId: string): Observable<TimetableFieldNumberVersion[]> {
    this.atlasApiService.validateParams({ ttfnId });
    return this.atlasApiService.post(
      `${this.INTERNAL_FIELD_NUMBERS}/${encodeURIComponent(String(ttfnId))}/revoke`);
  }

  public deleteVersions(ttfnId: string): Observable<void> {
    this.atlasApiService.validateParams({ ttfnId });
    return this.atlasApiService.delete(
      `${this.INTERNAL_FIELD_NUMBERS}/${encodeURIComponent(String(ttfnId))}`);
  }

}
