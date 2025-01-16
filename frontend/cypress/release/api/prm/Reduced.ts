import CommonUtils from '../../../support/util/common-utils';
import ReleaseApiUtils from '../../../support/util/release-api-utils';
import PrmConstants from './PrmConstants';

let parentServicePointSloid = "";
let numberWithoutCheckDigit = -1;
let trafficPointSloid = "";
let etagVersion = -1;
let sboid = "";

const freeText = "freeText";

const statusForAllPRMObjects = "VALIDATED";

// Means of Transport values
const BUS = "BUS";
const ELEVATOR = "ELEVATOR";
const meansOfTransport = [BUS];

const validatePrmObject = (object, validFrom: string, validTo: string) => {
  expect(object).to.have.property('id').that.is.a('number');

  expect(object).to.have.property('validFrom').that.is.a('string').and.to.equal(validFrom);
  expect(object).to.have.property('validTo').that.is.a('string').and.to.equal(validTo);
  expect(object).to.have.property('number').to.have.property('number').that.is.a('number').and.to.equal(numberWithoutCheckDigit);
  expect(object).to.have.property('status').that.is.a('string').and.to.equal(statusForAllPRMObjects);

  expect(object).to.have.property('etagVersion').that.is.an('number').and.greaterThan(-1);
};

// TODO: Is it helpful to add versioning (incl. increasing only the validTo-date) to the PRM-Objects?

describe('Create new ServicePoint and TrafficPoint for reduced StopPoint', { testIsolation: false }, () => {

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Create dependent Business Organisation', () => {
    CommonUtils.createDependentBusinessOrganisation(ReleaseApiUtils.today(), ReleaseApiUtils.today()).then((sboidOfBO: string) => {
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
});

describe('PRM: New reduced Stop Point based on Service and Traffic Point', { testIsolation: false }, () => {
  let stopPointId = -1;

  const validFrom = ReleaseApiUtils.todayAsAtlasString();
  let validTo = ReleaseApiUtils.todayAsAtlasString();
  const validateStopPoint = (stopPoint: any, expectedMeansOfTransport: string[]) => {
    validatePrmObject(stopPoint, validFrom, validTo)
    expect(stopPoint).to.have.property('reduced').to.be.true;
    expect(stopPoint).to.have.property('sloid').that.is.a('string').and.to.equal(parentServicePointSloid);
    expect(stopPoint).to.have.property('freeText').that.is.a('string').and.to.equal(freeText);

    expect(stopPoint).to.have.property('meansOfTransport').that.is.an('array');
    expect(stopPoint.meansOfTransport.length).to.equal(expectedMeansOfTransport.length);
    expectedMeansOfTransport.forEach(mot => {
      expect(stopPoint.meansOfTransport).to.include(mot);
    })
  }

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
      validateStopPoint(stopPoint, meansOfTransport);
      stopPointId = stopPoint.id;
    });
  });

  it('Step-2: Check reduced Stop Point', () => {
    CommonUtils.get(`/prm-directory/v1/stop-points?sloids=${parentServicePointSloid}&fromDate=${validFrom}&toDate=${validTo}`).then((response) => {

      expect(response.status).to.equal(200);
      expect(response.body.totalCount).to.be.greaterThan(0);

      const stopPoint = ReleaseApiUtils.getPrmObjectById(response.body, stopPointId, false, 1);
      validateStopPoint(stopPoint, meansOfTransport);
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

      const stopPoint = ReleaseApiUtils.getPrmObjectById(response.body, stopPointId, true, 1);
      validateStopPoint(stopPoint, updatedMeansOfTransport);
      etagVersion = stopPoint.etagVersion;
    });
  });
});

describe('PRM: New reduced Toilet based on Service, Traffic and Stop Point', { testIsolation: false }, () => {
  let toiletId = -1;
  let toiletSloid = "";

  const validFrom = ReleaseApiUtils.todayAsAtlasString();
  const validTo = ReleaseApiUtils.todayAsAtlasString();

  const wheelchairToilet = ReleaseApiUtils.extractOneRandomValue(PrmConstants.basicValuesAndNotApplicableAndPartially());
  const designation = "öffentliches WC im Bf Basel Bad Bf";

  const validateCommonToiletAttributes = (toilet, designation: string, additionalInformation: string) => {
    validatePrmObject(toilet, validFrom, validTo)
    expect(toilet).to.have.property('designation').that.is.a('string').and.to.equal(designation);
    expect(toilet).to.have.property('wheelchairToilet').that.is.a('string').and.to.equal(wheelchairToilet);
    expect(toilet).to.have.property('additionalInformation').and.to.equal(additionalInformation);

    expect(toilet).to.have.property('sloid').that.is.a('string').and.to.equal(toiletSloid);
    expect(toilet).to.have.property('parentServicePointSloid').that.is.a('string').and.to.equal(parentServicePointSloid);
  };

  it('Step-1: New reduced Toilet', () => {
    toiletSloid = `${parentServicePointSloid}:TOILET1` // Has to be initialized here, because before parentServicePointSloid is not known
    // Docu: https://confluence.sbb.ch/display/ATLAS/Data-Fact-Matrix#DataFactMatrix-StopPlaces(Haltestellen)
    CommonUtils.post('/prm-directory/v1/toilets', {
      parentServicePointSloid: parentServicePointSloid,
      sloid: toiletSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: 0, // TODO: Can this be removed?
      numberWithoutCheckDigit: numberWithoutCheckDigit,
      designation: designation,
      wheelchairToilet: wheelchairToilet
    }).then((response) => {
      expect(response.status).to.equal(201);

      const toilet = response.body;
      validateCommonToiletAttributes(toilet, designation, null);
      toiletId = toilet.id;
    });
  });

  it('Step-2: Get reduced Toilet', () => {
    CommonUtils.get(`/prm-directory/v1/toilets?parentServicePointSloids=${parentServicePointSloid}`).then((response) => {
      expect(response.status).to.equal(200);

      const toilet = ReleaseApiUtils.getPrmObjectById(response.body, toiletId, false, 1)
      validateCommonToiletAttributes(toilet, designation, null);
      etagVersion = toilet.etagVersion;
    });
  });

  it('Step-3: Change reduced Toilet', () => {
    const updatedDesignation = "designation2";
    const updatedAdditionalInformation = "additionalInformation2";

    CommonUtils.put(`/prm-directory/v1/toilets/${toiletId}`, {
      sloid: toiletSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersion,
      parentServicePointSloid: parentServicePointSloid,
      designation: updatedDesignation,
      additionalInformation: updatedAdditionalInformation,
      wheelchairToilet: wheelchairToilet
    }).then((response) => {
      expect(response.status).to.equal(200);

      const toilet = ReleaseApiUtils.getPrmObjectById(response.body, toiletId, true, 1);
      validateCommonToiletAttributes(toilet, updatedDesignation, updatedAdditionalInformation);
    });
  });
});
  // TODO: Checked with http-Files until here.

describe('PRM: New reduced Ticket Counter', { testIsolation: false }, () => {
  const referencePointElementType = "TICKET_COUNTER";
  let ticketCounterSloid = "";
  let ticketCounterId = -1;
  const designation = referencePointElementType;

  const additionalInformation = null;
  const openingHours = "https://www.sbb.ch/de/bahnhof-services/am-bahnhof/bahnhoefe.html";
  const inductionLoop = ReleaseApiUtils.extractOneRandomValue(PrmConstants.basicValuesAndNotApplicableAndPartially());
  const wheelchairAccess = ReleaseApiUtils.extractOneRandomValue(PrmConstants.basicValuesAndNotApplicableAndPartially());

  const validFrom = ReleaseApiUtils.todayAsAtlasString();
  const validTo = ReleaseApiUtils.todayAsAtlasString();

  const validateCommonTicketCounterAttributes = (ticketCounter, designation: string, additionalInformation: string) => {
    validatePrmObject(ticketCounter, validFrom, validTo)
    expect(ticketCounter).to.have.property('designation').that.is.a('string').and.to.equal(designation);
    expect(ticketCounter).to.have.property('wheelchairAccess').that.is.a('string').and.to.equal(wheelchairAccess);
    expect(ticketCounter).to.have.property('additionalInformation').and.to.equal(additionalInformation);

    expect(ticketCounter).to.have.property('sloid').that.is.a('string').and.to.equal(ticketCounterSloid);
    expect(ticketCounter).to.have.property('parentServicePointSloid').that.is.a('string').and.to.equal(parentServicePointSloid);

    expect(ticketCounter).to.have.property('inductionLoop').and.to.equal(inductionLoop);
    expect(ticketCounter).to.have.property('openingHours').and.to.equal(openingHours);
    expect(ticketCounter).to.have.property('type').and.to.equal(referencePointElementType);
  };

  it('Step-1: New reduced Ticket Counter', () => {
    ticketCounterSloid = `${parentServicePointSloid}:${referencePointElementType}1`; // Has to be initialized here, because before the input-variables are not known
    CommonUtils.post('/prm-directory/v1/contact-points', {
      sloid: ticketCounterSloid,
      validFrom: validFrom,
      validTo: validTo,
      parentServicePointSloid: parentServicePointSloid,
      designation: designation,
      additionalInformation: additionalInformation,
      inductionLoop: inductionLoop,
      openingHours: openingHours,
      wheelchairAccess: wheelchairAccess,
      type: referencePointElementType
    }).then((response) => {

      expect(response.status).to.equal(201);
      const ticketCounter = response.body;
      validateCommonTicketCounterAttributes(ticketCounter, designation, null);
      ticketCounterId = ticketCounter.id;
    });
  });

  it('Step-2: Get reduced Ticket Counter', () => {
    CommonUtils.get(`/prm-directory/v1/contact-points?parentServicePointSloids=${parentServicePointSloid}`).then((response) => {
      expect(response.status).to.equal(200);
      const ticketCounter = ReleaseApiUtils.getPrmObjectById(response.body, ticketCounterId, false, 1)
      validateCommonTicketCounterAttributes(ticketCounter, designation, null);
      etagVersion = ticketCounter.etagVersion;
    });
  });

  it('Step-3: Change reduced Ticket Counter', () => {
    const updatedDesignation = "Ticket Counter 2";
    const updatedAdditionalInformation = "Additional Information 2";

    CommonUtils.put(`/prm-directory/v1/contact-points/${ticketCounterId}`, {
      sloid: ticketCounterSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersion,
      parentServicePointSloid: parentServicePointSloid,
      designation: updatedDesignation,
      additionalInformation: updatedAdditionalInformation,
      inductionLoop: inductionLoop,
      openingHours: openingHours,
      wheelchairAccess: wheelchairAccess,
      type: referencePointElementType
    }).then((response) => {
      expect(response.status).to.equal(200);

      const ticketCounter = ReleaseApiUtils.getPrmObjectById(response.body, ticketCounterId, true, 1);
      validateCommonTicketCounterAttributes(ticketCounter, updatedDesignation, updatedAdditionalInformation);
    });
  });
});

describe('PRM: New reduced Platform', {testIsolation: false}, () => {
  const referencePointElementType = "PLATFORM";
  let platformId = -1;

  const validFrom = ReleaseApiUtils.todayAsAtlasString();
  const validTo = ReleaseApiUtils.todayAsAtlasString();

  let height = null;
  let inclinationLongitudinal = null;
  let additionalInformation = null;
  let wheelchairAreaLength = null;
  let wheelchairAreaWidth = null;
  let inductionLoop = null;
  let partialElevation = null;
  let tactileSystem = null;
  let vehicleAccess = null;
  let infoOpportunities = [];

  const validateCommonPlatformAttributes = (platform) => {
    validatePrmObject(platform, validFrom, validTo)

    expect(platform).to.have.property('sloid').and.to.equal(trafficPointSloid);
    expect(platform).to.have.property('parentServicePointSloid').and.to.equal(parentServicePointSloid);
    expect(platform).to.have.property('additionalInformation').and.to.equal(additionalInformation);
    expect(platform).to.have.property('height').and.to.be.equal(height);
    expect(platform).to.have.property('inclinationLongitudinal').and.to.be.equal(inclinationLongitudinal);
    expect(platform).to.have.property('infoOpportunities').and.to.be.an('array').and.to.deep.equal(infoOpportunities);
    expect(platform).to.have.property('partialElevation').and.to.be.equal(partialElevation);
    expect(platform).to.have.property('tactileSystem').and.to.be.equal(tactileSystem);
    expect(platform).to.have.property('vehicleAccess').and.to.be.equal(vehicleAccess);
    expect(platform).to.have.property('wheelchairAreaLength').and.to.be.equal(wheelchairAreaLength);
    expect(platform).to.have.property('wheelchairAreaWidth').and.to.be.equal(wheelchairAreaWidth);
  };

  it('Step-1: New reduced Platform', () => {
    // Docu: https://confluence.sbb.ch/display/ATLAS/Data-Fact-Matrix
    CommonUtils.post('/prm-directory/v1/platforms', {
      sloid: trafficPointSloid,
      validFrom: validFrom,
      validTo: validTo,
      parentServicePointSloid: parentServicePointSloid,
      numberWithoutCheckDigit: numberWithoutCheckDigit, // From ServicePoint
      additionalInformation: additionalInformation,
      height: height,
      inclinationLongitudinal: inclinationLongitudinal,
      infoOpportunities: infoOpportunities,
      partialElevation: partialElevation,
      tactileSystem: tactileSystem,
      vehicleAccess: vehicleAccess,
      wheelchairAreaLength: wheelchairAreaLength,
      wheelchairAreaWidth: wheelchairAreaWidth
    }).then((response) => {
      expect(response.status).to.equal(201);

      const platform = response.body;
      validateCommonPlatformAttributes(platform);
      platformId = platform.id;
    });
  });

  it('Step-2: Check reduced Platform', () => {
    CommonUtils.get(`/prm-directory/v1/platforms?sloids=${trafficPointSloid}`).then((response) => {
      expect(response.status).to.equal(200);

      const platform = ReleaseApiUtils.getPrmObjectById(response.body, platformId, false, 1)
      validateCommonPlatformAttributes(platform);
      etagVersion = platform.etagVersion;
    });
  });

  it('Step-3: Update reduced Platform', () => {
    height = ReleaseApiUtils.getRoundedRandomFloat(0, 100, 3);
    inclinationLongitudinal = ReleaseApiUtils.getRoundedRandomFloat(0, 100, 3);
    additionalInformation = "additionalInformation2";
    wheelchairAreaLength = ReleaseApiUtils.getRoundedRandomFloat(0, 100, 3);
    wheelchairAreaWidth = ReleaseApiUtils.getRoundedRandomFloat(0, 100, 3);
    inductionLoop = ReleaseApiUtils.extractOneRandomValue(PrmConstants.basicValuesAndNotApplicableAndPartially());
    partialElevation = ReleaseApiUtils.extractOneRandomValue(PrmConstants.booleanValues());
    tactileSystem = ReleaseApiUtils.extractOneRandomValue(PrmConstants.basicValues());
    vehicleAccess = ReleaseApiUtils.extractOneRandomValue(PrmConstants.vehicleAccessValues());

    CommonUtils.put(`/prm-directory/v1/platforms/${platformId}`, {
      sloid: trafficPointSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersion,
      parentServicePointSloid: parentServicePointSloid,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
      additionalInformation: additionalInformation,
      height: height,
      inclinationLongitudinal: inclinationLongitudinal,
      infoOpportunities: infoOpportunities,
      partialElevation: partialElevation,
      tactileSystem: tactileSystem,
      vehicleAccess: vehicleAccess,
      wheelchairAreaLength: wheelchairAreaLength,
      wheelchairAreaWidth: wheelchairAreaWidth
    }).then((response) => {
      expect(response.status).to.equal(200);

      const platform = ReleaseApiUtils.getPrmObjectById(response.body, platformId, true, 1);
      validateCommonPlatformAttributes(platform);
    });
  });
});


