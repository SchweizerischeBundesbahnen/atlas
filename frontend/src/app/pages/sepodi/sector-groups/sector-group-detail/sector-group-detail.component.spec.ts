import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { SectorGroupDetailComponent } from './sector-group-detail.component';
import { ActivatedRoute, Router } from '@angular/router';
import { SectorMapService } from '../../map/sector-map.service';
import { TrafficPointMapService } from '../../map/traffic-point-map.service';
import { of } from 'rxjs';
import { ValidityService } from '../../validity/validity.service';
import { SectorInternalService } from '../../../../api/service/sepodi/sector-internal.service';
import { BERN_TRAFFIC_POINT_PLATFORM_1 } from '../../../../../test/data/traffic-point-element';
import { BERN } from '../../../../../test/data/service-point';
import moment from 'moment';
import { ActivatedRouteMockType } from '../../../../app.testing.mocks';
import { AppTestingModule } from '../../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { SectorGroupService } from '../../../../api/service/sepodi/sector-group.service';
import {
  BERN_PLATFORM_1_SECTORGROUP_A,
  CREATE_BERN_PLATFORM_1_SECTORGROUP_A,
} from '../../../../../test/data/sectorgroup';
import { BERN_PLATFORM_1_SECTOR_A, BERN_PLATFORM_1_SECTOR_MULTIPLE } from '../../../../../test/data/sector';
import { MapService } from '../../map/map.service';
import { ContainerReadSectorVersion } from '../../../../api/model/containerReadSectorVersion';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { SectorGroupInternalService } from '../../../../api/service/sepodi/sector-group-internal.service';

describe('SectorGroupDetailComponent', () => {
  let component: SectorGroupDetailComponent;
  let fixture: ComponentFixture<SectorGroupDetailComponent>;
  let router: Router;

  let sectorMapServiceSpy: Mocked<
    Pick<
      SectorMapService,
      | 'displaySectorsOnMap'
      | 'clearDisplayedSectors'
      | 'displayCurrentSector'
      | 'clearCurrentSector'
      | 'setDisplayedSectors'
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
  >;
  let validityServiceSpy: Mocked<Pick<ValidityService, 'initValidity' | 'updateValidity' | 'validate'>>;
  let sectorInternalServiceSpy: Mocked<Pick<SectorInternalService, 'getSectors'>>;
  let sectorGroupServiceSpy: Mocked<
    Pick<SectorGroupService, 'createSectorGroup' | 'updateSectorGroup' | 'getSectorsBySectorGroupSloid'>
  >;
  let sectorGroupInternalServiceSpy: Mocked<Pick<SectorGroupInternalService, 'revokeSectorGroup'>>;
  let dialogServiceSpy: Mocked<Pick<DialogService, 'openDialogDataWithConfirmationResult'>>;

  const sectorOverview: ContainerReadSectorVersion = {
    objects: [],
    totalCount: 0,
  };

  function setupTestBed(activatedRoute: ActivatedRouteMockType) {
    Element.prototype.scrollIntoView = vi.fn();

    sectorMapServiceSpy = {
      displaySectorsOnMap: vi.fn(),
      clearDisplayedSectors: vi.fn(),
      displayCurrentSector: vi.fn(),
      clearCurrentSector: vi.fn(),
      setDisplayedSectors: vi.fn(),
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
    };
    validityServiceSpy = {
      initValidity: vi.fn(),
      updateValidity: vi.fn(),
      validate: vi.fn(),
    };
    validityServiceSpy.validate.mockReturnValue(of(true));

    sectorInternalServiceSpy = {
      getSectors: vi.fn(),
    };
    sectorGroupServiceSpy = {
      createSectorGroup: vi.fn(),
      updateSectorGroup: vi.fn(),
      getSectorsBySectorGroupSloid: vi.fn(),
    };
    sectorGroupInternalServiceSpy = {
      revokeSectorGroup: vi.fn(),
    };
    sectorGroupInternalServiceSpy.revokeSectorGroup.mockReturnValue(of(undefined));

    dialogServiceSpy = {
      openDialogDataWithConfirmationResult: vi.fn(),
    };
    dialogServiceSpy.openDialogDataWithConfirmationResult.mockReturnValue(of(true));

    return TestBed.configureTestingModule({
      imports: [AppTestingModule, SectorGroupDetailComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: SectorMapService, useValue: sectorMapServiceSpy },
        {
          provide: TrafficPointMapService,
          useValue: trafficPointMapServiceSpy,
        },
        { provide: SectorGroupService, useValue: sectorGroupServiceSpy },
        { provide: SectorInternalService, useValue: sectorInternalServiceSpy },
        { provide: MapService, useValue: mapServiceSpy },
        {
          provide: SectorGroupInternalService,
          useValue: sectorGroupInternalServiceSpy,
        },
        { provide: DialogService, useValue: dialogServiceSpy },
      ],
    })
      .overrideComponent(SectorGroupDetailComponent, {
        set: {
          providers: [{ provide: ValidityService, useValue: validityServiceSpy }, TranslatePipe],
        },
      })
      .compileComponents();
  }

  describe('new mode', () => {
    beforeEach(() => {
      const activatedRouteMock = {
        data: of({
          trafficPoint: BERN_TRAFFIC_POINT_PLATFORM_1,
          servicePoint: BERN,
          sectorGroup: [],
        }),
      };
      setupTestBed(activatedRouteMock);
      router = TestBed.inject(Router);
      fixture = TestBed.createComponent(SectorGroupDetailComponent);
      component = fixture.componentInstance;
      sectorInternalServiceSpy.getSectors.mockReturnValue(of(sectorOverview));
      sectorGroupServiceSpy.getSectorsBySectorGroupSloid.mockReturnValue(of([]));
      fixture.detectChanges();
    });

    it('should enter new', () => {
      expect(component.isNew).toBe(true);
    });

    it('should go back to sectorGroup overview', () => {
      vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
      component.back();

      expect(router.navigate).toHaveBeenCalledWith(['..'], expect.any(Object));
    });

    it('should save new sector group', () => {
      vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
      sectorGroupServiceSpy.createSectorGroup.mockReturnValue(of(CREATE_BERN_PLATFORM_1_SECTORGROUP_A[0]));
      sectorGroupServiceSpy.getSectorsBySectorGroupSloid.mockReturnValue(of(BERN_PLATFORM_1_SECTOR_MULTIPLE));

      component.form.controls.designation.setValue('A');
      component.form.controls.validFrom.setValue(moment('31.10.2000', 'dd.MM.yyyy'));
      component.form.controls.validTo.setValue(moment('31.10.2099', 'dd.MM.yyyy'));
      component.form.controls.sectorSloids!.setValue(['ch:1:sloid:7000:1:1:1', 'ch:1:sloid:7000:1:1:2']);
      component.save();

      expect(sectorGroupServiceSpy.createSectorGroup).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['..', 'ch:1:sloid:7000:1:1:5'], expect.any(Object));
    });
  });

  describe('edit mode', () => {
    beforeEach(() => {
      const activatedRouteMock = {
        data: of({
          trafficPoint: BERN_TRAFFIC_POINT_PLATFORM_1,
          servicePoint: BERN,
          sectorGroup: BERN_PLATFORM_1_SECTORGROUP_A,
        }),
      };
      setupTestBed(activatedRouteMock);
      router = TestBed.inject(Router);
      fixture = TestBed.createComponent(SectorGroupDetailComponent);
      component = fixture.componentInstance;
      sectorInternalServiceSpy.getSectors.mockReturnValue(of(sectorOverview));
      sectorGroupServiceSpy.getSectorsBySectorGroupSloid.mockReturnValue(of([]));

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

    it('should update sector group', () => {
      expect(component.isNew).toBe(false);

      vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
      sectorGroupServiceSpy.updateSectorGroup.mockReturnValue(of(BERN_PLATFORM_1_SECTORGROUP_A));
      sectorGroupServiceSpy.getSectorsBySectorGroupSloid.mockReturnValue(of(BERN_PLATFORM_1_SECTOR_A));
      component.toggleEdit();
      component.form.controls.designation.setValue('AAA');

      component.save();

      expect(sectorGroupServiceSpy.updateSectorGroup).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(['..', 'ch:1:sloid:7000:1:1:5'], expect.any(Object));
    });

    it('should revoke sector group', () => {
      vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));

      component.revoke();

      expect(sectorGroupInternalServiceSpy.revokeSectorGroup).toHaveBeenCalled();
    });
  });
});
