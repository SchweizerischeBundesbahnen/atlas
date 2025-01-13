import CommonUtils from '../../../support/util/common-utils';
import ReleaseApiUtils from '../../../support/util/release-api-utils';
import PrmConstants from 'PrmConstants';

let parentServicePointSloid = "";
let numberWithoutCheckDigit = -1;
let trafficPointSloid = "";
let etagVersion = -1;
let sboid = "";

let validFrom = ReleaseApiUtils.todayAsAtlasString();
let validTo = ReleaseApiUtils.todayAsAtlasString();
const freeText = "freeText";

const statusForAllPRMObjects = "VALIDATED";

// Means of Transport values
const BUS = "BUS";
const ELEVATOR = "ELEVATOR";
const meansOfTransport = [BUS];

const validatePrmObject = (stopPoint, expectedMeansOfTransport: string[]) => {
  expect(stopPoint).to.have.property('id').that.is.a('number');
  expect(stopPoint).to.have.property('reduced').to.be.true;

  expect(stopPoint).to.have.property('meansOfTransport').that.is.an('array');
  expect(stopPoint.meansOfTransport.length).to.equal(expectedMeansOfTransport.length);
  expectedMeansOfTransport.forEach(mot => {
    expect(stopPoint.meansOfTransport).to.include(mot);
  })

  expect(stopPoint).to.have.property('sloid').that.is.a('string').and.to.equal(parentServicePointSloid);
  expect(stopPoint).to.have.property('validFrom').that.is.a('string').and.to.equal(validFrom);
  expect(stopPoint).to.have.property('validTo').that.is.a('string').and.to.equal(validTo);
  expect(stopPoint).to.have.property('freeText').that.is.a('string').and.to.equal(freeText);
  expect(stopPoint).to.have.property('number').to.have.property('number').that.is.a('number').and.to.equal(numberWithoutCheckDigit);
  expect(stopPoint).to.have.property('status').that.is.a('string').and.to.equal(statusForAllPRMObjects);

  expect(stopPoint).to.have.property('etagVersion').that.is.an('number').and.greaterThan(-1);
};

describe('Create new ServicePoint and TrafficPoint for reduced StopPoint', { testIsolation: false }, () => {

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Create dependent Business Organisation', () => {
    CommonUtils.createDependentBusinessOrganisation(ReleaseApiUtils.today(), ReleaseApiUtils.today()).then((sboidOfBO:string) => {
      sboid = sboidOfBO
    });
  });

  it('Step-3: New ServicePoint', () => {
    CommonUtils.post('/service-point-directory/v1/service-points', {
      country: "SWITZERLAND",
      designationOfficial: ReleaseApiUtils.today(),
      businessOrganisation: sboid,
      // The service-point needs to have a meansOfTransport, so that a PRM-stopPoint can be created.
      // The meansOfTransport BUS leads to the reduced recording variant
      // Code: https://code.sbb.ch/projects/KI_ATLAS/repos/atlas/browse/base-atlas/src/main/java/ch/sbb/atlas/servicepoint/enumeration/MeanOfTransport.java
      meansOfTransport: meansOfTransport,
      validFrom: ReleaseApiUtils.LAST_ATLAS_DATE,
      validTo: ReleaseApiUtils.LAST_ATLAS_DATE
    }).then((response) => {
      expect(response.status).to.equal(201);

      expect(response.body).to.have.property('sloid').that.is.a('string');
      parentServicePointSloid = response.body.sloid;

      expect(response.body).property('number').property('number').that.is.a('number');
      numberWithoutCheckDigit = response.body.number.number;
    });
  });

  it('Step-4: New Traffic Point', () => {
    trafficPointSloid = `${parentServicePointSloid}:trafficPoint:1`;

    CommonUtils.post('/service-point-directory/v1/traffic-point-elements', {
      numberWithoutCheckDigit: numberWithoutCheckDigit,
      sloid: trafficPointSloid,
      parentSloid: parentServicePointSloid,
      validFrom: ReleaseApiUtils.FIRST_ATLAS_DATE,
      validTo: ReleaseApiUtils.FIRST_ATLAS_DATE,
      trafficPointElementType: "BOARDING_PLATFORM"
    }).then((response) => {
      expect(response.status).to.equal(201);
      expect(response.body.parentSloid).to.equal(parentServicePointSloid);

      expect(response.body).to.have.property('sloid').that.is.a('string');
      trafficPointSloid = response.body.sloid;

      expect(response.body).property('servicePointNumber').property('number').that.is.a('number').and.equals(numberWithoutCheckDigit);
    });
  });

  describe('PRM: New reduced Stop Point based on Service and Traffic Point', { testIsolation: false }, () => {
    let stopPointId = -1;

    it('Step-1: New reduced Stop Point', () => {
      // Docu: https://confluence.sbb.ch/display/ATLAS/Data-Fact-Matrix#DataFactMatrix-StopPlaces(Haltestellen)

      CommonUtils.post('/prm-directory/v1/stop-points', {
        sloid: parentServicePointSloid,
        validFrom: validFrom,
        validTo: validTo,
        etagVersion: 0, // TODO: Can this be removed?
        meansOfTransport: meansOfTransport,
        freeText: freeText,
        numberWithoutCheckDigit: numberWithoutCheckDigit
      }).then((response) => {
        expect(response.status).to.equal(201);

        const stopPoint = response.body;
        validatePrmObject(stopPoint, meansOfTransport);
        stopPointId = stopPoint.id;
      });
    });

    it('Step-2: Check reduced Stop Point', () => {
      CommonUtils.get(`/prm-directory/v1/stop-points?sloids=${parentServicePointSloid}&fromDate=${validFrom}&toDate=${validTo}`).then((response) => {

        expect(response.status).to.equal(200);
        expect(response.body.totalCount).to.be.greaterThan(0);

        const stopPoint = ReleaseApiUtils.getPrmObjectById(response.body, stopPointId, false);
        validatePrmObject(stopPoint, meansOfTransport);
        etagVersion = stopPoint.etagVersion;
      });
    });

    it('Step-3: Update reduced Stop Point', () => {
      validTo = ReleaseApiUtils.tomorrowAsAtlasString();
      const updatedMeansOfTransport = meansOfTransport.concat(ELEVATOR)
      CommonUtils.put(`/prm-directory/v1/stop-points/${stopPointId}`, {
        sloid: parentServicePointSloid,
        validFrom: validFrom,
        validTo: validTo, // Updated
        etagVersion: etagVersion,
        meansOfTransport: updatedMeansOfTransport,
        freeText: freeText,
        numberWithoutCheckDigit: numberWithoutCheckDigit
      }).then((response) => {
        expect(response.status).to.equal(200);

        const stopPoint = ReleaseApiUtils.getPrmObjectById(response.body, stopPointId, true);
        validatePrmObject(stopPoint, updatedMeansOfTransport);
        etagVersion = stopPoint.etagVersion;
      });
    });

    // TODO: Checked with http-Files until here.

    describe('PRM: New reduced Toilet based on Service, Traffic and Stop Point', { testIsolation: false }, () => {
      let toiletId = -1;
      let toiletSloid = `${parentServicePointSloid}:TOILET1`;

      const wheelchairToilet = PrmConstants.basicValuesAndNotApplicableAndPartially();
      const designation = "öffentliches WC im Bf Basel Bad Bf";

      const validateCommonToiletAttributes = (body, meansOfTransport: string[], designation: string, additionalInformation: string) => {
        validatePrmObject(body, meansOfTransport)
        expect(body).to.have.property('designation').that.is.a('string').and.to.equal(designation);
        expect(body).to.have.property('wheelchairToilet').that.is.a('array').and.to.equal(wheelchairToilet);
        expect(body).to.have.property('additionalInformation').that.is.a('string').and.to.equal(additionalInformation);
      };

      it('Step-1: New reduced Toilet', () => {
        // Docu: https://confluence.sbb.ch/display/ATLAS/Data-Fact-Matrix#DataFactMatrix-StopPlaces(Haltestellen)
        CommonUtils.post('/prm-directory/v1/toilets', {
          sloid: toiletSloid,
          validFrom: validFrom,
          validTo: validTo,
          etagVersion: 0, // TODO: Can this be removed?
          meansOfTransport: meansOfTransport,
          freeText: freeText,
          numberWithoutCheckDigit: numberWithoutCheckDigit,
          designation: designation,
        }).then((response) => {
          expect(response.status).to.equal(201);

          const toilet = response.body;
          validateCommonToiletAttributes(toilet, meansOfTransport, designation, "");
          toiletId = toilet.id;
        });
      });

      it('Step-2: Get reduced Toilet', () => {
        CommonUtils.get(`/prm-directory/v1/toilets?parentServicePointSloids=${parentServicePointSloid}`).then((response) => {
          expect(response.status).to.equal(200);

          const toilet = ReleaseApiUtils.getPrmObjectById(response.body, toiletId, false)
          validateCommonToiletAttributes(toilet, meansOfTransport, designation, "");
          etagVersion = toilet.etagVersion;
        });
      });

      it('Step-3: Change reduced Toilet', () => {
        const updatedDesignation = "designation2"; // Updated variable for designation
        const updatedAdditionalInformation = "additionalInformation2"; // Updated variable for additionalInformation

        CommonUtils.put(`/prm-directory/v1/toilets/${toiletId}`, {
          sloid: `${parentServicePointSloid}:TOILET1`,
          validFrom: validFrom,
          validTo: validTo,
          etagVersion: etagVersion,
          parentServicePointSloid: parentServicePointSloid,
          designation: updatedDesignation,
          additionalInformation: updatedAdditionalInformation,
          wheelchairToilet: wheelchairToilet
        }).then((response) => {
          expect(response.status).to.equal(200);

          const toilet = ReleaseApiUtils.getPrmObjectById(response.body, toiletId, true);
          validateCommonToiletAttributes(toilet, meansOfTransport, updatedDesignation, updatedAdditionalInformation);
          etagVersion = toilet.etagVersion;
        });
      });
    });
  });
});
