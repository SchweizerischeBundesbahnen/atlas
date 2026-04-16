import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
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
import { SectorInternalService } from '../../../../api/service/sepodi/sector-internal.service';
import { DialogService } from '../../../../core/components/dialog/dialog.service';

describe('SectorDetailComponent', () => {
  let component: SectorDetailComponent;
  let fixture: ComponentFixture<SectorDetailComponent>;
  let router: Router;

  let sectorMapServiceSpy: Mocked<
    Pick<
      SectorMapService,
      'displaySectorsOnMap' | 'clearDisplayedSectors' | 'displayCurrentSector' | 'clearCurrentSector'
    >
  >;
  let trafficPointMapServiceSpy: Mocked<
    Pick<
      TrafficPointMapService,
      | 'displayTrafficPointsOnMap'
      | 'displayCurrentTrafficPoint'
      | 'clearDisplayedTrafficPoints'
      | 'clearCurrentTrafficPoint'
    >
  >;
  let mapServiceSpy: Mocked<
    Pick<MapService, 'placeMarkerAndFlyTo' | 'enterCoordinateSelectionMode' | 'exitCoordinateSelectionMode'>
  > & {
    mapInitialized: BehaviorSubject<boolean>;
    clickedGeographyCoordinates: Subject<CoordinatePairWGS84>;
  };
  let validityServiceSpy: Mocked<Pick<ValidityService, 'initValidity' | 'updateValidity' | 'validate'>>;
  let sectorServiceSpy: Mocked<Pick<SectorService, 'createSector' | 'updateSector'>>;
  let sectorInternalServiceSpy: Mocked<Pick<SectorInternalService, 'revokeSector'>>;
  let dialogServiceSpy: Mocked<Pick<DialogService, 'openDialogDataWithConfirmationResult'>>;

  function setupTestBed(activatedRoute: ActivatedRouteMockType) {
    Element.prototype.scrollIntoView = vi.fn();

    sectorMapServiceSpy = {
      displaySectorsOnMap: vi.fn(),
      clearDisplayedSectors: vi.fn(),
      displayCurrentSector: vi.fn(),
      clearCurrentSector: vi.fn(),
    };
    trafficPointMapServiceSpy = {
      displayTrafficPointsOnMap: vi.fn(),
      displayCurrentTrafficPoint: vi.fn(),
      clearDisplayedTrafficPoints: vi.fn(),
      clearCurrentTrafficPoint: vi.fn(),
    };
    mapServiceSpy = {
      placeMarkerAndFlyTo: vi.fn(),
      enterCoordinateSelectionMode: vi.fn(),
      exitCoordinateSelectionMode: vi.fn(),
      mapInitialized: new BehaviorSubject<boolean>(true),
      clickedGeographyCoordinates: new Subject<CoordinatePairWGS84>(),
    };
    validityServiceSpy = {
      initValidity: vi.fn(),
      updateValidity: vi.fn(),
      validate: vi.fn(),
    };
    validityServiceSpy.validate.mockReturnValue(of(true));

    sectorServiceSpy = {
      createSector: vi.fn(),
      updateSector: vi.fn(),
    };
    sectorInternalServiceSpy = {
      revokeSector: vi.fn(),
    };
    sectorInternalServiceSpy.revokeSector.mockReturnValue(of(undefined));

    dialogServiceSpy = {
      openDialogDataWithConfirmationResult: vi.fn(),
    };
    dialogServiceSpy.openDialogDataWithConfirmationResult.mockReturnValue(of(true));

    return TestBed.configureTestingModule({
      imports: [AppTestingModule, SectorDetailComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: SectorMapService, useValue: sectorMapServiceSpy },
        {
          provide: TrafficPointMapService,
          useValue: trafficPointMapServiceSpy,
        },
        { provide: MapService, useValue: mapServiceSpy },
        { provide: SectorService, useValue: sectorServiceSpy },
        { provide: SectorInternalService, useValue: sectorInternalServiceSpy },
        { provide: DialogService, useValue: dialogServiceSpy },
      ],
    })
      .overrideComponent(SectorDetailComponent, {
        set: {
          providers: [{ provide: ValidityService, useValue: validityServiceSpy }, TranslatePipe],
        },
      })
      .compileComponents()
      .then();
  }

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
      expect(component.isNew).toBe(true);
      expect(component.servicePointDesignationOfficial).toBe('Bern');

      expect(mapServiceSpy.enterCoordinateSelectionMode).toHaveBeenCalled();
    });

    it('should go back to sector overview', () => {
      vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
      component.back();

      expect(router.navigate).toHaveBeenCalledWith(['..'], expect.any(Object));
    });

    it('should save new sector', () => {
      vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
      sectorServiceSpy.createSector.mockReturnValue(of(BERN_PLATFORM_1_SECTOR_A[0]));

      component.form.controls.designation.setValue('A');
      component.form.controls.validFrom.setValue(moment('31.10.2000', 'dd.MM.yyyy'));
      component.form.controls.validTo.setValue(moment('31.10.2099', 'dd.MM.yyyy'));
      component.form.controls.sectorGeolocation?.controls.north.setValue(1.2);
      component.form.controls.sectorGeolocation?.controls.east.setValue(1.2);

      component.save();

      expect(sectorServiceSpy.createSector).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['..', 'ch:1:sloid:7000:1:1:1'], expect.any(Object));
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
      expect(component.isNew).toBe(false);
      expect(component.selectedVersion).toBeTruthy();

      expect(component.selectedVersion.designation).toEqual('A');
      expect(component.maxValidity.validFrom).toEqual(new Date('2014-12-14'));
      expect(component.maxValidity.validTo).toEqual(new Date('2099-12-31'));

      expect(component.servicePointDesignationOfficial).toBe('Bern');
    });

    it('should toggle edit mode', () => {
      expect(component.form.enabled).toBe(false);

      component.toggleEdit();
      expect(component.form.enabled).toBe(true);

      component.toggleEdit();
      expect(component.form.enabled).toBe(false);
    });

    it('should update sector', () => {
      vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
      sectorServiceSpy.updateSector.mockReturnValue(of(BERN_PLATFORM_1_SECTOR_A));

      component.toggleEdit();
      component.form.controls.designation.setValue('AAA');

      component.save();

      expect(sectorServiceSpy.updateSector).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['..', 'ch:1:sloid:7000:1:1:1'], expect.any(Object));
    });

    it('should revoke sector', () => {
      vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));

      component.revoke();

      expect(sectorInternalServiceSpy.revokeSector).toHaveBeenCalled();
    });
  });
});
