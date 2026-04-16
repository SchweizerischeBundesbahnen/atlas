import { TestBed } from '@angular/core/testing';
import { stopPointResolver, StopPointResolver } from './stop-point.resolver';
import { firstValueFrom, Observable, of } from 'rxjs';
import { ReadStopPointVersion } from '../../../../api';
import { AppTestingModule } from '../../../../app.testing.module';
import { ServicePointDetailResolver } from '../../../sepodi/service-point-side-panel/service-point-detail.resolver';
import { ActivatedRouteSnapshot, convertToParamMap, RouterStateSnapshot } from '@angular/router';
import { STOP_POINT } from '../../util/stop-point-test-data';
import { StopPointService } from '../../../../api/service/prm/stop-point/stop-point.service';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

describe('stopPointResolver', () => {
  let resolver: StopPointResolver;
  let stopPointService: Mocked<Pick<StopPointService, 'getStopPointVersions'>>;

  beforeEach(() => {
    stopPointService = {
      getStopPointVersions: vi.fn(),
    };
    stopPointService.getStopPointVersions.mockReturnValue(of([STOP_POINT]));

    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [ServicePointDetailResolver, { provide: StopPointService, useValue: stopPointService }],
    });

    resolver = TestBed.inject(StopPointResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get version from service to display', async () => {
    const mockRoute = {
      paramMap: convertToParamMap({ stopPointSloid: 'ch:1:sloid:89008' }),
    };

    const result = TestBed.runInInjectionContext(() =>
      stopPointResolver(mockRoute as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    ) as Observable<ReadStopPointVersion[]>;

    const versions = await firstValueFrom(result);
    expect(versions.length).toBe(1);
    expect(versions[0].sloid).toBe('ch:1:sloid:89008');
  });

  it('should empty array on add', async () => {
    const mockRoute = {
      paramMap: convertToParamMap({ stopPointSloid: 'add' }),
    };

    const result = TestBed.runInInjectionContext(() =>
      stopPointResolver(mockRoute as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    ) as Observable<ReadStopPointVersion[]>;

    const versions = await firstValueFrom(result);
    expect(versions.length).toBe(0);
  });
});
