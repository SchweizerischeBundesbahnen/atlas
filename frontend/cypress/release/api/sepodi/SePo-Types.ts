import CommonUtils from '../../../support/util/common-utils';
import ReleaseApiUtils from '../../../support/util/release-api-utils';

describe(
  'SePo: Change a ServicePoint into all types it can have',
  { testIsolation: false },
  () => {
    let servicePointId: number;
    let sboid: string;
    let etagVersion: number = -1;

    // Use following countries for the uicCountryCode, as these only have very few service_point_versions
    // so that no conflicts can occur IRAQ=99, LEBANON=98, VIETNAM=32, TURKMENISTAN=67, ARMENIA=58, JAPAN=42
    const country = 'IRAQ';
    const numberShort = Cypress._.random(10000, 99999);
    const designationOfficial = new Date().toISOString();
    const sortCodeOfDestinationStation = '76608';
    const meansOfTransport = ['TRAIN'];
    const operatingPointTrafficPointType = 'TARIFF_POINT';

    const setEtagVersion = (etagVersionContainer) => {
      expect(etagVersionContainer)
        .property('etagVersion')
        .to.be.a('number')
        .and.greaterThan(etagVersion);
      etagVersion = etagVersionContainer.etagVersion;
    };

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

    it('Step-3: Create new ServicePoint', () => {
      CommonUtils.post('/service-point-directory/v1/service-points', {
        numberShort: numberShort,
        country: country,
        designationOfficial: designationOfficial,
        businessOrganisation: sboid,
        validFrom: '2019-06-18',
        validTo: '2099-12-31',
        etagVersion: etagVersion,
      }).then((response) => {
        expect(response.status).to.equal(
          CommonUtils.HTTP_REST_API_RESPONSE_CREATED
        );
        expect(response).property('body').property('id').to.be.a('number');
        servicePointId = response.body.id;

        expect(response)
          .property('body')
          .property('meansOfTransport')
          .to.exist.and.to.be.an('array')
          .that.has.lengthOf(0);
        
        setEtagVersion(response.body);
      });
    });

    it('Step-4: Update Service Point to FreightServicePoint', () => {
      CommonUtils.put(
        `/service-point-directory/v1/service-points/${servicePointId}`,
        {
          designationOfficial: designationOfficial,
          businessOrganisation: sboid,
          operatingPointRouteNetwork: false,
          validFrom: '2019-06-18',
          validTo: '2099-12-31',
          etagVersion: etagVersion,
          freightServicePoint: true,
          sortCodeOfDestinationStation: sortCodeOfDestinationStation,
        }
      ).then((response) => {
        expect(response)
          .property('status')
          .to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

        expect(response).property('body').to.be.an('array').to.have.lengthOf(1);
        const firstVersion = response.body[0];

        expect(firstVersion).property('freightServicePoint').to.be.true;
        expect(firstVersion)
          .property('sortCodeOfDestinationStation')
          .to.equal(sortCodeOfDestinationStation);

        setEtagVersion(firstVersion);
      });
    });

    // TODO: Fix Step-Count at last
    it('Step-5: Update Service Point to OperatingPoint', () => {
      CommonUtils.put(
        `/service-point-directory/v1/service-points/${servicePointId}`,
        {
          designationOfficial: designationOfficial,
          businessOrganisation: sboid,
          operatingPointRouteNetwork: false,
          validFrom: '2019-06-18',
          validTo: '2099-12-31',
          etagVersion: etagVersion,
          operatingPointTechnicalTimetableType: 'BRANCH',
        }
      ).then((response) => {
        expect(response)
          .property('status')
          .to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

        expect(response).property('body').to.be.an('array').to.have.lengthOf(1);
        const firstVersion = response.body[0];

        expect(firstVersion)
          .property('operatingPointTechnicalTimetableType')
          .to.equal('BRANCH');
        expect(firstVersion)
          .property('meansOfTransport')
          .to.be.an('array')
          .to.have.lengthOf(0);

        setEtagVersion(firstVersion);
      });
    });

    it('Step-6: Update Service Point to StopPoint', () => {
      CommonUtils.put(
        `/service-point-directory/v1/service-points/${servicePointId}`,
        {
          designationOfficial: designationOfficial,
          businessOrganisation: sboid,
          operatingPointRouteNetwork: false,
          meansOfTransport: meansOfTransport,
          validFrom: '2019-06-18',
          validTo: '2099-12-31',
          etagVersion: etagVersion,
          stopPointType: 'ORDERLY',
          freightServicePoint: false,
        }
      ).then((response) => {
        expect(response)
          .property('status')
          .to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

        expect(response).property('body').to.be.an('array').to.have.lengthOf(1);
        const firstVersion = response.body[0];

        expect(firstVersion).property('freightServicePoint').to.equal(false);
        expect(firstVersion)
          .property('meansOfTransport')
          .to.be.an('array')
          .to.have.lengthOf(1)
          .and.to.deep.equal(meansOfTransport);

        setEtagVersion(firstVersion);
      });
    });

    it('Step-7: Update Service Point to TariffPoint', () => {
      CommonUtils.put(
        `/service-point-directory/v1/service-points/${servicePointId}`,
        {
          designationOfficial: designationOfficial,
          businessOrganisation: sboid,
          operatingPointRouteNetwork: false,
          operatingPointKilometerMasterNumber: null,
          validFrom: '2019-06-18',
          validTo: '2099-12-31',
          etagVersion: etagVersion,
          operatingPointTrafficPointType: operatingPointTrafficPointType,
        }
      ).then((response) => {
        expect(response)
          .property('status')
          .to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

        expect(response).property('body').to.be.an('array').to.have.lengthOf(1);
        const firstVersion = response.body[0];

        expect(firstVersion)
          .property('operatingPointTrafficPointType')
          .to.equal(operatingPointTrafficPointType);
      });
    });

    it('Step-8: Check service point version', () => {
      CommonUtils.get(
        `/service-point-directory/v1/service-points/versions/${servicePointId}`
      ).then((response) => {
        expect(response)
          .property('status')
          .to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

        expect(response)
          .property('body')
          .property('operatingPointTrafficPointType')
          .to.equal(operatingPointTrafficPointType);
      });
    });
  }
);
