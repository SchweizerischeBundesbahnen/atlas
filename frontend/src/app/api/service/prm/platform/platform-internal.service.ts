import {inject, Injectable} from '@angular/core';
import {AtlasApiService} from "../../atlas-api.service";
import {Observable} from "rxjs";
import {PlatformOverview} from "../../../model/platformOverview";

@Injectable({
  providedIn: 'root'
})
export class PlatformInternalService {

  private readonly V1_PLATFORMS = '/prm-directory/internal/platforms/overview';

  private readonly atlasApiService = inject(AtlasApiService);

  public getPlatformOverview(parentSloid: String): Observable<Array<PlatformOverview>> {
    this.atlasApiService.validateParams({parentSloid});
    return this.atlasApiService.get(`${this.V1_PLATFORMS}/${parentSloid}`);
  }}
