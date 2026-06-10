import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { AddStopPointWorkflowComponent } from './add-stop-point-workflow.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { adminUserServiceMock } from '../../../../app.testing.mocks';
import { NotificationService } from '../../../../core/notification/notification.service';
import { AddStopPointWorkflowDialogData } from './add-stop-point-workflow-dialog-data';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';
import { DetailDialogHelperService } from '../../../../core/detail/detail-dialog-helper.service';
import { of } from 'rxjs';
import { DecisionType, JudgementType, ReadStopPointWorkflow } from '../../../../api';
import { Router } from '@angular/router';
import { UserService } from '../../../../core/auth/user/user.service';
import {
  ExaminantFormGroup,
  StopPointWorkflowDetailFormGroup,
} from '../detail-page/detail-form/stop-point-workflow-detail-form-group';
import { FormArray, FormControl, FormGroup } from '@angular/forms';
import { BoSelectionDisplayPipe } from '../../../../core/form-components/bo-select/bo-selection-display.pipe';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { StopPointWorkflowService } from '../../../../api/service/workflow/stop-point-workflow.service';

describe('AddStopPointWorkflowComponent', () => {
  const workflow: ReadStopPointWorkflow = {
    versionId: 1,
    sloid: 'ch:1:sloid:8000',
    workflowComment: 'No comment',
  };

  const workflowDialogData: AddStopPointWorkflowDialogData = {
    title: '',
    message: '',
    stopPoint: BERN_WYLEREGG,
  };

  let component: AddStopPointWorkflowComponent;
  let fixture: ComponentFixture<AddStopPointWorkflowComponent>;

  let dialogRefSpy: Mocked<Pick<MatDialogRef<AddStopPointWorkflowComponent>, 'close'>>;
  let notificationServiceSpy: Mocked<Pick<NotificationService, 'success'>>;
  let router: Mocked<Pick<Router, 'navigate'>>;
  let detailHelperService: Mocked<Pick<DetailDialogHelperService, 'confirmLeaveDirtyForm'>>;
  let stopPointWorkflowService: Mocked<Pick<StopPointWorkflowService, 'addStopPointWorkflow' | 'getExaminants'>>;

  beforeEach(async () => {
    dialogRefSpy = { close: vi.fn() };
    notificationServiceSpy = { success: vi.fn() };
    router = {
      navigate: vi.fn().mockReturnValue(Promise.resolve()),
    };
    detailHelperService = {
      confirmLeaveDirtyForm: vi.fn().mockReturnValue(of(true)),
    };
    stopPointWorkflowService = {
      addStopPointWorkflow: vi.fn().mockReturnValue(of(workflow)),
      getExaminants: vi.fn().mockReturnValue(of([])),
    };

    await TestBed.configureTestingModule({
      imports: [AddStopPointWorkflowComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: MAT_DIALOG_DATA,
          useValue: workflowDialogData,
        },
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: DetailDialogHelperService, useValue: detailHelperService },
        {
          provide: StopPointWorkflowService,
          useValue: stopPointWorkflowService,
        },
        { provide: UserService, useValue: adminUserServiceMock },
        { provide: Router, useValue: router },
        { provide: TranslatePipe },
        { provide: BoSelectionDisplayPipe },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AddStopPointWorkflowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should cancel workflow creation', () => {
    component.cancel();

    expect(detailHelperService.confirmLeaveDirtyForm).toHaveBeenCalledTimes(1);
    expect(dialogRefSpy.close).toHaveBeenCalledExactlyOnceWith(true);
  });

  it('should add workflow via service', () => {
    const firstExaminant = component.form.controls.examinants.at(0);
    firstExaminant.controls.firstName.setValue('');
    firstExaminant.controls.lastName.setValue('');
    firstExaminant.controls.personFunction.setValue('personFunction');
    firstExaminant.controls.organisation.setValue('organisation');
    firstExaminant.controls.mail.setValue('mail@sbb.ch');

    component.form.controls.workflowComment.setValue('YB isch wida Meista');

    component.addWorkflow();

    expect(stopPointWorkflowService.addStopPointWorkflow).toHaveBeenCalledTimes(1);
    expect(notificationServiceSpy.success).toHaveBeenCalledExactlyOnceWith('WORKFLOW.NOTIFICATION.ADD.SUCCESS');
    expect(dialogRefSpy.close).toHaveBeenCalledExactlyOnceWith();
  });

  it('should transform examinants firstName and lastName to null if empty', () => {
    const examinantFormGroup = new FormGroup<ExaminantFormGroup>({
      id: new FormControl<number | null>(null),
      firstName: new FormControl<string | null>(''),
      lastName: new FormControl<string | null>(''),
      personFunction: new FormControl<string | null>('personFunction1'),
      organisation: new FormControl<string | null>('organisation1'),
      mail: new FormControl<string | null>('mail1@sbb.ch'),
      judgementIcon: new FormControl<string | null>(null),
      judgement: new FormControl<JudgementType | null>(null),
      decisionType: new FormControl<DecisionType | null>(null),
      defaultExaminant: new FormControl(false),
    });

    const formArray = new FormArray<FormGroup<ExaminantFormGroup>>([examinantFormGroup]);
    component.form = new FormGroup<StopPointWorkflowDetailFormGroup>({
      ccEmails: new FormControl<Array<string> | null>(null),
      workflowComment: new FormControl<string | null>('Workflow comment 1'),
      designationOfficial: new FormControl<string | null>(null),
      examinants: formArray,
    });

    component.addWorkflow();

    expect(stopPointWorkflowService.addStopPointWorkflow).toHaveBeenCalledExactlyOnceWith(
      expect.objectContaining({
        examinants: [
          expect.objectContaining({
            firstName: null,
            lastName: null,
          }),
        ],
      })
    );

    expect(notificationServiceSpy.success).toHaveBeenCalledExactlyOnceWith('WORKFLOW.NOTIFICATION.ADD.SUCCESS');
    expect(dialogRefSpy.close).toHaveBeenCalledExactlyOnceWith();
  });
});
