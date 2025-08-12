import {inject, Injectable} from '@angular/core';
import {AtlasApiService} from '../atlas-api.service';
import {Observable} from 'rxjs';
import {ContainerReadLoadingPointVersion} from "../../model/containerReadLoadingPointVersion";

@Injectable({
  providedIn: 'root',
})
export class LoadingPointInternalService {

  private readonly BASE_PATH = '/service-point-directory/internal/loading-points';

  private readonly atlasApiService = inject(AtlasApiService);

  public getLoadingPointOverview(servicePointNumber: number, page?: number, size?: number, sort?: Array<string>): Observable<ContainerReadLoadingPointVersion> {
    const httpParams = this.atlasApiService.paramsOf({
      page,
      size,
      sort,
    });
    return this.atlasApiService.get(`${this.BASE_PATH}/${encodeURIComponent(String(servicePointNumber))}`, httpParams);
  }

}
