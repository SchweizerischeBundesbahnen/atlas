import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { ContainerSectorVersion } from '../../model/containerSectorVersion';

@Injectable({
  providedIn: 'root',
})
export class SectorInternalService {

  private readonly BASE_PATH = '/service-point-directory/internal/sectors';

  private readonly atlasApiService = inject(AtlasApiService);

  public getSectors(trafficPointSloid: string, page?: number, size?: number, sort?: Array<string>): Observable<ContainerSectorVersion> {
    const httpParams = this.atlasApiService.paramsOf({
      page,
      size,
      sort,
    });
    return this.atlasApiService.get(`${this.BASE_PATH}/${encodeURIComponent(trafficPointSloid)}/overview`, httpParams);
  }

}
