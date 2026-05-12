import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { ServicePointFormComponent } from './service-point-form.component';
import {
  ApplicationRole,
  ApplicationType,
  CoordinatePair,
  Country,
  MeanOfTransport,
  Permission,
  PermissionRestrictionType,
  ReadServicePointVersion,
  SpatialReference,
  StopPointType,
  SwissCanton,
} from '../../../../../api';
import { Component, output } from '@angular/core';
import { GeographyComponent } from '../../../geography/geography.component';
import { EMPTY, firstValueFrom, of } from 'rxjs';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormGroup } from '@angular/forms';
import { TranslationSortingService } from '../../../../../core/translation/translation-sorting.service';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { PermissionService } from '../../../../../core/auth/permission/permission.service';
import { LocationGeoInternalService } from '../../../../../api/service/location/location-geo-internal.service';
import { ServicePointFormGroupBuilder } from './form-group/service-point-detail-form-group';
import { BERN_WYLEREGG } from '../../../../../../test/data/service-point';
import { StationGroup } from './form-group/station-form-group';

@Component({
  template: ``,
})
class MockGeographyComponent {
  readonly coordinatesChanged = output<CoordinatePair>();
}

describe('ServicePointFormComponent', () => {
  let component: ServicePointFormComponent;
  let fixture: ComponentFixture<ServicePointFormComponent>;

  let translationSortingServiceSpy: Mocked<
    Pick<TranslationSortingService, 'sort'> & {
      translateService: {
        onLangChange: { subscribe: ReturnType<typeof vi.fn> };
      };
    }
  >;
  let dialogServiceSpy: Mocked<Pick<DialogService, 'openDialogDataWithConfirmationResult'>>;
  let geoDataServiceSpy: Mocked<Pick<LocationGeoInternalService, 'getLocationInformation'>>;

  let isAdmin = true;
  let permission = {} as Permission;
  const permissionServiceMock: Partial<PermissionService> = {
    get isAdmin() {
      return isAdmin;
    },
    getApplicationUserPermission: () => permission,
  };

  beforeEach(() => {
    translationSortingServiceSpy = {
      sort: vi.fn(),
      translateService: { onLangChange: { subscribe: vi.fn() } },
    };
    dialogServiceSpy = {
      openDialogDataWithConfirmationResult: vi.fn(),
    };
    dialogServiceSpy.openDialogDataWithConfirmationResult.mockReturnValue(of(true));
    geoDataServiceSpy = {
      getLocationInformation: vi.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        {
          provide: TranslationSortingService,
          useValue: translationSortingServiceSpy,
        },
        { provide: DialogService, useValue: dialogServiceSpy },
        {
          provide: LocationGeoInternalService,
          useValue: geoDataServiceSpy,
        },
        { provide: PermissionService, useValue: permissionServiceMock },
      ],
    });

    fixture = TestBed.createComponent(ServicePointFormComponent);
    component = fixture.componentInstance;
  });

  it('should update locationInformation when coordinates changed', async () => {
    component['_currentVersion'] = { id: 5 } as ReadServicePointVersion;
    component.geographyComponent = TestBed.createComponent(MockGeographyComponent)
      .componentInstance as GeographyComponent;

    const coordinatePair = {
      spatialReference: SpatialReference.Lv95,
      north: 5,
      east: 6,
    };

    geoDataServiceSpy.getLocationInformation.mockReturnValue(
      of({
        country: Country.Cuba,
        swissCanton: SwissCanton.Aargau,
        swissMunicipalityName: 'Gemeinde',
        swissLocalityName: 'Ort',
      })
    );

    component.ngOnInit();

    component.geographyComponent.coordinatesChanged.emit(coordinatePair);

    const locationInformation = await firstValueFrom(component.locationInformation$!);
    expect(locationInformation.canton).toEqual(SwissCanton.Aargau);
    expect(locationInformation.isoCountryCode).toEqual('CU');
    expect(locationInformation.municipalityName).toEqual('Gemeinde');
    expect(locationInformation.localityName).toEqual('Ort');
  });

  it('should show all bos on edit', () => {
    component['_currentVersion'] = { id: 5 } as ReadServicePointVersion;
    component.ngOnInit();

    expect(component.isNew).toBe(false);
    expect(component.boSboidRestriction).toHaveLength(0);
  });

  it('should show all bos new for admin', () => {
    isAdmin = true;
    component.ngOnInit();

    expect(component.isNew).toBe(true);
    expect(component.boSboidRestriction).toHaveLength(0);
  });

  it('should show only allowed bos on new for writer', () => {
    isAdmin = false;
    permission = {
      role: ApplicationRole.Writer,
      application: ApplicationType.Sepodi,
      permissionRestrictions: [
        {
          type: PermissionRestrictionType.BusinessOrganisation,
          valueAsString: 'ch:1:sboid:213',
        },
      ],
    };

    component.ngOnInit();

    expect(component.isNew).toBe(true);
    expect(component.boSboidRestriction).toHaveLength(1);
  });

  it('should select is StopPoint OnDemand', () => {
    //given
    component.form = ServicePointFormGroupBuilder.buildFormGroup(BERN_WYLEREGG, EMPTY);
    //when
    component.onStopPointChange(StopPointType.OnDemand);
    //then
    const meansOfTransportForm = (component.form?.controls?.spTypeGroup as FormGroup<StationGroup>).controls
      .stopPointGroup?.controls.meansOfTransport;
    expect(component.isMeanOfTransportOnDemandSelected).toBe(true);
    expect(meansOfTransportForm?.value).toHaveLength(1);
    expect(meansOfTransportForm?.value).toEqual([MeanOfTransport.OnDemand]);
  });

  it('should not select is StopPoint OnDemand', () => {
    //given
    component.form = ServicePointFormGroupBuilder.buildFormGroup(BERN_WYLEREGG, EMPTY);
    //when
    component.onStopPointChange(StopPointType.Orderly);
    //then
    const meansOfTransportForm = (component.form?.controls?.spTypeGroup as FormGroup<StationGroup>).controls
      .stopPointGroup?.controls.meansOfTransport;
    expect(component.isMeanOfTransportOnDemandSelected).toBe(false);
    expect(meansOfTransportForm?.value).not.toEqual([MeanOfTransport.OnDemand]);
  });
});
