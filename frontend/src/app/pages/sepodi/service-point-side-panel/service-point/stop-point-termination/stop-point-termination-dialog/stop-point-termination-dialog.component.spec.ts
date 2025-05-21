import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopPointTerminationDialogComponent } from './stop-point-termination-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DialogCloseComponent } from 'src/app/core/components/dialog/close/dialog-close.component';
import { DialogFooterComponent } from '../../../../../../core/components/dialog/footer/dialog-footer.component';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { CommentComponent } from '../../../../../../core/form-components/comment/comment.component';
import { DialogContentComponent } from '../../../../../../core/components/dialog/content/dialog-content.component';
import { StopPointTerminationDialogData } from './stop-point-termination-dialog-data';
import { WorkflowService } from '../../../../../../api/service/workflow/workflow.service';
import { of } from 'rxjs';
import { NotificationService } from '../../../../../../core/notification/notification.service';
import { User, UserAdministrationService } from '../../../../../../api';

const dialogRefSpy = jasmine.createSpyObj(['close']);
const notificationServiceSpy = jasmine.createSpyObj(['success']);
const workflowServiceMock = jasmine.createSpyObj(WorkflowService, {
  startTermination: of({}),
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
const workflowDialogData: StopPointTerminationDialogData = {
  title: 'TERMINATION_WORKFLOW.DIALOG.START_TERMINATION_TITLE',
  message: '',
  cancelText: 'DIALOG.CANCEL',
  confirmText: 'COMMON.SAVE',
  versionId: 123,
  sloid: 'ch:1:sloid:1',
  boTerminationDate: new Date(),
};

describe('StopPointTerminationDialogComponent', () => {
  let component: StopPointTerminationDialogComponent;
  let fixture: ComponentFixture<StopPointTerminationDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        StopPointTerminationDialogComponent,
        TranslateModule.forRoot(),
        DialogCloseComponent,
        DialogContentComponent,
        DialogFooterComponent,
        TranslatePipe,
        CommentComponent,
      ],
      providers: [
        { provide: MatDialogRef, useValue: dialogRefSpy },
        {
          provide: MAT_DIALOG_DATA,
          useValue: workflowDialogData,
        },
        { provide: WorkflowService, useValue: workflowServiceMock },
        { provide: NotificationService, useValue: notificationServiceSpy },
        {
          provide: UserAdministrationService,
          useValue: userAdministrationServiceMock,
        },
        { provide: TranslatePipe },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(StopPointTerminationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
