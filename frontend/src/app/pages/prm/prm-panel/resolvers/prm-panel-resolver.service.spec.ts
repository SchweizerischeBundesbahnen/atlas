import { TestBed } from '@angular/core/testing';

import { of } from 'rxjs';
import { AppTestingModule } from '../../../../app.testing.module';
import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';
import { BERN_WYLEREGG } from '../../../sepodi/service-point-test-data';
import { ServicePointsService } from '../../../../api';
import { PrmOverviewResolver } from './prm-panel-resolver.service';

describe('PrmOverviewResolver', () => {
  const servicePointsServiceSpy = jasmine.createSpyObj('servicePointsService', [
    'getServicePointVersionsBySloid',
  ]);
  servicePointsServiceSpy.getServicePointVersionsBySloid.and.returnValue(of([BERN_WYLEREGG]));

  let resolver: PrmOverviewResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        PrmOverviewResolver,
        {
          provide: ServicePointsService,
          useValue: servicePointsServiceSpy,
        },
      ],
    });
    resolver = TestBed.inject(PrmOverviewResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get version from service to display', () => {
    const mockRoute = {
      paramMap: convertToParamMap({ sloid: 'ch:1:sloid:89008' }),
    } as ActivatedRouteSnapshot;

    const resolvedVersion = resolver.resolve(mockRoute);

    resolvedVersion.subscribe((versions) => {
      expect(versions.length).toBe(1);
      expect(versions[0].sloid).toBe('ch:1:sloid:89008');
    });
  });
});
