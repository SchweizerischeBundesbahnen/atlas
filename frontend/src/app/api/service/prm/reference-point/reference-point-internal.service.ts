import {inject, Injectable} from '@angular/core';
import {AtlasApiService} from "../../atlas-api.service";
import {Observable} from "rxjs";
import {ReadReferencePointVersion} from "../../../model/readReferencePointVersion";

@Injectable({
  providedIn: 'root'
})
export class ReferencePointInternalService {

  private readonly atlasApiService = inject(AtlasApiService);
  private readonly INTERNAL_REFERENCE_POINT = '/prm-directory/internal/reference-points/overview';

  public getReferencePointsOverview(parentServicePointSloid: String): Observable<Array<ReadReferencePointVersion>> {
    this.atlasApiService.validateParams({parentServicePointSloid});
    return this.atlasApiService.get(`${this.INTERNAL_REFERENCE_POINT}/${parentServicePointSloid}`);
  }

}
