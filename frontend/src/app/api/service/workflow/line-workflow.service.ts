import {inject, Injectable} from '@angular/core';
import {AtlasApiService} from '../atlas-api.service';
import {Observable} from 'rxjs';
import {Workflow} from "../../model/workflow";
import {WorkflowStart} from "../../model/workflowStart";
import {ExaminantWorkflowCheck} from "../../model/examinantWorkflowCheck";

@Injectable({
  providedIn: 'root',
})
export class LineWorkflowService {

  private readonly LINE_WORKFLOW_INTERNAL = '/workflow/internal/line/workflows';

  private readonly atlasApiService = inject(AtlasApiService);

  getWorkflow(id: number):Observable<Workflow> {
    return this.atlasApiService.get(`${this.LINE_WORKFLOW_INTERNAL}/${encodeURIComponent(String(id))}`);
  }

  startWorkflow(workflowStart: WorkflowStart): Observable<Workflow>{
    this.atlasApiService.validateParams({ workflowStart });
    return this.atlasApiService.post(`${this.LINE_WORKFLOW_INTERNAL}`, workflowStart);
  }

  examinantCheck(id: number, examinantWorkflowCheck: ExaminantWorkflowCheck):Observable<Workflow>{
    this.atlasApiService.validateParams({ id, examinantWorkflowCheck });
    return this.atlasApiService.post(`${this.LINE_WORKFLOW_INTERNAL}/${encodeURIComponent(String(id))}/examinant-check`, examinantWorkflowCheck);
  }
}
