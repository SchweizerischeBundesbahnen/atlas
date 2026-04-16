import { ComponentFixture, TestBed } from '@angular/core/testing';
import { describe, expect, it, beforeEach, vi, type Mocked } from 'vitest';
import { TthChangeCantonDialogComponent } from './tth-change-canton-dialog.component';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AppTestingModule } from '../../../../app.testing.module';
import { FormModule } from '../../../../core/module/form.module';
import { BaseChangeDialogComponent } from '../base-change-dialog/base-change-dialog.component';
import { SwissCanton, TimetableHearingStatementV2 } from '../../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { of } from 'rxjs';
import { TimetableHearingStatementInternalService } from '../../../../api/service/lidi/timetable-hearing-statement-internal.service';

const statement: TimetableHearingStatementV2 = {
  id: 1,
  swissCanton: SwissCanton.Bern,
  statement: 'Öper isch am YB-Match gsi',
  publicComment: 'Napoli ist besser als YB',
  statementSender: {
    emails: new Set('fan@yb.ch'),
  },
};
const dialogRefSpy: Mocked<Pick<MatDialogRef<TthChangeCantonDialogComponent>, 'close'>> = { close: vi.fn() };
const dialogServiceSpy: Mocked<Pick<DialogService, 'confirmLeave'>> = {
  confirmLeave: vi.fn().mockReturnValue(of({})),
};
const notificationServiceSpy: Mocked<Pick<NotificationService, 'success'>> = {
  success: vi.fn(),
};
const mockTimetableHearingStatementsService: Mocked<
  Pick<TimetableHearingStatementInternalService, 'updateHearingCanton'>
> = { updateHearingCanton: vi.fn() };

describe('TthChangeCantonDialogComponent', () => {
  let component: TthChangeCantonDialogComponent;
  let fixture: ComponentFixture<TthChangeCantonDialogComponent>;

  mockTimetableHearingStatementsService.updateHearingCanton.mockReturnValue(
    of(undefined) as ReturnType<TimetableHearingStatementInternalService['updateHearingCanton']>
  );

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppTestingModule, FormModule, TthChangeCantonDialogComponent, BaseChangeDialogComponent],
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
          provide: TimetableHearingStatementInternalService,
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
    expect(dialogRefSpy.close).toHaveBeenCalledTimes(1);
    expect(notificationServiceSpy.success).toHaveBeenCalledWith('TTH.NOTIFICATION.CANTON_CHANGE.SUCCESS');
  });
});
