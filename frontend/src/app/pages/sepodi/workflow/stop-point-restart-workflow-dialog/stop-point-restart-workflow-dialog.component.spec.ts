import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopPointRestartWorkflowDialogComponent } from './stop-point-restart-workflow-dialog.component';
import {ReadStopPointWorkflow, StopPointWorkflowService, User, UserAdministrationService} from "../../../../api";
import {of} from "rxjs";
import {
  StopPointRejectWorkflowDialogData
} from "../stop-point-reject-workflow-dialog/stop-point-reject-workflow-dialog-data";
import {DialogFooterComponent} from "../../../../core/components/dialog/footer/dialog-footer.component";
import {DialogContentComponent} from "../../../../core/components/dialog/content/dialog-content.component";
import {DialogCloseComponent} from "../../../../core/components/dialog/close/dialog-close.component";
import {AppTestingModule} from "../../../../app.testing.module";
import {FormModule} from "../../../../core/module/form.module";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {NotificationService} from "../../../../core/notification/notification.service";
import {DetailHelperService} from "../../../../core/detail/detail-helper.service";
import {Router} from "@angular/router";
import {TranslatePipe} from "@ngx-translate/core";

const workflow: ReadStopPointWorkflow = {
  versionId: 1,
  sloid: 'ch:1:sloid:8000',
  workflowComment: "No comment",
  designationOfficial: "test",
  status: "HEARING",
};
const notificationServiceSpy = jasmine.createSpyObj(['success']);

const stopPointWorkflowService = jasmine.createSpyObj('StopPointWorkflowService', {
  restartStopPointWorkflow: of(workflow),
});

const workflowDialogData: StopPointRejectWorkflowDialogData = {
  title: '',
  message: '',
  workflowId: 123,
  rejectType: "CANCEL"
}

const router = jasmine.createSpyObj({
  navigate: Promise.resolve(),
  navigateByUrl: Promise.resolve(),
});

const detailHelperService = jasmine.createSpyObj({
  confirmLeaveDirtyForm: of(true),
});

const user: User = {
  sbbUserId: 'e123',
  lastName: 'Marek',
  firstName: 'Hamsik',
  mail: 'a@b.cd',
};

const userAdministrationServiceMock = jasmine.createSpyObj(UserAdministrationService, {
  getCurrentUser: of(user),
});

const dialogRefSpy = jasmine.createSpyObj(['close']);

function formGroup(component: StopPointRestartWorkflowDialogComponent) {
  const formGroup = component.formGroup;
  formGroup.controls.firstName.setValue('firstName');
  formGroup.controls.lastName.setValue('lastName');
  formGroup.controls.organisation.setValue('organisation');
  formGroup.controls.motivationComment.setValue('juva merda');
  formGroup.controls.mail.setValue("chef@chef.ch");
  formGroup.controls.designationOfficial.setValue("NEW DESIGNATION")
}

describe('StopPointRestartWorkflowDialogComponent', () => {
  let component: StopPointRestartWorkflowDialogComponent;
  let fixture: ComponentFixture<StopPointRestartWorkflowDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        StopPointRestartWorkflowDialogComponent,
        DialogFooterComponent,
        DialogContentComponent,
        DialogCloseComponent
      ],
      imports: [AppTestingModule, FormModule],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: workflowDialogData,
        },
        {provide: MatDialogRef, useValue: dialogRefSpy},
        {provide: NotificationService, useValue: notificationServiceSpy},
        {provide: StopPointWorkflowService, useValue: stopPointWorkflowService},
        {provide: UserAdministrationService, useValue: userAdministrationServiceMock},
        {provide: DetailHelperService, useValue: detailHelperService},
        {provide: Router, useValue: router},
        {provide: TranslatePipe}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StopPointRestartWorkflowDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should cancel restart workflow', () => {
    component.closeDialog();

    expect(detailHelperService.confirmLeaveDirtyForm).toHaveBeenCalledWith(component.formGroup);
    expect(dialogRefSpy.close).toHaveBeenCalled();
  });

  it('should restart workflow via service', () => {
    workflowDialogData.rejectType = "RESTART";
    formGroup(component);
    fixture.detectChanges()
    component.restartWorkflow()

    expect(stopPointWorkflowService.restartStopPointWorkflow).toHaveBeenCalled();
    expect(notificationServiceSpy.success).toHaveBeenCalled();
    expect(dialogRefSpy.close).toHaveBeenCalled();
  });
});
