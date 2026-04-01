import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { mock } from 'vitest-mock-extended';
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

const notificationService = mock<NotificationService>();
const dialogRef =
  mock<MatDialogRef<StatementDataProtectionCheckDialogComponent, boolean>>();
const timetableHearingStatementsService =
  mock<TimetableHearingStatementInternalService>();

describe('StatementDataProtectionCheckDialogComponent', () => {
  let component: StatementDataProtectionCheckDialogComponent;
  let fixture: ComponentFixture<StatementDataProtectionCheckDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
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
  });

  it('should init component without documents', () => {
    expect(component.hasDocuments).toBe(false);
  });
});
