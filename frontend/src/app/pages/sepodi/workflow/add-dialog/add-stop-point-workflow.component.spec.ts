import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddStopPointWorkflowComponent } from './add-stop-point-workflow.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { adminUserServiceMock } from '../../../../app.testing.mocks';
import { NotificationService } from '../../../../core/notification/notification.service';
import { AddStopPointWorkflowDialogData } from './add-stop-point-workflow-dialog-data';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';
import { DetailHelperService } from '../../../../core/detail/detail-helper.service';
import { of } from 'rxjs';
import {
  DecisionType,
  JudgementType,
  ReadStopPointWorkflow,
  StopPointWorkflowService,
} from '../../../../api';
import { Router } from '@angular/router';
import { UserService } from '../../../../core/auth/user/user.service';
import { ValidationService } from '../../../../core/validation/validation.service';
import {
  ExaminantFormGroup,
  StopPointWorkflowDetailFormGroup,
  StopPointWorkflowDetailFormGroupBuilder,
} from '../detail-page/detail-form/stop-point-workflow-detail-form-group';
import { FormArray, FormControl, FormGroup } from '@angular/forms';
import { BoSelectionDisplayPipe } from '../../../../core/form-components/bo-select/bo-selection-display.pipe';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

const workflow: ReadStopPointWorkflow = {
  versionId: 1,
  sloid: 'ch:1:sloid:8000',
  workflowComment: 'No comment',
};
const dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);
const notificationServiceSpy = jasmine.createSpyObj('NotificationService', [
  'success',
]);
const router = jasmine.createSpyObj({
  navigate: Promise.resolve(),
});
const detailHelperService = jasmine.createSpyObj({
  confirmLeaveDirtyForm: of(true),
});
const stopPointWorkflowService = jasmine.createSpyObj(
  'StopPointWorkflowService',
  {
    addStopPointWorkflow: of(workflow),
    getExaminants: of(workflow),
  }
);

const workflowDialogData: AddStopPointWorkflowDialogData = {
  title: '',
  message: '',
  stopPoint: BERN_WYLEREGG,
};

describe('AddStopPointWorkflowComponent', () => {
  let component: AddStopPointWorkflowComponent;
  let fixture: ComponentFixture<AddStopPointWorkflowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddStopPointWorkflowComponent, TranslateModule.forRoot()],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: MAT_DIALOG_DATA,
          useValue: workflowDialogData,
        },
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: DetailHelperService, useValue: detailHelperService },
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

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should cancel workflow creation', () => {
    component.cancel();

    expect(detailHelperService.confirmLeaveDirtyForm).toHaveBeenCalled();
    expect(dialogRefSpy.close).toHaveBeenCalled();
  });

  it('should add workflow via service', () => {
    spyOn(ValidationService, 'validateForm').and.callThrough();

    component.form.controls.examinants.push(
      StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup()
    );
    const firstExaminant = component.form.controls.examinants.at(0);
    firstExaminant.controls.firstName.setValue('');
    firstExaminant.controls.lastName.setValue('');
    firstExaminant.controls.personFunction.setValue('personFunction');
    firstExaminant.controls.organisation.setValue('organisation');
    firstExaminant.controls.mail.setValue('mail@sbb.ch');

    component.form.controls.workflowComment.setValue('YB isch wida Meista');

    component.addWorkflow();

    expect(stopPointWorkflowService.addStopPointWorkflow).toHaveBeenCalled();
    expect(notificationServiceSpy.success).toHaveBeenCalled();
    expect(dialogRefSpy.close).toHaveBeenCalled();
  });

  it('should transform examinants firstName and lastName to null if empty', () => {
    spyOn(ValidationService, 'validateForm').and.callThrough();

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

    const formArray = new FormArray<FormGroup<ExaminantFormGroup>>([
      examinantFormGroup,
    ]);
    component.form = new FormGroup<StopPointWorkflowDetailFormGroup>({
      ccEmails: new FormControl<Array<string> | null>(null),
      workflowComment: new FormControl<string | null>('Workflow comment 1'),
      designationOfficial: new FormControl<string | null>(null),
      examinants: formArray,
    });

    component.addWorkflow();

    expect(stopPointWorkflowService.addStopPointWorkflow).toHaveBeenCalledWith(
      jasmine.objectContaining({
        examinants: [
          jasmine.objectContaining({
            firstName: null,
            lastName: null,
          }),
        ],
      })
    );

    expect(notificationServiceSpy.success).toHaveBeenCalled();
    expect(dialogRefSpy.close).toHaveBeenCalled();
  });
});
