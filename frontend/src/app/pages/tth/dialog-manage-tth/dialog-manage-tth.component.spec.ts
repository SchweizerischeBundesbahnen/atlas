import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi, type Mocked } from 'vitest';
import { DialogManageTthComponent } from './dialog-manage-tth.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TimetableHearingYear } from '../../../api';
import { NotificationService } from '../../../core/notification/notification.service';
import { of } from 'rxjs';
import { MockAtlasButtonComponent, translateServiceProvider } from '../../../app.testing.mocks';
import { Component, output, input } from '@angular/core';
import { AppTestingModule } from '../../../app.testing.module';
import { TimetableHearingYearInternalService } from '../../../api/service/tth/timetable-hearing-year-internal.service';

@Component({
  selector: 'atlas-slide-toggle',
  template: '<p>MockAtlasSlideToggleComponent</p>',
  imports: [AppTestingModule],
})
class MockAtlasSlideToggleComponent {
  readonly toggle = input(false);
  readonly toggleChange = output<boolean>();
}

describe('DialogManageTthComponent', () => {
  let component: DialogManageTthComponent;
  let fixture: ComponentFixture<DialogManageTthComponent>;

  let tthYearLidiServiceSpy: Mocked<
    Pick<
      TimetableHearingYearInternalService,
      'getHearingYear' | 'updateTimetableHearingSettings' | 'closeTimetableHearingYear'
    >
  >;
  let notificationServiceSpy: Mocked<Pick<NotificationService, 'success' | 'error'>>;
  let matDialogRefSpy: Mocked<Pick<MatDialogRef<DialogManageTthComponent, boolean>, 'close'>>;

  const matDialogDataMock = 2020;
  const tthYear: Partial<TimetableHearingYear> = {
    statementEditable: true,
    statementCreatableInternal: false,
    statementCreatableExternal: true,
  };

  beforeEach(() => {
    tthYearLidiServiceSpy = {
      getHearingYear: vi.fn().mockReturnValue(of(tthYear)),
      updateTimetableHearingSettings: vi.fn(),
      closeTimetableHearingYear: vi.fn(),
    };
    notificationServiceSpy = {
      success: vi.fn(),
      error: vi.fn(),
    };
    matDialogRefSpy = {
      close: vi.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        translateServiceProvider,
        {
          provide: MAT_DIALOG_DATA,
          useValue: matDialogDataMock,
        },
        {
          provide: TimetableHearingYearInternalService,
          useValue: tthYearLidiServiceSpy,
        },
        {
          provide: NotificationService,
          useValue: notificationServiceSpy,
        },
        {
          provide: MatDialogRef,
          useValue: matDialogRefSpy,
        },
      ],
      imports: [AppTestingModule, DialogManageTthComponent, MockAtlasButtonComponent, MockAtlasSlideToggleComponent],
    });

    fixture = TestBed.createComponent(DialogManageTthComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and initialize', () => {
    expect(component).toBeTruthy();
    expect(component.currentView).toEqual(component.manageView);
    expect(tthYearLidiServiceSpy.getHearingYear).toHaveBeenCalledWith(2020);
  });

  it('should handleSaveAndCloseClick', () => {
    tthYearLidiServiceSpy.updateTimetableHearingSettings.mockReturnValue(of(undefined));

    component.handleSaveAndCloseClick();
    expect(component.actionButtonsDisabled).toBe(true);
    expect(tthYearLidiServiceSpy.updateTimetableHearingSettings).toHaveBeenCalledWith(2020, {
      statementEditable: true,
      statementCreatableInternal: false,
      statementCreatableExternal: true,
    } as TimetableHearingYear);
    expect(matDialogRefSpy.close).toHaveBeenCalledWith(true);
    expect(notificationServiceSpy.success).toHaveBeenCalledWith(
      'TTH.MANAGE_TIMETABLE_HEARING.SUCCESSFUL_SAVE_NOTIFICATION'
    );
  });

  it('should handleCloseViewTthCloseClick', () => {
    tthYearLidiServiceSpy.closeTimetableHearingYear.mockReturnValue(
      of({
        timetableYear: 2020,
        hearingFrom: new Date(),
        hearingTo: new Date(),
      })
    );

    component.handleCloseViewTthCloseClick();

    expect(component.actionButtonsDisabled).toBe(true);
    expect(tthYearLidiServiceSpy.closeTimetableHearingYear).toHaveBeenCalledWith(2020);
    expect(matDialogRefSpy.close).toHaveBeenCalledWith(true);
    expect(notificationServiceSpy.success).toHaveBeenCalledWith(
      'TTH.CLOSE_TIMETABLE_HEARING.SUCCESSFUL_CLOSE_NOTIFICATION'
    );
  });
});
