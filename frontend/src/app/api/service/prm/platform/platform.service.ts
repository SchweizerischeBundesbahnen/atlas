import {inject, Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {AtlasApiService} from "../../atlas-api.service";
import {PlatformVersion} from "../../../model/platformVersion";
import {ReadPlatformVersion} from "../../../model/readPlatformVersion";

@Injectable({
  providedIn: 'root'
})
export class PlatformService {

  private readonly V1_PLATFORMS = '/prm-directory/v1/platforms';

  private readonly atlasApiService = inject(AtlasApiService);

  public createPlatform(platformVersion: PlatformVersion): Observable<ReadPlatformVersion> {
    this.atlasApiService.validateParams({platformVersion: platformVersion});
    return this.atlasApiService.post(`${this.V1_PLATFORMS}`, platformVersion);
  }

  public updatePlatform(id: number, platformVersion: PlatformVersion): Observable<ReadPlatformVersion> {
    this.atlasApiService.validateParams({id, platformVersion: platformVersion});
    return this.atlasApiService.put(`${this.V1_PLATFORMS}/${id}`, platformVersion);
  }

  public getPlatformVersions(sloid: String): Observable<Array<ReadPlatformVersion>> {
    this.atlasApiService.validateParams({sloid});
    return this.atlasApiService.get(`${this.V1_PLATFORMS}/${sloid}`);
  }

}
