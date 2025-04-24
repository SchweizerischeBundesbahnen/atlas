import { Injectable } from '@angular/core';
import { AtlasApiService } from './atlasApi.service';
import { Observable } from 'rxjs';
import { ReadSublineVersionV2 } from '../model/readSublineVersionV2';
import { CreateSublineVersionV2 } from '../model/createSublineVersionV2';
import { SublineVersionV2 } from '../model/sublineVersionV2';

@Injectable({
  providedIn: 'root',
})
export class SublineService extends AtlasApiService {

  public getSublineVersionV2(slnid: string): Observable<ReadSublineVersionV2[]> {
    this.validateParams({ slnid });
    return this.get(`/line-directory/v2/sublines/versions/${encodeURIComponent(String(slnid))}`, 'json');
  }

  public createSublineVersionV2(createSublineVersionV2: CreateSublineVersionV2): Observable<ReadSublineVersionV2> {
    this.validateParams({ createSublineVersionV2 });
    return this.post(`/line-directory/v2/sublines/versions`, createSublineVersionV2);
  }

  public updateSublineVersionV2(id: number, sublineVersionV2: SublineVersionV2): Observable<ReadSublineVersionV2[]> {
    this.validateParams({ id, sublineVersionV2 });
    return this.put(`/line-directory/v2/sublines/versions/${encodeURIComponent(String(id))}`, sublineVersionV2);
  }

}
