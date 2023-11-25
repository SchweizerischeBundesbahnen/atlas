import { MeanOfTransport } from '../../api';

export class PrmMeanOfTransportHelper {
  static getReducedCompleteInstances(selectedMeansOfTransport: MeanOfTransport[]) {
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
    const reduced: MeanOfTransport[] = [];
    const complete: MeanOfTransport[] = [];
    selectedMeansOfTransport.forEach((mot: MeanOfTransport) => {
      reducedMeansOfTransport.forEach((red) => {
        if (red === mot) {
          reduced.push(red);
        }
      });
      completeMeansOfTransport.forEach((com) => {
        if (com === mot) {
          complete.push(com);
        }
      });
    });
    return { reduced, complete };
  }

  static isReduced(meansOfTransport: MeanOfTransport[]): boolean {
    const { reduced, complete } =
      PrmMeanOfTransportHelper.getReducedCompleteInstances(meansOfTransport);
    if (reduced.length > 0 && complete.length > 0) {
      new Error('Not allowed means of transport combination!');
    }
    return reduced.length > 0;
  }
}
