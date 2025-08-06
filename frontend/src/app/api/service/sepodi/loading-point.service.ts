import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { CreateLoadingPointVersion } from '../../model/createLoadingPointVersion';
import { ReadLoadingPointVersion } from '../../model/readLoadingPointVersion';

@Injectable({
  providedIn: 'root',
})
export class LoadingPointService {

  private readonly BASE_PATH = '/service-point-directory/v1/loading-points';

  private readonly atlasApiService = inject(AtlasApiService);

  public getLoadingPoint(servicePointNumber: number, loadingPointNumber: number): Observable<Array<ReadLoadingPointVersion>> {
    return this.atlasApiService.get(`${this.BASE_PATH}/${encodeURIComponent(String(servicePointNumber))}/${encodeURIComponent(String(loadingPointNumber))}`);
  }

  public createLoadingPoint(createLoadingPointVersion: CreateLoadingPointVersion): Observable<ReadLoadingPointVersion> {
    this.atlasApiService.validateParams({ createLoadingPointVersion });
    return this.atlasApiService.post(`${this.BASE_PATH}`);
  }

  public updateLoadingPoint(id: number, createLoadingPointVersion: CreateLoadingPointVersion): Observable<Array<ReadLoadingPointVersion>> {
    this.atlasApiService.validateParams({ id, createLoadingPointVersion });
    return this.atlasApiService.put(`${this.BASE_PATH}/${id}`, createLoadingPointVersion);
  }

}
