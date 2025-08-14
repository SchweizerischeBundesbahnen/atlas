import {inject, Injectable} from '@angular/core';
import {AtlasApiService} from "../atlas-api.service";
import {Observable} from "rxjs";
import {ReadStopPointVersion} from "../../model/readStopPointVersion";
import {StopPointVersion} from "../../model/stopPointVersion";

@Injectable({
  providedIn: 'root'
})
export class StopPointService {

  private readonly V1_STOP_POINTS = '/prm-directory/v1/stop-points';

  private readonly atlasApiService = inject(AtlasApiService);

  public getStopPointVersions(sloid: String): Observable<Array<ReadStopPointVersion>> {
    this.atlasApiService.validateParams({ sloid });
    return this.atlasApiService.get(`${this.V1_STOP_POINTS}/${sloid}`);
  }

  public createStopPoint(createStopPointVersion: StopPointVersion): Observable<ReadStopPointVersion> {
    this.atlasApiService.validateParams({ createStopPointVersion });
    return this.atlasApiService.post(`${this.V1_STOP_POINTS}`, createStopPointVersion);
  }

  public updateStopPoint(id: number, stopPointVersion: StopPointVersion): Observable<ReadStopPointVersion> {
    this.atlasApiService.validateParams({ id, updateServicePointVersion: stopPointVersion });
    return this.atlasApiService.put(`${this.V1_STOP_POINTS}/${id}`, stopPointVersion);
  }

}
