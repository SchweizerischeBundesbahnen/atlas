import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';
import { Status } from '../../../api';
import { TestBed } from '@angular/core/testing';
import { AppTestingModule } from '../../../app.testing.module';
import { ServicePointDetailResolver } from './service-point-detail.resolver';
import { BERN_WYLEREGG } from '../../../../test/data/service-point';
import { ServicePointService } from '../../../api/service/sepodi/service-point.service';

describe('ServicePointDetailResolver', () => {
  const servicePointServiceSpy = jasmine.createSpyObj('servicePointsService', [
    'getServicePointVersions',
  ]);
  servicePointServiceSpy.getServicePointVersions.and.returnValue(
    of([BERN_WYLEREGG])
  );

  let resolver: ServicePointDetailResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        ServicePointDetailResolver,
        { provide: ServicePointService, useValue: servicePointServiceSpy },
      ],
    });
    resolver = TestBed.inject(ServicePointDetailResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get version from service to display', () => {
    const mockRoute = {
      paramMap: convertToParamMap({ id: '1000' }),
    } as ActivatedRouteSnapshot;

    const resolvedVersion = resolver.resolve(mockRoute);

    resolvedVersion.subscribe((versions) => {
      expect(versions.length).toBe(1);
      expect(versions[0].id).toBe(1000);
      expect(versions[0].status).toBe(Status.Validated);
      expect(versions[0].sloid).toBe('ch:1:sloid:89008');
    });
  });
});
