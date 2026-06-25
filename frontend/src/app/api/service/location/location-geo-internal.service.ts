import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { CoordinatePair } from '../../model/coordinatePair';
import { GeoReference } from '../../model/geoReference';
import { GeoAdminHeightResponse } from '../../model/geoAdminHeightResponse';

@Injectable({
  providedIn: 'root',
})
export class LocationGeoInternalService {
  private readonly atlasApiService = inject(AtlasApiService);

  public getLocationInformation(
    coordinatePair: CoordinatePair,
    includeHeight: boolean = true
  ): Observable<GeoReference> {
    this.atlasApiService.validateParams({ ...coordinatePair, includeHeight });
    const httpParams = this.atlasApiService.paramsOf({ ...coordinatePair, includeHeight });
    return this.atlasApiService.get(`/location/internal/geo-reference`, httpParams);
  }

  getHeight(coordinatePair: CoordinatePair): Observable<GeoAdminHeightResponse> {
    this.atlasApiService.validateParams(coordinatePair);
    const httpParams = this.atlasApiService.paramsOf(coordinatePair);
    return this.atlasApiService.get(`/location/internal/geo-reference/height`, httpParams);
  }
}
