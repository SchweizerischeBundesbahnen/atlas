import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';
import {
  HearingStatus,
  SwissCanton,
  TimetableHearingService,
  TimetableHearingStatement,
} from '../../../api';
import { StatementDetailResolver } from './statement-detail.resolver';
import { of } from 'rxjs';
import { AppTestingModule } from '../../../app.testing.module';

const statement: TimetableHearingStatement = {
  id: 1234,
  swissCanton: SwissCanton.Aargau,
  statement: 'Mehr Busse bitte',
  statementSender: {
    email: 'luca@yb.ch',
  },
};

describe('StatementDetailResolver', () => {
  const timetableHearingServiceSpy = jasmine.createSpyObj('timetableHearingService', [
    'getStatement',
  ]);
  timetableHearingServiceSpy.getStatement.and.returnValue(of(statement));

  let resolver: StatementDetailResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        StatementDetailResolver,
        { provide: TimetableHearingService, useValue: timetableHearingServiceSpy },
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
