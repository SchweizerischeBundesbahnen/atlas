import { FormGroup } from '@angular/forms';
import {
  ServicePointDetailFormGroup,
  ServicePointFormGroupBuilder,
} from './service-point-detail-form-group';
import { ServicePointType } from './service-point-type';
import {
  Country,
  CreateServicePointVersion,
  MeanOfTransport,
  OperatingPointType,
} from '../../../../api';
import moment from 'moment';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';

describe('ServicePointFormGroup', () => {
  let servicePointFormGroup: FormGroup<ServicePointDetailFormGroup>;

  beforeEach(() => {
    servicePointFormGroup = ServicePointFormGroupBuilder.buildEmptyFormGroup();
    servicePointFormGroup.enable();

    servicePointFormGroup.controls.number.setValue(7000);
    servicePointFormGroup.controls.country?.setValue(Country.Switzerland);
    servicePointFormGroup.controls.businessOrganisation.setValue('ch:1:yb:best');
    servicePointFormGroup.controls.designationOfficial.setValue('YB Stadion');
    servicePointFormGroup.controls.validFrom.setValue(moment(new Date(2000 - 1 - 1)));
    servicePointFormGroup.controls.validTo.setValue(moment(new Date(2099 - 10 - 1)));
    servicePointFormGroup.controls.operatingPointRouteNetwork.setValue(true);
    servicePointFormGroup.controls.operatingPointKilometerMaster.setValue(7000);
  });

  it('should add validators to include one of stopPoint, freightServicePoint. stopPoints needs meansOfTransport', () => {
    servicePointFormGroup.controls.selectedType.setValue(ServicePointType.StopPoint);

    expect(servicePointFormGroup.valid).toBeFalse();

    servicePointFormGroup.controls.stopPoint.setValue(true);
    servicePointFormGroup.controls.meansOfTransport.setValue([MeanOfTransport.Bus]);
    expect(servicePointFormGroup.valid).toBeTrue();
  });

  it('should add validators to include one of stopPoint, freightServicePoint. freightServicePoint needs nothing', () => {
    servicePointFormGroup.controls.selectedType.setValue(ServicePointType.StopPoint);

    expect(servicePointFormGroup.valid).toBeFalse();

    servicePointFormGroup.controls.freightServicePoint.setValue(true);
    expect(servicePointFormGroup.valid).toBeTrue();
  });

  it('should remove oneOf Validator on change to FareStop', () => {
    servicePointFormGroup.controls.selectedType.setValue(ServicePointType.StopPoint);
    servicePointFormGroup.controls.selectedType.setValue(ServicePointType.FareStop);

    expect(servicePointFormGroup.valid).toBeTrue();
  });

  it('should require operatingPointType for OperatingPoint', () => {
    servicePointFormGroup.controls.selectedType.setValue(ServicePointType.OperatingPoint);

    expect(servicePointFormGroup.valid).toBeFalse();

    servicePointFormGroup.controls.operatingPointType.setValue(OperatingPointType.InventoryPoint);
    expect(servicePointFormGroup.valid).toBeTrue();
  });

  it('should set RouteNetwork false and KilometerMaster undefined when ServicePint', () => {
    servicePointFormGroup.controls.selectedType.setValue(ServicePointType.ServicePoint);
    const createServicePointVersion: CreateServicePointVersion =
      ServicePointFormGroupBuilder.getWritableServicePoint(servicePointFormGroup);

    expect(createServicePointVersion.operatingPointKilometerMasterNumber).toEqual(undefined);
    expect(createServicePointVersion.operatingPointRouteNetwork).toEqual(false);
  });

  it('should set RouteNetwork false and KilometerMaster undefined when FareStop', () => {
    servicePointFormGroup.controls.selectedType.setValue(ServicePointType.FareStop);
    const createServicePointVersion: CreateServicePointVersion =
      ServicePointFormGroupBuilder.getWritableServicePoint(servicePointFormGroup);

    expect(createServicePointVersion.operatingPointKilometerMasterNumber).toEqual(undefined);
    expect(createServicePointVersion.operatingPointRouteNetwork).toEqual(false);
  });

  it('should set RouteNetwork true and KilometerMaster undefined when OperatingPoint', () => {
    servicePointFormGroup.controls.selectedType.setValue(ServicePointType.OperatingPoint);
    const createServicePointVersion: CreateServicePointVersion =
      ServicePointFormGroupBuilder.getWritableServicePoint(servicePointFormGroup);

    expect(createServicePointVersion.operatingPointKilometerMasterNumber).toEqual(undefined);
    expect(createServicePointVersion.operatingPointRouteNetwork).toEqual(true);
  });

  it('should set RouteNetwork true and KilometerMaster undefined when StopPoint', () => {
    servicePointFormGroup.controls.selectedType.setValue(ServicePointType.StopPoint);
    const createServicePointVersion: CreateServicePointVersion =
      ServicePointFormGroupBuilder.getWritableServicePoint(servicePointFormGroup);

    expect(createServicePointVersion.operatingPointKilometerMasterNumber).toEqual(undefined);
    expect(createServicePointVersion.operatingPointRouteNetwork).toEqual(true);
  });

  it('should init MoT required on existing StopPoint', () => {
    servicePointFormGroup = ServicePointFormGroupBuilder.buildFormGroup(BERN_WYLEREGG);

    servicePointFormGroup.controls.meansOfTransport.setValue([]);
    expect(servicePointFormGroup.valid).toEqual(false);

    servicePointFormGroup.controls.meansOfTransport.setValue([MeanOfTransport.Bus]);
    expect(servicePointFormGroup.valid).toEqual(true);
  });
});
