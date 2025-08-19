import {inject, Injectable} from '@angular/core';
import {AtlasApiService} from "../../atlas-api.service";
import {Observable} from "rxjs";
import {ToiletOverview} from "../../../model/toiletOverview";

@Injectable({
  providedIn: 'root'
})
export class ToiletInternalService {

  private readonly V1_TOILETS = '/prm-directory/internal/toilets/overview';

  private readonly atlasApiService = inject(AtlasApiService);

  public getToiletOverview(sloid: String): Observable<Array<ToiletOverview>> {
    this.atlasApiService.validateParams({sloid});
    return this.atlasApiService.get(`${this.V1_TOILETS}/${sloid}`);
  }
}
