import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { BoDossierDetailComponent } from './bo-dossier-detail.component';
import { AppTestingModule } from '../../../../../app.testing.module';
import { TthDossier } from '../../../../../api/model/tthDossier';
import { SwissCanton, TimetableHearingStatementV2 } from '../../../../../api';
import { DossierStatus } from '../../../../../api/model/dossierStatus';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { TimetableHearingStatementInternalService } from '../../../../../api/service/lidi/timetable-hearing-statement-internal.service';
import { FormatPipe } from '../../../../../core/components/table/pipe/format.pipe';
import { DossierInternalService } from '../../../../../api/service/workflow/dossier-internal.service';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { OpenBoDossierInMailService } from './open-bo-dossier-in-mail.service';
import { mock } from 'vitest-mock-extended';

const dossier: TthDossier = {
  swissCanton: SwissCanton.Bern,
  boContactMail: 'info@bls.ch',
  boDeadlineToAnswer: new Date('2014-12-14'),
  questions: [{ question: 'Habt ihr mehr Busse?', id: 123 }],
  statementIds: [1000],
  id: 1234,
  topic: 'Mehr Busse bitte',
  dossierStatus: DossierStatus.DossierBoCheck,
};

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

describe('BoDossierDetail', () => {
  let component: BoDossierDetailComponent;
  let fixture: ComponentFixture<BoDossierDetailComponent>;

  let timetableHearingStatementInternalService: Mocked<Pick<TimetableHearingStatementInternalService, 'getStatement'>>;
  let dossierInternalService: Mocked<Pick<DossierInternalService, 'answerQuestion'>>;
  let notificationService: Mocked<Pick<NotificationService, 'success'>>;
  const openBoDossierInMailService = mock<OpenBoDossierInMailService>();

  beforeEach(async () => {
    timetableHearingStatementInternalService = {
      getStatement: vi.fn().mockReturnValue(of(statement)),
    };

    dossierInternalService = {
      answerQuestion: vi.fn().mockReturnValue(of(undefined)),
    };

    notificationService = {
      success: vi.fn(),
    };

    const activatedRoute = {
      snapshot: {
        data: {
          dossier: dossier,
        },
      },
    };

    await TestBed.configureTestingModule({
      imports: [BoDossierDetailComponent, AppTestingModule],
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
        FormatPipe,
      ],
    })
      .overrideProvider(OpenBoDossierInMailService, {
        useValue: openBoDossierInMailService,
      })
      .compileComponents();

    fixture = TestBed.createComponent(BoDossierDetailComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should sendAnswer when form is valid', () => {
    //given
    component.form.controls.answerToCanton.setValue('Ich bin einverstanden!');
    //when
    component.sendAnswer();
    //then
    expect(dossierInternalService.answerQuestion).toHaveBeenCalledTimes(1);
    expect(dossierInternalService.answerQuestion).toHaveBeenCalledWith(123, {
      answerToCanton: 'Ich bin einverstanden!',
    });
    expect(notificationService.success).toHaveBeenCalledTimes(1);
    expect(notificationService.success).toHaveBeenCalledWith('TTH.DOSSIER.NOTIFICATION.SENT_TO_CANTON');
    expect(component.form.disabled).toBe(true);
    expect(component.isDossierStatusBoCheck).toBe(false);
  });

  it('should open mailto on openInMail()', () => {
    //when
    component.openInMail();
    //then
    expect(openBoDossierInMailService.openDossierInMail).toHaveBeenCalledTimes(1);
  });
});
