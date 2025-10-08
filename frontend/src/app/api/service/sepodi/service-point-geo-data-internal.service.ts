import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { CoordinatePair } from '../../model/coordinatePair';
import { HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class ServicePointGeoDataInternalService {

  private readonly atlasApiService = inject(AtlasApiService);

  public getLocationInformation(coordinatePair: CoordinatePair): Observable<GeoReference> {
    const httpParams = new HttpParams()
      .append('east', coordinatePair.east)
      .append('north', coordinatePair.north)
      .append('spatialReference', coordinatePair.spatialReference);
    return this.atlasApiService.get(`/service-point-directory/internal/geodata/reverse-geocode`, httpParams);
  }

}
