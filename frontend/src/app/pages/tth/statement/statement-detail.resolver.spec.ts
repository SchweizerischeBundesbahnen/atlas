import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';
import {
  HearingStatus,
  SwissCanton,
  TimetableHearingStatementV2,
} from '../../../api';
import { StatementDetailResolver } from './statement-detail.resolver';
import { AppTestingModule } from '../../../app.testing.module';
import { of } from 'rxjs';
import { TimetableHearingStatementInternalService } from '../../../api/service/timetable-hearing-statement-internal.service';

const statement: TimetableHearingStatementV2 = {
  id: 1234,
  swissCanton: SwissCanton.Aargau,
  statement: 'Mehr Busse bitte',
  statementSender: {
    emails: new Set('luca@yb.ch'),
  },
};

describe('StatementDetailResolver', () => {
  const timetableHearingStatementsServiceSpy = jasmine.createSpyObj(
    'TimetableHearingStatementInternalService',
    ['getStatement']
  );
  timetableHearingStatementsServiceSpy.getStatement.and.returnValue(
    of(statement)
  );

  let resolver: StatementDetailResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        StatementDetailResolver,
        {
          provide: TimetableHearingStatementInternalService,
          useValue: timetableHearingStatementsServiceSpy,
        },
      ],
    });
    resolver = TestBed.inject(StatementDetailResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get statement from service', () => {
    const mockRoute = {
      paramMap: convertToParamMap({ id: '1234' }),
    } as ActivatedRouteSnapshot;
    mockRoute.data = { hearingStatus: HearingStatus.Archived };
    const statement = resolver.resolve(mockRoute);

    statement.subscribe((statement) => {
      expect(statement).toBeTruthy();
      expect(statement!.id).toBe(1234);
    });
  });
});
