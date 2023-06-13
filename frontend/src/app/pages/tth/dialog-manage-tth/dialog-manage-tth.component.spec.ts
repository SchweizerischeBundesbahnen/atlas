import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogManageTthComponent } from './dialog-manage-tth.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TimetableHearingService, TimetableHearingYear } from '../../../api';
import { NotificationService } from '../../../core/notification/notification.service';
import { of } from 'rxjs';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MockAtlasButtonComponent } from '../../../app.testing.mocks';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AppTestingModule } from '../../../app.testing.module';

@Component({
  selector: 'atlas-slide-toggle',
  template: '<p>MockAtlasSlideToggleComponent</p>',
})
class MockAtlasSlideToggleComponent {
  @Input() toggle = false;
  @Output() toggleChange = new EventEmitter<boolean>();
}

describe('DialogManageTthComponent', () => {
  let component: DialogManageTthComponent;
  let fixture: ComponentFixture<DialogManageTthComponent>;

  let tthServiceSpy: jasmine.SpyObj<TimetableHearingService>;
  let notificationServiceSpy: jasmine.SpyObj<NotificationService>;
  let matDialogRefSpy: jasmine.SpyObj<MatDialogRef<DialogManageTthComponent, boolean>>;

  const matDialogDataMock = 2020;
  const tthYear: Partial<TimetableHearingYear> = {
    statementEditable: true,
    statementCreatableInternal: false,
    statementCreatableExternal: true,
  };

  beforeEach(async () => {
    tthServiceSpy = jasmine.createSpyObj<TimetableHearingService>('TthServiceSpy', [
      'getHearingYear',
      'updateTimetableHearingSettings',
      'closeTimetableHearing',
    ]);
    notificationServiceSpy = jasmine.createSpyObj<NotificationService>('NotificationServiceSpy', [
      'success',
      'error',
    ]);
    matDialogRefSpy = jasmine.createSpyObj<MatDialogRef<DialogManageTthComponent, boolean>>(
      'MatDialogRefSpy',
      ['close']
    );

    await TestBed.configureTestingModule({
      declarations: [
        DialogManageTthComponent,
        MockAtlasButtonComponent,
        MockAtlasSlideToggleComponent,
      ],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: matDialogDataMock,
        },
        {
          provide: TimetableHearingService,
          useValue: tthServiceSpy,
        },
        {
          provide: NotificationService,
          useValue: notificationServiceSpy,
        },
        {
          provide: MatDialogRef<DialogManageTthComponent, boolean>,
          useValue: matDialogRefSpy,
        },
      ],
      imports: [
        AppTestingModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
    }).compileComponents();

    tthServiceSpy.getHearingYear.and.stub().and.returnValue(of(tthYear));

    fixture = TestBed.createComponent(DialogManageTthComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and initialize', () => {
    expect(component).toBeTruthy();
    expect(component.currentView).toEqual(component.manageView);
    expect(tthServiceSpy.getHearingYear).toHaveBeenCalledOnceWith(2020);
  });

  it('should handleSaveAndCloseClick', () => {
    tthServiceSpy.updateTimetableHearingSettings.and.stub().and.returnValue(of({}));

    component.handleSaveAndCloseClick();
    expect(component.actionButtonsDisabled).toBeTrue();
    expect(tthServiceSpy.updateTimetableHearingSettings).toHaveBeenCalledOnceWith(2020, {
      statementEditable: true,
      statementCreatableInternal: false,
      statementCreatableExternal: true,
    } as TimetableHearingYear);
    expect(matDialogRefSpy.close).toHaveBeenCalledOnceWith(true);
    expect(notificationServiceSpy.success).toHaveBeenCalledOnceWith(
      'TTH.MANAGE_TIMETABLE_HEARING.SUCCESSFUL_SAVE_NOTIFICATION'
    );
  });

  it('should handleCloseViewTthCloseClick', () => {
    tthServiceSpy.closeTimetableHearing.and.stub().and.returnValue(of({}));

    component.handleCloseViewTthCloseClick();

    expect(component.actionButtonsDisabled).toBeTrue();
    expect(tthServiceSpy.closeTimetableHearing).toHaveBeenCalledOnceWith(2020);
    expect(matDialogRefSpy.close).toHaveBeenCalledOnceWith(true);
    expect(notificationServiceSpy.success).toHaveBeenCalledOnceWith(
      'TTH.CLOSE_TIMETABLE_HEARING.SUCCESSFUL_CLOSE_NOTIFICATION'
    );
  });
});
