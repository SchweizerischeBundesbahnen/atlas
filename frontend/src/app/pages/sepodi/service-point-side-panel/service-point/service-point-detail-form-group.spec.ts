import { FormGroup } from '@angular/forms';
import {
  ServicePointDetailFormGroup,
  ServicePointFormGroupBuilder,
} from './service-point-detail-form-group';
import { ServicePointType } from './service-point-type';
import { Country, MeanOfTransport, OperatingPointType } from '../../../../api';
import moment from 'moment';

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
});
