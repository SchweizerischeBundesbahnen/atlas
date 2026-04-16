import { TestBed } from '@angular/core/testing';
import { describe, expect, it, beforeEach, vi, type Mocked } from 'vitest';
import { TranslatePipe } from '@ngx-translate/core';
import { OpenCantonDossierInMailService } from './open-canton-dossier-in-mail.service';
import { translateServiceProvider } from '../../../../../app.testing.mocks';
import { TimetableHearingStatementInternalService } from '../../../../../api/service/lidi/timetable-hearing-statement-internal.service';
import { SwissCanton, TimetableHearingStatementV2 } from '../../../../../api';
import { AppTestingModule } from '../../../../../app.testing.module';
import { of } from 'rxjs';

const translatePipeSpy: Mocked<Pick<TranslatePipe, 'transform'>> = {
  transform: vi.fn((key) => {
    switch (key) {
      case 'TTH.DOSSIER.TOPIC':
        return 'Thema';
      case 'TTH.DOSSIER.BO_QUESTION':
        return 'Frage an das Transportunternehmen';
      case 'TTH.DOSSIER.BO_ANSWER':
        return 'Rückmeldung des Transportunternehmens';
      case 'TTH.DOSSIER.INTERNAL_COMMENT':
        return 'Interne Begründung zum Zweck der Dokumentation';
      case 'TTH.DOSSIER.PUBLIC_COMMENT':
        return 'Öffentliche Begründung für Stellungnehmende';
      case 'TTH.DOSSIER.STATEMENTS':
        return 'Stellungnahmen des Dossiers';
      default:
        return key;
    }
  }),
};

const statementWithAnonymStatement: TimetableHearingStatementV2 = {
  id: 456,
  swissCanton: SwissCanton.Bern,
  statement: 'Mehr Bös pls',
  statementAnonymous: true,
  statementSender: {
    emails: new Set('mail@buerger.ch'),
  },
};

const statementWithAnonymizedStatement: TimetableHearingStatementV2 = {
  id: 123,
  swissCanton: SwissCanton.Bern,
  statement: 'Secret statement',
  statementAnonymous: false,
  anonymousStatement: 'This is an anonymized statement',
  statementSender: {
    emails: new Set('mail@buerger.ch'),
  },
};

const timetableHearingStatementInternalService: Mocked<Pick<TimetableHearingStatementInternalService, 'getStatement'>> =
  {
    getStatement: vi.fn((id: number) => {
      if (id === statementWithAnonymStatement.id) {
        return of(statementWithAnonymStatement);
      }
      return of(statementWithAnonymizedStatement);
    }),
  };

describe('OpenDossierInMailService', () => {
  let openDossierInMailService: OpenCantonDossierInMailService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        translateServiceProvider,
        { provide: OpenCantonDossierInMailService },
        { provide: TranslatePipe, useValue: translatePipeSpy },
        {
          provide: TimetableHearingStatementInternalService,
          useValue: timetableHearingStatementInternalService,
        },
      ],
    })
      .compileComponents()
      .then();

    openDossierInMailService = TestBed.inject(OpenCantonDossierInMailService);
  });

  it('should open mailto link', () => {
    const windowOpenSpy = vi.spyOn(window, 'open').mockImplementation(() => null);

    openDossierInMailService.openDossierInMailClient({
      topic: 'Thema',
      question: 'Frage an das Transportunternehmen',
      answer: 'Rückmeldung des Transportunternehmens',
      internalComment: 'Interne Begründung zum Zweck der Dokumentation',
      publicComment: 'Öffentliche Begründung für Stellungnehmende',
      statementIds: [456, 123],
    });

    const expectedMailToLink =
      'mailto:?subject=Dossier%20%22Thema%22&body=Thema%3A%0AThema%0A%0AFrage%20an%20das%20Transportunternehmen%3A%0AFrage%20an%20das%20Transportunternehmen%0A%0AR%C3%BCckmeldung%20des%20Transportunternehmens%3A%0AR%C3%BCckmeldung%20des%20Transportunternehmens%0A%0AInterne%20Begr%C3%BCndung%20zum%20Zweck%20der%20Dokumentation%3A%0AInterne%20Begr%C3%BCndung%20zum%20Zweck%20der%20Dokumentation%0A%0A%C3%96ffentliche%20Begr%C3%BCndung%20f%C3%BCr%20Stellungnehmende%3A%0A%C3%96ffentliche%20Begr%C3%BCndung%20f%C3%BCr%20Stellungnehmende%0A%0AStellungnahmen%20des%20Dossiers%3A%0A%5B456%5D%3A%20Mehr%20B%C3%B6s%20pls%0A%0A%5B123%5D%3A%20This%20is%20an%20anonymized%20statement%0A%0A';
    expect(windowOpenSpy).toHaveBeenCalledWith(expectedMailToLink, '_self');
  });
});
