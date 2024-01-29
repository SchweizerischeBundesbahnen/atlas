import { ServicePointCreationComponent } from './service-point-creation.component';
import {
  ApplicationRole,
  ApplicationType,
  Country,
  CreateServicePointVersion,
  PermissionRestrictionType,
  ServicePointsService,
  SwissCanton,
} from '../../../../../api';
import { FormControl, FormGroup } from '@angular/forms';
import { of } from 'rxjs';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { ServicePointFormGroupBuilder } from '../service-point-detail-form-group';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../../../core/auth/auth.service';
import { Countries } from '../../../../../core/country/Countries';
import { TestBed } from '@angular/core/testing';
import { MapService } from '../../../map/map.service';
import SpyObj = jasmine.SpyObj;
import anything = jasmine.anything;
import Spy = jasmine.Spy;

class AuthServiceMock implements Partial<AuthService> {
  getApplicationUserPermission = jasmine.createSpy();
  isAdmin = false;
}

const mapServiceSpy = jasmine.createSpyObj('MapService', ['refreshMap']);

describe('ServicePointCreationComponent', () => {
  let component: ServicePointCreationComponent;
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  let spy: SpyObj<any>;
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  let servicePointServiceSpy: SpyObj<any>;
  let notificationServiceSpy: SpyObj<NotificationService>;
  let routerSpy: SpyObj<Router>;
  let authServiceMock: AuthServiceMock;

  beforeEach(() => {
    servicePointServiceSpy = jasmine.createSpyObj(['createServicePoint']);
    notificationServiceSpy = jasmine.createSpyObj(['success']);
    routerSpy = jasmine.createSpyObj(['navigate']);
    routerSpy.navigate.and.returnValue(Promise.resolve(true));
    authServiceMock = new AuthServiceMock();

    TestBed.configureTestingModule({
      providers: [
        ServicePointCreationComponent,
        {
          provide: AuthService,
          useValue: authServiceMock,
        },
        {
          provide: ActivatedRoute,
          useValue: spy,
        },
        {
          provide: ServicePointsService,
          useValue: servicePointServiceSpy,
        },
        {
          provide: NotificationService,
          useValue: notificationServiceSpy,
        },
        {
          provide: Router,
          useValue: routerSpy,
        },
        { provide: MapService, useValue: mapServiceSpy },
      ],
    });

    component = TestBed.inject(ServicePointCreationComponent);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
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
