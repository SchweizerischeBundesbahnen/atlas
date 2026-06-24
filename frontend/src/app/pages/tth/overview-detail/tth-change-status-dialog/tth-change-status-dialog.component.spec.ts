import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { TthChangeStatusDialogComponent } from './tth-change-status-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AppTestingModule } from '../../../../app.testing.module';
import { SwissCanton, TimetableHearingStatementV2 } from '../../../../api';
import { of } from 'rxjs';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';
import { NotificationService } from '../../../../core/notification/notification.service';
import { CommentComponent } from '../../../../core/form-components/comment/comment.component';
import { ErrorNotificationComponent } from '../../../../core/notification/error/error-notification.component';
import { FormModule } from '../../../../core/module/form.module';
import { TranslatePipe } from '@ngx-translate/core';
import { By } from '@angular/platform-browser';
import { BaseChangeDialogComponent } from '../base-change-dialog/base-change-dialog.component';
import { MaintenanceIconComponent } from '../../../../core/components/header/maintenance-icon/maintenance-icon.component';
import {
  TimetableHearingStatementInternalService
} from '../../../../api/service/lidi/timetable-hearing-statement-internal.service';

const mockTimetableHearingStatementsService: Mocked<
  Pick<TimetableHearingStatementInternalService, 'updateHearingStatementStatus'>
> = {
  updateHearingStatementStatus: vi.fn(),
};
const dialogServiceSpy: Mocked<Pick<DialogService, 'confirmLeave'>> = {
  confirmLeave: vi.fn().mockReturnValue(of({})),
};
const dialogRefSpy: Mocked<Pick<MatDialogRef<TthChangeStatusDialogComponent>, 'close'>> = {
  close: vi.fn(),
};
const notificationServiceSpy: Mocked<Pick<NotificationService, 'success'>> = {
  success: vi.fn(),
};
const statement: TimetableHearingStatementV2 = {
  id: 1,
  swissCanton: SwissCanton.Bern,
  statement: 'Öper isch am YB-Match gsi',
  publicComment: 'Napoli ist besser als YB',
  internalComment: 'YB ist besser',
  statementSender: {
    emails: new Set('fan@yb.ch'),
  },
};

describe('TthChangeStatusDialogComponent', () => {
  let component: TthChangeStatusDialogComponent;
  let fixture: ComponentFixture<TthChangeStatusDialogComponent>;

  mockTimetableHearingStatementsService.updateHearingStatementStatus.mockReturnValue(
    of(undefined) as ReturnType<TimetableHearingStatementInternalService['updateHearingStatementStatus']>
  );

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        FormModule,
        TthChangeStatusDialogComponent,
        BaseChangeDialogComponent,
        CommentComponent,
        ErrorNotificationComponent,
        MaintenanceIconComponent,
      ],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: {
            title: 'Title',
            message: 'message',
            tths: [statement],
            internalComment: 'Forza Napoli',
            type: 'SINGLE',
            id: 1,
          },
        },
        { provide: MatSnackBarRef, useValue: {} },
        { provide: MAT_SNACK_BAR_DATA, useValue: {} },
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: DialogService, useValue: dialogServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        {
          provide: TimetableHearingStatementInternalService,
          useValue: mockTimetableHearingStatementsService,
        },
        { provide: TranslatePipe },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TthChangeStatusDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update hearing statement', () => {
    //when
    component.onClick();
    //then
    expect(dialogRefSpy.close).toHaveBeenCalledTimes(1);
    expect(notificationServiceSpy.success).toHaveBeenCalledWith('TTH.NOTIFICATION.STATUS_CHANGE.SUCCESS');
  });

  it('should render tth change status dialog', () => {
    component.onClick();

    const baseDialog = fixture.debugElement.query(By.css('atlas-base-change-dialog'));
    expect(baseDialog).not.toBeNull();
    expect(baseDialog.attributes['controlName']).toBe('internalComment');
    expect(baseDialog.attributes['maxChars']).toBe('5000');
  });
});
