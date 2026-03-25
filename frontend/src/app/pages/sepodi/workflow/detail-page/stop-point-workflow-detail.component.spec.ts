import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { StopPointWorkflowDetailComponent } from './stop-point-workflow-detail.component';
import { ActivatedRoute } from '@angular/router';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';
import {
  Country,
  DecisionType,
  JudgementType,
  MeanOfTransport,
  ReadServicePointVersion,
  ReadStopPointWorkflow,
  Status,
} from '../../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import { StopPointWorkflowDetailData } from './stop-point-workflow-detail-resolver.service';
import { of } from 'rxjs';
import { NotificationService } from '../../../../core/notification/notification.service';
import { MatDialog } from '@angular/material/dialog';
import { DecisionStepperComponent } from './decision/decision-stepper/decision-stepper.component';
import { ValidationService } from '../../../../core/validation/validation.service';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { StopPointWorkflowDetailFormGroupBuilder } from './detail-form/stop-point-workflow-detail-form-group';
import { StopPointWorkflowService } from '../../../../api/service/workflow/stop-point-workflow.service';
import { translateServiceProvider } from '../../../../app.testing.mocks';
import { BoSelectionDisplayPipe } from '../../../../core/form-components/bo-select/bo-selection-display.pipe';

describe('StopPointWorkflowDetailComponent', () => {
  const workflow: ReadStopPointWorkflow = {
    versionId: 1000,
    sloid: 'ch:1:sloid:8000',
    workflowComment: 'No comment',
    id: 1,
  };

  const workflowData: StopPointWorkflowDetailData = {
    workflow: workflow,
    servicePoint: [BERN_WYLEREGG],
  };

  const activatedRoute = {
    snapshot: {
      data: {
        workflow: workflowData,
      },
    },
  };

  let component: StopPointWorkflowDetailComponent;
  let fixture: ComponentFixture<StopPointWorkflowDetailComponent>;
  let dialogSpy: Mocked<Pick<MatDialog, 'open'>>;
  let spWfServiceSpy: Mocked<
    Pick<
      StopPointWorkflowService,
      'startStopPointWorkflow' | 'editStopPointWorkflow'
    >
  >;
  let notificationServiceSpy: Mocked<Pick<NotificationService, 'success'>>;
  let dialogServiceSpy: Mocked<
    Pick<
      DialogService,
      'openWithoutResult' | 'openDialogDataWithConfirmationResult'
    >
  >;

  beforeEach(() => {
    dialogSpy = {
      open: vi.fn(),
    };
    spWfServiceSpy = {
      startStopPointWorkflow: vi.fn().mockReturnValue(of(workflow)),
      editStopPointWorkflow: vi.fn(),
    };
    notificationServiceSpy = {
      success: vi.fn(),
    };
    dialogServiceSpy = {
      openWithoutResult: vi.fn(),
      openDialogDataWithConfirmationResult: vi.fn().mockReturnValue(of(true)),
    };

    TestBed.configureTestingModule({
      providers: [
        translateServiceProvider,
        BoSelectionDisplayPipe,
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TranslatePipe },
        { provide: DialogService, useValue: dialogServiceSpy },
        { provide: StopPointWorkflowService, useValue: spWfServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: ValidationService, useClass: ValidationService },
        {
          provide: MatDialog,
          useValue: dialogSpy,
        },
      ],
    });

    fixture = TestBed.createComponent(StopPointWorkflowDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should calculate old designation if version before was validated', () => {
    const servicePoint: ReadServicePointVersion[] = [
      {
        sloid: 'ch:1:sloid:89008',
        designationOfficial: 'Bern, Wyleregg 1',
        businessOrganisation: 'ch:1:sboid:100626',
        meansOfTransport: [MeanOfTransport.Bus],
        status: Status.Validated,
        validFrom: new Date('2014-12-14'),
        validTo: new Date('2021-03-31'),
        number: {
          number: 8589008,
          checkDigit: 7,
          uicCountryCode: 85,
          numberShort: 89008,
        },
        country: Country.Switzerland,
        stopPoint: true,
      },
      {
        sloid: 'ch:1:sloid:89008',
        designationOfficial: 'Bern, Wyleregg 2',
        businessOrganisation: 'ch:1:sboid:100626',
        meansOfTransport: [MeanOfTransport.Bus],
        status: Status.Draft,
        validFrom: new Date('2021-04-01'),
        validTo: new Date('2021-06-31'),
        number: {
          number: 8589008,
          checkDigit: 7,
          uicCountryCode: 85,
          numberShort: 89008,
        },
        country: Country.Switzerland,
        stopPoint: true,
      },
    ];

    const result = component.getOldDesignation(servicePoint, 1);
    expect(result).toBe('Bern, Wyleregg 1');
  });

  it('should calculate old designation if version before was not stoppoint', () => {
    const servicePoint: ReadServicePointVersion[] = [
      {
        sloid: 'ch:1:sloid:89008',
        designationOfficial: 'Bern, Wyleregg 1',
        businessOrganisation: 'ch:1:sboid:100626',
        meansOfTransport: [],
        status: Status.Validated,
        validFrom: new Date('2014-12-14'),
        validTo: new Date('2021-03-31'),
        number: {
          number: 8589008,
          checkDigit: 7,
          uicCountryCode: 85,
          numberShort: 89008,
        },
        country: Country.Switzerland,
        stopPoint: false,
      },
      {
        sloid: 'ch:1:sloid:89008',
        designationOfficial: 'Bern, Wyleregg 2',
        businessOrganisation: 'ch:1:sboid:100626',
        meansOfTransport: [MeanOfTransport.Bus],
        status: Status.Draft,
        validFrom: new Date('2021-04-01'),
        validTo: new Date('2021-06-31'),
        number: {
          number: 8589008,
          checkDigit: 7,
          uicCountryCode: 85,
          numberShort: 89008,
        },
        country: Country.Switzerland,
        stopPoint: true,
      },
    ];

    const result = component.getOldDesignation(servicePoint, 1);
    expect(result).toBe('-');
  });

  it('should switch to edit mode', () => {
    expect(component.form?.disabled).toBe(true);

    component.toggleEdit();
    expect(component.form?.enabled).toBe(true);
  });

  it('should stay in edit mode when confirmation canceled', () => {
    // given
    component.form?.enable();
    expect(component.form?.enabled).toBe(true);

    component.form?.controls.designationOfficial.setValue('Basel beste Sport');
    component.form?.markAsDirty();
    expect(component.form?.dirty).toBe(true);

    dialogServiceSpy.openDialogDataWithConfirmationResult.mockReturnValue(
      of(false)
    );

    // when & then
    component.toggleEdit();
    expect(component.form?.enabled).toBe(true);
  });

  it('should validate the form and call update if form is valid', () => {
    vi.spyOn(ValidationService, 'validateForm');

    component.toggleEdit();
    component.form.controls['designationOfficial'].setValue(
      'Official Designation'
    );
    component.form.controls['workflowComment'].setValue('Some comment');
    component.form.controls.examinants.push(
      StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup({
        firstName: 'DIDOK',
        lastName: 'MASTER',
        personFunction: 'Chef',
        mail: 'didok@chef.com',
        organisation: 'SBB',
        id: 1,
        judgement: JudgementType.Yes,
        decisionType: DecisionType.Voted,
        defaultExaminant: false,
      })
    );
    component.form.controls['ccEmails'].setValue(['test@atlas.ch']);

    spWfServiceSpy.editStopPointWorkflow.mockReturnValue(
      of({ id: 1 } as ReadStopPointWorkflow)
    );

    component.save();

    expect(spWfServiceSpy.editStopPointWorkflow).toHaveBeenCalledWith(
      component.workflow.id,
      {
        ccEmails: ['test@atlas.ch'],
        designationOfficial: 'Official Designation',
        workflowComment: 'Some comment',
        examinants: [
          {
            firstName: 'DIDOK',
            lastName: 'MASTER',
            personFunction: 'Chef',
            mail: 'didok@chef.com',
            organisation: 'SBB',
            id: 1,
            judgementIcon: 'bi-check-lg',
            judgement: JudgementType.Yes,
            decisionType: DecisionType.Voted,
            defaultExaminant: false,
          },
        ],
      }
    );
    expect(notificationServiceSpy.success).toHaveBeenCalledWith(
      'WORKFLOW.NOTIFICATION.EDIT.SUCCESS'
    );
  });

  it('should reject workflow', () => {
    component.rejectWorkflow();

    expect(dialogServiceSpy.openWithoutResult).toHaveBeenCalledTimes(1);
  });

  it('should open add examinants dialog for workflow in hearing', () => {
    component.addExaminants();

    expect(
      dialogServiceSpy.openDialogDataWithConfirmationResult
    ).toHaveBeenCalledTimes(1);
  });

  it('should cancel workflow', () => {
    component.cancelWorkflow();

    expect(dialogServiceSpy.openWithoutResult).toHaveBeenCalledTimes(1);
  });

  it('should open decision dialog and cancel', () => {
    dialogSpy.open.mockReturnValue({
      afterClosed: () => of(false),
    } as never);

    component.openDecisionDialog();

    expect(dialogSpy.open).toHaveBeenCalledExactlyOnceWith(
      DecisionStepperComponent,
      {
        data: 1,
        disableClose: true,
        panelClass: 'atlas-dialog-panel',
        backdropClass: 'atlas-dialog-backdrop',
      }
    );
  });
});
