import {inject, Injectable} from '@angular/core';
import {AtlasApiService} from "../../atlas-api.service";
import {Observable} from "rxjs";
import {ReadRelationVersion} from "../../../model/readRelationVersion";
import {RelationVersion} from "../../../model/relationVersion";

@Injectable({
  providedIn: 'root'
})
export class RelationService {

  private readonly V1_RELATION = '/prm-directory/v1/relations';

  private readonly atlasApiService = inject(AtlasApiService);

  public getRelationsBySloid(sloid: String): Observable<Array<ReadRelationVersion>> {
    this.atlasApiService.validateParams({sloid});
    return this.atlasApiService.get(`${this.V1_RELATION}/${sloid}`);
  }

  public updateRelation(id: number, relationVersion: RelationVersion): Observable<Array<ReadRelationVersion>> {
    this.atlasApiService.validateParams({id, relationVersion: relationVersion});
    return this.atlasApiService.put(`${this.V1_RELATION}/${id}`, relationVersion);
  }

}
