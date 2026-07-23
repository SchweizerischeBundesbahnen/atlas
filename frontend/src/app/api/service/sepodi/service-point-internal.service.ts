import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { ServicePointSearchRequest } from '../../model/servicePointSearchRequest';
import { ServicePointSearchResult } from '../../model/servicePointSearchResult';
import { ReadServicePointVersion } from '../../model/readServicePointVersion';
import { GlobalIdUpdate } from '../../model/globalIdUpdate';

@Injectable({
  providedIn: 'root',
})
export class ServicePointInternalService {
  private readonly BASE_PATH = '/service-point-directory/internal/service-points';

  private readonly atlasApiService = inject(AtlasApiService);

  public searchServicePoints(
    servicePointSearchRequest: ServicePointSearchRequest
  ): Observable<Array<ServicePointSearchResult>> {
    return this.atlasApiService.post(`${this.BASE_PATH}/search`, servicePointSearchRequest);
  }

  public searchSwissOnlyServicePoints(
    servicePointSearchRequest: ServicePointSearchRequest
  ): Observable<Array<ServicePointSearchResult>> {
    return this.atlasApiService.post(`${this.BASE_PATH}/search-swiss-only`, servicePointSearchRequest);
  }

  public searchServicePointsWithRouteNetworkTrue(
    servicePointSearchRequest: ServicePointSearchRequest
  ): Observable<Array<ServicePointSearchResult>> {
    return this.atlasApiService.post(`${this.BASE_PATH}/search-sp-with-route-network`, servicePointSearchRequest);
  }

  public validateServicePoint(id: number): Observable<ReadServicePointVersion> {
    return this.atlasApiService.post(`${this.BASE_PATH}/versions/${encodeURIComponent(String(id))}/skip-workflow`);
  }

  public revokeServicePoint(servicePointNumber: number): Observable<Array<ReadServicePointVersion>> {
    return this.atlasApiService.post(`${this.BASE_PATH}/${encodeURIComponent(String(servicePointNumber))}/revoke`);
  }

  public updateGlobalId(
    servicePointNumber: number,
    globalIdUpdate: GlobalIdUpdate
  ): Observable<Array<ReadServicePointVersion>> {
    this.atlasApiService.validateParams({ servicePointNumber, globalIdUpdate });
    return this.atlasApiService.put(
      `${this.BASE_PATH}/${encodeURIComponent(String(servicePointNumber))}/global-id`,
      globalIdUpdate
    );
  }

  public deleteGlobalId(servicePointNumber: number): Observable<Array<ReadServicePointVersion>> {
    this.atlasApiService.validateParams({ servicePointNumber });
    return this.atlasApiService.delete(`${this.BASE_PATH}/${encodeURIComponent(String(servicePointNumber))}/global-id`);
  }
}
