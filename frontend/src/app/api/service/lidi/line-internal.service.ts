import { Injectable } from '@angular/core';
import { AtlasApiService } from '../atlasApi.service';
import { Observable } from 'rxjs';
import { WorkflowStatus } from '../../model/workflowStatus';
import { ContainerLineVersionSnapshot } from '../../model/containerLineVersionSnapshot';
import { LineVersionSnapshot } from '../../model/lineVersionSnapshot';
import { UpdateLineVersionV2 } from '../../model/updateLineVersionV2';
import { AffectedSublinesModel } from '../../model/affectedSublinesModel';

@Injectable({
  providedIn: 'root',
})
export class LineInternalService extends AtlasApiService {

  public revokeLine(slnid: string): Observable<void> {
    this.validateParams({ slnid });
    return this.post(`/line-directory/internal/lines/${encodeURIComponent(String(slnid))}/revoke`);
  }

  public deleteLines(slnid: string): Observable<void> {
    this.validateParams({ slnid });
    return this.delete(`/line-directory/internal/lines/${encodeURIComponent(String(slnid))}`);
  }

  public skipWorkflow(id: number): Observable<void> {
    this.validateParams({ id });
    return this.post(`/line-directory/internal/lines/versions/${encodeURIComponent(String(id))}/skip-workflow`);
  }

  public getLineVersionSnapshot(searchCriteria?: Array<string>, validOn?: Date,
                                statusChoices?: Array<WorkflowStatus>, page?: number,
                                size?: number, sort?: Array<string>): Observable<ContainerLineVersionSnapshot> {
    const httpParams = this.paramsOf({ searchCriteria, validOn, statusChoices, page, size, sort });
    return this.get(`/line-directory/internal/lines/workflows`, 'json', httpParams);
  }

  public getLineVersionSnapshotById(id: number): Observable<LineVersionSnapshot> {
    this.validateParams({ id });
    return this.get(`/line-directory/internal/lines/workflows/${encodeURIComponent(String(id))}`, 'json');
  }

  public checkAffectedSublines(id: number, updateLineVersionV2: UpdateLineVersionV2): Observable<AffectedSublinesModel> {
    this.validateParams({ id, updateLineVersionV2 });
    return this.post(`/line-directory/internal/lines/affectedSublines/${encodeURIComponent(String(id))}`, updateLineVersionV2);
  }

}
