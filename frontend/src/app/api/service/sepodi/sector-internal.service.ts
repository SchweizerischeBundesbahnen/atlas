import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { ContainerReadSectorVersion } from '../../model/containerReadSectorVersion';
import { ReadSectorVersion } from '../../model/readSectorVersion';

@Injectable({
  providedIn: 'root',
})
export class SectorInternalService {

  private readonly BASE_PATH = '/service-point-directory/internal/sectors';

  private readonly atlasApiService = inject(AtlasApiService);

  public getSectors(trafficPointSloid: string, page?: number, size?: number, sort?: Array<string>): Observable<ContainerReadSectorVersion> {
    const httpParams = this.atlasApiService.paramsOf({
      page,
      size,
      sort,
    });
    return this.atlasApiService.get(`${this.BASE_PATH}/${encodeURIComponent(trafficPointSloid)}/overview`, httpParams);
  }

  public getSectorsValidToday(trafficPointSloid: string): Observable<Array<ReadSectorVersion>> {
    return this.atlasApiService.get(`${this.BASE_PATH}/actual-date/${encodeURIComponent(trafficPointSloid)}`);
  }

}
