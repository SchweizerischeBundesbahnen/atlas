import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';
import { TrafficPointElementsService } from '../../../api';
import { TestBed } from '@angular/core/testing';
import { AppTestingModule } from '../../../app.testing.module';
import { StopPointWorkflowDetailResolver } from './stop-point-workflow-detail-resolver.service';
import { BERN_WYLEREGG_TRAFFIC_POINTS } from '../../../../test/data/traffic-point-element';

describe('TrafficPointElementsDetailResolver', () => {
  const trafficPointServiceSpy = jasmine.createSpyObj('trafficPointElementsService', [
    'getTrafficPointElement',
  ]);
  trafficPointServiceSpy.getTrafficPointElement.and.returnValue(
    of([BERN_WYLEREGG_TRAFFIC_POINTS[0]]),
  );

  let resolver: StopPointWorkflowDetailResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        StopPointWorkflowDetailResolver,
        { provide: TrafficPointElementsService, useValue: trafficPointServiceSpy },
      ],
    });
    resolver = TestBed.inject(StopPointWorkflowDetailResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get versions from service to display', () => {
    const mockRoute = { paramMap: convertToParamMap({ id: '1000' }) } as ActivatedRouteSnapshot;

    const resolvedVersion = resolver.resolve(mockRoute);

    resolvedVersion.subscribe((versions) => {
      expect(versions.length).toBe(1);
      expect(versions[0].id).toBe(9298);
      expect(versions[0].sloid).toBe('ch:1:sloid:89008:0:1');
    });
  });
});
