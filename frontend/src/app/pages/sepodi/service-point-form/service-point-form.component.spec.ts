import { ServicePointFormComponent } from './service-point-form.component';
import SpyObj = jasmine.SpyObj;
import { AbstractControl, FormControl, FormGroup } from '@angular/forms';
import { SpatialReference } from '../../../api';
import { GeographyFormGroup } from '../geography/geography-form-group';

describe('ServicePointFormComponent', () => {
  let component: ServicePointFormComponent;
  let spy: SpyObj<any>;

  beforeEach(() => {
    spy = jasmine.createSpyObj(['mock']);
    component = new ServicePointFormComponent(spy, spy, spy);
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

  it('hasGeolocation false', () => {
    const spatialRefCtrl = new FormControl(SpatialReference.Lv95);
    (
      spyOnProperty<ServicePointFormComponent, 'spatialRefCtrl'>(
        component,
        'spatialRefCtrl',
        'get',
      ) as jasmine.Spy<(this: ServicePointFormComponent) => AbstractControl>
    ).and.returnValue(spatialRefCtrl);
    spyOn(component.geolocationToggleChange, 'emit');

    component.onGeolocationToggleChange(false);

    expect(component.spatialRefCtrl?.value).toBe(null);
    expect(component.geolocationToggleChange.emit).toHaveBeenCalledOnceWith();
  });

  it('hasGeolocation true', () => {
    const locationCtrls = {
      east: new FormControl(1),
      north: new FormControl(1),
    };
    const spatialRefCtrl = new FormControl(SpatialReference.Wgs84);
    (
      spyOnProperty<ServicePointFormComponent, 'spatialRefCtrl'>(
        component,
        'spatialRefCtrl',
        'get',
      ) as jasmine.Spy<(this: ServicePointFormComponent) => AbstractControl>
    ).and.returnValue(spatialRefCtrl);
    (
      spyOnProperty<ServicePointFormComponent, 'locationControls'>(
        component,
        'locationControls',
        'get',
      ) as jasmine.Spy<(this: ServicePointFormComponent) => Partial<GeographyFormGroup>>
    ).and.returnValue(locationCtrls);
    spyOn(component.geolocationToggleChange, 'emit');

    component.onGeolocationToggleChange(true);

    expect(component.spatialRefCtrl?.value).toBe(SpatialReference.Lv95);
    expect(component.geolocationToggleChange.emit).toHaveBeenCalledOnceWith({
      north: 1,
      east: 1,
      spatialReference: undefined!,
    });
  });
});
