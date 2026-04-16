import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { firstValueFrom, of } from 'rxjs';
import { TestBed } from '@angular/core/testing';
import { AppTestingModule } from '../../../app.testing.module';
import { LoadingPointsDetailResolver } from './loading-points-detail-resolver.service';
import { LOADING_POINT } from '../../../../test/data/loading-point';
import { LoadingPointService } from '../../../api/service/sepodi/loading-point.service';

describe('LoadingPointsDetailResolver', () => {
  let loadingPointsService: Mocked<Pick<LoadingPointService, 'getLoadingPoint'>>;
  let resolver: LoadingPointsDetailResolver;

  beforeEach(() => {
    loadingPointsService = {
      getLoadingPoint: vi.fn(),
    };
    loadingPointsService.getLoadingPoint.mockReturnValue(of(LOADING_POINT));

    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [LoadingPointsDetailResolver, { provide: LoadingPointService, useValue: loadingPointsService }],
    });
    resolver = TestBed.inject(LoadingPointsDetailResolver);
  });

  it('should get versions from service to display', async () => {
    const mockRoute = {
      paramMap: convertToParamMap({
        servicePointNumber: '8504414',
        number: 1231,
      }),
    } as ActivatedRouteSnapshot;

    const resolvedVersion = resolver.resolve(mockRoute);

    const versions = await firstValueFrom(resolvedVersion);
    expect(versions.length).toBe(2);
    expect(versions[0].number).toBe(1231);
    expect(versions[0].id).toBe(1255);
  });
});
