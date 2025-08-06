import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { CreateTrafficPointElementVersion } from '../../model/createTrafficPointElementVersion';
import { ReadTrafficPointElementVersion } from '../../model/readTrafficPointElementVersion';

@Injectable({
  providedIn: 'root',
})
export class TrafficPointElementService {

  private readonly BASE_PATH = '/service-point-directory/v1/traffic-point-elements';

  private readonly atlasApiService = inject(AtlasApiService);

  public getTrafficPointElement(sloid: string): Observable<Array<ReadTrafficPointElementVersion>> {
    return this.atlasApiService.get(`${this.BASE_PATH}/${encodeURIComponent(String(sloid))}`);
  }

  public createTrafficPoint(createTrafficPointElementVersion: CreateTrafficPointElementVersion): Observable<ReadTrafficPointElementVersion> {
    this.atlasApiService.validateParams({ createTrafficPointElementVersion });
    return this.atlasApiService.post(`${this.BASE_PATH}`);
  }

  public updateTrafficPoint(id: number, createTrafficPointElementVersion: CreateTrafficPointElementVersion): Observable<Array<ReadTrafficPointElementVersion>> {
    this.atlasApiService.validateParams({ id, createTrafficPointElementVersion });
    return this.atlasApiService.put(`${this.BASE_PATH}/${id}`, createTrafficPointElementVersion);
  }

}
