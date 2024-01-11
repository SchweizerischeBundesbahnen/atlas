import { TestBed } from '@angular/core/testing';

import { Observable, of } from 'rxjs';
import { AppTestingModule } from '../../../../app.testing.module';
import { ActivatedRouteSnapshot, convertToParamMap, RouterStateSnapshot } from '@angular/router';
import { ReadTrafficPointElementVersion, TrafficPointElementsService } from '../../../../api';
import { trafficPointElementResolver } from './traffic-point-element.resolver';
import { BERN_WYLEREGG_TRAFFIC_POINTS } from '../../../../../test/data/traffic-point-element';

describe('TrafficPointElementResolver', () => {
  const trafficPointElementsService = jasmine.createSpyObj('trafficPointElementsService', [
    'getTrafficPointElement',
  ]);
  trafficPointElementsService.getTrafficPointElement.and.returnValue(
    of(BERN_WYLEREGG_TRAFFIC_POINTS),
  );

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        {
          provide: TrafficPointElementsService,
          useValue: trafficPointElementsService,
        },
      ],
    });
  });

  it('should get traffic point from sepodi', () => {
    const mockRoute = {
      paramMap: convertToParamMap({ platformSloid: 'ch:1:sloid:89008:0:1' }),
    } as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() =>
      trafficPointElementResolver(mockRoute, {} as RouterStateSnapshot),
    ) as Observable<ReadTrafficPointElementVersion[]>;

    result.subscribe((versions) => {
      expect(versions.length).toBe(2);
      expect(versions[0].sloid).toBe('ch:1:sloid:89008:0:1');
    });
  });
});
