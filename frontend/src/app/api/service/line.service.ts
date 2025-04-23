import { Injectable } from '@angular/core';
import { AtlasApiService } from './atlasApi.service';
import { Status } from '../model/status';
import { LidiElementType } from '../model/lidiElementType';
import { ElementType } from '../model/elementType';
import { Observable } from 'rxjs';
import { ContainerLine } from '../model/containerLine';
import { Line } from '../model/line';
import { LineVersion } from '../model/lineVersion';
import { LineVersionV2 } from '../model/lineVersionV2';
import { UpdateLineVersionV2 } from '../model/updateLineVersionV2';

@Injectable({
  providedIn: 'root',
})
export class LineService extends AtlasApiService {

  public getLines(swissLineNumber?: string, searchCriteria?: Array<string>,
                  statusRestrictions?: Array<Status>, typeRestrictions?: Array<LidiElementType>,
                  elementRestrictions?: Array<ElementType>, businessOrganisation?: string,
                  validOn?: Date, fromDate?: Date, toDate?: Date, validToFromDate?: Date, createdAfter?: string,
                  modifiedAfter?: string, page?: number, size?: number, sort?: Array<string>,
  ): Observable<ContainerLine> {
    const httpParams = this.paramsOf({
      swissLineNumber,
      searchCriteria,
      statusRestrictions,
      typeRestrictions,
      elementRestrictions,
      businessOrganisation,
      validOn,
      fromDate,
      toDate,
      validToFromDate,
      createdAfter,
      modifiedAfter,
      page,
      size,
      sort,
    });
    return this.get(`/line-directory/v1/lines`, httpParams);
  }

  public getLine(slnid: string): Observable<Line> {
    this.validateParams({ slnid });
    return this.get(`/line-directory/v1/lines/${encodeURIComponent(String(slnid))}`);
  }

  public getLineVersions(slnid: string): Observable<LineVersion[]> {
    this.validateParams({ slnid });
    return this.get(`/line-directory/v1/lines/versions/${encodeURIComponent(String(slnid))}`);
  }

  public getLineVersionsV2(slnid: string): Observable<LineVersionV2[]> {
    this.validateParams({ slnid });
    return this.get(`/line-directory/v2/lines/versions/${encodeURIComponent(String(slnid))}`);
  }

  public createLineVersionV2(lineVersionV2: LineVersionV2): Observable<LineVersionV2> {
    this.validateParams({ lineVersionV2 });
    return this.post(`/line-directory/v2/lines/versions`, lineVersionV2);
  }

  public updateLineVersion(id: number, updateLineVersionV2: UpdateLineVersionV2): Observable<LineVersionV2[]> {
    this.validateParams({ id, updateLineVersionV2 });
    return this.put(`/line-directory/v2/lines/versions/${encodeURIComponent(String(id))}`, updateLineVersionV2);
  }

}
