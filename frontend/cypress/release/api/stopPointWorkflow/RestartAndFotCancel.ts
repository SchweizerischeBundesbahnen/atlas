import {
  RestartStopPointWorkflowData,
  SePoDependentInfo,
} from '../../../support/util/release-api/release-api-utils';
import StopPointWorkflow from '../../../support/util/release-api/stop-point-workflow';

describe(
  'StopPointWorkflow: Scenario restart-and-cancel',
  { testIsolation: false },
  () => {
    let info: SePoDependentInfo;
    let stopPointWorkflowId: number;
    let stopPointWorkflowRestartedId: number;
    let examinantIds: number[];

    it('Step-1: Login on ATLAS', () => {
      cy.atlasLogin();
    });

    it('Step-2: Create dependent Business Organisation and Service Point', () => {
      StopPointWorkflow.createDependentStopPointObjects().then(
        (sePoDependentInfo: SePoDependentInfo) => {
          info = sePoDependentInfo;
        }
      );
    });

    it('Step-3: Create the workflow', () => {
      StopPointWorkflow.create(
        info.parentServicePointId,
        info.parentServicePointSloid
      ).then((id: number) => (stopPointWorkflowId = id));
    });

    it('Step-4: Start the workflow', () => {
      StopPointWorkflow.start(stopPointWorkflowId).then(
        (ids: number[]) => (examinantIds = ids)
      );
    });

    it('Step-5: Restart the workflow', () => {
      StopPointWorkflow.restart(stopPointWorkflowId, examinantIds).then(
        (data: RestartStopPointWorkflowData) => {
          stopPointWorkflowRestartedId = data.stopPointWorkflowId;
          expect(stopPointWorkflowId).to.not.equal(
            stopPointWorkflowRestartedId
          );
        }
      );
    });

    it('Step-6: Cancel the restarted workflow', () => {
      StopPointWorkflow.cancel(stopPointWorkflowRestartedId);
    });

    it('Step-7: Check status of (restarted) workflows', () => {
      StopPointWorkflow.checkStatus(stopPointWorkflowId, 'REJECTED');
      StopPointWorkflow.checkStatus(stopPointWorkflowRestartedId, 'CANCELED');
    });
  }
);
