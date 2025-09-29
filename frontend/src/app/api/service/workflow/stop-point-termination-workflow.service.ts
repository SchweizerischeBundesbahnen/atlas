import {inject, Injectable} from '@angular/core';
import {AtlasApiService} from '../atlas-api.service';
import {TerminationStopPointAddWorkflow} from '../../model/terminationStopPointAddWorkflow';
import {Observable} from 'rxjs';
import {TerminationInfo} from '../../model/terminationInfo';
import {ContainerTerminationStopPointWorkflowModel} from '../../model/containerTerminationStopPointWorkflowModel';
import {TerminationWorkflowStatus} from '../../model/terminationWorkflowStatus';
import {TerminationDecision} from '../../model/terminationDecision';
import {TerminationStopPointWorkflowModel} from "../../model/terminationStopPointWorkflowModel";
import {TerminationAbort} from "../../model/terminationAbort";

@Injectable({
  providedIn: 'root',
})
export class StopPointTerminationWorkflowService {

  private readonly STOP_POINT_TERMINATION = '/workflow/v1/termination-stop-point/workflows';
  private readonly STOP_POINT_TERMINATION_INTERNAL = '/workflow/internal/termination-stop-point/workflows';

  private readonly atlasApiService = inject(AtlasApiService);

  public startTermination(terminationStopPointAddWorkflow: TerminationStopPointAddWorkflow): Observable<TerminationStopPointAddWorkflow> {
    return this.atlasApiService.post(`${this.STOP_POINT_TERMINATION}`, terminationStopPointAddWorkflow);
  }

  public getTerminationInfoBySloid(sloid: string): Observable<TerminationInfo> {
    this.atlasApiService.validateParams({ sloid });
    return this.atlasApiService.get(`${this.STOP_POINT_TERMINATION_INTERNAL}/termination-info/${encodeURIComponent(String(sloid))}`);
  }

  getTerminationStopPointWorkflows(searchCriterias?: string[], sboids?: string[], workflowIds?: number[],
                                   status?: TerminationWorkflowStatus[], page?: number, size?: number, sort?: string[],
  ): Observable<ContainerTerminationStopPointWorkflowModel> {
    const httpParams = this.atlasApiService.paramsOf({
      searchCriterias,
      sboids,
      workflowIds,
      status,
      page,
      size,
      sort,
    });
    return this.atlasApiService.get(this.STOP_POINT_TERMINATION, httpParams);
  }

  public getTerminationById(id: number): Observable<TerminationStopPointWorkflowModel> {
    this.atlasApiService.validateParams({ id });
    return this.atlasApiService.get(`${this.STOP_POINT_TERMINATION}/${encodeURIComponent(String(id))}`);
  }

  decisionInfoPlus(id: number, decision: TerminationDecision): Observable<TerminationStopPointAddWorkflow> {
    this.atlasApiService.validateParams({ id, decision });
    return this.atlasApiService.post(`${this.STOP_POINT_TERMINATION_INTERNAL}/decision/info-plus/${encodeURIComponent(String(id))}`, decision);
  }

  decisionNova(id: number, decision: TerminationDecision): Observable<TerminationStopPointAddWorkflow> {
    this.atlasApiService.validateParams({ id, decision });
    return this.atlasApiService.post(`${this.STOP_POINT_TERMINATION_INTERNAL}/decision/nova/${encodeURIComponent(String(id))}`, decision);
  }

  abortTermination(id: number, terminationAbort: TerminationAbort): Observable<TerminationStopPointAddWorkflow> {
    this.atlasApiService.validateParams({ id, terminationCancel: terminationAbort });
    return this.atlasApiService.post(`${this.STOP_POINT_TERMINATION_INTERNAL}/abort/${encodeURIComponent(String(id))}`, terminationAbort);
  }
}
