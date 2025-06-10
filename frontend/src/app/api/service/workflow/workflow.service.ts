import {inject, Injectable} from '@angular/core';
import {AtlasApiService} from "../atlasApi.service";
import {TerminationStopPointAddWorkflow} from "../../model/terminationStopPointAddWorkflow";
import {Observable} from "rxjs";
import {TerminationInfo} from "../../model/terminationInfo";

@Injectable({
  providedIn: 'root'
})
export class WorkflowService {

  private readonly STOP_POINT_TERMINATION = '/workflow/internal/termination-stop-point/workflows';

  private readonly atlasApiService = inject(AtlasApiService);

  public startTermination( terminationStopPointAddWorkflow:TerminationStopPointAddWorkflow): Observable<TerminationStopPointAddWorkflow> {
    return this.atlasApiService.post(`${this.STOP_POINT_TERMINATION}`,terminationStopPointAddWorkflow);
  }

  public getTerminationInfoBySloid(sloid: string): Observable<TerminationInfo> {
    this.atlasApiService.validateParams({ sloid });
    return this.atlasApiService.get(`${this.STOP_POINT_TERMINATION}/termination-info/${encodeURIComponent(String(sloid))}`);
  }

}
