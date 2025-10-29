import { ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';
import { TestBed } from '@angular/core/testing';
import { AppTestingModule } from '../../../../app.testing.module';
import { SectorDetailResolver } from './sector-detail-resolver.service';
import { SpatialReference } from '../../../../api';
import { SectorService } from '../../../../api/service/sepodi/sector.service';

describe('SectorDetailResolver', () => {
  const sectorService = jasmine.createSpyObj('sectorService', ['getSector']);

  let resolver: SectorDetailResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        SectorDetailResolver,
        {
          provide: SectorService,
          useValue: sectorService,
        },
      ],
    });
    resolver = TestBed.inject(SectorDetailResolver);

    sectorService.getSector.and.returnValue(
      of([
        {
          trafficPointSloid: 'ch:1:sloid:7000::1',
          validFrom: new Date('2014-12-14'),
          validTo: new Date('2014-12-14'),
          designation: 'A',
          sectorGeolocation: {
            lv95: {
              north: 0,
              east: 0,
              spatialReference: SpatialReference.Lv95,
            },
            spatialReference: 'WGS84WEB',
            wgs84: {
              north: 0,
              east: 0,
              spatialReference: SpatialReference.Wgs84,
            },
            lv03: {
              north: 0,
              east: 0,
              spatialReference: SpatialReference.Lv03,
            },
          },
          sloid: 'ch:1:sloid:7000::1:1',
        },
      ])
    );
  });

  it('should get versions from service to display', () => {
    const mockRoute = {
      paramMap: convertToParamMap({
        sectorSloid: 'ch:1:sloid:7000::1:1',
      }),
    } as unknown as ActivatedRouteSnapshot;

    const resolvedVersion = resolver.resolve(mockRoute);

    resolvedVersion.subscribe((versions) => {
      expect(versions.length).toBe(1);
      expect(versions[0].sloid).toBe('ch:1:sloid:7000::1:1');
    });
  });
});
