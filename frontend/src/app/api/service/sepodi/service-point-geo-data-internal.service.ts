import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { CoordinatePair } from '../../model/coordinatePair';
import { GeoReference } from '../../model/geoReference';

@Injectable({
  providedIn: 'root',
})
export class ServicePointGeoDataInternalService {

  private readonly atlasApiService = inject(AtlasApiService);

  public getLocationInformation(coordinatePair: CoordinatePair): Observable<GeoReference> {
    const httpParams = this.atlasApiService.paramsOf({
      coordinatePair
    });
    return this.atlasApiService.get(`/internal/geodata/reverse-geocode`, httpParams);
  }

}
