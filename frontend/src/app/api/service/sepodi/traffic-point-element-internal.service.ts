import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { ContainerReadTrafficPointElementVersion } from '../../model/containerReadTrafficPointElementVersion';
import { ReadTrafficPointElementVersion } from '../../model/readTrafficPointElementVersion';

@Injectable({
  providedIn: 'root',
})
export class TrafficPointElementInternalService {

  private readonly BASE_PATH = '/service-point-directory/v1/traffic-point-elements';

  private readonly atlasApiService = inject(AtlasApiService);

  public getPlatformsOfServicePoint(servicePointNumber: number, page?: number, size?: number, sort?: Array<string>): Observable<ContainerReadTrafficPointElementVersion> {
    const httpParams = this.atlasApiService.paramsOf({
      page,
      size,
      sort,
    });
    return this.atlasApiService.get(`${this.BASE_PATH}/platforms/${encodeURIComponent(String(servicePointNumber))}`, httpParams);
  }

  public getAreasOfServicePoint(servicePointNumber: number, page?: number, size?: number, sort?: Array<string>): Observable<ContainerReadTrafficPointElementVersion> {
    const httpParams = this.atlasApiService.paramsOf({
      page,
      size,
      sort,
    });
    return this.atlasApiService.get(`${this.BASE_PATH}/platforms/${encodeURIComponent(String(servicePointNumber))}`, httpParams);
  }

  public getTrafficPointsOfServicePointValidToday(servicePointNumber: number): Observable<Array<ReadTrafficPointElementVersion>> {
    return this.atlasApiService.get(`${this.BASE_PATH}/actual-date/${encodeURIComponent(String(servicePointNumber))}`);
  }
}
