import { ServicePointFormComponent } from './service-point-form.component';
import SpyObj = jasmine.SpyObj;

describe('ServicePointFormComponent', () => {
  let component: ServicePointFormComponent;
  let spy: SpyObj<any>;

  beforeEach(() => {
    spy = jasmine.createSpyObj(['test']);
    component = new ServicePointFormComponent(spy, spy, spy);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should test component method setOperatingPointRouteNetwork with argument true', () => {
    component.setOperatingPointRouteNetwork(true);

    expect(component.form.controls.operatingPointRouteNetwork.value).toBe(true);
    expect(component.form.controls.operatingPointKilometer.value).toBe(true);
    expect(component.form.controls.operatingPointKilometer.disabled).toBe(true);
    expect(component.form.controls.operatingPointKilometerMaster.value).toBe(
      component.form.controls.number.value,
    );
    expect(component.form.controls.operatingPointKilometerMaster.disabled).toBe(true);
  });

  it('should test component method setOperatingPointRouteNetwork with argument false', () => {
    component.setOperatingPointRouteNetwork(false);

    expect(component.form.controls.operatingPointRouteNetwork.value).toBe(false);
    expect(component.form.controls.operatingPointKilometer.value).toBe(false);
    expect(component.form.controls.operatingPointKilometer.enabled).toBe(true);
    expect(component.form.controls.operatingPointKilometerMaster.value).toBe(null);
    expect(component.form.controls.operatingPointKilometerMaster.enabled).toBe(true);
  });

  it('should test component method setOperatingPointKilometer with argument true', () => {
    component.setOperatingPointKilometer(true);

    expect(component.form.controls.operatingPointKilometer.value).toBe(true);
  });

  it('should test component method setOperatingPointKilometer with argument false', () => {
    component.setOperatingPointKilometer(false);

    expect(component.form.controls.operatingPointKilometer.value).toBe(false);
  });
});
