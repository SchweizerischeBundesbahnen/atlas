import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopPointRejectWorkflowDialogComponent } from './stop-point-reject-workflow-dialog.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { FormModule } from '../../../../core/module/form.module';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { NotificationService } from '../../../../core/notification/notification.service';
import {
  ReadStopPointWorkflow,
  StopPointWorkflowService,
  User,
  UserAdministrationService,
} from '../../../../api';
import { Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { of } from 'rxjs';
import { StopPointRejectWorkflowDialogData } from './stop-point-reject-workflow-dialog-data';
import { DetailHelperService } from '../../../../core/detail/detail-helper.service';
import { DialogFooterComponent } from '../../../../core/components/dialog/footer/dialog-footer.component';
import { DialogContentComponent } from '../../../../core/components/dialog/content/dialog-content.component';
import { DialogCloseComponent } from '../../../../core/components/dialog/close/dialog-close.component';

const workflow: ReadStopPointWorkflow = {
  versionId: 1,
  sloid: 'ch:1:sloid:8000',
  workflowComment: 'No comment',
};
const notificationServiceSpy = jasmine.createSpyObj(['success']);
const stopPointWorkflowService = jasmine.createSpyObj({
  rejectStopPointWorkflow: of(workflow),
  cancelStopPointWorkflow: of(workflow),
});

const workflowDialogData: StopPointRejectWorkflowDialogData = {
  title: '',
  message: '',
  workflowId: 123,
  rejectType: 'CANCEL',
};
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

const userAdministrationServiceMock = jasmine.createSpyObj(
  UserAdministrationService,
  {
    getCurrentUser: of(user),
  }
);

const dialogRefSpy = jasmine.createSpyObj(['close']);

function formGroup(component: StopPointRejectWorkflowDialogComponent) {
  const formGroup = component.formGroup;
  formGroup.controls.firstName.setValue('firstName');
  formGroup.controls.lastName.setValue('lastName');
  formGroup.controls.organisation.setValue('organisation');
  formGroup.controls.motivationComment.setValue('juva merda');
}

describe('StopPointRejectWorkflowDialogComponent', () => {
  let component: StopPointRejectWorkflowDialogComponent;
  let fixture: ComponentFixture<StopPointRejectWorkflowDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        FormModule,
        StopPointRejectWorkflowDialogComponent,
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
        { provide: DetailHelperService, useValue: detailHelperService },
        { provide: Router, useValue: router },
        { provide: TranslatePipe },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(StopPointRejectWorkflowDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should cancel reject workflow', () => {
    component.closeDialog();

    expect(detailHelperService.confirmLeaveDirtyForm).toHaveBeenCalledWith(
      component.formGroup
    );
    expect(dialogRefSpy.close).toHaveBeenCalled();
  });

  it('should reject workflow via service', () => {
    workflowDialogData.rejectType = 'CANCEL';
    formGroup(component);
    fixture.detectChanges();
    component.rejectWorkflow();

    expect(stopPointWorkflowService.cancelStopPointWorkflow).toHaveBeenCalled();
    expect(notificationServiceSpy.success).toHaveBeenCalled();
    expect(dialogRefSpy.close).toHaveBeenCalled();
  });

  it('should cancel workflow via service', () => {
    workflowDialogData.rejectType = 'REJECT';
    formGroup(component);
    fixture.detectChanges();
    component.rejectWorkflow();

    expect(stopPointWorkflowService.rejectStopPointWorkflow).toHaveBeenCalled();
    expect(notificationServiceSpy.success).toHaveBeenCalled();
    expect(dialogRefSpy.close).toHaveBeenCalled();
  });
});
