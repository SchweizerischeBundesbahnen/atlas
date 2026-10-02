import { describe, expect, it } from 'vitest';
import { TrafficPointElementFormGroupBuilder } from './traffic-point-detail-form-group';
import {
  DESIGNATION_OPERATIONAL_MAX_DIGITS_WITH_TRAIN,
  DESIGNATION_OPERATIONAL_MAX_DIGITS_WITHOUT_TRAIN,
  resolveDesignationOperationalMaxDigits,
} from './designation-operational-validator';
import { BERN, BERN_WYLEREGG } from '../../../../../test/data/service-point';
import { BERN_WYLEREGG_TRAFFIC_POINTS } from '../../../../../test/data/traffic-point-element';
import { MeanOfTransport, ReadServicePointVersion, ReadTrafficPointElementVersion } from '../../../../api';

function trafficPointWithDesignationOperational(designationOperational: string): ReadTrafficPointElementVersion {
  return { ...BERN_WYLEREGG_TRAFFIC_POINTS[0], designationOperational };
}

describe('resolveDesignationOperationalMaxDigits', () => {
  it('should allow 3 digits when a version has mean of transport TRAIN', () => {
    expect(resolveDesignationOperationalMaxDigits(BERN)).toBe(DESIGNATION_OPERATIONAL_MAX_DIGITS_WITH_TRAIN);
  });

  it('should allow 2 digits when no version has mean of transport TRAIN', () => {
    expect(resolveDesignationOperationalMaxDigits([BERN_WYLEREGG])).toBe(
      DESIGNATION_OPERATIONAL_MAX_DIGITS_WITHOUT_TRAIN
    );
  });

  it('should allow 2 digits when there is no service point version', () => {
    expect(resolveDesignationOperationalMaxDigits([])).toBe(DESIGNATION_OPERATIONAL_MAX_DIGITS_WITHOUT_TRAIN);
  });

  it('should allow 3 digits when TRAIN is only present in an outdated version', () => {
    const outdatedTrainVersion: ReadServicePointVersion = {
      ...BERN_WYLEREGG,
      meansOfTransport: [MeanOfTransport.Train],
      validFrom: new Date('1990-01-01'),
      validTo: new Date('1991-12-31'),
    };
    const currentBusVersion: ReadServicePointVersion = {
      ...BERN_WYLEREGG,
      meansOfTransport: [MeanOfTransport.Bus],
    };

    expect(resolveDesignationOperationalMaxDigits([outdatedTrainVersion, currentBusVersion])).toBe(
      DESIGNATION_OPERATIONAL_MAX_DIGITS_WITH_TRAIN
    );
  });
});

describe('TrafficPointElementFormGroupBuilder designationOperational', () => {
  describe('with mean of transport TRAIN (max 3 digits)', () => {
    it('should accept 3 digits', () => {
      const form = TrafficPointElementFormGroupBuilder.buildFormGroup(
        trafficPointWithDesignationOperational('123'),
        DESIGNATION_OPERATIONAL_MAX_DIGITS_WITH_TRAIN
      );

      expect(form.controls.designationOperational.valid).toBe(true);
    });

    it('should reject 4 digits', () => {
      const form = TrafficPointElementFormGroupBuilder.buildFormGroup(
        trafficPointWithDesignationOperational('1234'),
        DESIGNATION_OPERATIONAL_MAX_DIGITS_WITH_TRAIN
      );

      expect(form.controls.designationOperational.valid).toBe(false);
      expect(form.controls.designationOperational.errors).toEqual({
        designation_operational_max_digits: { maxDigits: DESIGNATION_OPERATIONAL_MAX_DIGITS_WITH_TRAIN },
      });
    });
  });

  describe('without mean of transport TRAIN (max 2 digits)', () => {
    it('should accept 2 digits', () => {
      const form = TrafficPointElementFormGroupBuilder.buildFormGroup(
        trafficPointWithDesignationOperational('02'),
        DESIGNATION_OPERATIONAL_MAX_DIGITS_WITHOUT_TRAIN
      );

      expect(form.controls.designationOperational.valid).toBe(true);
    });

    it('should reject 3 digits and report the allowed maximum', () => {
      const form = TrafficPointElementFormGroupBuilder.buildFormGroup(
        trafficPointWithDesignationOperational('123'),
        DESIGNATION_OPERATIONAL_MAX_DIGITS_WITHOUT_TRAIN
      );

      expect(form.controls.designationOperational.valid).toBe(false);
      expect(form.controls.designationOperational.errors).toEqual({
        designation_operational_max_digits: { maxDigits: DESIGNATION_OPERATIONAL_MAX_DIGITS_WITHOUT_TRAIN },
      });
    });
  });

  it('should not validate an empty value', () => {
    const form = TrafficPointElementFormGroupBuilder.buildFormGroup(
      trafficPointWithDesignationOperational(''),
      DESIGNATION_OPERATIONAL_MAX_DIGITS_WITHOUT_TRAIN
    );

    expect(form.controls.designationOperational.valid).toBe(true);

    form.controls.designationOperational.setValue(null);
    expect(form.controls.designationOperational.valid).toBe(true);

    form.controls.designationOperational.setValue(undefined);
    expect(form.controls.designationOperational.valid).toBe(true);
  });

  it('should still reject non numeric values', () => {
    const form = TrafficPointElementFormGroupBuilder.buildFormGroup(
      trafficPointWithDesignationOperational('A'),
      DESIGNATION_OPERATIONAL_MAX_DIGITS_WITHOUT_TRAIN
    );

    expect(form.controls.designationOperational.valid).toBe(false);
    expect(form.controls.designationOperational.errors?.['pattern']).toBeTruthy();
  });

  describe('updateDesignationOperationalMaxDigits', () => {
    it('should mark a prefilled legacy 3 digit value invalid and block saving', () => {
      const form = TrafficPointElementFormGroupBuilder.buildFormGroup(
        trafficPointWithDesignationOperational('123'),
        DESIGNATION_OPERATIONAL_MAX_DIGITS_WITH_TRAIN
      );
      expect(form.controls.designationOperational.valid).toBe(true);

      TrafficPointElementFormGroupBuilder.updateDesignationOperationalMaxDigits(
        form,
        DESIGNATION_OPERATIONAL_MAX_DIGITS_WITHOUT_TRAIN
      );

      expect(form.controls.designationOperational.value).toBe('123');
      expect(form.controls.designationOperational.valid).toBe(false);
      expect(form.valid).toBe(false);
    });

    it('should accept the value again after correction', () => {
      const form = TrafficPointElementFormGroupBuilder.buildFormGroup(
        trafficPointWithDesignationOperational('123'),
        DESIGNATION_OPERATIONAL_MAX_DIGITS_WITH_TRAIN
      );
      TrafficPointElementFormGroupBuilder.updateDesignationOperationalMaxDigits(
        form,
        DESIGNATION_OPERATIONAL_MAX_DIGITS_WITHOUT_TRAIN
      );

      form.controls.designationOperational.setValue('12');

      expect(form.controls.designationOperational.valid).toBe(true);
      expect(form.valid).toBe(true);
    });

    it('should widen the limit to 3 digits when the service point has TRAIN', () => {
      const form = TrafficPointElementFormGroupBuilder.buildFormGroup(
        trafficPointWithDesignationOperational('123'),
        DESIGNATION_OPERATIONAL_MAX_DIGITS_WITHOUT_TRAIN
      );
      expect(form.controls.designationOperational.valid).toBe(false);

      TrafficPointElementFormGroupBuilder.updateDesignationOperationalMaxDigits(
        form,
        DESIGNATION_OPERATIONAL_MAX_DIGITS_WITH_TRAIN
      );

      expect(form.controls.designationOperational.valid).toBe(true);
    });
  });
});
