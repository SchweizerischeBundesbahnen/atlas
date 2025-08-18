import {inject, Injectable} from '@angular/core';
import {AtlasApiService} from "../../atlas-api.service";
import {Observable} from "rxjs";
import {ParkingLotOverview} from "../../../model/parkingLotOverview";

@Injectable({
  providedIn: 'root'
})
export class ParkingLotInternalService {

  private readonly V1_PLATFORMS = '/prm-directory/internal/parking-lots/overview';

  private readonly atlasApiService = inject(AtlasApiService);

  public getParkingLotsOverview(parentServicePointSloid: String): Observable<Array<ParkingLotOverview>> {
    this.atlasApiService.validateParams({parentServicePointSloid});
    return this.atlasApiService.get(`${this.V1_PLATFORMS}/${parentServicePointSloid}`);
  }


}
