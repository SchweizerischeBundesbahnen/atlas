import { ComponentFixture, TestBed } from '@angular/core/testing';
import { describe, expect, it, beforeEach, vi, type Mocked } from 'vitest';
import { SwissCanton, TimetableHearingStatementV2 } from '../../../../api';
import { AppTestingModule } from '../../../../app.testing.module';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { By } from '@angular/platform-browser';
import { StatementDialogComponent } from './statement.dialog.component';
import { FormModule } from '../../../../core/module/form.module';
import { FormControl, FormGroup } from '@angular/forms';
import { NotificationService } from '../../../../core/notification/notification.service';
import { MockAtlasButtonComponent } from '../../../../app.testing.mocks';
import { of } from 'rxjs';
import { TimetableHearingStatementInternalService } from '../../../../api/service/lidi/timetable-hearing-statement-internal.service';

const mockTimetableHearingStatementsService: Mocked<
  Pick<TimetableHearingStatementInternalService, 'updateHearingStatement'>
> = {
  updateHearingStatement: vi.fn(),
};
let dialogRefSpy: Mocked<Pick<MatDialogRef<StatementDialogComponent>, 'close'>>;
const notificationServiceSpy: Mocked<Pick<NotificationService, 'success'>> = {
  success: vi.fn(),
};
const statement: TimetableHearingStatementV2 = {
  id: 1,
  swissCanton: SwissCanton.Bern,
  statement: 'Canton change statement.',
  publicComment: 'This is justification.',
  cantonTransferComment: 'This is canton change comment.',
  statementSender: {
    emails: new Set('atlas@sbb.ch'),
  },
};
const form = new FormGroup({
  swissCanton: new FormControl(SwissCanton.Bern),
  cantonTransferComment: new FormControl('Changing canton.'),
});

describe('StatementDialogComponent', () => {
  let component: StatementDialogComponent;
  let fixture: ComponentFixture<StatementDialogComponent>;

  mockTimetableHearingStatementsService.updateHearingStatement.mockReturnValue(of(statement));

  beforeEach(() => {
    dialogRefSpy = { close: vi.fn() };
    TestBed.configureTestingModule({
      imports: [AppTestingModule, FormModule, StatementDialogComponent, MockAtlasButtonComponent],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: form },
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: MatDialogRef, useValue: dialogRefSpy },
        {
          provide: TimetableHearingStatementInternalService,
          useValue: mockTimetableHearingStatementsService,
        },
        { provide: TranslatePipe },
      ],
    });

    fixture = TestBed.createComponent(StatementDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should change canton and add comment', () => {
    //when
    component.changeCantonAndAddComment();
    //then
    expect(dialogRefSpy.close).toHaveBeenCalledTimes(1);
    expect(notificationServiceSpy.success).toHaveBeenCalledWith('TTH.STATEMENT.NOTIFICATION.EDIT_SUCCESS');
  });

  it('should go back to edit mode', () => {
    //when
    component.goBackToStatementDetailEditMode();
    //then
    expect(dialogRefSpy.close).toHaveBeenCalledTimes(1);
  });

  it('should create dialog with title, text, label, comment and matDialogActions', () => {
    const title = fixture.debugElement.query(By.css('h1'));
    expect(title.nativeElement.textContent).toBe('TTH.STATEMENT.DIALOG.TITLE');

    const dropdownLabel = fixture.debugElement.query(By.css('div > span'));
    expect(dropdownLabel.nativeElement.textContent).to.contain('TTH.STATEMENT.DIALOG.TEXT');

    const dropdownSelect = fixture.debugElement.query(By.css('atlas-form-comment'));
    expect(dropdownSelect.nativeElement).toBeTruthy();

    const matDialogActions = fixture.debugElement.query(By.css('mat-dialog-actions'));
    expect(matDialogActions.nativeElement).toBeTruthy();
  });
});
