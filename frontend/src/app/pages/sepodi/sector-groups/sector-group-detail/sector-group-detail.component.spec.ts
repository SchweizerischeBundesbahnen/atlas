import { ComponentFixture, TestBed } from '@angular/core/testing';

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
import {
  BERN_PLATFORM_1_SECTOR_A,
  BERN_PLATFORM_1_SECTOR_MULTIPLE,
} from '../../../../../test/data/sector';
import { MapService } from '../../map/map.service';
import { ContainerReadSectorVersion } from '../../../../api/model/containerReadSectorVersion';

describe('SectorGroupDetailComponent', () => {
  let component: SectorGroupDetailComponent;
  let fixture: ComponentFixture<SectorGroupDetailComponent>;

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

  const validityService = jasmine.createSpyObj<ValidityService>([
    'initValidity',
    'updateValidity',
    'validate',
  ]);
  validityService.validate.and.returnValue(of(true));

  const sectorInternalService = jasmine.createSpyObj<SectorInternalService>([
    'getSectors',
  ]);
  const sectorOverview: ContainerReadSectorVersion = {
    objects: [],
    totalCount: 0,
  };

  const sectorGroupService = jasmine.createSpyObj<SectorGroupService>([
    'createSectorGroup',
    'updateSectorGroup',
    'getSectorsBySectorGroupSloid',
  ]);

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
      sectorInternalService.getSectors.and.returnValue(of(sectorOverview));
      sectorGroupService.getSectorsBySectorGroupSloid.and.returnValue(of([]));
      fixture.detectChanges();
    });

    it('should enter new', () => {
      expect(component.isNew).toBeTrue();
    });

    it('should go back to sectorGroup overview', () => {
      spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
      component.back();

      expect(router.navigate).toHaveBeenCalledWith(['..'], jasmine.any(Object));
    });

    it('should save new sector group', () => {
      spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
      sectorGroupService.createSectorGroup.and.returnValue(
        of(CREATE_BERN_PLATFORM_1_SECTORGROUP_A[0])
      );
      sectorGroupService.getSectorsBySectorGroupSloid.and.returnValue(
        of(BERN_PLATFORM_1_SECTOR_MULTIPLE)
      );

      component.form.controls.designation.setValue('A');
      component.form.controls.validFrom.setValue(
        moment('31.10.2000', 'dd.MM.yyyy')
      );
      component.form.controls.validTo.setValue(
        moment('31.10.2099', 'dd.MM.yyyy')
      );
      component.form.controls.sectorSloids!.setValue([
        'ch:1:sloid:7000:1:1:1',
        'ch:1:sloid:7000:1:1:2',
      ]);
      component.save();

      expect(sectorGroupService.createSectorGroup).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(
        ['..', 'ch:1:sloid:7000:1:1:5'],
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
          sectorGroup: BERN_PLATFORM_1_SECTORGROUP_A,
        }),
      };
      setupTestBed(activatedRouteMock);
      router = TestBed.inject(Router);
      fixture = TestBed.createComponent(SectorGroupDetailComponent);
      component = fixture.componentInstance;
      sectorInternalService.getSectors.and.returnValue(of(sectorOverview));
      sectorGroupService.getSectorsBySectorGroupSloid.and.returnValue(of([]));

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

    it('should update sector group', () => {
      expect(component.isNew).toBeFalse();

      spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
      sectorGroupService.updateSectorGroup.and.returnValue(
        of(BERN_PLATFORM_1_SECTORGROUP_A)
      );
      sectorGroupService.getSectorsBySectorGroupSloid.and.returnValue(
        of(BERN_PLATFORM_1_SECTOR_A)
      );
      component.toggleEdit();
      component.form.controls.designation.setValue('AAA');

      component.save();

      expect(sectorGroupService.updateSectorGroup).toHaveBeenCalled();
      expect(router.navigate).toHaveBeenCalledWith(
        ['..', 'ch:1:sloid:7000:1:1:5'],
        jasmine.any(Object)
      );
    });
  });

  async function setupTestBed(activatedRoute: ActivatedRouteMockType) {
    return TestBed.configureTestingModule({
      imports: [AppTestingModule, SectorGroupDetailComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: SectorMapService, useValue: sectorMapService },
        { provide: TrafficPointMapService, useValue: trafficPointMapService },
        { provide: SectorGroupService, useValue: sectorGroupService },
        { provide: SectorInternalService, useValue: sectorInternalService },
        { provide: MapService, useValue: mapService },
      ],
    })
      .overrideComponent(SectorGroupDetailComponent, {
        set: {
          providers: [
            { provide: ValidityService, useValue: validityService },
            TranslatePipe,
          ],
        },
      })
      .compileComponents();
  }
});
