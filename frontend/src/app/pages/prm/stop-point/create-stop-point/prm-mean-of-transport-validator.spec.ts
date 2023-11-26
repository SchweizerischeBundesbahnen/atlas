import { MeanOfTransport } from '../../../../api';
import { PrmMeanOfTransportValidator } from './prm-mean-of-transport-validator';
import { FormControl } from '@angular/forms';

describe('PrmMeanOfTransportValidator', () => {
  it('isReducedOrComplete: should validate complete', () => {
    //given & when
    const result = PrmMeanOfTransportValidator.isReducedOrComplete(
      new FormControl([MeanOfTransport.Train]),
    );
    //then
    expect(result).toBeNull();
  });

  it('isReducedOrComplete: should validate reduced', () => {
    //given && when
    const result = PrmMeanOfTransportValidator.isReducedOrComplete(
      new FormControl([MeanOfTransport.Bus]),
    );
    //then
    expect(result).toBeNull();
  });

  it('isReducedOrComplete: should not validate reduced and complete', () => {
    //given && when
    const result = PrmMeanOfTransportValidator.isReducedOrComplete(
      new FormControl([MeanOfTransport.Bus, MeanOfTransport.Train]),
    );
    //then
    expect(result).toEqual({ meansOfTransportWrongCombination: ['BUS', 'TRAIN'] });
  });
});
