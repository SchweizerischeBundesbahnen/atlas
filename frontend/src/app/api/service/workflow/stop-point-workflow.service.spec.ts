import { TestBed } from '@angular/core/testing';
import { AtlasApiService } from '../atlas-api.service';
import { HttpClient, HttpParams } from '@angular/common/http';
import { UserService } from '../../../core/auth/user/user.service';
import { StopPointWorkflowService } from './stop-point-workflow.service';
import { StopPointAddWorkflow } from '../../model/stopPointAddWorkflow';
import { EditStopPointWorkflow } from '../../model/editStopPointWorkflow';
import { AddExaminants } from '../../model/addExaminants';
import { OverrideDecision } from '../../model/overrideDecision';
import { JudgementType } from '../../model/judgementType';
import { OtpRequest } from '../../model/otpRequest';
import { OtpVerification } from '../../model/otpVerification';
import { Decision } from '../../model/decision';
import { StopPointRejectWorkflow } from '../../model/stopPointRejectWorkflow';
import { StopPointRestartWorkflow } from '../../model/stopPointRestartWorkflow';

describe('StopPointWorkflowService', () => {
  let service: StopPointWorkflowService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        StopPointWorkflowService,
        AtlasApiService,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });
    service = TestBed.inject(StopPointWorkflowService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'get');
    spyOn(apiService, 'post');
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'paramsOf').and.callThrough();
  });

  it('should getStopPointWorkflow', () => {
    // when
    service.getStopPointWorkflow(1);

    // then
    expect(apiService.get).toHaveBeenCalledOnceWith('/workflow/v1/stop-point/workflows/1');
  });

  it('should getStopPointWorkflows', () => {
    // when
    service.getStopPointWorkflows(['Bern']);

    // then
    expect(apiService.get).toHaveBeenCalledOnceWith('/workflow/v1/stop-point/workflows', jasmine.any(HttpParams));
  });

  it('should addStopPointWorkflow', () => {
    // given
    const stopPointAddWorkflow: StopPointAddWorkflow = {
      sloid: 'ch:1sloid:700',
      versionId: 123,
      applicantMail: 'a@b.ch',
      workflowComment: 'Comment',
    };

    // when
    service.addStopPointWorkflow(stopPointAddWorkflow);

    // then
    expect(apiService.post).toHaveBeenCalledOnceWith('/workflow/v1/stop-point/workflows', stopPointAddWorkflow);
  });

  it('should startStopPointWorkflow', () => {
    // when
    service.startStopPointWorkflow(1);

    // then
    expect(apiService.post).toHaveBeenCalledOnceWith('/workflow/internal/stop-point/workflows/start/1');
  });

  it('should editStopPointWorkflow', () => {
    const editStopPointWorkflow:EditStopPointWorkflow = {
      designationOfficial: 'NewBern'
    };
    // when
    service.editStopPointWorkflow(1, editStopPointWorkflow);

    // then
    expect(apiService.post).toHaveBeenCalledOnceWith('/workflow/internal/stop-point/workflows/edit/1', editStopPointWorkflow);
  });

  it('should addExaminantsToStopPointWorkflow', () => {
    const addExaminants:AddExaminants = {
      ccEmails: [],
      examinants: [{
          organisation: "Gemeinde Thun",
          mail: "Thun@see.ch"
      }]
    };
    // when
    service.addExaminantsToStopPointWorkflow(1, addExaminants);

    // then
    expect(apiService.post).toHaveBeenCalledOnceWith('/workflow/internal/stop-point/workflows/add-examinants/1', addExaminants);
  });

  it('should getDecision', () => {
    // when
    service.getDecision(154);

    // then
    expect(apiService.post).toHaveBeenCalledOnceWith('/workflow/internal/stop-point/workflows/decisions/154');
  });

  it('should overrideVoteWorkflow', () => {
    const overrideDecision:OverrideDecision={
      firstName: 'Bernt', fotJudgement: JudgementType.No, lastName: 'Muili'
    };
    // when
    service.overrideVoteWorkflow(1,154, overrideDecision);

    // then
    expect(apiService.post).toHaveBeenCalledOnceWith('/workflow/internal/stop-point/workflows/override-vote/1/154', overrideDecision);
  });


  it('should obtainOtp', () => {
    const otpRequest: OtpRequest={
      examinantMail: "bernt@mueli.ch"
    };
    // when
    service.obtainOtp(1, otpRequest);

    // then
    expect(apiService.post).toHaveBeenCalledOnceWith('/workflow/internal/stop-point/workflows/obtain-otp/1', otpRequest);
  });

  it('should verifyOtp', () => {
    const otpVerification: OtpVerification={
      examinantMail: 'bernt@mueli.ch', pinCode: '64548'
    };
    // when
    service.verifyOtp(1, otpVerification);

    // then
    expect(apiService.post).toHaveBeenCalledOnceWith('/workflow/internal/stop-point/workflows/verify-otp/1', otpVerification);
  });

  it('should voteWorkflow', () => {
    const decision: Decision={
      examinantMail: "", firstName: "", judgement: JudgementType.No, lastName: "", organisation: "", personFunction: "", pinCode: ""
    };
    // when
    service.voteWorkflow(1,154, decision);

    // then
    expect(apiService.post).toHaveBeenCalledOnceWith('/workflow/internal/stop-point/workflows/vote/1/154', decision);
  });

  it('should getExaminants', () => {
    // when
    service.getExaminants(541654);

    // then
    expect(apiService.get).toHaveBeenCalledOnceWith('/workflow/internal/stop-point/workflows/541654/examinants');
  });

  it('should cancelStopPointWorkflow', () => {
    const stopPointRejectWorkflow: StopPointRejectWorkflow={
      mail: 'this@here.ch', organisation: 'bav'
    };

    // when
    service.cancelStopPointWorkflow(1, stopPointRejectWorkflow);

    // then
    expect(apiService.post).toHaveBeenCalledOnceWith('/workflow/internal/stop-point/workflows/cancel/1', stopPointRejectWorkflow);
  });

  it('should rejectStopPointWorkflow', () => {
    const stopPointRejectWorkflow: StopPointRejectWorkflow={
      mail: 'this@here.ch', organisation: 'bav'
    };

    // when
    service.rejectStopPointWorkflow(1, stopPointRejectWorkflow);

    // then
    expect(apiService.post).toHaveBeenCalledOnceWith('/workflow/internal/stop-point/workflows/reject/1', stopPointRejectWorkflow);
  });

  it('should restartStopPointWorkflow', () => {
    const stopPointRestartWorkflow: StopPointRestartWorkflow={
      designationOfficial: 'NewWashington', mail: '', organisation: ''
    };

    // when
    service.restartStopPointWorkflow(1, stopPointRestartWorkflow);

    // then
    expect(apiService.post).toHaveBeenCalledOnceWith('/workflow/internal/stop-point/workflows/restart/1', stopPointRestartWorkflow);
  });

});
