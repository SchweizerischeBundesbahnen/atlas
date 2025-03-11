import { SePoDependentInfo } from '../../../support/util/release-api/release-api-utils';
import StopPointWorkflow from '../../../support/util/release-api/stop-point-workflow';

describe(
  'StopPointWorkflow: Scenario create-and-fot-reject',
  { testIsolation: false },
  () => {
    let info: SePoDependentInfo;
    let stopPointWorkflowId: number;

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

    it('Step-4: Reject the workflow', () => {
      StopPointWorkflow.reject(stopPointWorkflowId);
    });

    it('Step-5: Check status of workflow is REJECTED', () => {
      StopPointWorkflow.checkStatus(stopPointWorkflowId, 'REJECTED');
    });
  }
);
