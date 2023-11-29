import { TestBed } from '@angular/core/testing';

import { Observable, of } from 'rxjs';
import { AppTestingModule } from '../../../../app.testing.module';
import { ActivatedRouteSnapshot, convertToParamMap, RouterStateSnapshot } from '@angular/router';
import { BERN_WYLEREGG } from '../../../sepodi/service-point-test-data';
import { ReadServicePointVersion, ServicePointsService } from '../../../../api';
import { prmPanelResolver, PrmPanelResolver } from './prm-panel-resolver.service';

describe('PrmOverviewResolver', () => {
  const servicePointsServiceSpy = jasmine.createSpyObj('servicePointsService', [
    'getServicePointVersionsBySloid',
  ]);
  servicePointsServiceSpy.getServicePointVersionsBySloid.and.returnValue(of([BERN_WYLEREGG]));

  let resolver: PrmPanelResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        PrmPanelResolver,
        {
          provide: ServicePointsService,
          useValue: servicePointsServiceSpy,
        },
      ],
    });
    resolver = TestBed.inject(PrmPanelResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get version from service to display', () => {
    const mockRoute = {
      paramMap: convertToParamMap({ sloid: 'ch:1:sloid:89008' }),
    } as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() =>
      prmPanelResolver(mockRoute, {} as RouterStateSnapshot),
    ) as Observable<ReadServicePointVersion[]>;

    result.subscribe((versions) => {
      expect(versions.length).toBe(1);
      expect(versions[0].sloid).toBe('ch:1:sloid:89008');
    });
  });
});
