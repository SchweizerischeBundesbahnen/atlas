import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TthChangeStatusDialogComponent } from './tth-change-status-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AppTestingModule } from '../../../../app.testing.module';
import { SwissCanton, TimetableHearingService, TimetableHearingStatement } from '../../../../api';
import { of } from 'rxjs';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';
import { NotificationService } from '../../../../core/notification/notification.service';
import { CommentComponent } from '../../../../core/form-components/comment/comment.component';
import { ErrorNotificationComponent } from '../../../../core/notification/error/error-notification.component';
import { FormModule } from '../../../../core/module/form.module';
import { TranslatePipe } from '@ngx-translate/core';
import { By } from '@angular/platform-browser';

const mockTimetableHearingService = jasmine.createSpyObj('timetableHearingService', [
  'updateHearingStatementStatus',
]);
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

  beforeEach(async () => {
    mockTimetableHearingService.updateHearingStatementStatus.and.returnValue(of(statement));
    await TestBed.configureTestingModule({
      declarations: [TthChangeStatusDialogComponent, CommentComponent, ErrorNotificationComponent],
      imports: [AppTestingModule, FormModule],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: {
            title: 'Title',
            message: 'message',
            tths: [statement],
            justification: 'Forza Napoli',
            id: 1,
          },
        },
        { provide: MatSnackBarRef, useValue: {} },
        { provide: MAT_SNACK_BAR_DATA, useValue: {} },
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: DialogService, useValue: dialogServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: TimetableHearingService, useValue: mockTimetableHearingService },
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
      'TTH.NOTIFICATION.STATUS_CHANGE.SUCCESS'
    );
  });

  it('should close dialog when form is dirty', () => {
    //given
    component.tthChangeStatusFormGroup.markAsDirty();
    //when
    component.closeDialog();
    //then
    expect(dialogServiceSpy.confirmLeave).toHaveBeenCalled();

    expect(dialogRefSpy.close).toHaveBeenCalledWith();
  });

  it('should close dialog when form is not dirty', () => {
    //when
    component.closeDialog();
    //then

    expect(dialogRefSpy.close).toHaveBeenCalledWith();
  });

  it('should render tth change status dialog', () => {
    component.onClick();

    const title = fixture.debugElement.query(By.css('div.dialog > div.mb-5 > span.font-bold-4xl'));
    expect(title.nativeElement.innerText).toBe('Title');

    const content = fixture.debugElement.query(By.css('div.dialog > div > span.message'));
    expect(content.nativeElement.innerText).toBe('message');

    const justification = fixture.debugElement.query(By.css('form-comment'));
    const justificationValue = justification.nativeNode.querySelector('textarea').value;
    expect(justificationValue).toBe('Forza Napoli');

    const cancelButton = fixture.debugElement.query(By.css('mat-dialog-actions button.me-3'));
    expect(cancelButton.nativeElement.innerText).toBe('DIALOG.CANCEL');

    const confirmButton = fixture.debugElement.query(
      By.css('mat-dialog-actions button.primary-color-btn')
    );
    expect(confirmButton.nativeElement.innerText).toBe('DIALOG.OK');
  });
});
