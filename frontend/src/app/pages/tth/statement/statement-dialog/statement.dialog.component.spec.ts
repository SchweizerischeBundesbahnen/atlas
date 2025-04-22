import { ComponentFixture, TestBed } from '@angular/core/testing';
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
import { TimetableHearingStatementInternalService } from '../../../../api/service/timetable-hearing-statement-internal.service';

const mockTimetableHearingStatementsService = jasmine.createSpyObj(
  'TimetableHearingStatementInternalService',
  ['updateHearingStatement']
);
const dialogRefSpy = jasmine.createSpyObj(['close']);
const notificationServiceSpy = jasmine.createSpyObj(['success']);
const statement: TimetableHearingStatementV2 = {
  id: 1,
  swissCanton: SwissCanton.Bern,
  statement: 'Canton change statement.',
  justification: 'This is justification.',
  comment: 'This is canton change comment.',
  statementSender: {
    emails: new Set('atlas@sbb.ch'),
  },
};
const form = new FormGroup({
  swissCanton: new FormControl(SwissCanton.Bern),
  comment: new FormControl('Changing canton.'),
});

describe('StatementDialogComponent', () => {
  let component: StatementDialogComponent;
  let fixture: ComponentFixture<StatementDialogComponent>;

  mockTimetableHearingStatementsService.updateHearingStatement.and.returnValue(
    of(statement)
  );

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        FormModule,
        StatementDialogComponent,
        MockAtlasButtonComponent,
      ],
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
    }).compileComponents();

    fixture = TestBed.createComponent(StatementDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should change canton and add comment', () => {
    //when
    component.changeCantonAndAddComment();
    //then
    expect(dialogRefSpy.close).toHaveBeenCalled();
    expect(notificationServiceSpy.success).toHaveBeenCalledWith(
      'TTH.STATEMENT.NOTIFICATION.EDIT_SUCCESS'
    );
  });

  it('should go back to edit mode', () => {
    //when
    component.goBackToStatementDetailEditMode();
    //then
    expect(dialogRefSpy.close).toHaveBeenCalled();
  });

  it('should create dialog with title, text, label, comment and matDialogActions', () => {
    expect(component).toBeTruthy();

    const title = fixture.debugElement.query(By.css('h1'));
    expect(title.nativeElement.innerText).toBe('TTH.STATEMENT.DIALOG.TITLE');

    const dropdownLabel = fixture.debugElement.query(By.css('div > span'));
    expect(dropdownLabel.nativeElement.innerText).toBe(
      'TTH.STATEMENT.DIALOG.TEXT'
    );

    const dropdownSelect = fixture.debugElement.query(By.css('form-comment'));
    expect(dropdownSelect.nativeElement).toBeTruthy();

    const matDialogActions = fixture.debugElement.query(
      By.css('mat-dialog-actions')
    );
    expect(matDialogActions.nativeElement).toBeTruthy();
  });
});
