import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, Mocked, vi } from 'vitest';
import { StopPointTerminationWorkflowDetail } from './stop-point-termination-workflow-detail';
import { ActivatedRoute, Router } from '@angular/router';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';
import { StopPointTerminationWorkflowDetailData } from './stop-point-termination-workflow-resolver';
import { TranslatePipe } from '@ngx-translate/core';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { BoSelectionDisplayPipe } from '../../../../core/pipe/bo-selection-display.pipe';
import { of } from 'rxjs';
import { TerminationWorkflowStatus } from '../../../../api/model/terminationWorkflowStatus';
import { TerminationDecision } from '../../../../api/model/terminationDecision';
import { StopPointTerminationWorkflowDetailFormGroupBuilder } from './stop-point-termination-workflow-detail-form-group';
import moment from 'moment/moment';
import { TerminationStopPointWorkflowModel } from '../../../../api/model/terminationStopPointWorkflowModel';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import {
  TerminationDecisionDetailDialogComponent
} from './decision/decision-detail/termination-decision-detail-dialog.component';
import { translateServiceProvider } from '../../../../app.testing.mocks';
import TerminationDecisionPersonEnum = TerminationDecision.TerminationDecisionPersonEnum;

describe('StopPointTerminationWorkflowDetail', () => {
  let component: StopPointTerminationWorkflowDetail;
  let fixture: ComponentFixture<StopPointTerminationWorkflowDetail>;

  let dialogServiceSpy: Mocked<Pick<DialogService, 'openWithoutResult' | 'openDialogDataWithConfirmationResult'>>;

  beforeEach(() => {
    const workflow: TerminationStopPointWorkflowModel = {
      id: 10,
      sloid: 'ch:1sloid:700',
      versionId: 1000,
      boTerminationDate: new Date('2029-06-01'),
      applicantMail: 'a@b.ch',
      workflowComment: 'Comment',
      designationOfficial: 'designationOfficial',
      versionValidTo: new Date('9999-06-01'),
    };

    const workflowData: StopPointTerminationWorkflowDetailData = {
      workflow: workflow,
      servicePoint: [BERN_WYLEREGG],
    };

    const activatedRoute = {
      data: of({
        workflow: workflowData,
      }),
    };

    dialogServiceSpy = {
      openWithoutResult: vi.fn(),
      openDialogDataWithConfirmationResult: vi.fn().mockReturnValue(of(true)),
    };

    TestBed.configureTestingModule({
      imports: [StopPointTerminationWorkflowDetail],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TranslatePipe },
        { provide: BoSelectionDisplayPipe },
        { provide: Router },
        { provide: DialogService, useValue: dialogServiceSpy },
        translateServiceProvider,
      ],
    });

    fixture = TestBed.createComponent(StopPointTerminationWorkflowDetail);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.terminationPermission).toBeUndefined();
  });

  it('should go to atlas stoppoint', () => {
    vi.spyOn(window, 'open').mockImplementation(() => null);

    component.goToAtlasStopPoint();
    expect(window.open).toHaveBeenCalledWith('/service-point-directory/service-points/8589008?id=1000', '_blank');
  });

  it('should open decision', () => {
    component.onOpenDecision(component.form.controls.examinants.at(0));
    expect(dialogServiceSpy.openWithoutResult).toHaveBeenCalledTimes(1);
  });

  it('should open decision dialog', () => {
    component.openDecisionDialog();
    expect(dialogServiceSpy.openDialogDataWithConfirmationResult).toHaveBeenCalledTimes(1);
  });

  it('should open decision dialog on nova revote with an extra day and judgement prefilled', () => {
    component.workflow.status = TerminationWorkflowStatus.TerminationNotApproved;
    component.terminationPermission = TerminationDecisionPersonEnum.Nova;

    const expectedDecisionForm = StopPointTerminationWorkflowDetailFormGroupBuilder.buildTerminationDecisionFormGroup();
    expectedDecisionForm.controls.terminationDecisionPerson.setValue(TerminationDecisionPersonEnum.Nova);
    expectedDecisionForm.controls.terminationDate.setValue(moment('2029-06-01'));

    component.openDecisionDialog();
    expect(dialogServiceSpy.openDialogDataWithConfirmationResult).toHaveBeenCalledExactlyOnceWith(
      expect.objectContaining({ workflowId: 10 }),
      TerminationDecisionDetailDialogComponent
    );
  });
});
