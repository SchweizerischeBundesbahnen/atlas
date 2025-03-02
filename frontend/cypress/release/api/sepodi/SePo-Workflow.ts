import CommonUtils from '../../../support/util/common-utils';
import ReleaseApiUtils from '../../../support/util/release-api-utils';

describe(
  'SePo: Scenario skip-workflow: New StopPoint',
  { testIsolation: false },
  () => {
    const today = ReleaseApiUtils.today();
    const dateInMonth = today.getMonth();
    const meansOfTransport = 'UNKNOWN';
    const north = `12051${dateInMonth}`; // LV95 in Switzerland
    const east = `26520${dateInMonth}`;
    const height = '1';
    const validFrom = today;
    const designationOfficial = `${ReleaseApiUtils.date().toISOString()}API`;

    let sboid: string;
    let numberWithoutCheckDigit: string;
    let servicePointVersionId: number;

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
        country: 'SWITZERLAND',
        designationOfficial: designationOfficial,
        businessOrganisation: sboid,
        meansOfTransport: [meansOfTransport],
        validFrom: validFrom,
        validTo: '9999-12-31',
        servicePointGeolocation: {
          spatialReference: 'LV95',
          north: north,
          east: east,
          height: height,
        },
      }).then((response) => {
        expect(response.status).to.equal(
          CommonUtils.HTTP_REST_API_RESPONSE_CREATED
        );

        // expect(response.body).to.have.property('sloid').that.is.a('string');
        // sloid = response.body.sloid;

        expect(response.body)
          .property('number')
          .property('number')
          .that.is.a('string');
        numberWithoutCheckDigit = response.body.number.number;

        expect(response.body).to.have.property('id').that.is.a('number');
        servicePointVersionId = response.body.id;
      });
    });

    it.skip('Step-4: Skip workflow for Service Point', () => {
      CommonUtils.post(
        `/service-point-directory/v1/service-points/versions/${servicePointVersionId}/skip-workflow`,
        {}
      ).then((response) => {
        expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

        expect(response.body).to.have.property('status').that.is.a('string');
        expect(response.body.status).to.equal('VALIDATED');

        expect(response.body).to.have.property('id').that.is.a('number');
        expect(response.body.id).to.equal(servicePointVersionId);
      });
    });

    it.skip('Step-5: Check SePo status', () => {
      CommonUtils.get(
        `/service-point-directory/v1/service-points/${numberWithoutCheckDigit}`
      ).then((response) => {
        expect(response)
          .property('status')
          .to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

        expect(response)
          .property('body')
          .property('objects')
          .to.exist.and.to.be.an('array')
          .that.has.lengthOf(1);

        const sePoObject = response.body.objects[0];

        expect(sePoObject).property('status').to.equal('VALIDATED');
        expect(sePoObject).property('id').to.equal(numberWithoutCheckDigit);
      });
    });
  }
);
