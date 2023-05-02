import { TestBed } from '@angular/core/testing';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';
import { OpenStatementInMailService } from './open-statement-in-mail.service';
import { Status, SwissCanton, TimetableFieldNumber, TimetableHearingStatement } from '../../../api';
import { AppTestingModule } from '../../../app.testing.module';

const translatePipeSpy = jasmine.createSpyObj('translatePipe', ['transform']);
translatePipeSpy.transform
  .withArgs('TTH.STATEMENT.TTFN')
  .and.returnValue('Fahrplanfeld')
  .withArgs('TTH.STATEMENT.STATEMENT')
  .and.returnValue('Stellungnahme')
  .withArgs('TTH.STATEMENT.REQUEST')
  .and.returnValue('Anfrage Stellungnahme')
  .withArgs('TTH.STATEMENT.STOP_POINT')
  .and.returnValue('Haltestelle');

describe('OpenStatementInMailService', () => {
  let openStatementInMailService: OpenStatementInMailService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [
        { provide: OpenStatementInMailService },
        { provide: TranslatePipe, useValue: translatePipeSpy },
      ],
    });

    openStatementInMailService = TestBed.inject(OpenStatementInMailService);
  });

  it('should construct mailto link with ttfn', () => {
    const statement: TimetableHearingStatement = {
      swissCanton: SwissCanton.Bern,
      statement: 'Mehr Bös pls',
      statementSender: {
        email: 'me@sbb.ch',
      },
    };
    const ttfn: TimetableFieldNumber = {
      ttfnid: 'ttfnid',
      number: '1.1',
      description: 'description',
      swissTimetableFieldNumber: 'asdf',
      status: Status.Validated,
      validFrom: new Date('2021-06-01'),
      validTo: new Date('2029-06-01'),
      businessOrganisation: 'sbb',
    };

    const mailToLink = openStatementInMailService.buildMailToLink(statement, ttfn);

    expect(mailToLink).toBe(
      'mailto:?subject=Anfrage%20Stellungnahme%20Fahrplanfeld:%201.1%20description%0D%0D&body=Fahrplanfeld:%201.1%20description%0D%0DStellungnahme:%20Mehr%20B%C3%B6s%20pls'
    );
  });

  it('should construct mailto link without ttfn', () => {
    const statement: TimetableHearingStatement = {
      swissCanton: SwissCanton.Bern,
      statement: 'Mehr Bös pls',
      statementSender: {
        email: 'me@sbb.ch',
      },
    };

    const mailToLink = openStatementInMailService.buildMailToLink(statement, undefined);

    expect(mailToLink).toBe('mailto:?body=Stellungnahme:%20Mehr%20B%C3%B6s%20pls');
  });

  it('should construct mailto link with stopplace', () => {
    const statement: TimetableHearingStatement = {
      swissCanton: SwissCanton.Bern,
      statement: 'Mehr Bös pls',
      stopPlace: 'Erste Haltestelle nach der Post',
      statementSender: {
        email: 'me@sbb.ch',
      },
    };
    const ttfn: TimetableFieldNumber = {
      ttfnid: 'ttfnid',
      number: '1.1',
      description: 'description',
      swissTimetableFieldNumber: 'asdf',
      status: Status.Validated,
      validFrom: new Date('2021-06-01'),
      validTo: new Date('2029-06-01'),
      businessOrganisation: 'sbb',
    };

    const mailToLink = openStatementInMailService.buildMailToLink(statement, ttfn);

    expect(mailToLink).toBe(
      'mailto:?subject=Anfrage%20Stellungnahme%20Fahrplanfeld:%201.1%20description%0D%0D&body=Fahrplanfeld:%201.1%20description%0D%0DHaltestelle:%20Erste%20Haltestelle%20nach%20der%20Post%0D%0DStellungnahme:%20Mehr%20B%C3%B6s%20pls'
    );
  });
});
