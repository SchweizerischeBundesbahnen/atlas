import {
  HearingStatus,
  StatementStatus,
  SwissCanton,
  TimetableFieldNumber,
  TimetableHearingStatementAlternating,
  TimetableHearingStatementV2,
  TimetableHearingYear,
  TransportCompany,
} from '../../../../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder } from '@angular/forms';
import { BehaviorSubject, EMPTY, of } from 'rxjs';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslatePipe } from '@ngx-translate/core';
import { CantonStatementDetailComponent } from './canton-statement-detail.component';
import {
  adminPermissionServiceMock,
  translateServiceProvider,
} from '../../../../../app.testing.mocks';
import { By } from '@angular/platform-browser';
import { LoadingSpinnerService } from '../../../../../core/components/loading-spinner/loading-spinner.service';
import { StatementShareService } from '../../../overview-detail/statement-share-service';
import { PermissionService } from '../../../../../core/auth/permission/permission.service';
import { TimetableHearingStatementInternalService } from '../../../../../api/service/lidi/timetable-hearing-statement-internal.service';
import { TimetableHearingYearInternalService } from '../../../../../api/service/lidi/timetable-hearing-year-internal.service';
import { TimetableYearChangeInternalService } from '../../../../../api/service/lidi/timetable-year-change-internal.service';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { mock, mockClear } from 'vitest-mock-extended';

const existingStatement: TimetableHearingStatementV2 = {
  id: 1,
  swissCanton: SwissCanton.Bern,
  statement: 'Öper isch am YB-Match gsi',
  editor: 'Harry Potter',
  statementSender: {
    emails: new Set('fan@yb.ch'),
  },
  documents: [
    { id: 123, fileName: 'fileName', fileSize: 1234, anonymous: true },
    { id: 234, fileName: 'fileName.pdf', fileSize: 1234, anonymous: false },
  ],
};

const years: TimetableHearingYear[] = [
  {
    timetableYear: 2024,
    hearingFrom: new Date('2023-05-1'),
    hearingTo: new Date('2023-05-31'),
  },
];

let component: CantonStatementDetailComponent;
let fixture: ComponentFixture<CantonStatementDetailComponent>;
let router: Router;

const mockStatementShareService: Mocked<
  Pick<StatementShareService, 'getCloneStatement' | 'clearCachedStatement'> & {
    statement: TimetableHearingStatementV2;
  }
> = {
  getCloneStatement: vi.fn().mockReturnValue(existingStatement),
  clearCachedStatement: vi.fn(),
  statement: existingStatement,
};
const mockTimetableHearingYearsService: Mocked<
  Pick<TimetableHearingYearInternalService, 'getHearingYears'>
> = {
  getHearingYears: vi.fn(),
};

const mockTimetableHearingStatementsService: Mocked<
  Pick<
    TimetableHearingStatementInternalService,
    | 'createStatement'
    | 'getNextStatement'
    | 'getPreviousStatement'
    | 'getResponsibleTransportCompanies'
    | 'updateHearingStatement'
    | 'getStatementDocument'
  >
> = {
  createStatement: vi.fn(),
  getNextStatement: vi.fn(),
  getPreviousStatement: vi.fn(),
  getResponsibleTransportCompanies: vi.fn(),
  updateHearingStatement: vi.fn(),
  getStatementDocument: vi.fn(),
};

const timetableYearChangeInternalServiceSpy: Mocked<
  Pick<TimetableYearChangeInternalService, 'getTimetableYearChange'>
> = {
  getTimetableYearChange: vi.fn().mockReturnValue(EMPTY),
};

const alternation: TimetableHearingStatementAlternating = {
  timetableHearingStatement: existingStatement,
  pageable: {
    pageNumber: 1,
  },
};
const transportCompany: TransportCompany = {
  number: '#0001',
  businessRegisterName: 'Schweizerische Bundesbahnen SBB',
};
mockTimetableHearingStatementsService.getNextStatement.mockReturnValue(
  of(alternation)
);
mockTimetableHearingStatementsService.getPreviousStatement.mockReturnValue(
  of(alternation)
);
mockTimetableHearingStatementsService.getResponsibleTransportCompanies.mockReturnValue(
  of([transportCompany])
);
mockStatementShareService.getCloneStatement.mockReturnValue(existingStatement);

const blob = 'Blob' as unknown as Blob;
mockTimetableHearingStatementsService.getStatementDocument.mockReturnValue(
  of(blob)
);

const dialogService = mock<DialogService>();

describe('StatementDetailComponent for existing statement', () => {
  beforeEach(() => {
    const mockRoute = {
      snapshot: {
        data: {
          statement: existingStatement,
        },
        params: {
          canton: 'be',
        },
      },
    };
    setupTestBed(mockRoute);

    fixture = TestBed.createComponent(CantonStatementDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
    expect(component.isNew).toBe(false);
  });

  it('should load existing Statement form successfully', () => {
    expect(component.form.controls.statement.value).toBe(
      existingStatement.statement
    );
  });

  it('should switch to edit mode successfully', () => {
    expect(component.form.enabled).toBe(false);

    component.toggleEdit();
    expect(component.form.enabled).toBe(true);
  });

  it('should not enable form when hearingStatus is Archived', () => {
    component.hearingStatus = HearingStatus.Archived;

    expect(component.form.enabled).toBeFalsy();
  });

  it('should not enable form when hearingStatus is Archived and clicking on toggleEdit', () => {
    //given
    component.hearingStatus = HearingStatus.Archived;

    //when
    component.toggleEdit();

    //then
    expect(component.form.enabled).toBeFalsy();
  });

  it('should go to next statement', () => {
    component.hearingStatus = HearingStatus.Archived;

    component.next();
    expect(
      mockTimetableHearingStatementsService.getNextStatement
    ).toHaveBeenCalledTimes(1);
  });

  it('should go to previous statement', () => {
    component.hearingStatus = HearingStatus.Archived;

    component.previous();
    expect(
      mockTimetableHearingStatementsService.getPreviousStatement
    ).toHaveBeenCalledTimes(1);
  });

  it('should update statement', () => {
    mockTimetableHearingStatementsService.updateHearingStatement.mockReturnValue(
      of(existingStatement)
    );
    component.toggleEdit();
    expect(component.form.enabled).toBe(true);

    component.form.controls.timetableYear.setValue(2025);
    component.form.controls.statementStatus.setValue(StatementStatus.Received);
    component.form.controls.statement.setValue('New comment');
    component.form.controls.statementSender.controls.emails.setValue([
      'test@bav.ch',
    ]);
    component.save();
    expect(
      mockTimetableHearingStatementsService.updateHearingStatement
    ).toHaveBeenCalledTimes(1);
  });

  it('should cantonSelectionChanged', () => {
    //given
    mockTimetableHearingStatementsService.updateHearingStatement.mockReturnValue(
      of(existingStatement)
    );
    //when
    component.cantonSelectionChanged();
    //then
    expect(component.form.controls.editor.getRawValue()).toBe(
      existingStatement.editor
    );
    expect(component.form.controls.oldSwissCanton.getRawValue()).toBe(
      component.initialValueForCanton
    );
  });

  it('should removeDocument', () => {
    //given
    mockTimetableHearingStatementsService.updateHearingStatement.mockReturnValue(
      of(existingStatement)
    );
    expect(component.form.controls.documents.controls.length).toBe(2);

    //when
    component.removeDocument('fileName.pdf');
    //then
    expect(component.form.controls.documents.controls.length).toBe(1);
  });

  it('should downloadLocalFile', () => {
    //given
    expect(component.uploadedFiles.length).toBe(2);
    mockTimetableHearingStatementsService.updateHearingStatement.mockReturnValue(
      of(existingStatement)
    );
    const blob = 'Blob' as unknown as Blob;
    mockTimetableHearingStatementsService.getStatementDocument.mockReturnValue(
      of(blob)
    );
    const documents = existingStatement.documents;
    //when
    component.downloadLocalFile(1, documents);
    //then
    expect(component.uploadedFiles.length).toBe(4);
    expect(component.uploadedFiles[0].name).toBeDefined();
    expect(component.uploadedFiles[1].name).toBeDefined();
  });

  it('should open data protection dialog and reload on save', () => {
    //given
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
    //when
    component.openDataProtectionCheck();
    //then
    expect(
      dialogService.openCustomDataWithConfirmationResult
    ).toHaveBeenCalledTimes(1);
    expect(router.navigate).toHaveBeenCalledTimes(1);
  });
});

describe('test editButton', () => {
  function setup(hearingStatus: HearingStatus) {
    const mockRoute = {
      snapshot: {
        data: {
          statement: existingStatement,
          hearingStatus,
        },
        params: {
          canton: 'be',
        },
      },
    };
    setupTestBed(mockRoute);

    mockTimetableHearingYearsService.getHearingYears.mockReturnValue(
      of([
        {
          ...years[0],
          statementEditable: true,
        },
      ])
    );

    fixture = TestBed.createComponent(CantonStatementDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  }

  it('should not show edit button when HearingStatus is Archived', () => {
    setup(HearingStatus.Archived);

    const buttons = fixture.debugElement.queryAll(By.css('atlas-button'));
    const buttonsText = buttons.map(
      (button) => button.nativeElement.attributes['buttontext']?.value
    );
    expect(buttonsText).not.toContain('COMMON.EDIT');
  });

  it('should show edit button when HearingStatus is not Archived and statement is editable', () => {
    setup(HearingStatus.Active);

    const buttons = fixture.debugElement.queryAll(By.css('atlas-button'));
    const buttonsText = buttons.map(
      (button) => button.nativeElement.attributes['buttontext']?.value
    );
    expect(buttonsText).toContain('COMMON.EDIT');
  });
});

describe('StatementDetailComponent for new statement', () => {
  beforeEach(() => {
    const mockRoute = {
      snapshot: {
        data: {
          statement: undefined,
        },
        params: {
          canton: 'be',
        },
      },
    };
    setupTestBed(mockRoute);

    fixture = TestBed.createComponent(CantonStatementDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.isNew).toBe(true);
  });

  describe('create new statement', () => {
    it('successfully', () => {
      vi.spyOn(router, 'navigate').mockResolvedValue(true);
      mockTimetableHearingStatementsService.createStatement.mockReturnValue(
        of(existingStatement)
      );

      component.form.controls.swissCanton.setValue(SwissCanton.Bern);
      component.form.controls.statement.setValue('my yb busses');
      component.form.controls.statementSender.controls.emails.setValue([
        'fan@yb.ch',
      ]);
      fixture.detectChanges();

      component.save();
      expect(
        mockTimetableHearingStatementsService.createStatement
      ).toHaveBeenCalledTimes(1);

      fixture.detectChanges();

      const snackBarContainer =
        fixture.nativeElement.parentElement.querySelector(
          'mat-snack-bar-container'
        );
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer.textContent.trim()).toBe(
        'TTH.STATEMENT.NOTIFICATION.ADD_SUCCESS'
      );
      expect(snackBarContainer.classList).toContain('success');
      expect(router.navigate).toHaveBeenCalledTimes(1);
    });
  });

  it('should fill responsible transport companies on ttfn change', () => {
    component.ttfnSelectionChanged({
      ttfnid: 'ch:1:ttfnid:123',
    } as TimetableFieldNumber);
    expect(
      mockTimetableHearingStatementsService.getResponsibleTransportCompanies
    ).toHaveBeenCalledTimes(1);
  });
});

function setupTestBed(activatedRoute: {
  snapshot: { data: { statement: undefined | TimetableHearingStatementV2 } };
}) {
  mockClear(dialogService);
  dialogService.openCustomDataWithConfirmationResult.mockReturnValue(of(true));

  mockTimetableHearingYearsService.getHearingYears.mockReturnValue(of(years));

  TestBed.configureTestingModule({
    providers: [
      translateServiceProvider,
      { provide: FormBuilder },
      {
        provide: LoadingSpinnerService,
        useValue: { loading: new BehaviorSubject(false) },
      },
      {
        provide: TimetableHearingYearInternalService,
        useValue: mockTimetableHearingYearsService,
      },
      {
        provide: TimetableHearingStatementInternalService,
        useValue: mockTimetableHearingStatementsService,
      },
      {
        provide: TimetableYearChangeInternalService,
        useValue: timetableYearChangeInternalServiceSpy,
      },
      {
        provide: StatementShareService,
        useValue: mockStatementShareService,
      },
      {
        provide: DialogService,
        useValue: dialogService,
      },
      { provide: PermissionService, useValue: adminPermissionServiceMock },
      { provide: TranslatePipe },
      {
        provide: ActivatedRoute,
        useValue: activatedRoute,
      },
    ],
  });
}
