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
import {StopPointWorkflowDetailFormComponent} from "../detail-form/stop-point-workflow-detail-form.component";
import {StringListComponent} from "../../../../core/form-components/string-list/string-list.component";
import {DisplayDatePipe} from "../../../../core/pipe/display-date.pipe";
import {SplitServicePointNumberPipe} from "../../../../core/search-service-point/split-service-point-number.pipe";
import {DetailPageContentComponent} from "../../../../core/components/detail-page-content/detail-page-content.component";
import {DetailPageContainerComponent} from "../../../../core/components/detail-page-container/detail-page-container.component";
import {DetailFooterComponent} from "../../../../core/components/detail-footer/detail-footer.component";
import {DetailHelperService} from "../../../../core/detail/detail-helper.service";
import {of} from "rxjs";
import {ReadStopPointWorkflow, StopPointWorkflowService} from "../../../../api";
import {Router} from "@angular/router";
import {AtlasSpacerComponent} from "../../../../core/components/spacer/atlas-spacer.component";
import {UserService} from "../../../../core/auth/user/user.service";
import {DialogFooterComponent} from "../../../../core/components/dialog/footer/dialog-footer.component";
import {DialogContentComponent} from "../../../../core/components/dialog/content/dialog-content.component";
import {DialogCloseComponent} from "../../../../core/components/dialog/close/dialog-close.component";

const workflow: ReadStopPointWorkflow = {
  versionId: 1,
  sloid: 'ch:1:sloid:8000',
  workflowComment: "No comment"
};
const dialogRefSpy = jasmine.createSpyObj(['close']);
const notificationServiceSpy = jasmine.createSpyObj(['success']);
const router = jasmine.createSpyObj({
  navigate: Promise.resolve(),
});
const detailHelperService = jasmine.createSpyObj({
  confirmLeaveDirtyForm: of(true),
});
const stopPointWorkflowService = jasmine.createSpyObj({
  addStopPointWorkflow: of(workflow),
});

const workflowDialogData: AddStopPointWorkflowDialogData = {
  title: '',
  message: '',
  stopPoint: BERN_WYLEREGG,
}

describe('AddStopPointWorkflowComponent', () => {
  let component: AddStopPointWorkflowComponent;
  let fixture: ComponentFixture<AddStopPointWorkflowComponent>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      declarations: [
        AddStopPointWorkflowComponent,
        WorkflowFormComponent,
        WorkflowCheckFormComponent,
        CommentComponent,
        ErrorNotificationComponent,
        MockAtlasButtonComponent,
        StopPointWorkflowDetailFormComponent,
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
        DialogContentComponent,
      ],
      imports: [AppTestingModule, FormModule],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: workflowDialogData,
        },
        {provide: MatDialogRef, useValue: dialogRefSpy},
        {provide: NotificationService, useValue: notificationServiceSpy},
        {provide: DetailHelperService, useValue: detailHelperService},
        {provide: StopPointWorkflowService, useValue: stopPointWorkflowService},
        {provide: UserService, useValue: adminUserServiceMock},
        {provide: Router, useValue: router},
        {provide: TranslatePipe},
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
    const firstExaminant = component.form.controls.examinants.at(0);
    firstExaminant.controls.firstName.setValue('firstName');
    firstExaminant.controls.lastName.setValue('lastName');
    firstExaminant.controls.personFunction.setValue('personFunction');
    firstExaminant.controls.organisation.setValue('organisation');
    firstExaminant.controls.mail.setValue('mail@sbb.ch');

    component.form.controls.workflowComment.setValue('YB isch wida Meista');

    component.addWorkflow();

    expect(stopPointWorkflowService.addStopPointWorkflow).toHaveBeenCalled();
    expect(notificationServiceSpy.success).toHaveBeenCalled();
    expect(dialogRefSpy.close).toHaveBeenCalled();
  });

});
