import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AtlasApiService } from '../../atlas-api.service';
import { ReadWheelchairAccessibility } from '../../../model/readWheelchairAccessibility';
import { ReadAccessibility } from '../../../model/readAccessibility';

@Injectable({
  providedIn: 'root',
})
export class WheelchairAccessibilityInternalService {
  private readonly BASE = '/prm-directory/internal/wheelchair-accessibility';

  private readonly atlasApiService = inject(AtlasApiService);

  public getPlatformAccessibilityToday(sloid: string): Observable<ReadWheelchairAccessibility> {
    this.atlasApiService.validateParams({ sloid });
    return this.atlasApiService.get(`${this.BASE}/platform/${sloid}/today`);
  }

  public getStopPointAccessibilityToday(sloid: string): Observable<ReadWheelchairAccessibility> {
    this.atlasApiService.validateParams({ sloid });
    return this.atlasApiService.get(`${this.BASE}/stop-point/${sloid}/today`);
  }

  public getPlatformAccessibility(sloid: string, startingFrom?: Date): Observable<ReadAccessibility> {
    this.atlasApiService.validateParams({ sloid });
    const httpParams = this.atlasApiService.paramsOf({
      startingFrom,
    });
    return this.atlasApiService.get(`${this.BASE}/platform/${sloid}`, httpParams);
  }

  public getStopPointAccessibility(sloid: string, startingFrom?: Date): Observable<ReadAccessibility> {
    this.atlasApiService.validateParams({ sloid });
    const httpParams = this.atlasApiService.paramsOf({
      startingFrom,
    });
    return this.atlasApiService.get(`${this.BASE}/stop-point/${sloid}`, httpParams);
  }
}
