import { ServicePointCreationComponent } from './service-point-creation.component';
import SpyObj = jasmine.SpyObj;
import { CoordinateTransformationService } from '../geography/coordinate-transformation.service';
import {
  ApplicationRole,
  ApplicationType,
  Country,
  CreateServicePointVersion,
  PermissionRestrictionType,
  SpatialReference,
  SwissCanton,
} from '../../../api';
import { MapService } from '../map/map.service';
import { FormControl, FormGroup } from '@angular/forms';
import { of } from 'rxjs';
import { NotificationService } from '../../../core/notification/notification.service';
import anything = jasmine.anything;
import { ServicePointFormGroupBuilder } from '../service-point-side-panel/service-point/service-point-detail-form-group';
import Spy = jasmine.Spy;
import { Router } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { Countries } from '../../../core/country/Countries';

class AuthServiceMock implements Partial<AuthService> {
  getApplicationUserPermission = jasmine.createSpy();
  isAdmin = false;
}

describe('ServicePointCreationComponent', () => {
  let component: ServicePointCreationComponent;
  let spy: SpyObj<any>;
  let coordinateTransformationServiceSpy: SpyObj<CoordinateTransformationService>;
  let mapServiceSpy: SpyObj<MapService>;
  let servicePointServiceSpy: SpyObj<any>;
  let notificationServiceSpy: SpyObj<NotificationService>;
  let routerSpy: SpyObj<Router>;
  let authServiceMock: AuthServiceMock;

  beforeEach(() => {
    spy = jasmine.createSpyObj(['mock']);
    coordinateTransformationServiceSpy = jasmine.createSpyObj<CoordinateTransformationService>([
      'isCoordinatesPairValidForTransformation',
      'transform',
    ]);
    mapServiceSpy = jasmine.createSpyObj(['placeMarkerAndFlyTo'], {
      isEditMode: { next: jasmine.createSpy() },
      isGeolocationActivated: { next: jasmine.createSpy() },
    });
    servicePointServiceSpy = jasmine.createSpyObj(['createServicePoint']);
    notificationServiceSpy = jasmine.createSpyObj(['success']);
    routerSpy = jasmine.createSpyObj(['navigate']);
    authServiceMock = new AuthServiceMock();
    component = new ServicePointCreationComponent(
      <AuthService>(<unknown>authServiceMock),
      spy,
      routerSpy,
      spy,
      mapServiceSpy,
      coordinateTransformationServiceSpy,
      servicePointServiceSpy,
      notificationServiceSpy,
    );
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should activateGeolocation', () => {
    coordinateTransformationServiceSpy.isCoordinatesPairValidForTransformation.and.returnValue(
      true,
    );
    coordinateTransformationServiceSpy.transform.and.returnValue({
      east: 2,
      north: 2,
      spatialReference: SpatialReference.Wgs84,
    });

    component.activateGeolocation({
      north: 1,
      east: 1,
      spatialReference: SpatialReference.Lv95,
    });

    expect(mapServiceSpy.placeMarkerAndFlyTo).toHaveBeenCalledOnceWith({ lat: 2, lng: 2 });
    expect(mapServiceSpy.isGeolocationActivated.next).toHaveBeenCalledOnceWith(true);
    expect(mapServiceSpy.coordinateSelectionMode.next).toHaveBeenCalledOnceWith(true);
  });

  it('should save', () => {
    (component.form as FormGroup) = new FormGroup({
      country: new FormControl(),
    });
    servicePointServiceSpy.createServicePoint.and.returnValue(
      of({
        number: {
          number: 8557385,
        },
      }),
    );

    (
      spyOn(ServicePointFormGroupBuilder, 'getWritableServicePoint') as Spy<
        () => Partial<CreateServicePointVersion>
      >
    ).and.returnValue({
      numberShort: 57385,
    });

    component.onSave();

    expect(component.form.touched).toBeTrue();
    expect(component.form.disabled).toBeTrue();
    expect(servicePointServiceSpy.createServicePoint).toHaveBeenCalledOnceWith({
      numberShort: 57385,
    });
    expect(notificationServiceSpy.success).toHaveBeenCalledOnceWith(
      'SEPODI.SERVICE_POINTS.NOTIFICATION.ADD_SUCCESS',
    );
    expect(routerSpy.navigate).toHaveBeenCalledOnceWith([8557385], anything());
  });

  it('should get country options role supervisor', () => {
    authServiceMock.getApplicationUserPermission.withArgs(ApplicationType.Sepodi).and.returnValue({
      role: ApplicationRole.Supervisor,
      application: ApplicationType.Sepodi,
      permissionRestrictions: [],
    });

    const countries = component['getCountryOptions']();

    expect(countries).toEqual([
      Country.Switzerland,
      Country.GermanyBus,
      Country.AustriaBus,
      Country.ItalyBus,
      Country.FranceBus,
      ...Countries.filteredCountries().sort(Countries.compareFn),
    ]);
  });

  it('should get country options role admin', () => {
    authServiceMock.getApplicationUserPermission.withArgs(ApplicationType.Sepodi).and.returnValue({
      role: ApplicationRole.Reader,
      application: ApplicationType.Sepodi,
      permissionRestrictions: [],
    });
    authServiceMock.isAdmin = true;

    const countries = component['getCountryOptions']();

    expect(countries).toEqual([
      Country.Switzerland,
      Country.GermanyBus,
      Country.AustriaBus,
      Country.ItalyBus,
      Country.FranceBus,
      ...Countries.filteredCountries().sort(Countries.compareFn),
    ]);
  });

  it('should get country options role super user', () => {
    authServiceMock.getApplicationUserPermission.withArgs(ApplicationType.Sepodi).and.returnValue({
      role: ApplicationRole.SuperUser,
      application: ApplicationType.Sepodi,
      permissionRestrictions: [
        { type: PermissionRestrictionType.Country, valueAsString: Country.Cuba },
        { type: PermissionRestrictionType.Country, valueAsString: Country.FranceBus },
        { type: PermissionRestrictionType.Canton, valueAsString: SwissCanton.Uri },
      ],
    });

    const countries = component['getCountryOptions']();

    expect(countries).toEqual([Country.FranceBus, Country.Cuba]);
  });

  it('should get country options role writer', () => {
    authServiceMock.getApplicationUserPermission.withArgs(ApplicationType.Sepodi).and.returnValue({
      role: ApplicationRole.Writer,
      application: ApplicationType.Sepodi,
      permissionRestrictions: [
        { type: PermissionRestrictionType.Country, valueAsString: Country.Cuba },
        { type: PermissionRestrictionType.Country, valueAsString: Country.FranceBus },
        { type: PermissionRestrictionType.Canton, valueAsString: SwissCanton.Uri },
      ],
    });

    const countries = component['getCountryOptions']();

    expect(countries).toEqual([Country.FranceBus, Country.Cuba]);
  });
});
