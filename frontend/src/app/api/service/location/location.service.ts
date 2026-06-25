import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { SloidLocationModel } from '../../model/sloidLocationModel';

@Injectable({
  providedIn: 'root',
})
export class LocationService {
  private readonly LOCATION = '/location/v1';

  private readonly atlasApiService = inject(AtlasApiService);

  public getSloidLocationModel(slnid: string): Observable<Array<SloidLocationModel>> {
    return this.atlasApiService.get(`${this.LOCATION}/sloid/${encodeURIComponent(String(slnid))}`);
  }
}
