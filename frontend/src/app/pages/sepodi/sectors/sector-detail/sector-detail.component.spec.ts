import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SectorDetailComponent } from './sector-detail.component';
import { ActivatedRoute, Router } from '@angular/router';
import { SectorMapService } from '../../map/sector-map.service';
import { BehaviorSubject, of, Subject } from 'rxjs';
import { BERN_TRAFFIC_POINT_PLATFORM_1 } from '../../../../../test/data/traffic-point-element';
import { BERN } from '../../../../../test/data/service-point';
import { ActivatedRouteMockType } from '../../../../app.testing.mocks';
import { AppTestingModule } from '../../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { CoordinatePairWGS84, MapService } from '../../map/map.service';
import { TrafficPointMapService } from '../../map/traffic-point-map.service';
import { SectorService } from '../../../../api/service/sepodi/sector.service';
import { BERN_PLATFORM_1_SECTOR_A } from '../../../../../test/data/sector';
import moment from 'moment/moment';
import { ValidityService } from '../../validity/validity.service';

describe('SectorDetailComponent', () => {
  let component: SectorDetailComponent;
  let fixture: ComponentFixture<SectorDetailComponent>;

  let router: Router;

  const sectorMapService = jasmine.createSpyObj<SectorMapService>([
    'displaySectorsOnMap',
    'clearDisplayedSectors',
    'displayCurrentSector',
    'clearCurrentSector',
  ]);
  const trafficPointMapService = jasmine.createSpyObj<TrafficPointMapService>([
    'displayTrafficPointsOnMap',
    'displayCurrentTrafficPoint',
    'clearDisplayedTrafficPoints',
    'clearCurrentTrafficPoint',
  ]);
  const mapService = jasmine.createSpyObj<MapService>([
    'placeMarkerAndFlyTo',
    'enterCoordinateSelectionMode',
    'exitCoordinateSelectionMode',
  ]);
  mapService.mapInitialized = new BehaviorSubject<boolean>(true);
  mapService.clickedGeographyCoordinates = new Subject<CoordinatePairWGS84>();

  const validityService = jasmine.createSpyObj<ValidityService>([
    'initValidity',
    'updateValidity',
    'validate',
  ]);
  validityService.validate.and.returnValue(of(true));

  const sectorService = jasmine.createSpyObj<SectorService>([
    'createSector',
    'updateSector',
  ]);

  describe('new mode', () => {
    beforeEach(() => {
      const activatedRouteMock = {
        data: of({
          trafficPoint: BERN_TRAFFIC_POINT_PLATFORM_1,
          servicePoint: BERN,
          sector: [],
        }),
      };
      setupTestBed(activatedRouteMock);
      router = TestBed.inject(Router);
      fixture = TestBed.createComponent(SectorDetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should enter new with coordinate selection mode', () => {
      expect(component.isNew).toBeTrue();
      expect(component.servicePointDesignationOfficial).toBe('Bern');

      expect(mapService.enterCoordinateSelectionMode).toHaveBeenCalled();
    });

    it('should go back to sector overview', () => {
      spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
      component.back();

      expect(router.navigate).toHaveBeenCalledWith(['..'], jasmine.any(Object));
    });

    it('should save new sector', () => {
      spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
      sectorService.createSector.and.returnValue(
        of(BERN_PLATFORM_1_SECTOR_A[0])
      );

      component.form.controls.designation.setValue('A');
      component.form.controls.validFrom.setValue(
        moment('31.10.2000', 'dd.MM.yyyy')
      );
      component.form.controls.validTo.setValue(
        moment('31.10.2099', 'dd.MM.yyyy')
      );
      component.form.controls.sectorGeolocation?.controls.north.setValue(1.2);
      component.form.controls.sectorGeolocation?.controls.east.setValue(1.2);

      component.save();

      expect(sectorService.createSector).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(
        ['..', 'ch:1:sloid:7000:1:1:1'],
        jasmine.any(Object)
      );
    });
  });

  describe('edit mode', () => {
    beforeEach(() => {
      const activatedRouteMock = {
        data: of({
          trafficPoint: BERN_TRAFFIC_POINT_PLATFORM_1,
          servicePoint: BERN,
          sector: BERN_PLATFORM_1_SECTOR_A,
        }),
      };
      setupTestBed(activatedRouteMock);
      router = TestBed.inject(Router);
      fixture = TestBed.createComponent(SectorDetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should display current designation and validity', () => {
      expect(component.isNew).toBeFalse();
      expect(component.selectedVersion).toBeTruthy();

      expect(component.selectedVersion.designation).toEqual('A');
      expect(component.maxValidity.validFrom).toEqual(new Date('2014-12-14'));
      expect(component.maxValidity.validTo).toEqual(new Date('2099-12-31'));

      expect(component.servicePointDesignationOfficial).toBe('Bern');
    });

    it('should toggle edit mode', () => {
      expect(component.form.enabled).toBeFalse();

      component.toggleEdit();
      expect(component.form.enabled).toBeTrue();

      component.toggleEdit();
      expect(component.form.enabled).toBeFalse();
    });

    it('should update sector', () => {
      spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
      sectorService.updateSector.and.returnValue(of(BERN_PLATFORM_1_SECTOR_A));

      component.toggleEdit();
      component.form.controls.designation.setValue('AAA');

      component.save();

      expect(sectorService.updateSector).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(
        ['..', 'ch:1:sloid:7000:1:1:1'],
        jasmine.any(Object)
      );
    });
  });

  function setupTestBed(activatedRoute: ActivatedRouteMockType) {
    return TestBed.configureTestingModule({
      imports: [AppTestingModule, SectorDetailComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: SectorMapService, useValue: sectorMapService },
        { provide: TrafficPointMapService, useValue: trafficPointMapService },
        { provide: MapService, useValue: mapService },
        { provide: SectorService, useValue: sectorService },
      ],
    })
      .overrideComponent(SectorDetailComponent, {
        set: {
          providers: [
            { provide: ValidityService, useValue: validityService },
            TranslatePipe,
          ],
        },
      })
      .compileComponents()
      .then();
  }
});
