import {ComponentFixture, TestBed} from '@angular/core/testing';
import {beforeEach, describe, expect, it, type Mocked, vi} from 'vitest';
import {StopPointTerminationDialogComponent} from './stop-point-termination-dialog.component';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {DialogCloseComponent} from '../../../../../../core/components/dialog/close/dialog-close.component';
import {DialogFooterComponent} from '../../../../../../core/components/dialog/footer/dialog-footer.component';
import {CommentComponent} from '../../../../../../core/form-components/comment/comment.component';
import {DialogContentComponent} from '../../../../../../core/components/dialog/content/dialog-content.component';
import {StopPointTerminationDialogData} from './stop-point-termination-dialog-data';
import {
    StopPointTerminationWorkflowService
} from '../../../../../../api/service/workflow/stop-point-termination-workflow.service';
import {of} from 'rxjs';
import {NotificationService} from '../../../../../../core/notification/notification.service';
import {Permission, User} from '../../../../../../api';
import {UserAdministrationService} from '../../../../../../api/service/user-administration/user-administration.service';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {TerminationStopPointWorkflowModel} from '../../../../../../api/model/terminationStopPointWorkflowModel';
import {translateServiceProvider} from '../../../../../../app.testing.mocks';

const workflowDialogData: StopPointTerminationDialogData = {
  title: 'TERMINATION_WORKFLOW.DIALOG.START_TERMINATION_TITLE',
  message: '',
  cancelText: 'DIALOG.CANCEL',
  confirmText: 'COMMON.SAVE',
  versionId: 123,
  sloid: 'ch:1:sloid:1',
  boTerminationDate: new Date(),
};

const user: User = {
  sbbUserId: 'e123',
  lastName: 'Marek',
  firstName: 'Hamsik',
  mail: 'a@b.cd',
  permissions: new Set<Permission>(),
};

describe('StopPointTerminationDialogComponent', () => {
  let component: StopPointTerminationDialogComponent;
  let fixture: ComponentFixture<StopPointTerminationDialogComponent>;

  let dialogRefSpy: Mocked<Pick<MatDialogRef<StopPointTerminationDialogComponent>, 'close'>>;
  let notificationServiceSpy: Mocked<Pick<NotificationService, 'success'>>;
  let workflowServiceSpy: Mocked<Pick<StopPointTerminationWorkflowService, 'startTermination'>>;
  let userAdministrationServiceSpy: Mocked<Pick<UserAdministrationService, 'getCurrentUser'>>;

  beforeEach(() => {
    dialogRefSpy = { close: vi.fn() };
    notificationServiceSpy = { success: vi.fn() };
    workflowServiceSpy = { startTermination: vi.fn() };
    workflowServiceSpy.startTermination.mockReturnValue(of({} as TerminationStopPointWorkflowModel));
    userAdministrationServiceSpy = { getCurrentUser: vi.fn() };
    userAdministrationServiceSpy.getCurrentUser.mockReturnValue(of(user));

    TestBed.configureTestingModule({
      imports: [
        StopPointTerminationDialogComponent,
        DialogCloseComponent,
        DialogContentComponent,
        DialogFooterComponent,
        CommentComponent,
      ],
      providers: [
        { provide: MatDialogRef, useValue: dialogRefSpy },
        {
          provide: MAT_DIALOG_DATA,
          useValue: workflowDialogData,
        },
        {
          provide: StopPointTerminationWorkflowService,
          useValue: workflowServiceSpy,
        },
        { provide: NotificationService, useValue: notificationServiceSpy },
        {
          provide: UserAdministrationService,
          useValue: userAdministrationServiceSpy,
        },
        provideHttpClient(),
        provideHttpClientTesting(),
        translateServiceProvider,
      ],
    });

    fixture = TestBed.createComponent(StopPointTerminationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
