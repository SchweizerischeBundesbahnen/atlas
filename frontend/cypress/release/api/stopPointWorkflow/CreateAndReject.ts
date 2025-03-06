import CommonUtils from '../../../support/util/common-utils';
import ReleaseApiUtils from '../../../support/util/release-api-utils';

describe(
  'StopPointWorkflow: Scenario create-and-fot-reject',
  { testIsolation: false },
  () => {
    let sboid: string;
    let parentServicePointId: number;
    let parentServicePointSloid: string;
    let stopPointWorkflowId: number;

    const meansOfTransport = 'UNKNOWN';
    // Mark designationOfficial to be able to filter the emails in Outlook
    const designationOfficial: string = `${new Date().toISOString()}API`;

    const spatialReference: string = 'LV95';
    const country: string = 'SWITZERLAND';
    const dateInMonth: string = new Date()
      .getMonth()
      .toString()
      .padStart(2, '0');
    const north: string = `12051${dateInMonth}`;
    const east: string = `26520${dateInMonth}`;

    const height: string = '1';
    const validFrom: string = ReleaseApiUtils.todayAsAtlasString();
    const validTo: string = ReleaseApiUtils.LAST_ATLAS_DATE;

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
      CommonUtils.post('/service-point-directory/v1/service-points', {
        country: country,
        designationOfficial: designationOfficial,
        businessOrganisation: sboid,
        // The service-point need to have a meansOfTransport,
        // so that a StopPointWorkflow can be created and started.
        meansOfTransport: [meansOfTransport],
        validFrom: validFrom,
        validTo: validTo,
        servicePointGeolocation: {
          spatialReference: spatialReference,
          north: north,
          east: east,
          height: height,
        },
      }).then((response) => {
        expect(response)
          .property('status')
          .to.equal(CommonUtils.HTTP_REST_API_RESPONSE_CREATED);

        expect(response).property('body').property('id').to.be.a('number');
        parentServicePointId = response.body.id;

        expect(response).property('body').property('sloid').to.be.a('string');
        parentServicePointSloid = response.body.sloid;

        expect(response)
          .property('body')
          .property('number')
          .property('number')
          .to.be.a('number');
      });
    });

    it('Step-4: Create the workflow', () => {
      ReleaseApiUtils.createStopPointWorkflow(
        parentServicePointId,
        parentServicePointSloid
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
