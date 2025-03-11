import CommonUtils from '../../../support/util/common-utils';
import ReleaseApiUtils, {
  SePoDependentInfo,
} from '../../../support/util/release-api/release-api-utils';

describe(
  'StopPointWorkflow: Scenario create-and-fot-reject',
  { testIsolation: false },
  () => {
    let sboid: string;
    let info: SePoDependentInfo;
    let stopPointWorkflowId: number;

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

    it('Step-5: Reject the workflow', () => {
      ReleaseApiUtils.rejectWorkflow(stopPointWorkflowId);
    });

    it('Step-6: Check status of workflow is REJECTED', () => {
      ReleaseApiUtils.checkWorkflowStatus(stopPointWorkflowId, 'REJECTED');
    });
  }
);
