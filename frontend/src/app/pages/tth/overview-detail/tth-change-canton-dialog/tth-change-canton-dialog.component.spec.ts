import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TthChangeCantonDialogComponent } from './tth-change-canton-dialog.component';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AppTestingModule } from '../../../../app.testing.module';
import { FormModule } from '../../../../core/module/form.module';
import { BaseChangeDialogComponent } from '../base-change-dialog/base-change-dialog.component';
import {
  SwissCanton,
  TimetableHearingStatement,
  TimetableHearingStatementsService,
} from '../../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { of } from 'rxjs';

const statement: TimetableHearingStatement = {
  id: 1,
  swissCanton: SwissCanton.Bern,
  statement: 'Luca is am yb match gsi',
  justification: 'Napoli ist besser als YB',
  statementSender: {
    email: 'luca@yb.ch',
  },
};
const dialogRefSpy = jasmine.createSpyObj(['close']);
const dialogServiceSpy = jasmine.createSpyObj(DialogService, { confirmLeave: of({}) });
const notificationServiceSpy = jasmine.createSpyObj(['success']);
const mockTimetableHearingStatementsService = jasmine.createSpyObj(
  'timetableHearingStatementsService',
  ['updateHearingCanton'],
);

describe('TthChangeCantonDialogComponent', () => {
  let component: TthChangeCantonDialogComponent;
  let fixture: ComponentFixture<TthChangeCantonDialogComponent>;

  mockTimetableHearingStatementsService.updateHearingCanton.and.returnValue(of(statement));

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TthChangeCantonDialogComponent, BaseChangeDialogComponent],
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

    fixture = TestBed.createComponent(TthChangeCantonDialogComponent);
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
      'TTH.NOTIFICATION.CANTON_CHANGE.SUCCESS',
    );
  });
});
