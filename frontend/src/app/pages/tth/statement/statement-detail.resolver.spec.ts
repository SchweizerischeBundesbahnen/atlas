import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';
import { HearingStatus, SwissCanton, TimetableHearingStatementV2 } from '../../../api';
import { StatementDetailResolver } from './statement-detail.resolver';
import { AppTestingModule } from '../../../app.testing.module';
import { firstValueFrom, of } from 'rxjs';
import { TimetableHearingStatementInternalService } from '../../../api/service/lidi/timetable-hearing-statement-internal.service';
import { describe, expect, it, beforeEach, vi, type Mocked } from 'vitest';

const statement: TimetableHearingStatementV2 = {
  id: 1234,
  swissCanton: SwissCanton.Aargau,
  statement: 'Mehr Busse bitte',
  statementSender: {
    emails: new Set('fan@yb.ch'),
  },
};

describe('StatementDetailResolver', () => {
  const timetableHearingStatementsServiceSpy: Mocked<Pick<TimetableHearingStatementInternalService, 'getStatement'>> = {
    getStatement: vi.fn().mockReturnValue(of(statement)),
  };

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

  it('should get statement from service', async () => {
    const mockRoute = {
      paramMap: convertToParamMap({ id: '1234' }),
    } as ActivatedRouteSnapshot;
    mockRoute.data = { hearingStatus: HearingStatus.Archived };
    const statementObs = resolver.resolve(mockRoute);

    const statement = await firstValueFrom(statementObs);
    expect(statement).toBeTruthy();
    expect(statement!.id).toBe(1234);
  });
});
