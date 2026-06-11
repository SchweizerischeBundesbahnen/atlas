import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi, type Mocked } from 'vitest';

import { StopPointRestartWorkflowDialogComponent } from './stop-point-restart-workflow-dialog.component';
import { Permission, ReadStopPointWorkflow, User } from '../../../../api';
import { of } from 'rxjs';
import { StopPointRejectWorkflowDialogData } from '../stop-point-reject-workflow-dialog/stop-point-reject-workflow-dialog-data';
import { DialogFooterComponent } from '../../../../core/components/dialog/footer/dialog-footer.component';
import { DialogContentComponent } from '../../../../core/components/dialog/content/dialog-content.component';
import { DialogCloseComponent } from '../../../../core/components/dialog/close/dialog-close.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { FormModule } from '../../../../core/module/form.module';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DetailDialogHelperService } from '../../../../core/detail/detail-dialog-helper.service';
import { Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { UserAdministrationService } from '../../../../api/service/user-administration/user-administration.service';
import { StopPointWorkflowService } from '../../../../api/service/workflow/stop-point-workflow.service';

describe('StopPointRestartWorkflowDialogComponent', () => {
  let component: StopPointRestartWorkflowDialogComponent;
  let fixture: ComponentFixture<StopPointRestartWorkflowDialogComponent>;

  const workflow: ReadStopPointWorkflow = {
    versionId: 1,
    sloid: 'ch:1:sloid:8000',
    workflowComment: 'No comment',
    designationOfficial: 'test',
    status: 'HEARING',
  };

  const user: User = {
    sbbUserId: 'e123',
    lastName: 'Marek',
    firstName: 'Hamsik',
    mail: 'a@b.cd',
    permissions: new Set<Permission>(),
  };

  const workflowDialogData: StopPointRejectWorkflowDialogData = {
    title: '',
    message: '',
    workflowId: 123,
    rejectType: 'CANCEL',
  };

  let notificationServiceSpy: Mocked<Pick<NotificationService, 'success'>>;
  let stopPointWorkflowService: Mocked<Pick<StopPointWorkflowService, 'restartStopPointWorkflow'>>;
  let router: Mocked<Pick<Router, 'navigate' | 'navigateByUrl'>>;
  let detailHelperService: Mocked<Pick<DetailDialogHelperService, 'confirmLeaveDirtyForm'>>;
  let userAdministrationServiceMock: Mocked<Pick<UserAdministrationService, 'getCurrentUser'>>;
  let dialogRefSpy: Mocked<Pick<MatDialogRef<StopPointRestartWorkflowDialogComponent>, 'close'>>;

  function formGroup(component: StopPointRestartWorkflowDialogComponent) {
    const formGroup = component.formGroup;
    formGroup.controls.firstName.setValue('firstName');
    formGroup.controls.lastName.setValue('lastName');
    formGroup.controls.organisation.setValue('organisation');
    formGroup.controls.motivationComment.setValue('juva merda');
    formGroup.controls.mail.setValue('chef@chef.ch');
    formGroup.controls.designationOfficial.setValue('NEW DESIGNATION');
  }

  beforeEach(() => {
    notificationServiceSpy = { success: vi.fn() };
    stopPointWorkflowService = {
      restartStopPointWorkflow: vi.fn().mockReturnValue(of(workflow)),
    };
    router = {
      navigate: vi.fn().mockReturnValue(Promise.resolve()),
      navigateByUrl: vi.fn().mockReturnValue(Promise.resolve()),
    };
    detailHelperService = {
      confirmLeaveDirtyForm: vi.fn().mockReturnValue(of(true)),
    };
    userAdministrationServiceMock = {
      getCurrentUser: vi.fn().mockReturnValue(of(user)),
    };
    dialogRefSpy = { close: vi.fn() };

    TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        FormModule,
        StopPointRestartWorkflowDialogComponent,
        DialogFooterComponent,
        DialogContentComponent,
        DialogCloseComponent,
      ],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: workflowDialogData,
        },
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        {
          provide: StopPointWorkflowService,
          useValue: stopPointWorkflowService,
        },
        {
          provide: UserAdministrationService,
          useValue: userAdministrationServiceMock,
        },
        { provide: DetailDialogHelperService, useValue: detailHelperService },
        { provide: Router, useValue: router },
        { provide: TranslatePipe },
      ],
    });

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
    workflowDialogData.rejectType = 'RESTART';
    formGroup(component);
    fixture.detectChanges();
    component.restartWorkflow();

    expect(stopPointWorkflowService.restartStopPointWorkflow).toHaveBeenCalled();
    expect(notificationServiceSpy.success).toHaveBeenCalled();
    expect(dialogRefSpy.close).toHaveBeenCalled();
  });
});
