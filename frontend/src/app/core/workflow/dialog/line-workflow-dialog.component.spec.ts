import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LineWorkflowDialogComponent } from './line-workflow-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { LineWorkflowDialogData } from './line-workflow-dialog-data';
import {
  LineVersionWorkflow,
  Permission,
  Status,
  User,
  Workflow,
  WorkflowProcessingStatus,
  WorkflowStart,
} from '../../../api';
import { CommentComponent } from '../../form-components/comment/comment.component';
import { ErrorNotificationComponent } from '../../notification/error/error-notification.component';
import { AppTestingModule } from '../../../app.testing.module';
import { FormModule } from '../../module/form.module';
import { NotificationService } from '../../notification/notification.service';
import { of } from 'rxjs';
import { LineWorkflowCheckFormComponent } from '../workflow-check-form/line-workflow-check-form.component';
import { LineWorkflowFormComponent } from '../workflow-form/line-workflow-form.component';
import {
  adminPermissionServiceMock,
  MockAtlasButtonComponent,
} from '../../../app.testing.mocks';
import { PermissionService } from '../../auth/permission/permission.service';
import { DialogFooterComponent } from '../../components/dialog/footer/dialog-footer.component';
import { DialogContentComponent } from '../../components/dialog/content/dialog-content.component';
import { DialogCloseComponent } from '../../components/dialog/close/dialog-close.component';
import { UserAdministrationService } from '../../../api/service/user-administration/user-administration.service';
import { LineWorkflowService } from '../../../api/service/workflow/line-workflow.service';
import WorkflowTypeEnum = WorkflowStart.WorkflowTypeEnum;

const dialogRefSpy = jasmine.createSpyObj(['close']);
const notificationServiceSpy = jasmine.createSpyObj(['success']);

const user: User = {
  sbbUserId: 'e123',
  lastName: 'Marek',
  firstName: 'Hamsik',
  mail: 'a@b.cd',
  permissions: new Set<Permission>(),
};
const userAdministrationServiceMock = jasmine.createSpyObj(
  UserAdministrationService,
  {
    getCurrentUser: of(user),
  }
);
const workflow: Workflow = {
  id: 1,
  businessObjectId: 14214,
  description: 'Linie 5',
  swissId: 'ch:1:slnid:1000003',
  workflowComment: 'I mag am abe später hei',
  client: {
    firstName: 'Greta',
    lastName: 'Thunberger',
    personFunction: 'Influenca',
    mail: 'thun@greta.com',
  },
  workflowType: WorkflowTypeEnum.Line,
};
const workflowServiceMock = jasmine.createSpyObj(LineWorkflowService, {
  getWorkflow: of(workflow),
  startWorkflow: of({}),
});

describe('LineWorkflowDialogComponent new', () => {
  let component: LineWorkflowDialogComponent;
  let fixture: ComponentFixture<LineWorkflowDialogComponent>;

  beforeEach(async () => {
    setupTestBed({
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
    });

    fixture = TestBed.createComponent(LineWorkflowDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create new workflow form with user info prefilled', () => {
    expect(component).toBeTruthy();

    expect(component.workflowStartFormGroup.value.mail).toBe(user.mail);
    expect(component.workflowStartFormGroup.value.firstName).toBe(
      user.firstName
    );
    expect(component.workflowStartFormGroup.value.lastName).toBe(user.lastName);
  });

  it('should start workflow', () => {
    component.workflowStartFormGroup.controls.comment.setValue(
      'I mag an worfklof ga starte'
    );
    component.workflowStartFormGroup.controls.function.setValue('I bims, a TU');

    component.startWorkflow();

    expect(workflowServiceMock.startWorkflow).toHaveBeenCalled();
  });
});

describe('LineWorkflowDialogComponent open', () => {
  let component: LineWorkflowDialogComponent;
  let fixture: ComponentFixture<LineWorkflowDialogComponent>;

  beforeEach(async () => {
    setupTestBed({
      lineRecord: {
        id: 123,
        validFrom: new Date(),
        validTo: new Date(),
        slnid: 'ch:1:slnid:1000003',
        businessOrganisation: 'ch:1:sboid:110000',
        status: Status.Draft,
        versionNumber: 0,
        lineVersionWorkflows: new Set<LineVersionWorkflow>([
          {
            workflowId: workflow.id,
            workflowProcessingStatus: WorkflowProcessingStatus.InProgress,
          },
        ]),
      },
      descriptionForWorkflow: 'Toller Workflow',
      title: 'Acciaroli bello',
      message: 'Andiamo in spiaggia?',
    });

    fixture = TestBed.createComponent(LineWorkflowDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should display existing workflow', () => {
    expect(component.workflowStartFormGroup.value.mail).toBe(
      workflow.client!.mail
    );
    expect(component.workflowStartFormGroup.value.lastName).toBe(
      workflow.client!.lastName
    );
    expect(component.workflowStartFormGroup.value.firstName).toBe(
      workflow.client!.firstName
    );
    expect(component.workflowStartFormGroup.value.function).toBe(
      workflow.client!.personFunction
    );

    expect(component.workflowStartFormGroup.value.comment).toBe(
      workflow.workflowComment
    );
  });
});

function setupTestBed(workflowDialogData: LineWorkflowDialogData) {
  TestBed.configureTestingModule({
    imports: [
      AppTestingModule,
      FormModule,
      LineWorkflowDialogComponent,
      LineWorkflowFormComponent,
      LineWorkflowCheckFormComponent,
      CommentComponent,
      ErrorNotificationComponent,
      MockAtlasButtonComponent,
      DialogCloseComponent,
      DialogFooterComponent,
      DialogContentComponent,
    ],
    providers: [
      { provide: PermissionService, useValue: adminPermissionServiceMock },
      {
        provide: UserAdministrationService,
        useValue: userAdministrationServiceMock,
      },
      { provide: LineWorkflowService, useValue: workflowServiceMock },
      {
        provide: MAT_DIALOG_DATA,
        useValue: workflowDialogData,
      },
      { provide: MatDialogRef, useValue: dialogRefSpy },
      { provide: NotificationService, useValue: notificationServiceSpy },
      { provide: TranslatePipe },
    ],
  })
    .compileComponents()
    .then();
}
