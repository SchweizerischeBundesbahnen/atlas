import { MeanOfTransport } from '../../../api';

const reducedMeansOfTransport: MeanOfTransport[] = [
  MeanOfTransport.Elevator,
  MeanOfTransport.Bus,
  MeanOfTransport.Chairlift,
  MeanOfTransport.CableCar,
  MeanOfTransport.Boat,
  MeanOfTransport.Tram,
];

const completeMeansOfTransport: MeanOfTransport[] = [
  MeanOfTransport.Train,
  MeanOfTransport.Metro,
  MeanOfTransport.RackRailway,
];

export class PrmMeanOfTransportHelper {
  static getReducedCompleteInstances(selectedMeansOfTransport: MeanOfTransport[]) {
    const hasReduced = selectedMeansOfTransport.some((s) => reducedMeansOfTransport.includes(s));
    const hasComplete = selectedMeansOfTransport.some((s) => completeMeansOfTransport.includes(s));
    return { hasReduced, hasComplete };
  }

  static isReduced(meansOfTransport: MeanOfTransport[]): boolean {
    const { hasReduced, hasComplete } =
      PrmMeanOfTransportHelper.getReducedCompleteInstances(meansOfTransport);
    if (hasReduced && hasComplete) {
      throw new Error('Not allowed means of transport combination!');
    }
    return hasReduced;
  }
}
