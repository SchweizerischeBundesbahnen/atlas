import {inject, Injectable} from '@angular/core';
import {AtlasApiService} from "../../atlas-api.service";
import {Observable} from "rxjs";
import {ToiletVersion} from "../../../model/toiletVersion";
import {ReadToiletVersion} from "../../../model/readToiletVersion";

@Injectable({
  providedIn: 'root'
})
export class ToiletService {

  private readonly V1_TOILETS
    = '/prm-directory/v1/toilets';

  private readonly atlasApiService = inject(AtlasApiService);

  public createToiletVersion(toiletVersion: ToiletVersion): Observable<ReadToiletVersion> {
    this.atlasApiService.validateParams({platformVersion: toiletVersion});
    return this.atlasApiService.post(`${this.V1_TOILETS}`, toiletVersion);
  }

  public updateToiletVersion(id: number, toiletVersion: ToiletVersion): Observable<ReadToiletVersion> {
    this.atlasApiService.validateParams({id, platformVersion: toiletVersion})
    return this.atlasApiService.put(`${this.V1_TOILETS}/${id}`, toiletVersion);
  }

  public getToiletVersions(sloid: String): Observable<Array<ReadToiletVersion>> {
    this.atlasApiService.validateParams({sloid});
    return this.atlasApiService.get(`${this.V1_TOILETS}/${sloid}`);
  }

}
