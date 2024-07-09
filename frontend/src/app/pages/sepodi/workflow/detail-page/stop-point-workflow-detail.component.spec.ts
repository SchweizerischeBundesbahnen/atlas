import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { StopPointWorkflowDetailComponent } from './stop-point-workflow-detail.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { FormModule } from '../../../../core/module/form.module';
import { ActivatedRoute } from '@angular/router';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';
import {
  Country, DecisionType,
  JudgementType,
  MeanOfTransport,
  ReadServicePointVersion,
  ReadStopPointWorkflow,
  Status,
  StopPointWorkflowService,
} from '../../../../api';
import { StringListComponent } from '../../../../core/form-components/string-list/string-list.component';
import { MockAtlasButtonComponent } from '../../../../app.testing.mocks';
import { DisplayDatePipe } from '../../../../core/pipe/display-date.pipe';
import { SplitServicePointNumberPipe } from '../../../../core/search-service-point/split-service-point-number.pipe';
import { TranslatePipe } from '@ngx-translate/core';
import { DetailPageContentComponent } from '../../../../core/components/detail-page-content/detail-page-content.component';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { AtlasSpacerComponent } from '../../../../core/components/spacer/atlas-spacer.component';
import { StopPointWorkflowDetailData } from './stop-point-workflow-detail-resolver.service';
import { UserDetailInfoComponent } from '../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import { of } from 'rxjs';
import { NotificationService } from '../../../../core/notification/notification.service';
import { StopPointWorkflowDetailFormComponent } from './detail-form/stop-point-workflow-detail-form.component';
import { StopPointRejectWorkflowDialogService } from '../stop-point-reject-workflow-dialog/stop-point-reject-workflow-dialog.service';
import { MatDialog } from '@angular/material/dialog';
import { DecisionStepperComponent } from './decision/decision-stepper/decision-stepper.component';
import { ValidationService } from '../../../../core/validation/validation.service';
import { DialogService } from '../../../../core/components/dialog/dialog.service';

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

describe('StopPointWorkflowDetailComponent', () => {
  let component: StopPointWorkflowDetailComponent;
  let fixture: ComponentFixture<StopPointWorkflowDetailComponent>;

  let stopPointRejectWorkflowDialogServiceSpy: StopPointRejectWorkflowDialogService;
  let dialogSpy = jasmine.createSpyObj(['open', 'afterClosed']);
  let spWfServiceSpy = jasmine.createSpyObj('StopPointWorkflowService', {
    startStopPointWorkflow: of(workflow),
    editStopPointWorkflow: jasmine.createSpy('editStopPointWorkflow'),
  });
  let notificationServiceSpy = jasmine.createSpyObj(['success']);
  let dialogServiceSpy = jasmine.createSpyObj('DialogService', ['confirm']);

  beforeEach(async () => {
    stopPointRejectWorkflowDialogServiceSpy = jasmine.createSpyObj(['openDialog']);
    dialogSpy = jasmine.createSpyObj(['open']);
    spWfServiceSpy = jasmine.createSpyObj('StopPointWorkflowService', {
      startStopPointWorkflow: of(workflow),
    });
    notificationServiceSpy = jasmine.createSpyObj(['success']);
    dialogServiceSpy = jasmine.createSpyObj('DialogService', ['confirm']);

    TestBed.configureTestingModule({
      declarations: [
        StopPointWorkflowDetailComponent,
        StopPointWorkflowDetailFormComponent,
        StringListComponent,
        MockAtlasButtonComponent,
        DisplayDatePipe,
        SplitServicePointNumberPipe,
        DetailPageContentComponent,
        DetailPageContainerComponent,
        DetailFooterComponent,
        AtlasSpacerComponent,
        UserDetailInfoComponent,
      ],
      imports: [AppTestingModule, FormModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TranslatePipe },
        { provide: DialogService, useValue: dialogServiceSpy },
        { provide: StopPointWorkflowService, useValue: spWfServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: ValidationService, useClass: ValidationService },
        {
          provide: StopPointRejectWorkflowDialogService,
          useValue: stopPointRejectWorkflowDialogServiceSpy,
        },
        {
          provide: MatDialog,
          useValue: dialogSpy,
        },
      ],
    })
      .compileComponents()
      .then();

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
        number: { number: 8589008, checkDigit: 7, uicCountryCode: 85, numberShort: 89008 },
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
        number: { number: 8589008, checkDigit: 7, uicCountryCode: 85, numberShort: 89008 },
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
        number: { number: 8589008, checkDigit: 7, uicCountryCode: 85, numberShort: 89008 },
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
        number: { number: 8589008, checkDigit: 7, uicCountryCode: 85, numberShort: 89008 },
        country: Country.Switzerland,
        stopPoint: true,
      },
    ];

    const result = component.getOldDesignation(servicePoint, 1);
    expect(result).toBe('-');
  });

  it('should switch to edit mode', () => {
    expect(component.form?.disabled).toBeTrue();

    component.toggleEdit();
    expect(component.form?.enabled).toBeTrue();
  });

  it('should stay in edit mode when confirmation canceled', () => {
    // given
    component.form?.enable();
    expect(component.form?.enabled).toBeTrue();

    component.form?.controls.designationOfficial.setValue('Basel beste Sport');
    component.form?.markAsDirty();
    expect(component.form?.dirty).toBeTrue();

    dialogServiceSpy.confirm.and.returnValue(of(false));

    // when & then
    component.toggleEdit();
    expect(component.form?.enabled).toBeTrue();
  });

  it('should validate the form and call update if form is valid', () => {
    spyOn(ValidationService, 'validateForm').and.callThrough();

    component.toggleEdit();
    component.form.controls['designationOfficial'].setValue('Official Designation');
    component.form.controls['workflowComment'].setValue('Some comment');
    component.form.controls['examinants'].setValue([{
      firstName: 'DIDOK',
      lastName: 'MASTER',
      personFunction: 'Chef',
      mail: 'didok@chef.com',
      organisation: 'SBB',
      id: 1,
      judgementIcon: "",
      judgement: JudgementType.Yes,
      decisionType: DecisionType.Voted,
    }]);

    spWfServiceSpy.editStopPointWorkflow.and.returnValue(of({ id: 1 }));

    component.save();

    expect(spWfServiceSpy.editStopPointWorkflow).toHaveBeenCalledWith(component.workflow.id, {
      designationOfficial: 'Official Designation',
      workflowComment: 'Some comment',
      examinants: [{
        firstName: 'DIDOK',
        lastName: 'MASTER',
        personFunction: 'Chef',
        mail: 'didok@chef.com',
        organisation: 'SBB',
        id: 1,
        judgementIcon: "",
        judgement: JudgementType.Yes,
        decisionType: DecisionType.Voted,
      }]
    });
    expect(notificationServiceSpy.success).toHaveBeenCalledWith(
      'WORKFLOW.NOTIFICATION.EDIT.SUCCESS',
    );
  });

  it('should start workflow', fakeAsync(() => {
    component.startWorkflow();
    tick();
    expect(spWfServiceSpy.startStopPointWorkflow).toHaveBeenCalledOnceWith(1);
    expect(notificationServiceSpy.success).toHaveBeenCalledOnceWith(
      'WORKFLOW.NOTIFICATION.START.SUCCESS',
    );
  }));

  it('should reject workflow', () => {
    component.rejectWorkflow();

    expect(stopPointRejectWorkflowDialogServiceSpy.openDialog).toHaveBeenCalledTimes(1);
  });

  it('should cancel workflow', () => {
    component.cancelWorkflow();

    expect(stopPointRejectWorkflowDialogServiceSpy.openDialog).toHaveBeenCalledTimes(1);
  });

  it('should open decision dialog', () => {
    dialogSpy.open.and.returnValue({
      afterClosed: () => of(false),
    });

    component.openDecisionDialog();

    expect(dialogSpy.open).toHaveBeenCalledOnceWith(DecisionStepperComponent, {
      data: 1,
      disableClose: true,
      panelClass: 'atlas-dialog-panel',
      backdropClass: 'atlas-dialog-backdrop',
    });
  });
});
