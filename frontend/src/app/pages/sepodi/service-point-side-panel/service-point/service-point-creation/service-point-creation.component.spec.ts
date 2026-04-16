import { beforeEach, describe, expect, it, vi, type Mocked } from 'vitest';
import { ServicePointCreationComponent } from './service-point-creation.component';
import {
  ApplicationRole,
  ApplicationType,
  Country,
  CreateServicePointVersion,
  PermissionRestrictionType,
  SwissCanton,
} from '../../../../../api';
import { FormControl, FormGroup } from '@angular/forms';
import { of } from 'rxjs';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { ServicePointFormGroupBuilder } from '../service-point-form/form-group/service-point-detail-form-group';
import { ActivatedRoute, Router } from '@angular/router';
import { Countries } from '../../../../../core/country/Countries';
import { TestBed } from '@angular/core/testing';
import { MapService } from '../../../map/map.service';
import { PermissionService } from '../../../../../core/auth/permission/permission.service';
import { ServicePointService } from '../../../../../api/service/sepodi/service-point.service';

class PermissionServiceMock implements Partial<PermissionService> {
  getApplicationUserPermission = vi.fn();
  isAdmin = false;
}

describe('ServicePointCreationComponent', () => {
  let component: ServicePointCreationComponent;
  let servicePointServiceSpy: Mocked<Pick<ServicePointService, 'createServicePoint'>>;
  let notificationServiceSpy: Mocked<Pick<NotificationService, 'success'>>;
  let routerSpy: Mocked<Pick<Router, 'navigate'>>;
  let mapServiceSpy: Mocked<Pick<MapService, 'refreshMap'>>;
  let permissionServiceMock: PermissionServiceMock;

  beforeEach(() => {
    servicePointServiceSpy = { createServicePoint: vi.fn() };
    notificationServiceSpy = { success: vi.fn() };
    routerSpy = { navigate: vi.fn() };
    routerSpy.navigate.mockReturnValue(Promise.resolve(true));
    mapServiceSpy = { refreshMap: vi.fn() };
    permissionServiceMock = new PermissionServiceMock();

    TestBed.configureTestingModule({
      providers: [
        ServicePointCreationComponent,
        {
          provide: PermissionService,
          useValue: permissionServiceMock,
        },
        {
          provide: ActivatedRoute,
          useValue: {},
        },
        {
          provide: ServicePointService,
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
    servicePointServiceSpy.createServicePoint.mockReturnValue(
      of({
        number: {
          number: 8557385,
        },
      } as ReturnType<ServicePointService['createServicePoint']> extends import('rxjs').Observable<infer T> ? T : never)
    );

    vi.spyOn(ServicePointFormGroupBuilder.mapper, 'getWritableServicePoint').mockReturnValue({
      numberShort: 57385,
    } as Partial<CreateServicePointVersion> as CreateServicePointVersion);

    component.onSave();

    expect(component.form.touched).toBe(true);
    expect(component.form.disabled).toBe(true);
    expect(servicePointServiceSpy.createServicePoint).toHaveBeenCalledWith({
      numberShort: 57385,
    });
    expect(notificationServiceSpy.success).toHaveBeenCalledWith('SEPODI.SERVICE_POINTS.NOTIFICATION.ADD_SUCCESS');
    expect(routerSpy.navigate).toHaveBeenCalledWith([8557385], expect.anything());
  });

  it('should get country options role supervisor', () => {
    permissionServiceMock.getApplicationUserPermission.mockReturnValue({
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
    permissionServiceMock.getApplicationUserPermission.mockReturnValue({
      role: ApplicationRole.Reader,
      application: ApplicationType.Sepodi,
      permissionRestrictions: [],
    });
    permissionServiceMock.isAdmin = true;

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
    permissionServiceMock.getApplicationUserPermission.mockReturnValue({
      role: ApplicationRole.SuperUser,
      application: ApplicationType.Sepodi,
      permissionRestrictions: [
        {
          type: PermissionRestrictionType.Country,
          valueAsString: Country.Cuba,
        },
        {
          type: PermissionRestrictionType.Country,
          valueAsString: Country.FranceBus,
        },
        {
          type: PermissionRestrictionType.Canton,
          valueAsString: SwissCanton.Uri,
        },
      ],
    });

    const countries = component['getCountryOptions']();

    expect(countries).toEqual([Country.FranceBus, Country.Cuba]);
  });

  it('should get country options role writer', () => {
    permissionServiceMock.getApplicationUserPermission.mockReturnValue({
      role: ApplicationRole.Writer,
      application: ApplicationType.Sepodi,
      permissionRestrictions: [
        {
          type: PermissionRestrictionType.Country,
          valueAsString: Country.Cuba,
        },
        {
          type: PermissionRestrictionType.Country,
          valueAsString: Country.FranceBus,
        },
        {
          type: PermissionRestrictionType.Canton,
          valueAsString: SwissCanton.Uri,
        },
      ],
    });

    const countries = component['getCountryOptions']();

    expect(countries).toEqual([Country.FranceBus, Country.Cuba]);
  });
});
