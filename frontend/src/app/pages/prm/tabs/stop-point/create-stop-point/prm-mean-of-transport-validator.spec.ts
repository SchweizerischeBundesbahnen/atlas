import { describe, expect, it } from 'vitest';

import { MeanOfTransport } from '../../../../../api';
import { PrmMeanOfTransportValidator } from './prm-mean-of-transport-validator';
import { FormControl } from '@angular/forms';

describe('PrmMeanOfTransportValidator', () => {
  it('isReducedOrComplete: should validate complete', () => {
    const result = PrmMeanOfTransportValidator.isReducedOrComplete(new FormControl([MeanOfTransport.Train]));

    expect(result).toBeNull();
  });

  it('isReducedOrComplete: should validate reduced', () => {
    const result = PrmMeanOfTransportValidator.isReducedOrComplete(new FormControl([MeanOfTransport.Bus]));

    expect(result).toBeNull();
  });

  it('isReducedOrComplete: should not validate reduced and complete', () => {
    const result = PrmMeanOfTransportValidator.isReducedOrComplete(
      new FormControl([MeanOfTransport.Bus, MeanOfTransport.Train])
    );

    expect(result).toEqual({
      meansOfTransportWrongCombination: ['BUS', 'TRAIN'],
    });
  });
});
