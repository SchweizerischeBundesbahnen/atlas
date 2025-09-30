import { inject, Injectable } from '@angular/core';
import { AtlasApiService } from '../atlas-api.service';
import { Observable } from 'rxjs';
import { StopPointAddWorkflow } from '../../model/stopPointAddWorkflow';
import { ReadStopPointWorkflow } from '../../model/readStopPointWorkflow';
import { EditStopPointWorkflow } from '../../model/editStopPointWorkflow';
import { AddExaminants } from '../../model/addExaminants';
import { ReadDecision } from '../../model/readDecision';
import { OverrideDecision } from '../../model/overrideDecision';
import { OtpRequest } from '../../model/otpRequest';
import { StopPointPerson } from '../../model/stopPointPerson';
import { OtpVerification } from '../../model/otpVerification';
import { Decision } from '../../model/decision';
import { WorkflowStatus } from '../../model/workflowStatus';
import { ContainerReadStopPointWorkflow } from '../../model/containerReadStopPointWorkflow';
import { StopPointRejectWorkflow } from '../../model/stopPointRejectWorkflow';
import { StopPointRestartWorkflow } from '../../model/stopPointRestartWorkflow';

@Injectable({
  providedIn: 'root',
})
export class StopPointWorkflowService {

  private readonly STOP_POINT_WORKFLOW = '/workflow/v1/stop-point/workflows';
  private readonly STOP_POINT_WORKFLOW_INTERNAL = '/workflow/internal/stop-point/workflows';

  private readonly atlasApiService = inject(AtlasApiService);

  getStopPointWorkflow(id: number):Observable<ReadStopPointWorkflow> {
    return this.atlasApiService.get(`${this.STOP_POINT_WORKFLOW}/${encodeURIComponent(String(id))}`);
  }

  getStopPointWorkflows(searchCriterias?: Array<string>, workflowIds?: Array<number>, status?: Array<WorkflowStatus>, sloids?: Array<string>, designationOfficial?: Array<string>, localityName?: string, sboids?: Array<string>, createdAt?: string, versionValidFrom?: Date, filterByNoDecision?: boolean, page?: number, size?: number, sort?: Array<string>):Observable<ContainerReadStopPointWorkflow>{
    const httpParams = this.atlasApiService.paramsOf({
      searchCriterias,
      workflowIds,
      status,
      sloids,
      designationOfficial,
      localityName,
      sboids,
      createdAt,
      versionValidFrom,
      filterByNoDecision,
      page,
      size,
      sort,
    });
    return this.atlasApiService.get(this.STOP_POINT_WORKFLOW, httpParams);
  }

  addStopPointWorkflow(stopPointAddWorkflow: StopPointAddWorkflow): Observable<ReadStopPointWorkflow> {
    this.atlasApiService.validateParams({ stopPointAddWorkflow });
    return this.atlasApiService.post(`${this.STOP_POINT_WORKFLOW}`, stopPointAddWorkflow);
  }

  startStopPointWorkflow(id: number):Observable<ReadStopPointWorkflow> {
    return this.atlasApiService.post(`${this.STOP_POINT_WORKFLOW_INTERNAL}/start/${encodeURIComponent(String(id))}`);
  }

  editStopPointWorkflow(id: number, editStopPointWorkflow: EditStopPointWorkflow): Observable<ReadStopPointWorkflow>{
    this.atlasApiService.validateParams({ id, editStopPointWorkflow });
    return this.atlasApiService.post(`${this.STOP_POINT_WORKFLOW_INTERNAL}/edit/${encodeURIComponent(String(id))}`, editStopPointWorkflow);
  }

  addExaminantsToStopPointWorkflow(id: number, addExaminants: AddExaminants){
    this.atlasApiService.validateParams({ id, addExaminants });
    return this.atlasApiService.post(`${this.STOP_POINT_WORKFLOW_INTERNAL}/add-examinants/${encodeURIComponent(String(id))}`, addExaminants);
  }

  getDecision(personId: number):Observable<ReadDecision>{
    return this.atlasApiService.post(`${this.STOP_POINT_WORKFLOW_INTERNAL}/decisions/${encodeURIComponent(String(personId))}`);
  }

  overrideVoteWorkflow(id: number, personId: number, overrideDecision: OverrideDecision):Observable<void>{
    this.atlasApiService.validateParams({ id, personId, overrideDecision });
    return this.atlasApiService.post(`${this.STOP_POINT_WORKFLOW_INTERNAL}/override-vote/${encodeURIComponent(String(id))}/${encodeURIComponent(String(personId))}`, overrideDecision);

  }

  obtainOtp(id: number, otpRequest: OtpRequest):Observable<void>{
    this.atlasApiService.validateParams({ id, otpRequest });
    return this.atlasApiService.post(`${this.STOP_POINT_WORKFLOW_INTERNAL}/obtain-otp/${encodeURIComponent(String(id))}`, otpRequest);
  }

  verifyOtp(id: number, otpVerification: OtpVerification):Observable<StopPointPerson>{
    this.atlasApiService.validateParams({ id, otpVerification });
    return this.atlasApiService.post(`${this.STOP_POINT_WORKFLOW_INTERNAL}/verify-otp/${encodeURIComponent(String(id))}`, otpVerification);
  }

  voteWorkflow(id: number, personId: number, decision: Decision):Observable<void>{
    this.atlasApiService.validateParams({ id, personId, decision });
    return this.atlasApiService.post(`${this.STOP_POINT_WORKFLOW_INTERNAL}/vote/${encodeURIComponent(String(id))}/${encodeURIComponent(String(personId))}`, decision);
  }

  getExaminants(servicePointVersionId: number):Observable<Array<StopPointPerson>>{
    return this.atlasApiService.get(`${this.STOP_POINT_WORKFLOW_INTERNAL}/${encodeURIComponent(String(servicePointVersionId))}/examinants`);
  }

  cancelStopPointWorkflow(id: number, stopPointRejectWorkflow: StopPointRejectWorkflow):Observable<ReadStopPointWorkflow>{
    this.atlasApiService.validateParams({ id, stopPointRejectWorkflow });
    return this.atlasApiService.post(`${this.STOP_POINT_WORKFLOW_INTERNAL}/cancel/${encodeURIComponent(String(id))}`, stopPointRejectWorkflow);
  }

  rejectStopPointWorkflow(id: number, stopPointRejectWorkflow: StopPointRejectWorkflow): Observable<ReadStopPointWorkflow>{
    this.atlasApiService.validateParams({ id, stopPointRejectWorkflow });
    return this.atlasApiService.post(`${this.STOP_POINT_WORKFLOW_INTERNAL}/reject/${encodeURIComponent(String(id))}`, stopPointRejectWorkflow);
  }

  restartStopPointWorkflow(id: number, stopPointRestartWorkflow: StopPointRestartWorkflow): Observable<ReadStopPointWorkflow>{
    this.atlasApiService.validateParams({ id, stopPointRestartWorkflow });
    return this.atlasApiService.post(`${this.STOP_POINT_WORKFLOW_INTERNAL}/restart/${encodeURIComponent(String(id))}`, stopPointRestartWorkflow);
  }

}
