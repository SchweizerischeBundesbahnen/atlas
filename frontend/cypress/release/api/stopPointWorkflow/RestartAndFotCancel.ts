import CommonUtils from '../../../support/util/common-utils';
import ReleaseApiUtils, {
  SePoDependentInfo,
} from '../../../support/util/release-api-utils';

describe(
  'StopPointWorkflow: Scenario restart-and-cancel',
  { testIsolation: false },
  () => {
    let sboid: string;
    let info: SePoDependentInfo;
    let stopPointWorkflowId: number;
    let stopPointWorkflowRestartedId: number;

    const meansOfTransport = ['UNKNOWN'];

    const spatialReference: string = 'LV95';
    const dateInMonth: string = new Date()
      .getMonth()
      .toString()
      .padStart(2, '0');
    const north: string = `12051${dateInMonth}`;
    const east: string = `26520${dateInMonth}`;

    const height: string = '1';

    it('Step-1: Login on ATLAS', () => {
      cy.atlasLogin();
    });

    it('Step-2: Create dependent Business Organisation', () => {
      CommonUtils.createDependentBusinessOrganisation(
        ReleaseApiUtils.today(),
        ReleaseApiUtils.today()
      ).then((sboidOfBO: string) => {
        sboid = sboidOfBO;
      });
    });

    it('Step-3: Create new Service Point', () => {
      // The service-point need to have a meansOfTransport,
      // so that a StopPointWorkflow can be created and started.
      ReleaseApiUtils.createDependentServicePoint(sboid, meansOfTransport, {
        servicePointGeolocation: {
          spatialReference: spatialReference,
          north: north,
          east: east,
          height: height,
        },
      }).then((sePoDependentInfo: SePoDependentInfo) => {
        info = sePoDependentInfo;
      });
    });

    it('Step-4: Create the workflow', () => {
      ReleaseApiUtils.createStopPointWorkflow(
        info.parentServicePointId,
        info.parentServicePointSloid
      ).then((id: number) => (stopPointWorkflowId = id));
    });

    it('Step-5: Start the workflow', () => {
      ReleaseApiUtils.startStopPointWorkflow(stopPointWorkflowId);
    });

    it('Step-6: Restart the workflow', () => {
      ReleaseApiUtils.restartStopPointWorkflow(stopPointWorkflowId).then(
        (id: number) => {
          stopPointWorkflowRestartedId = id;
          expect(stopPointWorkflowId).to.not.equal(
            stopPointWorkflowRestartedId
          );
        }
      );
    });

    it('Step-7: Cancel the restarted workflow', () => {
      ReleaseApiUtils.cancelStopPointWorkflow(stopPointWorkflowRestartedId);
    });

    it('Step-8: Check status of (restarted) workflows', () => {
      ReleaseApiUtils.checkWorkflowStatus(stopPointWorkflowId, 'REJECTED');
      ReleaseApiUtils.checkWorkflowStatus(
        stopPointWorkflowRestartedId,
        'CANCELED'
      );
    });
  }
);
