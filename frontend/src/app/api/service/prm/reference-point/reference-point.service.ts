import {inject, Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {ReferencePointVersion} from "../../../model/referencePointVersion";
import {ReadReferencePointVersion} from "../../../model/readReferencePointVersion";
import {AtlasApiService} from "../../atlas-api.service";

@Injectable({
  providedIn: 'root'
})
export class ReferencePointService {

  private readonly atlasApiService = inject(AtlasApiService);
  private readonly V1_REFERENCE_POINT = '/prm-directory/v1/reference-points';

  public createReferencePoint(referencePointVersion: ReferencePointVersion): Observable<ReadReferencePointVersion> {
    this.atlasApiService.validateParams({referencePointVersion: referencePointVersion});
    return this.atlasApiService.post(`${this.V1_REFERENCE_POINT}`, referencePointVersion);
  }

  public updateReferencePoint(id: number, referencePointVersion: ReferencePointVersion): Observable<ReadReferencePointVersion> {
    this.atlasApiService.validateParams({id, platformVersion: referencePointVersion});
    return this.atlasApiService.put(`${this.V1_REFERENCE_POINT}/${id}`, referencePointVersion);
  }

  public getReferencePointVersions(sloid: String): Observable<Array<ReadReferencePointVersion>> {
    this.atlasApiService.validateParams({sloid});
    return this.atlasApiService.get(`${this.V1_REFERENCE_POINT}/${sloid}`);
  }

}
