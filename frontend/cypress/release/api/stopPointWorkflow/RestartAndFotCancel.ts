import ReleaseApiUtils, {
  RestartStopPointWorkflowData,
  SePoDependentInfo,
} from '../../../support/util/release-api-utils';

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
      ReleaseApiUtils.createDependentStopPointObjects().then(
        (sePoDependentInfo: SePoDependentInfo) => {
          info = sePoDependentInfo;
        }
      );
    });

    it('Step-3: Create the workflow', () => {
      ReleaseApiUtils.createStopPointWorkflow(
        info.parentServicePointId,
        info.parentServicePointSloid
      ).then((id: number) => (stopPointWorkflowId = id));
    });

    it('Step-4: Start the workflow', () => {
      ReleaseApiUtils.startStopPointWorkflow(stopPointWorkflowId).then(
        (ids: number[]) => (examinantIds = ids)
      );
    });

    it('Step-5: Restart the workflow', () => {
      ReleaseApiUtils.restartStopPointWorkflow(
        stopPointWorkflowId,
        examinantIds
      ).then((data: RestartStopPointWorkflowData) => {
        stopPointWorkflowRestartedId = data.stopPointWorkflowId;
        expect(stopPointWorkflowId).to.not.equal(stopPointWorkflowRestartedId);
      });
    });

    it('Step-6: Cancel the restarted workflow', () => {
      ReleaseApiUtils.cancelStopPointWorkflow(stopPointWorkflowRestartedId);
    });

    it('Step-7: Check status of (restarted) workflows', () => {
      ReleaseApiUtils.checkWorkflowStatus(stopPointWorkflowId, 'REJECTED');
      ReleaseApiUtils.checkWorkflowStatus(
        stopPointWorkflowRestartedId,
        'CANCELED'
      );
    });
  }
);
