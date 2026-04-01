import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { mock, mockClear } from 'vitest-mock-extended';
import {
  SwissCanton,
  TimetableHearingStatementV2,
} from '../../../../../../api';
import { NotificationService } from '../../../../../../core/notification/notification.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { StatementDataProtectionCheckDialogComponent } from './statement-data-protection-check.dialog.component';
import { TimetableHearingStatementInternalService } from '../../../../../../api/service/lidi/timetable-hearing-statement-internal.service';
import {
  MockAtlasButtonComponent,
  translateServiceProvider,
} from '../../../../../../app.testing.mocks';
import { TranslatePipe } from '@ngx-translate/core';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { of } from 'rxjs';

const statementWithoutDocuments: TimetableHearingStatementV2 = {
  id: 1,
  swissCanton: SwissCanton.Bern,
  statement: 'Canton change statement.',
  publicComment: 'This is justification.',
  cantonTransferComment: 'This is canton change comment.',
  statementSender: {
    emails: new Set('atlas@sbb.ch'),
  },
};

const statementWithTwoDocuments: TimetableHearingStatementV2 = {
  ...statementWithoutDocuments,
  documents: [
    { id: 1, fileName: 'file1.pdf', fileSize: 10 },
    { id: 2, fileName: 'file2.pdf', fileSize: 10 },
  ],
};

const notificationService = mock<NotificationService>();
const dialogRef =
  mock<MatDialogRef<StatementDataProtectionCheckDialogComponent, boolean>>();
const timetableHearingStatementsService =
  mock<TimetableHearingStatementInternalService>();
timetableHearingStatementsService.checkDataProtection.mockReturnValue(
  of(undefined)
);

describe('StatementDataProtectionCheckDialogComponent', () => {
  let component: StatementDataProtectionCheckDialogComponent;
  let fixture: ComponentFixture<StatementDataProtectionCheckDialogComponent>;

  function setupTestBed(statement: TimetableHearingStatementV2) {
    mockClear(dialogRef);
    mockClear(timetableHearingStatementsService);

    TestBed.configureTestingModule({
      imports: [MockAtlasButtonComponent],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: statement },
        { provide: NotificationService, useValue: notificationService },
        { provide: MatDialogRef, useValue: dialogRef },
        {
          provide: TimetableHearingStatementInternalService,
          useValue: timetableHearingStatementsService,
        },
        { provide: TranslatePipe },
        translateServiceProvider,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(
      StatementDataProtectionCheckDialogComponent
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  describe('without documents', () => {
    beforeEach(() => {
      setupTestBed(statementWithoutDocuments);
    });

    it('should perform data protection check without documents', () => {
      //given
      expect(component.hasDocuments).toBe(false);

      // when
      component.statementFormGroup.controls.hasStatementPersonalInformation.setValue(
        false
      );
      component.completeTextDataProtection();

      // then
      expect(
        timetableHearingStatementsService.checkDataProtection
      ).toHaveBeenCalledTimes(1);
      expect(notificationService.success).toHaveBeenCalledTimes(1);
      expect(dialogRef.close).toHaveBeenCalledExactlyOnceWith(true);
    });

    it('should force anonymizing of text if it has personal information', () => {
      // when
      component.statementFormGroup.controls.hasStatementPersonalInformation.setValue(
        true
      );
      component.completeTextDataProtection();

      // then
      expect(
        component.statementFormGroup.hasError('NO_ANONYMIZATION_DETECTED')
      ).toBe(true);

      // when
      component.statementFormGroup.controls.hasStatementPersonalInformation.setValue(
        true
      );
      component.statementFormGroup.controls.anonymousStatement.setValue(
        'anonymized text'
      );
      component.completeTextDataProtection();

      // then
      expect(
        component.statementFormGroup.hasError('NO_ANONYMIZATION_DETECTED')
      ).toBe(false);
    });

    it('should cancel', () => {
      // when
      component.closeDialog();

      // then
      expect(dialogRef.close).toHaveBeenCalledExactlyOnceWith(false);
    });
  });

  describe('with documents', () => {
    beforeEach(() => {
      setupTestBed(statementWithTwoDocuments);
    });

    it('should complete text and document protection', () => {
      //given
      expect(component.hasDocuments).toBe(true);

      // step 1
      expect(component.stepper().selectedIndex).toBe(0);

      component.statementFormGroup.controls.hasStatementPersonalInformation.setValue(
        false
      );
      component.completeTextDataProtection();

      expect(
        timetableHearingStatementsService.checkDataProtection
      ).toHaveBeenCalledTimes(0);

      // step 2
      expect(component.stepper().selectedIndex).toBe(1);

      component.documentFormGroup.controls.documents
        .at(0)
        .controls.hasDocumentPersonalInformation.setValue(false);
      component.documentFormGroup.controls.documents
        .at(1)
        .controls.hasDocumentPersonalInformation.setValue(false);

      component.completeFileDataProtection();

      expect(
        timetableHearingStatementsService.checkDataProtection
      ).toHaveBeenCalledTimes(1);
    });

    it('should cancel', () => {
      // when
      component.closeDialog();

      // then
      expect(dialogRef.close).toHaveBeenCalledExactlyOnceWith(false);
    });
  });
});
