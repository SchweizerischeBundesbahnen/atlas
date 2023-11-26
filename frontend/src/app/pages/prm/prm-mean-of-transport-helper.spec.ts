import { PrmMeanOfTransportHelper } from './prm-mean-of-transport-helper';
import { MeanOfTransport } from '../../api';

describe('PrmMeanOfTransportHelper', () => {
  it('getReducedCompleteInstances: should get complete and reduced', () => {
    //given
    const completeReducedMeansOfTransport: MeanOfTransport[] = [
      MeanOfTransport.Train,
      MeanOfTransport.Bus,
    ];
    //when
    const result = PrmMeanOfTransportHelper.getReducedCompleteInstances(
      completeReducedMeansOfTransport,
    );
    expect(result.hasReduced).toBeTruthy();
    expect(result.hasComplete).toBeTruthy();
  });

  it('getReducedCompleteInstances: should get only reduced', () => {
    //given
    const completeReducedMeansOfTransport: MeanOfTransport[] = [MeanOfTransport.Bus];
    //when
    const result = PrmMeanOfTransportHelper.getReducedCompleteInstances(
      completeReducedMeansOfTransport,
    );
    expect(result.hasReduced).toBeTruthy();
    expect(result.hasComplete).toBeFalsy();
  });

  it('getReducedCompleteInstances: should get only complete ', () => {
    //given
    const completeReducedMeansOfTransport: MeanOfTransport[] = [MeanOfTransport.Train];
    //when
    const result = PrmMeanOfTransportHelper.getReducedCompleteInstances(
      completeReducedMeansOfTransport,
    );
    expect(result.hasReduced).toBeFalsy();
    expect(result.hasComplete).toBeTruthy();
  });

  it('isReduced: should return true', () => {
    //given
    const reducedMeansOfTransport: MeanOfTransport[] = [MeanOfTransport.Bus];
    //when
    const result = PrmMeanOfTransportHelper.isReduced(reducedMeansOfTransport);
    expect(result).toBeTruthy();
  });

  it('isReduced: should return false', () => {
    //given
    const reducedMeansOfTransport: MeanOfTransport[] = [MeanOfTransport.Train];
    //when
    const result = PrmMeanOfTransportHelper.isReduced(reducedMeansOfTransport);
    expect(result).toBeFalsy();
  });

  it('isReduced: should return error when complete and reduced', () => {
    //given
    const completeReducedMeansOfTransport: MeanOfTransport[] = [
      MeanOfTransport.Train,
      MeanOfTransport.Bus,
    ];
    //when
    expect(() => PrmMeanOfTransportHelper.isReduced(completeReducedMeansOfTransport)).toThrow(
      new Error('Not allowed means of' + ' transport combination!'),
    );
  });
});
