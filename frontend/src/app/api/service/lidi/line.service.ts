import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { LineVersionV2 } from '../../model/lineVersionV2';
import { UpdateLineVersionV2 } from '../../model/updateLineVersionV2';

@Injectable({
  providedIn: 'root',
})
export class LineService {

  private readonly V2_LINES = '/line-directory/v2/lines';

  private readonly atlasApiService = inject(AtlasApiService);

  public getLineVersionsV2(slnid: string): Observable<LineVersionV2[]> {
    this.atlasApiService.validateParams({ slnid });
    return this.atlasApiService.get(`${this.V2_LINES}/versions/${encodeURIComponent(String(slnid))}`);
  }

  public createLineVersionV2(lineVersionV2: LineVersionV2): Observable<LineVersionV2> {
    this.atlasApiService.validateParams({ lineVersionV2 });
    return this.atlasApiService.post(`${this.V2_LINES}/versions`, lineVersionV2);
  }

  public updateLineVersion(id: number, updateLineVersionV2: UpdateLineVersionV2): Observable<LineVersionV2[]> {
    this.atlasApiService.validateParams({ id, updateLineVersionV2 });
    return this.atlasApiService.put(`${this.V2_LINES}/versions/${encodeURIComponent(String(id))}`, updateLineVersionV2);
  }

}
