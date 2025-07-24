import { FormGroup } from '@angular/forms';
import {
  ServicePointDetailFormGroup,
  ServicePointFormGroupBuilder,
} from './service-point-detail-form-group';
import { ServicePointType } from '../../service-point-type';
import {
  Country,
  CreateServicePointVersion,
  MeanOfTransport,
  OperatingPointType,
  StopPointType,
} from '../../../../../../api';
import moment from 'moment';
import { BERN_WYLEREGG } from '../../../../../../../test/data/service-point';
import { EMPTY } from 'rxjs';

describe('ServicePointFormGroup', () => {
  let servicePointFormGroup: FormGroup<ServicePointDetailFormGroup>;

  beforeEach(() => {
    servicePointFormGroup =
      ServicePointFormGroupBuilder.buildEmptyFormGroup(EMPTY);
    servicePointFormGroup.enable();

    servicePointFormGroup.controls.number.setValue(7000);
    servicePointFormGroup.controls.country?.setValue(Country.Switzerland);
    servicePointFormGroup.controls.businessOrganisation.setValue(
      'ch:1:yb:best'
    );
    servicePointFormGroup.controls.designationOfficial.setValue('YB Stadion');
    servicePointFormGroup.controls.validityGroup.controls.validFrom.setValue(
      moment(new Date(2000 - 1 - 1))
    );
    servicePointFormGroup.controls.validityGroup.controls.validTo.setValue(
      moment(new Date(2099 - 10 - 1))
    );
  });

  it(
    'should add validators to include one of stopPoint, freightServicePoint. stopPoints needs meansOfTransport and' +
      ' stopPointType',
    () => {
      servicePointFormGroup.controls.selectedType.markAsDirty();
      servicePointFormGroup.controls.selectedType.setValue(
        ServicePointType.StopPoint
      );
      expect(servicePointFormGroup.valid).toBeFalse();

      if (
        !('stopPoint' in servicePointFormGroup.controls.spTypeGroup!.controls)
      ) {
        throw 'wrong form group';
      }
      servicePointFormGroup.controls.spTypeGroup?.controls.stopPoint.setValue(
        true
      );
      servicePointFormGroup.controls.spTypeGroup?.controls.stopPointGroup?.controls.meansOfTransport.setValue(
        [MeanOfTransport.Bus]
      );
      servicePointFormGroup.controls.spTypeGroup?.controls.stopPointGroup?.controls.stopPointType.setValue(
        'ON_REQUEST'
      );
      expect(servicePointFormGroup.valid).toBeTrue();
    }
  );

  it('should add validators to include one of stopPoint, freightServicePoint. freightServicePoint needs nothing', () => {
    servicePointFormGroup.controls.selectedType.markAsDirty();
    servicePointFormGroup.controls.selectedType.setValue(
      ServicePointType.StopPoint
    );
    expect(servicePointFormGroup.valid).toBeFalse();

    if (
      !('stopPoint' in servicePointFormGroup.controls.spTypeGroup!.controls)
    ) {
      throw 'wrong form group';
    }
    servicePointFormGroup.controls.spTypeGroup?.controls.freightServicePoint.setValue(
      true
    );
    expect(servicePointFormGroup.valid).toBeTrue();
  });

  it('should remove oneOf Validator on change to FareStop', () => {
    servicePointFormGroup.controls.selectedType.setValue(
      ServicePointType.StopPoint
    );
    servicePointFormGroup.controls.selectedType.setValue(
      ServicePointType.FareStop
    );

    expect(servicePointFormGroup.valid).toBeTrue();
  });

  it('should require operatingPointType for OperatingPoint', () => {
    servicePointFormGroup.controls.selectedType.markAsDirty();
    servicePointFormGroup.controls.selectedType.setValue(
      ServicePointType.OperatingPoint
    );
    expect(servicePointFormGroup.valid).toBeFalse();

    if (
      !(
        'operatingPointType' in
        servicePointFormGroup.controls.spTypeGroup!.controls
      )
    ) {
      throw 'wrong form group';
    }
    servicePointFormGroup.controls.spTypeGroup?.controls.operatingPointType.setValue(
      OperatingPointType.InventoryPoint
    );
    expect(servicePointFormGroup.valid).toBeTrue();
  });

  it('should set RouteNetwork and KilometerMaster undefined when ServicePoint', () => {
    servicePointFormGroup.controls.selectedType.setValue(
      ServicePointType.ServicePoint
    );
    const createServicePointVersion: CreateServicePointVersion =
      ServicePointFormGroupBuilder.mapper.getWritableServicePoint(
        servicePointFormGroup
      );

    expect(
      createServicePointVersion.operatingPointKilometerMasterNumber
    ).toEqual(undefined);
    expect(createServicePointVersion.operatingPointRouteNetwork).toEqual(
      undefined
    );
  });

  it('should set RouteNetwork and KilometerMaster undefined when FareStop', () => {
    servicePointFormGroup.controls.selectedType.setValue(
      ServicePointType.FareStop
    );
    const createServicePointVersion: CreateServicePointVersion =
      ServicePointFormGroupBuilder.mapper.getWritableServicePoint(
        servicePointFormGroup
      );

    expect(
      createServicePointVersion.operatingPointKilometerMasterNumber
    ).toEqual(undefined);
    expect(createServicePointVersion.operatingPointRouteNetwork).toEqual(
      undefined
    );
  });

  it('should set RouteNetwork true and KilometerMaster undefined when OperatingPoint', () => {
    servicePointFormGroup.controls.selectedType.markAsDirty();
    servicePointFormGroup.controls.selectedType.setValue(
      ServicePointType.OperatingPoint
    );
    servicePointFormGroup.controls.routeNetworkGroup?.controls.operatingPointKilometerMaster.setValue(
      7000
    );
    servicePointFormGroup.controls.routeNetworkGroup?.controls.operatingPointRouteNetwork.setValue(
      true
    );
    const createServicePointVersion: CreateServicePointVersion =
      ServicePointFormGroupBuilder.mapper.getWritableServicePoint(
        servicePointFormGroup
      );

    expect(
      createServicePointVersion.operatingPointKilometerMasterNumber
    ).toEqual(undefined);
    expect(createServicePointVersion.operatingPointRouteNetwork).toEqual(true);
  });

  it('should set RouteNetwork true and KilometerMaster undefined when StopPoint', () => {
    servicePointFormGroup.controls.selectedType.markAsDirty();
    servicePointFormGroup.controls.selectedType.setValue(
      ServicePointType.StopPoint
    );
    servicePointFormGroup.controls.routeNetworkGroup?.controls.operatingPointKilometerMaster.setValue(
      7000
    );
    servicePointFormGroup.controls.routeNetworkGroup?.controls.operatingPointRouteNetwork.setValue(
      true
    );
    const createServicePointVersion: CreateServicePointVersion =
      ServicePointFormGroupBuilder.mapper.getWritableServicePoint(
        servicePointFormGroup
      );

    expect(
      createServicePointVersion.operatingPointKilometerMasterNumber
    ).toEqual(undefined);
    expect(createServicePointVersion.operatingPointRouteNetwork).toEqual(true);
  });

  it('should validate MoT and stopPointType required on existing StopPoint', () => {
    servicePointFormGroup = ServicePointFormGroupBuilder.buildFormGroup(
      BERN_WYLEREGG,
      EMPTY
    );
    if (
      !('stopPoint' in servicePointFormGroup.controls.spTypeGroup!.controls)
    ) {
      throw 'wrong form group';
    }
    servicePointFormGroup.enable();
    servicePointFormGroup.controls.spTypeGroup?.controls.stopPointGroup?.controls.meansOfTransport.setValue(
      []
    );
    servicePointFormGroup.controls.spTypeGroup?.controls.stopPointGroup?.controls.stopPointType.setValue(
      undefined
    );
    expect(servicePointFormGroup.valid).toEqual(false);

    servicePointFormGroup.controls.spTypeGroup?.controls.stopPointGroup?.controls.meansOfTransport.setValue(
      [MeanOfTransport.Bus]
    );
    expect(servicePointFormGroup.valid).toEqual(false);

    servicePointFormGroup.controls.spTypeGroup?.controls.stopPointGroup?.controls.stopPointType.setValue(
      StopPointType.Unknown
    );
    expect(servicePointFormGroup.valid).toEqual(false);

    servicePointFormGroup.controls.spTypeGroup?.controls.stopPointGroup?.controls.stopPointType.setValue(
      StopPointType.OnRequest
    );
    expect(servicePointFormGroup.valid).toEqual(true);
  });
});
