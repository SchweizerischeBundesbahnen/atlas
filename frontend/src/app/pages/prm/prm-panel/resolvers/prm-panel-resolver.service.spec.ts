import { TestBed } from '@angular/core/testing';
import { firstValueFrom, Observable, of } from 'rxjs';
import { AppTestingModule } from '../../../../app.testing.module';
import { ActivatedRouteSnapshot, convertToParamMap, RouterStateSnapshot } from '@angular/router';
import { ReadServicePointVersion } from '../../../../api';
import { prmPanelResolver, PrmPanelResolver } from './prm-panel-resolver.service';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';
import { ServicePointService } from '../../../../api/service/sepodi/service-point.service';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

describe('PrmOverviewResolver', () => {
  let resolver: PrmPanelResolver;
  let servicePointService: Mocked<Pick<ServicePointService, 'getServicePointVersionsBySloid'>>;

  beforeEach(() => {
    servicePointService = {
      getServicePointVersionsBySloid: vi.fn(),
    };
    servicePointService.getServicePointVersionsBySloid.mockReturnValue(of([BERN_WYLEREGG]));

    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [PrmPanelResolver, { provide: ServicePointService, useValue: servicePointService }],
    });

    resolver = TestBed.inject(PrmPanelResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get version from service to display', async () => {
    const mockRoute = {
      paramMap: convertToParamMap({ stopPointSloid: 'ch:1:sloid:89008' }),
    };

    const result = TestBed.runInInjectionContext(() =>
      prmPanelResolver(mockRoute as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
    ) as Observable<ReadServicePointVersion[]>;

    const versions = await firstValueFrom(result);
    expect(versions.length).toBe(1);
    expect(versions[0].sloid).toBe('ch:1:sloid:89008');
  });
});
