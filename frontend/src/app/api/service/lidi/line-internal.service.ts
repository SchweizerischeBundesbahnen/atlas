import { inject, Injectable } from '@angular/core';
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
export class LineInternalService {

  private readonly INTERNAL_LINES = '/line-directory/internal/lines';

  private readonly atlasApiService = inject(AtlasApiService);

  public revokeLine(slnid: string): Observable<void> {
    this.atlasApiService.validateParams({ slnid });
    return this.atlasApiService.post(
      `${this.INTERNAL_LINES}/${encodeURIComponent(String(slnid))}/revoke`);
  }

  public deleteLines(slnid: string): Observable<void> {
    this.atlasApiService.validateParams({ slnid });
    return this.atlasApiService.delete(
      `${this.INTERNAL_LINES}/${encodeURIComponent(String(slnid))}`);
  }

  public skipWorkflow(id: number): Observable<void> {
    this.atlasApiService.validateParams({ id });
    return this.atlasApiService.post(
      `${this.INTERNAL_LINES}/versions/${encodeURIComponent(String(id))}/skip-workflow`);
  }

  public getLineVersionSnapshot(searchCriteria?: Array<string>, validOn?: Date,
                                statusChoices?: Array<WorkflowStatus>, page?: number,
                                size?: number, sort?: Array<string>): Observable<ContainerLineVersionSnapshot> {
    const httpParams = this.atlasApiService.paramsOf({ searchCriteria, validOn, statusChoices, page, size, sort });
    return this.atlasApiService.get(
      `${this.INTERNAL_LINES}/workflows`, httpParams);
  }

  public getLineVersionSnapshotById(id: number): Observable<LineVersionSnapshot> {
    this.atlasApiService.validateParams({ id });
    return this.atlasApiService.get(
      `${this.INTERNAL_LINES}/workflows/${encodeURIComponent(String(id))}`);
  }

  public checkAffectedSublines(id: number, updateLineVersionV2: UpdateLineVersionV2): Observable<AffectedSublinesModel> {
    this.atlasApiService.validateParams({ id, updateLineVersionV2 });
    return this.atlasApiService.post(
      `${this.INTERNAL_LINES}/affectedSublines/${encodeURIComponent(String(id))}`,
      updateLineVersionV2);
  }

}
