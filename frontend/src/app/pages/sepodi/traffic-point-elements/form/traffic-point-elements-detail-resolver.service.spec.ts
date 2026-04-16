import { ActivatedRouteSnapshot, convertToParamMap, Router } from '@angular/router';
import { firstValueFrom, of } from 'rxjs';
import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { AppTestingModule } from '../../../../app.testing.module';
import { TrafficPointElementsDetailResolver } from './traffic-point-elements-detail-resolver.service';
import { BERN_WYLEREGG_TRAFFIC_POINTS } from '../../../../../test/data/traffic-point-element';
import { TrafficPointElementService } from '../../../../api/service/sepodi/traffic-point-element.service';
import { TrafficPointElementType } from '../../../../api';
import { Pages } from '../../../pages';
import { mock, mockDeep } from 'vitest-mock-extended';

describe('TrafficPointElementsDetailResolver', () => {
  const trafficPointElementService = mock<TrafficPointElementService>();

  let resolver: TrafficPointElementsDetailResolver;
  let router: Router;

  beforeEach(() => {
    trafficPointElementService.getTrafficPointElement.mockReturnValue(of([BERN_WYLEREGG_TRAFFIC_POINTS[0]]));

    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        TrafficPointElementsDetailResolver,
        {
          provide: TrafficPointElementService,
          useValue: trafficPointElementService,
        },
      ],
    });
    resolver = TestBed.inject(TrafficPointElementsDetailResolver);
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get versions from service to display', async () => {
    const mockRoute = mockDeep<ActivatedRouteSnapshot>({
      data: {
        isTrafficPointArea: false,
      },
      paramMap: convertToParamMap({
        trafficPointSloid: 'ch:1:sloid:89008:0:1',
      }),
    });

    const resolvedVersion = resolver.resolve(mockRoute);

    const versions = await firstValueFrom(resolvedVersion);
    expect(versions.length).toBe(1);
    expect(versions[0].id).toBe(9298);
    expect(versions[0].sloid).toBe('ch:1:sloid:89008:0:1');
  });

  it('should navigate to area if type is area but route is trafficPointElements', async () => {
    trafficPointElementService.getTrafficPointElement.mockReturnValue(
      of([
        {
          id: 9298,
          designationOperational: '1',
          compassDirection: 53.0,
          trafficPointElementType: TrafficPointElementType.BoardingArea,
          sloid: 'ch:1:sloid:89008:0:1',
          validFrom: new Date('2019-07-22'),
          validTo: new Date('2099-12-31'),
          servicePointNumber: {
            number: 8589008,
            uicCountryCode: 85,
            numberShort: 89008,
            checkDigit: 7,
          },
          servicePointSloid: 'ch:1:sloid:89008',
          hasGeolocation: false,
        },
      ])
    );

    const mockRoute = mockDeep<ActivatedRouteSnapshot>({
      data: {
        isTrafficPointArea: false,
      },
      paramMap: convertToParamMap({
        servicePointNumber: 8589008,
        trafficPointSloid: 'ch:1:sloid:89008:1',
      }),
    });

    vi.spyOn(router, 'navigate').mockImplementation(() => Promise.resolve(true));

    await firstValueFrom(resolver.resolve(mockRoute));
    expect(router.navigate).toHaveBeenCalledExactlyOnceWith([
      Pages.SEPODI.path,
      Pages.SERVICE_POINTS.path,
      8589008,
      Pages.TRAFFIC_POINT_ELEMENTS_AREA.path,
      'ch:1:sloid:89008:0:1',
    ]);
  });
});
