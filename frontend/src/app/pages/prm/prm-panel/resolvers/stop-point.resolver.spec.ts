import { TestBed } from '@angular/core/testing';

import { stopPointResolver, StopPointResolver } from './stop-point.resolver';
import { Observable, of } from 'rxjs';
import { PersonWithReducedMobilityService, ReadStopPointVersion } from '../../../../api';
import { AppTestingModule } from '../../../../app.testing.module';
import { ServicePointDetailResolver } from '../../../sepodi/service-point-side-panel/service-point-detail.resolver';
import { ActivatedRouteSnapshot, convertToParamMap, RouterStateSnapshot } from '@angular/router';
import { STOP_POINT } from '../../util/stop-point-test-data.spec';

describe('stopPointResolver', () => {
  const personWithReducedMobilityServiceSpy = jasmine.createSpyObj(
    'personWithReducedMobilityService',
    ['getStopPointVersions'],
  );
  personWithReducedMobilityServiceSpy.getStopPointVersions.and.returnValue(of([STOP_POINT]));

  let resolver: StopPointResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        ServicePointDetailResolver,
        {
          provide: PersonWithReducedMobilityService,
          useValue: personWithReducedMobilityServiceSpy,
        },
      ],
    });
    resolver = TestBed.inject(StopPointResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get version from service to display', () => {
    const mockRoute = {
      paramMap: convertToParamMap({ sloid: 'ch:1:sloid:89008' }),
    } as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() =>
      stopPointResolver(mockRoute, {} as RouterStateSnapshot),
    ) as Observable<ReadStopPointVersion[]>;

    result.subscribe((versions) => {
      expect(versions.length).toBe(1);
      expect(versions[0].sloid).toBe('ch:1:sloid:89008');
    });
  });
});
