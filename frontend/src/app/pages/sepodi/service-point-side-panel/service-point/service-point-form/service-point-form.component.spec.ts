import { ServicePointFormComponent } from './service-point-form.component';
import { FormControl, FormGroup } from '@angular/forms';
import SpyObj = jasmine.SpyObj;

describe('ServicePointFormComponent', () => {
  let component: ServicePointFormComponent;
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
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
});
