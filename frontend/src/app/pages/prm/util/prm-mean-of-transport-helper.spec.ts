import { describe, expect, it } from 'vitest';

import { PrmMeanOfTransportHelper } from './prm-mean-of-transport-helper';
import { MeanOfTransport } from '../../../api';

describe('PrmMeanOfTransportHelper', () => {
  it('getReducedCompleteInstances: should get complete and reduced', () => {
    const completeReducedMeansOfTransport: MeanOfTransport[] = [MeanOfTransport.Train, MeanOfTransport.Bus];

    const result = PrmMeanOfTransportHelper.getReducedCompleteInstances(completeReducedMeansOfTransport);

    expect(result.hasReduced).toBeTruthy();
    expect(result.hasComplete).toBeTruthy();
  });

  it('getReducedCompleteInstances: should get only reduced', () => {
    const completeReducedMeansOfTransport: MeanOfTransport[] = [MeanOfTransport.Bus];

    const result = PrmMeanOfTransportHelper.getReducedCompleteInstances(completeReducedMeansOfTransport);

    expect(result.hasReduced).toBeTruthy();
    expect(result.hasComplete).toBeFalsy();
  });

  it('getReducedCompleteInstances: should get only complete ', () => {
    const completeReducedMeansOfTransport: MeanOfTransport[] = [MeanOfTransport.Train];

    const result = PrmMeanOfTransportHelper.getReducedCompleteInstances(completeReducedMeansOfTransport);

    expect(result.hasReduced).toBeFalsy();
    expect(result.hasComplete).toBeTruthy();
  });

  it('isReduced: should return true', () => {
    const reducedMeansOfTransport: MeanOfTransport[] = [MeanOfTransport.Bus];

    const result = PrmMeanOfTransportHelper.isReduced(reducedMeansOfTransport);

    expect(result).toBeTruthy();
  });

  it('isReduced: should return false', () => {
    const reducedMeansOfTransport: MeanOfTransport[] = [MeanOfTransport.Train];

    const result = PrmMeanOfTransportHelper.isReduced(reducedMeansOfTransport);

    expect(result).toBeFalsy();
  });

  it('isReduced: should return error when complete and reduced', () => {
    const completeReducedMeansOfTransport: MeanOfTransport[] = [MeanOfTransport.Train, MeanOfTransport.Bus];

    expect(() => PrmMeanOfTransportHelper.isReduced(completeReducedMeansOfTransport)).toThrow(
      new Error('Not allowed means of' + ' transport combination!')
    );
  });
});
