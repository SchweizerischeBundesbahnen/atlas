import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { ContainerReadSectorGroupVersion } from '../../model/containerReadSectorGroupVersion';

@Injectable({
  providedIn: 'root',
})
export class SectorGroupInternalService {

  private readonly BASE_PATH = '/service-point-directory/internal/sector-groups';

  private readonly atlasApiService = inject(AtlasApiService);

  public getSectorGroups(trafficPointSloid: string, page?: number, size?: number, sort?: Array<string>): Observable<ContainerReadSectorGroupVersion> {
    const httpParams = this.atlasApiService.paramsOf({
      page,
      size,
      sort,
    });
    return this.atlasApiService.get(`${this.BASE_PATH}/${encodeURIComponent(trafficPointSloid)}/overview`, httpParams);
  }

  public revokeSectorGroup(sectorGroupSloid: string): Observable<void> {
    return this.atlasApiService.post(`${this.BASE_PATH}/${encodeURIComponent(sectorGroupSloid)}/revoke`);
  }

}
