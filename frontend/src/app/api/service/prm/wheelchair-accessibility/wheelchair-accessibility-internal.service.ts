import {inject, Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {AtlasApiService} from "../../atlas-api.service";
import {ReadWheelchairAccessibility} from "../../../model/readWheelchairAccessibility";

@Injectable({
  providedIn: 'root'
})
export class WheelchairAccessibilityInternalService {

  private readonly BASE = '/prm-directory/internal/wheelchair-accessibility';

  private readonly atlasApiService = inject(AtlasApiService);

  public getPlatformAccessibilityToday(sloid: string): Observable<ReadWheelchairAccessibility> {
    this.atlasApiService.validateParams({sloid});
    return this.atlasApiService.get(`${this.BASE}/${sloid}/platforms`);
  }

  public getStopPointAccessibilityToday(sloid: string): Observable<ReadWheelchairAccessibility> {
    this.atlasApiService.validateParams({sloid});
    return this.atlasApiService.get(`${this.BASE}/${sloid}/stop-points`);
  }

}
