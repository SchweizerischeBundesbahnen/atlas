import { TestBed } from '@angular/core/testing';
import { describe, expect, it, beforeEach, vi, type Mocked } from 'vitest';
import { ActivatedRouteSnapshot, convertToParamMap, Router, RouterStateSnapshot } from '@angular/router';
import { DossierDetailResolver, dossierResolver } from './dossier-detail-resolver.service';
import { firstValueFrom, Observable, of, throwError } from 'rxjs';
import { TthDossier } from '../../../../api/model/tthDossier';
import { DossierInternalService } from '../../../../api/service/workflow/dossier-internal.service';
import { HearingStatus, SwissCanton } from '../../../../api';

const dossier: TthDossier = {
  swissCanton: SwissCanton.Bern,
  boContactMail: 'info@bls.ch',
  boDeadlineToAnswer: new Date('2014-12-14'),
  questions: [{ question: 'Habt ihr mehr Busse?' }],
  statementIds: [1000],
  id: 1234,
  topic: 'Mehr Busse bitte',
};

describe('DossierDetailResolver', () => {
  const dossierInternalService: Mocked<Pick<DossierInternalService, 'getDossier'>> = {
    getDossier: vi.fn(),
  };
  const router: Mocked<Pick<Router, 'navigate'>> = {
    navigate: vi.fn().mockResolvedValue(true),
  };

  let resolver: DossierDetailResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        DossierDetailResolver,
        {
          provide: DossierInternalService,
          useValue: dossierInternalService,
        },
        {
          provide: Router,
          useValue: router,
        },
      ],
    });
    resolver = TestBed.inject(DossierDetailResolver);

    dossierInternalService.getDossier.mockReturnValue(of(dossier));
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get dossier from service', async () => {
    const mockRoute = {
      paramMap: convertToParamMap({ id: '1234' }),
    } as ActivatedRouteSnapshot;
    mockRoute.data = { hearingStatus: HearingStatus.Archived };

    const result = TestBed.runInInjectionContext(() =>
      dossierResolver(mockRoute, {} as RouterStateSnapshot)
    ) as Observable<TthDossier | undefined>;

    const statement = await firstValueFrom(result);
    expect(statement).toBeTruthy();
    expect(statement!.id).toBe(1234);
  });

  it('should be undefined on add', async () => {
    const mockRoute = {
      paramMap: convertToParamMap({ id: 'add' }),
    } as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() =>
      dossierResolver(mockRoute, {} as RouterStateSnapshot)
    ) as Observable<TthDossier | undefined>;

    const statement = await firstValueFrom(result);
    expect(statement).toBeUndefined();
  });

  it('should route on error', async () => {
    dossierInternalService.getDossier.mockReturnValue(throwError(() => 'Dossier not found'));

    const mockRoute = {
      paramMap: convertToParamMap({ id: '1234' }),
    } as ActivatedRouteSnapshot;
    mockRoute.data = { hearingStatus: HearingStatus.Archived };

    const result = TestBed.runInInjectionContext(() =>
      dossierResolver(mockRoute, {} as RouterStateSnapshot)
    ) as Observable<TthDossier | undefined>;
    const statement = await firstValueFrom(result);
    expect(statement).toBeUndefined();
    expect(router.navigate).toHaveBeenCalledTimes(1);
  });
});
