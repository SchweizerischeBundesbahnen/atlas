import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlasApi.service';
import { Observable } from 'rxjs';
import { ReadSublineVersionV2 } from '../../model/readSublineVersionV2';
import { CreateSublineVersionV2 } from '../../model/createSublineVersionV2';
import { SublineVersionV2 } from '../../model/sublineVersionV2';

@Injectable({
  providedIn: 'root',
})
export class SublineService {

  private readonly SUBLINE_VERSIONS = '/line-directory/v2/sublines/versions';

  private readonly atlasApiService = inject(AtlasApiService);

  public getSublineVersionV2(slnid: string): Observable<ReadSublineVersionV2[]> {
    this.atlasApiService.validateParams({ slnid });
    return this.atlasApiService.get(
      `${this.SUBLINE_VERSIONS}/${encodeURIComponent(String(slnid))}`);
  }

  public createSublineVersionV2(createSublineVersionV2: CreateSublineVersionV2): Observable<ReadSublineVersionV2> {
    this.atlasApiService.validateParams({ createSublineVersionV2 });
    return this.atlasApiService.post(this.SUBLINE_VERSIONS, createSublineVersionV2);
  }

  public updateSublineVersionV2(id: number, sublineVersionV2: SublineVersionV2): Observable<ReadSublineVersionV2[]> {
    this.atlasApiService.validateParams({ id, sublineVersionV2 });
    return this.atlasApiService.put(
      `${this.SUBLINE_VERSIONS}/${encodeURIComponent(String(id))}`, sublineVersionV2);
  }

}
