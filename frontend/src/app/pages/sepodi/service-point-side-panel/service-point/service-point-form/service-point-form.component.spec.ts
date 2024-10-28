import { ServicePointFormComponent } from './service-point-form.component';
import {FormControl, FormGroup} from '@angular/forms';
import {
  ApplicationRole,
  ApplicationType,
  CoordinatePair,
  Country,
  PermissionRestrictionType,
  ReadServicePointVersion,
  SpatialReference,
  SwissCanton,
} from '../../../../../api';
import { EventEmitter } from '@angular/core';
import { GeographyComponent } from '../../../geography/geography.component';
import { of } from 'rxjs';
import {ServicePointDetailFormGroup, ServicePointFormGroupBuilder} from "../service-point-detail-form-group";
import {AtLeastOneValidator} from "../../../../../core/validation/boolean-cross-validator/at-least-one-validator";

describe('ServicePointFormComponent', () => {
  let component: ServicePointFormComponent;

  const translationSortingServiceSpy = jasmine.createSpyObj(['sort'], {
    translateService: { onLangChange: jasmine.createSpyObj(['subscribe']) },
  });
  const geoDataServiceSpy = jasmine.createSpyObj(['getLocationInformation']);
  const authServiceSpy = jasmine.createSpyObj(['getApplicationUserPermission']);
  authServiceSpy.isAdmin = true;
  let servicePointFormGroup: FormGroup<ServicePointDetailFormGroup>;
  const dialogServiceSpy = jasmine.createSpyObj('DialogService', ['confirm']);


  beforeEach(() => {
    dialogServiceSpy.confirm.and.returnValue(of(true));
    servicePointFormGroup = ServicePointFormGroupBuilder.buildEmptyFormGroup();

    component = new ServicePointFormComponent(
      translationSortingServiceSpy,
      dialogServiceSpy,
      geoDataServiceSpy,
      authServiceSpy
    );
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should test component method setOperatingPointRouteNetwork with argument true', () => {
    const form = new FormGroup({
      operatingPointRouteNetwork: new FormControl(),
      operatingPointKilometer: new FormControl(),
      operatingPointKilometerMaster: new FormControl(),
    });
    const currentVersion = {
      number: {
        number: 1,
      },
    };

    (
      spyOnProperty<ServicePointFormComponent, 'form'>(component, 'form', 'get') as jasmine.Spy<
        (this: ServicePointFormComponent) => FormGroup
      >
    ).and.returnValue(form);

    (
      spyOnProperty<ServicePointFormComponent, 'currentVersion'>(
        component,
        'currentVersion',
        'get',
      ) as jasmine.Spy<(this: ServicePointFormComponent) => object>
    ).and.returnValue(currentVersion);

    component.setOperatingPointRouteNetwork(true);

    expect(component.form?.controls.operatingPointRouteNetwork.value).toBe(true);
    expect(component.form?.controls.operatingPointKilometer.value).toBe(true);
    expect(component.form?.controls.operatingPointKilometer.disabled).toBe(true);
    expect(component.form?.controls.operatingPointKilometerMaster.value).toBe(1);
    expect(component.form?.controls.operatingPointKilometerMaster.disabled).toBe(true);
  });

  it('should test component method setOperatingPointRouteNetwork with argument false', () => {
    const form = new FormGroup({
      operatingPointRouteNetwork: new FormControl(),
      operatingPointKilometer: new FormControl({
        value: null,
        disabled: true,
      }),
      operatingPointKilometerMaster: new FormControl({
        value: 5,
        disabled: true,
      }),
    });
    (
      spyOnProperty<ServicePointFormComponent, 'form'>(component, 'form', 'get') as jasmine.Spy<
        (this: ServicePointFormComponent) => FormGroup
      >
    ).and.returnValue(form);

    component.setOperatingPointRouteNetwork(false);

    expect(component.form?.controls.operatingPointRouteNetwork.value).toBe(false);
    expect(component.form?.controls.operatingPointKilometer.value).toBe(false);
    expect(component.form?.controls.operatingPointKilometer.enabled).toBe(true);
    expect(component.form?.controls.operatingPointKilometerMaster.value).toBe(null);
    expect(component.form?.controls.operatingPointKilometerMaster.enabled).toBe(true);
  });

  it('should test component method setOperatingPointKilometer with argument true', () => {
    const form = new FormGroup({
      operatingPointKilometer: new FormControl(null),
    });
    (
      spyOnProperty<ServicePointFormComponent, 'form'>(component, 'form', 'get') as jasmine.Spy<
        (this: ServicePointFormComponent) => FormGroup
      >
    ).and.returnValue(form);

    component.setOperatingPointKilometer(true);

    expect(component.form?.controls.operatingPointKilometer.value).toBe(true);
  });

  it('should test component method setOperatingPointKilometer with argument false', () => {
    const form = new FormGroup({
      operatingPointKilometer: new FormControl(null),
      operatingPointKilometerMaster: new FormControl(5),
    });
    (
      spyOnProperty<ServicePointFormComponent, 'form'>(component, 'form', 'get') as jasmine.Spy<
        (this: ServicePointFormComponent) => FormGroup
      >
    ).and.returnValue(form);

    component.setOperatingPointKilometer(false);

    expect(component.form?.controls.operatingPointKilometer.value).toBe(false);
    expect(component.form?.controls.operatingPointKilometerMaster.value).toBe(null);
  });

  it('should update locationInformation when coordinates changed', (done) => {
    component['_currentVersion'] = { id: 5 } as ReadServicePointVersion;
    component.geographyComponent = {
      coordinatesChanged: new EventEmitter<CoordinatePair>(),
    } as GeographyComponent;

    const coordinatePair = {
      spatialReference: SpatialReference.Lv95,
      north: 5,
      east: 6,
    };

    geoDataServiceSpy.getLocationInformation.withArgs(coordinatePair).and.returnValue(
      of({
        country: Country.Cuba,
        swissCanton: SwissCanton.Aargau,
        swissMunicipalityName: 'Gemeinde',
        swissLocalityName: 'Ort',
      }),
    );

    component.ngOnInit();

    component.geographyComponent.coordinatesChanged.emit(coordinatePair);

    component.locationInformation$?.subscribe((locationInformation) => {
      expect(locationInformation.canton).toEqual(SwissCanton.Aargau);
      expect(locationInformation.isoCountryCode).toEqual('CU');
      expect(locationInformation.municipalityName).toEqual('Gemeinde');
      expect(locationInformation.localityName).toEqual('Ort');
      done();
    });
  });

  it('should show all bos on edit', () => {
    component['_currentVersion'] = { id: 5 } as ReadServicePointVersion;
    component.ngOnInit();

    expect(component.isNew).toBeFalse();
    expect(component.boSboidRestriction).toHaveSize(0);
  });

  it('should show all bos new for admin', () => {
    authServiceSpy.isAdmin = true;
    component.ngOnInit();

    expect(component.isNew).toBeTrue();
    expect(component.boSboidRestriction).toHaveSize(0);
  });

  it('should show only allowed bos on new for writer', () => {
    authServiceSpy.isAdmin = false;
    authServiceSpy.getApplicationUserPermission.and.returnValue({
      role: ApplicationRole.Writer,
      application: ApplicationType.Sepodi,
      permissionRestrictions: [
        {
          type: PermissionRestrictionType.BusinessOrganisation,
          valueAsString: 'ch:1:sboid:213',
        },
      ],
    });

    component.ngOnInit();

    expect(component.isNew).toBeTrue();
    expect(component.boSboidRestriction).toHaveSize(1);
  });

  it('should add AtLeastOneValidator if selectedType is StopPoint', () => {
    component.form = servicePointFormGroup;
    component.form.controls['selectedType'].setValue('STOP_POINT');
    spyOn(AtLeastOneValidator, 'of').and.callThrough();

    component.setStopPointValidator();
    expect(AtLeastOneValidator.of).toHaveBeenCalledWith('stopPoint', 'freightServicePoint');
  });
});
