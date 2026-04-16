import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { LineWorkflowDialogComponent } from './line-workflow-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
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
import { NotificationService } from '../../notification/notification.service';
import { of } from 'rxjs';
import { adminPermissionServiceMock, translateServiceProvider } from '../../../app.testing.mocks';
import { PermissionService } from '../../auth/permission/permission.service';
import { UserAdministrationService } from '../../../api/service/user-administration/user-administration.service';
import { LineWorkflowService } from '../../../api/service/workflow/line-workflow.service';
import WorkflowTypeEnum = WorkflowStart.WorkflowTypeEnum;

const user: User = {
  sbbUserId: 'e123',
  lastName: 'Marek',
  firstName: 'Hamsik',
  mail: 'a@b.cd',
  permissions: new Set<Permission>(),
};

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

describe('LineWorkflowDialogComponent new', () => {
  let component: LineWorkflowDialogComponent;
  let fixture: ComponentFixture<LineWorkflowDialogComponent>;

  let dialogRefStub: Mocked<Pick<MatDialogRef<LineWorkflowDialogComponent>, 'close'>>;
  let notificationServiceStub: Mocked<Pick<NotificationService, 'success'>>;
  let userAdministrationServiceStub: Mocked<Pick<UserAdministrationService, 'getCurrentUser'>>;
  let workflowServiceStub: Mocked<Pick<LineWorkflowService, 'getWorkflow' | 'startWorkflow'>>;

  beforeEach(() => {
    // Mocking
    dialogRefStub = { close: vi.fn() };
    notificationServiceStub = { success: vi.fn() };
    userAdministrationServiceStub = {
      getCurrentUser: vi.fn().mockReturnValue(of(user)),
    };
    workflowServiceStub = {
      getWorkflow: vi.fn().mockReturnValue(of(workflow)),
      startWorkflow: vi.fn().mockReturnValue(of({})),
    };

    // Config
    TestBed.configureTestingModule({
      providers: [
        { provide: PermissionService, useValue: adminPermissionServiceMock },
        {
          provide: UserAdministrationService,
          useValue: userAdministrationServiceStub,
        },
        { provide: LineWorkflowService, useValue: workflowServiceStub },
        {
          provide: MAT_DIALOG_DATA,
          useValue: {
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
          } satisfies LineWorkflowDialogData,
        },
        { provide: MatDialogRef, useValue: dialogRefStub },
        { provide: NotificationService, useValue: notificationServiceStub },
        translateServiceProvider,
      ],
    });

    // Arrangement
    fixture = TestBed.createComponent(LineWorkflowDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create new workflow form with user info prefilled', () => {
    expect(component).toBeTruthy();

    expect(component.workflowStartFormGroup.value.mail).toBe(user.mail);
    expect(component.workflowStartFormGroup.value.firstName).toBe(user.firstName);
    expect(component.workflowStartFormGroup.value.lastName).toBe(user.lastName);
  });

  it('should start workflow', () => {
    component.workflowStartFormGroup.controls.comment.setValue('I mag an worfklof ga starte');
    component.workflowStartFormGroup.controls.function.setValue('I bims, a TU');

    component.startWorkflow();

    expect(workflowServiceStub.startWorkflow).toHaveBeenCalled();
  });
});

describe('LineWorkflowDialogComponent open', () => {
  let component: LineWorkflowDialogComponent;
  let fixture: ComponentFixture<LineWorkflowDialogComponent>;

  let dialogRefStub: Mocked<Pick<MatDialogRef<LineWorkflowDialogComponent>, 'close'>>;
  let notificationServiceStub: Mocked<Pick<NotificationService, 'success'>>;
  let userAdministrationServiceStub: Mocked<Pick<UserAdministrationService, 'getCurrentUser'>>;
  let workflowServiceStub: Mocked<Pick<LineWorkflowService, 'getWorkflow' | 'startWorkflow'>>;

  beforeEach(() => {
    // Mocking
    dialogRefStub = { close: vi.fn() };
    notificationServiceStub = { success: vi.fn() };
    userAdministrationServiceStub = {
      getCurrentUser: vi.fn().mockReturnValue(of(user)),
    };
    workflowServiceStub = {
      getWorkflow: vi.fn().mockReturnValue(of(workflow)),
      startWorkflow: vi.fn().mockReturnValue(of({})),
    };

    // Config
    TestBed.configureTestingModule({
      providers: [
        { provide: PermissionService, useValue: adminPermissionServiceMock },
        {
          provide: UserAdministrationService,
          useValue: userAdministrationServiceStub,
        },
        { provide: LineWorkflowService, useValue: workflowServiceStub },
        {
          provide: MAT_DIALOG_DATA,
          useValue: {
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
          } satisfies LineWorkflowDialogData,
        },
        { provide: MatDialogRef, useValue: dialogRefStub },
        { provide: NotificationService, useValue: notificationServiceStub },
        translateServiceProvider,
      ],
    });

    // Arrangement
    fixture = TestBed.createComponent(LineWorkflowDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should display existing workflow', () => {
    expect(component.workflowStartFormGroup.value.mail).toBe(workflow.client!.mail);
    expect(component.workflowStartFormGroup.value.lastName).toBe(workflow.client!.lastName);
    expect(component.workflowStartFormGroup.value.firstName).toBe(workflow.client!.firstName);
    expect(component.workflowStartFormGroup.value.function).toBe(workflow.client!.personFunction);

    expect(component.workflowStartFormGroup.value.comment).toBe(workflow.workflowComment);
  });
});
