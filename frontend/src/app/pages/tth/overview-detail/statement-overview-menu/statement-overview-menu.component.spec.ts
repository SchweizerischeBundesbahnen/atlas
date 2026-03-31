import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { SwissCanton, TimetableHearingStatementV2 } from 'src/app/api';
import { StatementOverviewMenuComponent } from './statement-overview-menu.component';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { StatementShareService } from '../statement-share-service';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { translateServiceProvider } from '../../../../app.testing.mocks';
import { Pages } from '../../../pages';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

const router: Mocked<Pick<Router, 'navigate'>> = {
  navigate: vi.fn().mockResolvedValue(undefined),
};

const timetableHearingStatement: TimetableHearingStatementV2 = {
  timetableYear: 2001,
  statementStatus: 'REVOKED',
  ttfnid: 'ch:1:ttfnid:1000008',
  timetableFieldNumber: 'ch:1:ttfnid:1000008',
  swissCanton: 'BASEL_COUNTRY',
  responsibleTransportCompanies: [
    {
      id: 1000,
      number: '#0001',
      abbreviation: 'SBB',
      businessRegisterName: 'Schweizerische Bundesbahnen SBB',
    },
    {
      id: 1001,
      number: '#0001',
      abbreviation: 'Post Auto',
      businessRegisterName: 'Post Auto',
    },
    {
      id: 1002,
      number: '#0001',
      abbreviation: 'BLS',
      businessRegisterName: 'BLS',
    },
  ],
  statementSender: { emails: new Set('a@b.c') },
  statement: 'Ich hätte gerne mehrere Verbindungen am Abend.',
  documents: [],
};

const statementShareService = {
  statement: timetableHearingStatement,
};

describe('StatementOverviewMenuComponent', () => {
  let component: StatementOverviewMenuComponent;
  let fixture: ComponentFixture<StatementOverviewMenuComponent>;

  let dialogService: Mocked<
    Pick<DialogService, 'openDialogDataWithConfirmationResult'>
  >;

  beforeEach(() => {
    dialogService = {
      openDialogDataWithConfirmationResult: vi.fn().mockReturnValue(of(true)),
    };

    TestBed.configureTestingModule({
      providers: [
        {
          provide: DialogService,
          useValue: dialogService,
        },
        {
          provide: StatementShareService,
          useValue: statementShareService,
        },
        {
          provide: Router,
          useValue: router,
        },
        { provide: ActivatedRoute, useValue: { parent: {} } },
        { provide: TranslatePipe },
        translateServiceProvider,
      ],
    });

    fixture = TestBed.createComponent(StatementOverviewMenuComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('row', timetableHearingStatement);
    fixture.componentRef.setInput('column', { disabled: false });

    fixture.detectChanges();
    router.navigate.mockClear();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should duplicate dossier', () => {
    const statement = {
      swissCanton: SwissCanton.Aargau,
      statement: 'This is a statement',
      statementSender: {
        emails: new Set('muster@muster.com'),
      },
    } as TimetableHearingStatementV2;
    component.duplicate(statement);

    expect(statementShareService.statement).toBe(statement);
  });

  it('should create dossier', () => {
    const statement = {
      swissCanton: SwissCanton.Aargau,
      statement: 'This is a statement',
      statementSender: {
        emails: new Set('muster@muster.com'),
      },
    } as TimetableHearingStatementV2;
    component.createDossier(statement);
    expect(router.navigate).toHaveBeenCalledWith(
      ['..', Pages.TTH_DOSSIERS.path, 'add'],
      expect.any(Object)
    );
  });

  it('should add statement to existing dossier', () => {
    const statement = {
      swissCanton: SwissCanton.Aargau,
      statement: 'This is a statement',
      statementSender: {
        emails: new Set('muster@muster.com'),
      },
    } as TimetableHearingStatementV2;
    component.addToDossier(statement);

    expect(
      dialogService.openDialogDataWithConfirmationResult
    ).toHaveBeenCalledTimes(1);
  });

  it('should change canton via dialog', () => {
    const statement = {
      swissCanton: SwissCanton.Aargau,
      statement: 'This is a statement',
      statementSender: {
        emails: new Set('muster@muster.com'),
      },
    } as TimetableHearingStatementV2;
    component.switchCanton(statement);

    expect(
      dialogService.openDialogDataWithConfirmationResult
    ).toHaveBeenCalledTimes(1);
  });
});
