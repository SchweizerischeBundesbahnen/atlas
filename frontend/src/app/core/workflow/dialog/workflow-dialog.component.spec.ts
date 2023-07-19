import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkflowDialogComponent } from './workflow-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { WorkflowDialogData } from './workflow-dialog-data';
import { Status, User, UserAdministrationService, WorkflowService } from '../../../api';
import { CommentComponent } from '../../form-components/comment/comment.component';
import { ErrorNotificationComponent } from '../../notification/error/error-notification.component';
import { AppTestingModule } from '../../../app.testing.module';
import { FormModule } from '../../module/form.module';
import { NotificationService } from '../../notification/notification.service';
import { of } from 'rxjs';

const dialogRefSpy = jasmine.createSpyObj(['close']);
const notificationServiceSpy = jasmine.createSpyObj(['success']);

const workflowDialogData: WorkflowDialogData = {
  lineRecord: {
    id: 123,
    validFrom: new Date(),
    validTo: new Date(),
    slnid: 'ch:1:slnid:1000003',
    businessOrganisation: 'ch:1:sboid:110000',
    status: Status.Draft,
    versionNumber: 0,
  },
  descriptionForWorkflow: 'Toller Workflow',
  title: 'Acciaroli bello',
  message: 'Andiamo in spiaggia?',
};

const user: User = {
  sbbUserId: 'e123',
  lastName: 'Marek',
  firstName: 'Hamsik',
  mail: 'a@b.cd',
};
const userAdministrationServiceMock = jasmine.createSpyObj(UserAdministrationService, {
  getCurrentUser: of(user),
});
const workflowServiceMock = jasmine.createSpyObj(WorkflowService, {
  getWorkflow: of({}),
  startWorkflow: of({}),
});

describe('WorkflowDialogComponent', () => {
  let component: WorkflowDialogComponent;
  let fixture: ComponentFixture<WorkflowDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WorkflowDialogComponent, CommentComponent, ErrorNotificationComponent],
      imports: [AppTestingModule, FormModule],
      providers: [
        { provide: UserAdministrationService, useValue: userAdministrationServiceMock },
        { provide: WorkflowService, useValue: workflowServiceMock },
        {
          provide: MAT_DIALOG_DATA,
          useValue: workflowDialogData,
        },
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: TranslatePipe },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(WorkflowDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();

    expect(component.workflowStartFormGroup.value.mail).toBe(user.mail);
  });

  it('should start workflow', () => {
    component.workflowStartFormGroup.controls.comment.setValue('I mag an worfklof ga starte');
    component.workflowStartFormGroup.controls.function.setValue('I bims, a TU');

    component.startWorkflow();

    expect(workflowServiceMock.startWorkflow).toHaveBeenCalled();
  });
});
