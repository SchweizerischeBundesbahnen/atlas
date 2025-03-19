import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AddStopPointWorkflowComponent} from './add-stop-point-workflow.component';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {TranslatePipe} from '@ngx-translate/core';
import {WorkflowFormComponent} from "../../../../core/workflow/workflow-form/workflow-form.component";
import {WorkflowCheckFormComponent} from "../../../../core/workflow/workflow-check-form/workflow-check-form.component";
import {CommentComponent} from "../../../../core/form-components/comment/comment.component";
import {ErrorNotificationComponent} from "../../../../core/notification/error/error-notification.component";
import {adminUserServiceMock, MockAtlasButtonComponent} from "../../../../app.testing.mocks";
import {AppTestingModule} from "../../../../app.testing.module";
import {NotificationService} from "../../../../core/notification/notification.service";
import {FormModule} from "../../../../core/module/form.module";
import {AddStopPointWorkflowDialogData} from "./add-stop-point-workflow-dialog-data";
import {BERN_WYLEREGG} from "../../../../../test/data/service-point";
import {StringListComponent} from "../../../../core/form-components/string-list/string-list.component";
import {DisplayDatePipe} from "../../../../core/pipe/display-date.pipe";
import {SplitServicePointNumberPipe} from "../../../../core/search-service-point/split-service-point-number.pipe";
import {DetailPageContentComponent} from "../../../../core/components/detail-page-content/detail-page-content.component";
import {DetailPageContainerComponent} from "../../../../core/components/detail-page-container/detail-page-container.component";
import {DetailFooterComponent} from "../../../../core/components/detail-footer/detail-footer.component";
import {DetailHelperService} from "../../../../core/detail/detail-helper.service";
import {of} from "rxjs";
import {
  DecisionType,
  JudgementType,
  ReadServicePointVersion,
  ReadStopPointWorkflow,
  StopPointWorkflowService
} from "../../../../api";
import {Router} from "@angular/router";
import {AtlasSpacerComponent} from "../../../../core/components/spacer/atlas-spacer.component";
import {UserService} from "../../../../core/auth/user/user.service";
import {DialogFooterComponent} from "../../../../core/components/dialog/footer/dialog-footer.component";
import {DialogContentComponent} from "../../../../core/components/dialog/content/dialog-content.component";
import {DialogCloseComponent} from "../../../../core/components/dialog/close/dialog-close.component";
import {ValidationService} from "../../../../core/validation/validation.service";
import {
  ExaminantFormGroup,
  StopPointWorkflowDetailFormGroup, StopPointWorkflowDetailFormGroupBuilder
} from "../detail-page/detail-form/stop-point-workflow-detail-form-group";
import {FormArray, FormControl, FormGroup} from "@angular/forms";
import {Component, Input} from "@angular/core";
import {
  StopPointWorkflowExaminantsTableComponent
} from "../detail-page/examinant-table/stop-point-workflow-examinants-table.component";

const workflow: ReadStopPointWorkflow = {
  versionId: 1,
  sloid: 'ch:1:sloid:8000',
  workflowComment: "No comment"
};
const dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);
const notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['success']);
const router = jasmine.createSpyObj({
  navigate: Promise.resolve(),
});
const detailHelperService = jasmine.createSpyObj({
  confirmLeaveDirtyForm: of(true),
});
const stopPointWorkflowService = jasmine.createSpyObj('StopPointWorkflowService', {
  addStopPointWorkflow: of(workflow)
});

const workflowDialogData: AddStopPointWorkflowDialogData = {
  title: '',
  message: '',
  stopPoint: BERN_WYLEREGG,
}

@Component({
  selector: 'stop-point-workflow-detail-form',
  template: '<p>Mock AddStopPointWorkflowDetailForm Component</p>',
  imports: [AppTestingModule, FormModule]
})
export class MockStopPointWorkflowDetailFormComponent {
  @Input() stopPoint!: ReadServicePointVersion;
  @Input() oldDesignation?: string;
  @Input() form!: FormGroup<StopPointWorkflowDetailFormGroup>;
  @Input() currentWorkflow?: ReadStopPointWorkflow;
}

describe('AddStopPointWorkflowComponent', () => {
  let component: AddStopPointWorkflowComponent;
  let fixture: ComponentFixture<AddStopPointWorkflowComponent>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule, FormModule, AddStopPointWorkflowComponent,
        WorkflowFormComponent,
        WorkflowCheckFormComponent,
        StopPointWorkflowExaminantsTableComponent,
        CommentComponent,
        ErrorNotificationComponent,
        MockAtlasButtonComponent,
        MockStopPointWorkflowDetailFormComponent,
        StringListComponent,
        MockAtlasButtonComponent,
        DisplayDatePipe,
        SplitServicePointNumberPipe,
        DetailPageContentComponent,
        DetailPageContainerComponent,
        DetailFooterComponent,
        AtlasSpacerComponent,
        DialogCloseComponent,
        DialogFooterComponent,
        DialogContentComponent],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: workflowDialogData,
        },
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: DetailHelperService, useValue: detailHelperService },
        { provide: StopPointWorkflowService, useValue: stopPointWorkflowService },
        { provide: UserService, useValue: adminUserServiceMock },
        { provide: Router, useValue: router },
        { provide: TranslatePipe },
      ],
    })
      .compileComponents()
      .then();

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

    component.form.controls.examinants.push(StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup());
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
      defaultExaminant: new FormControl(false)
    });

    const formArray = new FormArray<FormGroup<ExaminantFormGroup>>([examinantFormGroup]);
    component.form = new FormGroup<StopPointWorkflowDetailFormGroup>({
      ccEmails: new FormControl<Array<string> | null>(null),
      workflowComment: new FormControl<string | null>('Workflow comment 1'),
      designationOfficial: new FormControl<string | null>(null),
      examinants: formArray,
    });

    component.addWorkflow();

    expect(stopPointWorkflowService.addStopPointWorkflow).toHaveBeenCalledWith(jasmine.objectContaining({
      examinants: [
        jasmine.objectContaining({
          firstName: null,
          lastName: null,
        })
      ]
    }));

    expect(notificationServiceSpy.success).toHaveBeenCalled();
    expect(dialogRefSpy.close).toHaveBeenCalled();
  });

});
