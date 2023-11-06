import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TthChangeStatusDialogComponent } from './tth-change-status-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AppTestingModule } from '../../../../app.testing.module';
import {
  SwissCanton,
  TimetableHearingStatement,
  TimetableHearingStatementsService,
} from '../../../../api';
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

const mockTimetableHearingStatementsService = jasmine.createSpyObj(
  'timetableHearingStatementsService',
  ['updateHearingStatementStatus'],
);
const dialogServiceSpy = jasmine.createSpyObj(DialogService, { confirmLeave: of({}) });
const dialogRefSpy = jasmine.createSpyObj(['close']);
const notificationServiceSpy = jasmine.createSpyObj(['success']);
const statement: TimetableHearingStatement = {
  id: 1,
  swissCanton: SwissCanton.Bern,
  statement: 'Luca is am yb match gsi',
  justification: 'Napoli ist besser als YB',
  statementSender: {
    email: 'luca@yb.ch',
  },
};

describe('TthChangeStatusDialogComponent', () => {
  let component: TthChangeStatusDialogComponent;
  let fixture: ComponentFixture<TthChangeStatusDialogComponent>;

  mockTimetableHearingStatementsService.updateHearingStatementStatus.and.returnValue(of(statement));

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        TthChangeStatusDialogComponent,
        BaseChangeDialogComponent,
        CommentComponent,
        ErrorNotificationComponent,
        MaintenanceIconComponent,
      ],
      imports: [AppTestingModule, FormModule],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: {
            title: 'Title',
            message: 'message',
            tths: [statement],
            justification: 'Forza Napoli',
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
          provide: TimetableHearingStatementsService,
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
    expect(dialogRefSpy.close).toHaveBeenCalled();
    expect(notificationServiceSpy.success).toHaveBeenCalledWith(
      'TTH.NOTIFICATION.STATUS_CHANGE.SUCCESS',
    );
  });

  it('should render tth change status dialog', () => {
    component.onClick();

    const baseDialog = fixture.debugElement.query(By.css('app-base-change-dialog'));
    expect(baseDialog).not.toBeNull();
    expect(baseDialog.attributes['controlName']).toBe('justification');
    expect(baseDialog.attributes['maxChars']).toBe('5000');
  });
});
