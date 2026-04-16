import { TestBed } from '@angular/core/testing';
import { describe, expect, it, beforeEach, vi, type Mocked } from 'vitest';

import { OpenBoDossierInMailService } from './open-bo-dossier-in-mail.service';
import { AppTestingModule } from '../../../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { translateServiceProvider } from '../../../../../app.testing.mocks';
import { SwissCanton, TimetableHearingStatementV2 } from '../../../../../api';
import { TimetableHearingStatementInternalService } from '../../../../../api/service/lidi/timetable-hearing-statement-internal.service';
import { of } from 'rxjs';

const translatePipeSpy: Mocked<Pick<TranslatePipe, 'transform'>> = {
  transform: vi.fn((key: string | null | undefined) => {
    switch (key) {
      case 'TTH.DOSSIER.INQUIRY_FROM_THE_CANTON':
        return 'Rückfrage des Kantons';
      case 'TTH.DOSSIER.ID_AND_TOPIC':
        return 'Dossier-ID und Thema';
      case 'TTH.DOSSIER.STATEMENTS':
        return 'Stellungnahmen des Dossiers';
      case 'TTH.CANTON.BE':
        return 'Bern';
      case 'PAGES.TTH.TITLE_MENU':
        return 'Fahrplananhörung';
      default:
        return key;
    }
  }),
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
    getStatement: vi.fn(() => of(statementWithAnonymizedStatement)),
  };

describe('OpenBoDossierInMailService', () => {
  let service: OpenBoDossierInMailService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        translateServiceProvider,
        {
          provide: TimetableHearingStatementInternalService,
          useValue: timetableHearingStatementInternalService,
        },
        { provide: TranslatePipe, useValue: translatePipeSpy },
      ],
    })
      .compileComponents()
      .then();

    service = TestBed.inject(OpenBoDossierInMailService);
  });

  it('should open mailto link', () => {
    const windowOpenSpy = vi.spyOn(window, 'open').mockImplementation(() => null);

    service.openDossierInMail({
      id: 0,
      statementIds: [1],
      topic: 'Thema',
      cantonQuestion: 'Frage an das Transportunternehmen',
      swissCanton: 'BERN',
    });

    const result =
      'mailto:?subject=Fahrplananh%C3%B6rung%20-%20Dossier%200%20-%20%22Thema%22&body=Dossier-ID%20und%20Thema%3A%0A0%20-%20%22Thema%22%0A%0AR%C3%BCckfrage%20des%20Kantons%20Bern%3A%0AFrage%20an%20das%20Transportunternehmen%0A%0AStellungnahmen%20des%20Dossiers%3A%0A%5B123%5D%3A%20This%20is%20an%20anonymized%20statement%0A%0A';
    expect(windowOpenSpy).toHaveBeenCalledWith(result, '_self');
  });
});
