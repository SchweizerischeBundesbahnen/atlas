import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { CreateServicePointVersion } from '../../model/createServicePointVersion';
import { ReadServicePointVersion } from '../../model/readServicePointVersion';
import { UpdateServicePointVersion } from '../../model/updateServicePointVersion';
import { ServicePointFotComment } from '../../model/servicePointFotComment';

@Injectable({
  providedIn: 'root',
})
export class ServicePointService {

  private readonly BASE_PATH = '/service-point-directory/v1/service-points';

  private readonly atlasApiService = inject(AtlasApiService);

  public getServicePointVersions(servicePointNumber: number): Observable<Array<ReadServicePointVersion>> {
    return this.atlasApiService.get(`${this.BASE_PATH}/${servicePointNumber}`);
  }

  public getServicePointVersionsBySloid(sloid: string): Observable<Array<ReadServicePointVersion>> {
    return this.atlasApiService.get(`${this.BASE_PATH}/sloid/${encodeURIComponent(String(sloid))}`);
  }

  public createServicePoint(createServicePointVersion: CreateServicePointVersion): Observable<ReadServicePointVersion> {
    this.atlasApiService.validateParams({ createServicePointVersion });
    return this.atlasApiService.post(`${this.BASE_PATH}`);
  }

  public updateServicePoint(id: number, updateServicePointVersion: UpdateServicePointVersion): Observable<ReadServicePointVersion> {
    this.atlasApiService.validateParams({ id, updateServicePointVersion });
    return this.atlasApiService.put(`${this.BASE_PATH}/${id}`, updateServicePointVersion);
  }

  public getFotComment(servicePointNumber: number): Observable<ServicePointFotComment> {
    return this.atlasApiService.get(`${this.BASE_PATH}/${encodeURIComponent(String(servicePointNumber))}/fot-comment`);
  }

  public saveFotComment(servicePointNumber: number, servicePointFotComment: ServicePointFotComment): Observable<ReadServicePointVersion> {
    this.atlasApiService.validateParams({ servicePointNumber, servicePointFotComment });
    return this.atlasApiService.put(`${this.BASE_PATH}/${encodeURIComponent(String(servicePointNumber))}/fot-comment`, servicePointFotComment);
  }

}
