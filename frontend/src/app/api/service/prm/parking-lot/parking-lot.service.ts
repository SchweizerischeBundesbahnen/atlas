import {inject, Injectable} from '@angular/core';
import {AtlasApiService} from "../../atlas-api.service";
import {Observable} from "rxjs";
import {ParkingLotVersion} from "../../../model/parkingLotVersion";
import {ReadParkingLotVersion} from "../../../model/readParkingLotVersion";

@Injectable({
  providedIn: 'root'
})
export class ParkingLotService {

  private readonly V1_PARKING_LOTS = '/prm-directory/v1/parking-lots';

  private readonly atlasApiService = inject(AtlasApiService);

  public createParkingLot(parkingLotVersion: ParkingLotVersion): Observable<ReadParkingLotVersion> {
    this.atlasApiService.validateParams({parkingLotVersion: parkingLotVersion});
    return this.atlasApiService.post(`${this.V1_PARKING_LOTS}`, parkingLotVersion);
  }

  public updateParkingLot(id: number, parkingLotVersion: ParkingLotVersion): Observable<ReadParkingLotVersion> {
    this.atlasApiService.validateParams({id, platformVersion: parkingLotVersion});
    return this.atlasApiService.put(`${this.V1_PARKING_LOTS}/${id}`, parkingLotVersion);
  }

  public getParkingLotVersions(sloid: String): Observable<Array<ReadParkingLotVersion>> {
    this.atlasApiService.validateParams({sloid});
    return this.atlasApiService.get(`${this.V1_PARKING_LOTS}/${sloid}`);
  }


}
