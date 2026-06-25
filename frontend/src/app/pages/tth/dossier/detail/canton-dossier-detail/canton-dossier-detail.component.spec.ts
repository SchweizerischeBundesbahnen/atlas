import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { CantonDossierDetailComponent } from './canton-dossier-detail.component';
import { ActivatedRoute, Router } from '@angular/router';
import { SwissCanton, TimetableHearingStatementV2 } from '../../../../../api';
import { of } from 'rxjs';
import { TimetableHearingStatementInternalService } from '../../../../../api/service/lidi/timetable-hearing-statement-internal.service';
import { TthDossier } from '../../../../../api/model/tthDossier';
import { DossierInternalService } from '../../../../../api/service/workflow/dossier-internal.service';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { ActivatedRouteMockType } from '../../../../../app.testing.mocks';
import { DossierStatus } from '../../../../../api/model/dossierStatus';
import { FormatPipe } from '../../../../../core/components/table/pipe/format.pipe';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { OpenCantonDossierInMailService } from './open-canton-dossier-in-mail.service';
import { mock, mockClear } from 'vitest-mock-extended';
import { AppTestingModule } from '../../../../../app.testing.module';

const statement: TimetableHearingStatementV2 = {
  id: 456,
  swissCanton: SwissCanton.Bern,
  timetableYear: 2023,
  statement: 'Mehr Bös pls',
  statementSender: {
    emails: new Set('me@sbb.ch'),
  },
  documents: [],
};
const timetableHearingStatementInternalService: Mocked<Pick<TimetableHearingStatementInternalService, 'getStatement'>> =
  {
    getStatement: vi.fn().mockReturnValue(of(statement)),
  };
const dossier: TthDossier = {
  swissCanton: SwissCanton.Bern,
  boContactMail: 'info@bls.ch',
  boDeadlineToAnswer: new Date('2014-12-14'),
  questions: [{ question: 'Habt ihr mehr Busse?' }],
  statementIds: [1000],
  id: 1234,
  topic: 'Mehr Busse bitte',
  dossierStatus: DossierStatus.Added,
};
const dossierInternalService: Mocked<
  Pick<DossierInternalService, 'createDossier' | 'updateDossier' | 'sendDossierToBo' | 'completeDossier'>
> = {
  createDossier: vi.fn().mockReturnValue(of(dossier)),
  updateDossier: vi.fn().mockReturnValue(of(dossier)),
  sendDossierToBo: vi.fn().mockReturnValue(of(undefined)),
  completeDossier: vi.fn().mockReturnValue(of(undefined)),
};

const notificationService: Mocked<Pick<NotificationService, 'success'>> = {
  success: vi.fn(),
};
const openDossierInMailService: Mocked<Pick<OpenCantonDossierInMailService, 'openDossierInMailClient'>> = {
  openDossierInMailClient: vi.fn(),
};
const dialogService = mock<DialogService>();
dialogService.openDialogDataWithConfirmationResult.mockReturnValue(of(true));
dialogService.openDialogDataWithCustomResult.mockReturnValue(of([1, 2]));

let router: Mocked<Pick<Router, 'navigate'>>;

describe('DossierDetailComponent', () => {
  let component: CantonDossierDetailComponent;
  let fixture: ComponentFixture<CantonDossierDetailComponent>;

  describe('on create', () => {
    beforeEach(() => {
      const activatedRoute = {
        snapshot: {
          data: {
            dossier: undefined,
          },
        },
      };
      setupTestBed(activatedRoute);
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should load new mode', () => {
      expect(component.swissCanton).toBe(SwissCanton.Bern);
      expect(component.timetableHearingYear).toBe(2023);

      expect(component.isNew).toBe(true);
      expect(component.form.enabled).toBe(true);
    });

    it('should save dossier', () => {
      component.form.controls.topic.setValue('Takt Bern Salem');
      component.selectedStatements = [456];

      component.save();
      expect(dossierInternalService.createDossier).toHaveBeenCalledTimes(1);
      expect(notificationService.success).toHaveBeenCalledTimes(1);
    });

    it('should open statement select dialog', () => {
      component.openAddStatementsDialog();

      expect(dialogService.openDialogDataWithCustomResult).toHaveBeenCalledTimes(1);
      expect(component.selectedStatements).toEqual([1, 2]);
    });
  });

  describe('on edit', () => {
    beforeEach(() => {
      const activatedRoute = {
        snapshot: {
          data: {
            dossier: dossier,
          },
        },
      };
      setupTestBed(activatedRoute);

      mockClear(dialogService);
    });

    it('should create', () => {
      expect(component).toBeTruthy();
      expect(component.isNew).toBe(false);

      expect(component.currentDossier).toBeDefined();
      expect(component.isEditable).toBe(true);
      expect(component.isSendableToBo).toBe(true);
    });

    it('should toggle edit', () => {
      expect(component.form.enabled).toBe(false);

      component.toggleEdit();
      expect(component.form.enabled).toBe(true);
      expect(component.form.controls.answerToCanton.enabled).toBe(false);

      component.toggleEdit();
      expect(component.form.enabled).toBe(false);
    });

    it('should open internal feedback mail', () => {
      component.openInternalFeedbackMail();
      expect(openDossierInMailService.openDossierInMailClient).toHaveBeenCalledTimes(1);
    });

    it('should not enable question fields if answer already here', () => {
      component.form.controls.answerToCanton.setValue('We have more buses');

      component.toggleEdit();
      expect(component.form.controls.question.enabled).toBe(false);
      expect(component.form.controls.boContactMail.enabled).toBe(false);
      expect(component.form.controls.boDeadlineToAnswer.enabled).toBe(false);
    });

    it('should update', () => {
      expect(component.form.enabled).toBe(false);
      component.toggleEdit();
      expect(component.form.enabled).toBe(true);

      component.form.controls.topic.setValue('updated topic');
      component.save();

      expect(dossierInternalService.updateDossier).toHaveBeenCalledTimes(1);
    });

    it('should send to bo', () => {
      component.sendToBo();

      expect(dossierInternalService.sendDossierToBo).toHaveBeenCalledTimes(1);
    });

    it('should complete to bo', () => {
      component.completeDossier(DossierStatus.Canceled);

      expect(dialogService.openDialogDataWithConfirmationResult).toHaveBeenCalledTimes(1);
      expect(dossierInternalService.completeDossier).toHaveBeenCalledTimes(1);
    });

    it('should go back', () => {
      component.back();

      expect(router.navigate).toHaveBeenCalledTimes(1);
    });
  });

  function setupTestBed(activatedRoute: ActivatedRouteMockType) {
    router = {
      navigate: vi.fn().mockResolvedValue(true),
    };
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: activatedRoute,
        },
        {
          provide: TimetableHearingStatementInternalService,
          useValue: timetableHearingStatementInternalService,
        },
        {
          provide: DossierInternalService,
          useValue: dossierInternalService,
        },
        {
          provide: NotificationService,
          useValue: notificationService,
        },
        {
          provide: DialogService,
          useValue: dialogService,
        },
        {
          provide: Router,
          useValue: router,
        },
        {
          provide: FormatPipe,
          useValue: { transform: () => 'transformed mock' },
        },
      ],
    }).overrideComponent(CantonDossierDetailComponent, {
      remove: {
        providers: [OpenCantonDossierInMailService],
      },
      add: {
        providers: [
          {
            provide: OpenCantonDossierInMailService,
            useValue: openDossierInMailService,
          },
        ],
      },
    });

    fixture = TestBed.createComponent(CantonDossierDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }
});
