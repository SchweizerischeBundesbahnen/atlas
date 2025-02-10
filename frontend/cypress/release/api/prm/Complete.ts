import CommonUtils from '../../../support/util/common-utils';
import ReleaseApiUtils from '../../../support/util/release-api-utils';
import PrmConstants from './PrmConstants';

// Documentation: PRM-Data-Fact-Matrix: https://confluence.sbb.ch/x/vgdpl

let parentServicePointSloid = '';
let referencePointSloid = '';
let numberWithoutCheckDigit = -1;
let trafficPointSloid = '';
let etagVersion = -1;
let etagVersionRelation = -1;

const statusForAllPRMObjects = 'VALIDATED';

// Means of Transport values
const TRAIN = 'TRAIN';
const METRO = 'METRO';
// The service-point needs to have a meansOfTransport, so that a PRM-stopPoint can be created.
// The meansOfTransport TRAIN leads to the complete recording variant
// Code: https://code.sbb.ch/projects/KI_ATLAS/repos/atlas/browse/base-atlas/src/main/java/ch/sbb/atlas/servicepoint/enumeration/MeanOfTransport.java
let meansOfTransport = [TRAIN];

const validatePrmObject = (object, validFrom: string, validTo: string) => {
  expect(object).to.have.property('id').that.is.a('number');

  expect(object)
    .to.have.property('validFrom')
    .that.is.a('string')
    .and.to.equal(validFrom);
  expect(object)
    .to.have.property('validTo')
    .that.is.a('string')
    .and.to.equal(validTo);
  expect(object)
    .to.have.property('number')
    .to.have.property('number')
    .that.is.a('number')
    .and.to.equal(numberWithoutCheckDigit);
  expect(object)
    .to.have.property('status')
    .that.is.a('string')
    .and.to.equal(statusForAllPRMObjects);

  expect(object)
    .to.have.property('etagVersion')
    .that.is.a('number')
    .and.greaterThan(-1);
};

describe(
  'Create ServicePoint and TrafficPoint for complete StopPoint',
  { testIsolation: false },
  () => {
    let sboid = '';

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

    it('Step-3: New ServicePoint', () => {
      CommonUtils.post('/service-point-directory/v1/service-points', {
        country: 'SWITZERLAND',
        designationOfficial: ReleaseApiUtils.today(),
        businessOrganisation: sboid,
        meansOfTransport: meansOfTransport,
        validFrom: '2019-06-18',
        validTo: '9999-12-31',
      }).then((response) => {
        expect(response.status).to.equal(
          CommonUtils.HTTP_REST_API_RESPONSE_CREATED
        );

        expect(response.body).to.have.property('sloid').that.is.a('string');
        parentServicePointSloid = response.body.sloid;

        expect(response.body)
          .property('number')
          .property('number')
          .that.is.a('number');
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
        trafficPointElementType: 'BOARDING_PLATFORM',
      }).then((response) => {
        expect(response.status).to.equal(
          CommonUtils.HTTP_REST_API_RESPONSE_CREATED
        );
        expect(response.body.parentSloid).to.equal(parentServicePointSloid);

        expect(response.body).to.have.property('sloid').that.is.a('string');
        trafficPointSloid = response.body.sloid;

        expect(response.body)
          .property('servicePointNumber')
          .property('number')
          .that.is.a('number')
          .and.equals(numberWithoutCheckDigit);
      });
    });
  }
);

describe('PRM: Complete Stop Point', { testIsolation: false }, () => {
  let stopPointId = -1;

  const validate = (stopPoint: any) => {
    validatePrmObject(stopPoint, validFrom, validTo);
    expect(stopPoint).to.have.property('reduced').to.equal(false);
    expect(stopPoint)
      .to.have.property('sloid')
      .that.is.a('string')
      .and.to.equal(parentServicePointSloid);
    expect(stopPoint)
      .to.have.property('freeText')
      .that.is.a('string')
      .and.to.equal(freeText);

    expect(stopPoint).to.have.property('meansOfTransport').that.is.an('array');
    expect(stopPoint)
      .to.have.property('meansOfTransport')
      .to.have.property('length')
      .to.equal(meansOfTransport.length);
    meansOfTransport.forEach((mot) => {
      expect(stopPoint.meansOfTransport).to.include(mot);
    });

    expect(stopPoint)
      .to.have.property('alternativeTransport')
      .and.to.equal(alternativeTransport);
    expect(stopPoint)
      .to.have.property('alternativeTransportCondition')
      .and.to.equal(alternativeTransportCondition);
    expect(stopPoint)
      .to.have.property('assistanceAvailability')
      .and.to.equal(assistanceAvailability);
    expect(stopPoint)
      .to.have.property('assistanceCondition')
      .and.to.equal(assistanceCondition);
    expect(stopPoint)
      .to.have.property('assistanceService')
      .and.to.equal(assistanceService);
    expect(stopPoint)
      .to.have.property('audioTicketMachine')
      .and.to.equal(audioTicketMachine);
    expect(stopPoint)
      .to.have.property('dynamicAudioSystem')
      .and.to.equal(dynamicAudioSystem);
    expect(stopPoint)
      .to.have.property('dynamicOpticSystem')
      .and.to.equal(dynamicOpticSystem);
    expect(stopPoint)
      .to.have.property('infoTicketMachine')
      .and.to.equal(infoTicketMachine);
    expect(stopPoint).to.have.property('visualInfo').and.to.equal(visualInfo);
    expect(stopPoint)
      .to.have.property('wheelchairTicketMachine')
      .and.to.equal(wheelchairTicketMachine);
    expect(stopPoint)
      .to.have.property('assistanceRequestFulfilled')
      .and.to.equal(assistanceRequestFulfilled);
    expect(stopPoint)
      .to.have.property('ticketMachine')
      .and.to.equal(ticketMachine);
    expect(stopPoint)
      .to.have.property('additionalInformation')
      .and.to.equal(additionalInformation);

    expect(stopPoint).to.have.property('freeText').and.to.equal(freeText);
    expect(stopPoint).to.have.property('address').and.to.equal(address);
    expect(stopPoint).to.have.property('zipCode').and.to.equal(zipCode);
    expect(stopPoint).to.have.property('city').and.to.equal(city);

    // TODO: Uncomment, when ATLAS-2728 is fixed
    //       expect(stopPoint)
    //       .to.have.property('interoperable')
    //       .and.to.equal(interoperable);
    expect(stopPoint).to.have.property('url').and.to.equal(url);
  };

  const validFrom = ReleaseApiUtils.todayAsAtlasString();
  const validTo = ReleaseApiUtils.todayAsAtlasString();
  const additionalInformation = null;
  const alternativeTransport = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );
  const assistanceAvailability = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );
  const assistanceService = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );
  const audioTicketMachine = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );
  const dynamicAudioSystem = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );
  const dynamicOpticSystem = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );
  const visualInfo = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );
  const wheelchairTicketMachine = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );
  const assistanceRequestFulfilled = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValues()
  );
  const ticketMachine = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValues()
  );
  const freeText = 'freeText';
  const address = 'address';
  const zipCode = 'zipCode';
  const city = 'city';
  const alternativeTransportCondition = 'alternativeTransportCondition';
  const assistanceCondition = 'assistanceCondition';
  const infoTicketMachine = 'infoTicketMachine';
  const url = 'url';
  const interoperable = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.booleanValues()
  );

  it('Step-1: New complete Stop Point', () => {
    CommonUtils.post('/prm-directory/v1/stop-points', {
      sloid: parentServicePointSloid,
      validFrom: validFrom,
      validTo: validTo,
      meansOfTransport: meansOfTransport,
      freeText: freeText,
      address: address,
      zipCode: zipCode,
      city: city,
      alternativeTransport: alternativeTransport,
      alternativeTransportCondition: alternativeTransportCondition,
      assistanceAvailability: assistanceAvailability,
      assistanceCondition: assistanceCondition,
      assistanceService: assistanceService,
      audioTicketMachine: audioTicketMachine,
      additionalInformation: additionalInformation,
      dynamicAudioSystem: dynamicAudioSystem,
      dynamicOpticSystem: dynamicOpticSystem,
      infoTicketMachine: infoTicketMachine,
      interoperable: interoperable,
      url: url,
      visualInfo: visualInfo,
      wheelchairTicketMachine: wheelchairTicketMachine,
      assistanceRequestFulfilled: assistanceRequestFulfilled,
      ticketMachine: ticketMachine,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
    }).then((response) => {
      expect(response.status).to.equal(
        CommonUtils.HTTP_REST_API_RESPONSE_CREATED
      );

      const stopPoint = response.body;
      validate(stopPoint);
      stopPointId = stopPoint.id;
    });
  });

  it('Step-2: Check complete Stop Point', () => {
    CommonUtils.get(
      `/prm-directory/v1/stop-points?sloids=${parentServicePointSloid}&fromDate=${validFrom}&toDate=${validTo}`
    ).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const stopPoint = ReleaseApiUtils.getPrmObjectById(
        response.body,
        stopPointId,
        false,
        1
      );
      validate(stopPoint);
      etagVersion = stopPoint.etagVersion;
    });
  });

  it('Step-3: Update complete Stop Point', () => {
    meansOfTransport = meansOfTransport.concat(METRO);

    CommonUtils.put(`/prm-directory/v1/stop-points/${stopPointId}`, {
      sloid: parentServicePointSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersion,
      meansOfTransport: meansOfTransport,
      freeText: freeText,
      address: address,
      zipCode: zipCode,
      city: city,
      alternativeTransport: alternativeTransport,
      alternativeTransportCondition: alternativeTransportCondition,
      assistanceAvailability: assistanceAvailability,
      assistanceCondition: assistanceCondition,
      assistanceService: assistanceService,
      audioTicketMachine: audioTicketMachine,
      additionalInformation: additionalInformation,
      dynamicAudioSystem: dynamicAudioSystem,
      dynamicOpticSystem: dynamicOpticSystem,
      infoTicketMachine: infoTicketMachine,
      url: url,
      visualInfo: visualInfo,
      wheelchairTicketMachine: wheelchairTicketMachine,
      assistanceRequestFulfilled: assistanceRequestFulfilled,
      ticketMachine: ticketMachine,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
    }).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const stopPoint = ReleaseApiUtils.getPrmObjectById(
        response.body,
        stopPointId,
        true,
        1
      );
      validate(stopPoint);
      etagVersion = stopPoint.etagVersion;
    });
  });
});

describe('PRM: Complete Reference Point', { testIsolation: false }, () => {
  let designation = 'designation';
  let referencePointId = -1;

  const validFrom = ReleaseApiUtils.todayAsAtlasString();
  const validTo = ReleaseApiUtils.todayAsAtlasString();
  const referencePointType = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.referencePointTypeValues()
  );
  const additionalInformation = 'additional';
  const mainReferencePoint = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.booleanValues()
  );

  const validate = (referencePoint: any) => {
    validatePrmObject(referencePoint, validFrom, validTo);
    expect(referencePoint)
      .to.have.property('sloid')
      .and.to.equal(referencePointSloid);
    expect(referencePoint)
      .to.have.property('parentServicePointSloid')
      .and.to.equal(parentServicePointSloid);
    expect(referencePoint)
      .to.have.property('designation')
      .and.to.equal(designation);
    expect(referencePoint)
      .to.have.property('referencePointType')
      .and.to.equal(referencePointType);
    expect(referencePoint)
      .to.have.property('additionalInformation')
      .and.to.equal(additionalInformation);
    expect(referencePoint)
      .to.have.property('mainReferencePoint')
      .and.to.equal(mainReferencePoint);
  };

  it('Step-1: Create new complete Reference Point', () => {
    referencePointSloid = `${parentServicePointSloid}:referencePoint1`; // Has to be initialized here, because before parentServicePointSloid is not known

    CommonUtils.post('/prm-directory/v1/reference-points', {
      sloid: referencePointSloid,
      validFrom: validFrom,
      validTo: validTo,
      parentServicePointSloid: parentServicePointSloid,
      designation: designation,
      referencePointType: referencePointType,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
      additionalInformation: additionalInformation,
      mainReferencePoint: mainReferencePoint,
    }).then((response) => {
      expect(response.status).to.equal(
        CommonUtils.HTTP_REST_API_RESPONSE_CREATED
      );

      const referencePoint = response.body;
      validate(referencePoint);
      referencePointId = referencePoint.id;
    });
  });

  it('Step-2: Check complete Reference Point', () => {
    CommonUtils.get(
      `/prm-directory/v1/reference-points?sloids=${referencePointSloid}`
    ).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const referencePoint = ReleaseApiUtils.getPrmObjectById(
        response.body,
        referencePointId,
        false,
        1
      );
      validate(referencePoint);
      etagVersion = referencePoint.etagVersion;
    });
  });

  it('Step-3: Update complete Reference Point', () => {
    designation = 'designation-changed';

    CommonUtils.put(`/prm-directory/v1/reference-points/${referencePointId}`, {
      sloid: referencePointSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersion,
      parentServicePointSloid: parentServicePointSloid,
      designation: designation,
      referencePointType: referencePointType,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
      additionalInformation: additionalInformation,
      mainReferencePoint: mainReferencePoint,
    }).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const referencePoint = ReleaseApiUtils.getPrmObjectById(
        response.body,
        referencePointId,
        true,
        1
      );
      validate(referencePoint);
      etagVersion = referencePoint.etagVersion;
    });
  });
});

describe('PRM: Check relations for sloid', { testIsolation: false }, () => {
  const NO_RELATIONSHIP_YET = 0;

  const checkResponse = (response) => {
    expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);
    expect(response.body.totalCount).to.equal(NO_RELATIONSHIP_YET);
    expect(response.body)
      .to.have.property('objects')
      .that.is.an('array')
      .to.have.property('length')
      .to.equal(NO_RELATIONSHIP_YET);
  };

  it('Step-1: No relation for sloid', () => {
    CommonUtils.get(`/prm-directory/v1/relations/${referencePointSloid}`).then(
      (response) => {
        expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);
        expect(response.body)
          .to.be.an('array')
          .to.have.property('length')
          .to.equal(NO_RELATIONSHIP_YET);
      }
    );
  });

  it('Step-2: No TOILET-relation for sloid', () => {
    CommonUtils.get(
      `/prm-directory/v1/relations?sloids=${referencePointSloid}&referencePointElementTypes=TOILET`
    ).then((response) => {
      checkResponse(response);
    });
  });

  it('Step-3: No TICKET_COUNTER-relation for sloid', () => {
    CommonUtils.get(
      `/prm-directory/v1/relations?sloids=${referencePointSloid}&referencePointElementTypes=CONTACT_POINT`
    ).then((response) => {
      checkResponse(response);
    });
  });

  it('Step-4: No PLATFORM-relation for sloid', () => {
    CommonUtils.get(
      `/prm-directory/v1/relations?sloids=${referencePointSloid}&referencePointElementTypes=PLATFORM`
    ).then((response) => {
      checkResponse(response);
    });
  });

  it('Step-5: No PARKING_LOT-relation for sloid', () => {
    CommonUtils.get(
      `/prm-directory/v1/relations?sloids=${referencePointSloid}&referencePointElementTypes=PARKING_LOT`
    ).then((response) => {
      checkResponse(response);
    });
  });

  it('Step-6: No relation for parent-sloid', () => {
    CommonUtils.get(
      `/prm-directory/v1/relations?parentServicePointSloids=${parentServicePointSloid}`
    ).then((response) => {
      checkResponse(response);
    });
  });
});

describe('PRM: Complete Toilet', { testIsolation: false }, () => {
  let toiletId = -1;
  let relationId = -1;
  let toiletSloid = '';
  let etagVersionToilet = -1;

  let tactileVisualMarks = CommonUtils.TO_BE_COMPLETED;
  let contrastingAreas = CommonUtils.TO_BE_COMPLETED;
  let stepFreeAccess = CommonUtils.TO_BE_COMPLETED;

  let designation = 'öffentliches WC im Bf Basel Bad Bf';
  let additionalInformation = 'additionalInformation';
  let wheelchairToilet = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );

  const validFrom = ReleaseApiUtils.todayAsAtlasString();
  const validTo = ReleaseApiUtils.todayAsAtlasString();
  const referencePointElementType = `TOILET`;
  const numberOfExpectedToilets = 1;

  const validateCommonCompleteToiletAttributes = (prmObject) => {
    validatePrmObject(prmObject, validFrom, validTo);

    expect(prmObject)
      .to.have.property('parentServicePointSloid')
      .that.is.a('string')
      .and.to.equal(parentServicePointSloid);
  };

  const validate = (toilet) => {
    validateCommonCompleteToiletAttributes(toilet);
    expect(toilet)
      .to.have.property('sloid')
      .that.is.a('string')
      .and.to.equal(toiletSloid);
    expect(toilet)
      .to.have.property('designation')
      .that.is.a('string')
      .and.to.equal(designation);
    expect(toilet)
      .to.have.property('wheelchairToilet')
      .that.is.a('string')
      .and.to.equal(wheelchairToilet);
    expect(toilet)
      .to.have.property('additionalInformation')
      .and.to.equal(additionalInformation);
  };

  const validateRelation = (relation) => {
    validateCommonCompleteToiletAttributes(relation);
    expect(relation)
      .to.have.property('referencePointSloid')
      .that.is.a('string')
      .and.to.equal(referencePointSloid);
    expect(relation)
      .to.have.property('elementSloid')
      .that.is.a('string')
      .and.to.equal(toiletSloid);
    expect(relation)
      .to.have.property('parentServicePointSloid')
      .that.is.a('string')
      .and.to.equal(parentServicePointSloid);
    expect(relation)
      .to.have.property('referencePointElementType')
      .that.is.a('string')
      .and.to.equal(referencePointElementType);
    expect(relation)
      .to.have.property('tactileVisualMarks')
      .that.is.a('string')
      .and.to.equal(tactileVisualMarks);
    expect(relation)
      .to.have.property('contrastingAreas')
      .that.is.a('string')
      .and.to.equal(contrastingAreas);
    expect(relation)
      .to.have.property('stepFreeAccess')
      .that.is.a('string')
      .and.to.equal(stepFreeAccess);
  };

  it('Step-1: New complete Toilet', () => {
    toiletSloid = `${parentServicePointSloid}:${referencePointElementType}1`; // Initialisierung des sloid

    CommonUtils.post('/prm-directory/v1/toilets', {
      sloid: toiletSloid,
      validFrom: validFrom,
      validTo: validTo,
      parentServicePointSloid: parentServicePointSloid,
      designation: designation,
      additionalInformation: additionalInformation,
      wheelchairToilet: wheelchairToilet,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
    }).then((response) => {
      expect(response.status).to.equal(
        CommonUtils.HTTP_REST_API_RESPONSE_CREATED
      );
      const toilet = response.body;
      validate(toilet);
      toiletId = toilet.id;
    });
  });

  it('Step-2: Get complete Toilet', () => {
    CommonUtils.get(`/prm-directory/v1/toilets?sloids=${toiletSloid}`).then(
      (response) => {
        expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

        const toilet = ReleaseApiUtils.getPrmObjectById(
          response.body,
          toiletId,
          false,
          1
        );
        validate(toilet);
        etagVersionToilet = toilet.etagVersion;
      }
    );
  });

  const checkRelation = (queryParameters: string) => {
    CommonUtils.get(`/prm-directory/v1/relations?${queryParameters}`).then(
      (response) => {
        expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

        expect(response.body)
          .to.have.property('objects')
          .that.is.an('array')
          .and.to.have.property('length')
          .which.equals(numberOfExpectedToilets);

        const toiletRelation =
          response.body.objects[numberOfExpectedToilets - 1];
        validateRelation(toiletRelation);

        relationId = toiletRelation.id;
        etagVersionRelation = toiletRelation.etagVersion;
      }
    );
  };

  const checkRelations = () => {
    checkRelation(
      `parentServicePointSloids=${parentServicePointSloid}&referencePointElementTypes=${referencePointElementType}`
    );
    checkRelation(
      `referencePointSloids=${referencePointSloid}&referencePointElementTypes=${referencePointElementType}`
    );
    checkRelation(`sloids=${toiletSloid}`);

    CommonUtils.get(`/prm-directory/v1/relations/${toiletSloid}`).then(
      (response) => {
        expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);
        const relation = ReleaseApiUtils.getPrmObjectById(
          response.body,
          relationId,
          true,
          1
        );
        validateRelation(relation);
      }
    );
  };

  it('Step-3: Check relations after create', () => {
    checkRelations();
  });

  it('Step-4: Update complete toilet relation', () => {
    tactileVisualMarks = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.basicValuesAndNotApplicableAndWithRemoteControl()
    );
    contrastingAreas = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.basicValuesAndNotApplicableAndPartially()
    );
    stepFreeAccess = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.stepFreeAccessValues()
    );

    CommonUtils.put(`/prm-directory/v1/relations/${relationId}`, {
      sloid: toiletSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersionRelation,
      parentServicePointSloid: parentServicePointSloid,
      tactileVisualMarks: tactileVisualMarks,
      contrastingAreas: contrastingAreas,
      stepFreeAccess: stepFreeAccess,
      referencePointElementType: referencePointElementType,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
      referencePointSloid: referencePointSloid,
    }).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const relation = ReleaseApiUtils.getPrmObjectById(
        response.body,
        relationId,
        true,
        1
      );
      validateRelation(relation);
      etagVersionRelation = relation.etagVersion;
      relationId = relation.id;
    });
  });

  it('Step-5: Check relations after update', () => {
    checkRelations();
  });

  it('Step-6: Update complete Toilet', () => {
    designation = 'designation2';
    additionalInformation = 'additionalInformation2';
    wheelchairToilet = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.basicValuesAndNotApplicableAndPartially()
    );

    CommonUtils.put(`/prm-directory/v1/toilets/${toiletId}`, {
      sloid: toiletSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersionToilet,
      parentServicePointSloid: parentServicePointSloid,
      designation: designation,
      additionalInformation: additionalInformation,
      wheelchairToilet: wheelchairToilet,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
    }).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const toilet = ReleaseApiUtils.getPrmObjectById(
        response.body,
        toiletId,
        true,
        1
      );
      validate(toilet);
      etagVersionToilet = toilet.etagVersion;
    });
  });

  it('Step-7: Re-Check relations after toilet update', () => {
    checkRelations();
  });
});

describe('PRM: Complete Ticket Counter', { testIsolation: false }, () => {
  let ticketCounterId = -1;
  let ticketCounterSloid = '';
  let relationId = -1;
  let etagVersionTicketCounter = -1;

  let tactileVisualMarks = CommonUtils.TO_BE_COMPLETED;
  let contrastingAreas = CommonUtils.TO_BE_COMPLETED;
  let stepFreeAccess = CommonUtils.TO_BE_COMPLETED;

  let designation = 'öffentliches Ticket-Counter im Bf Basel Bad Bf';
  let additionalInformation = 'additionalInformation';
  let wheelchairAccess = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );

  const validFrom = ReleaseApiUtils.todayAsAtlasString();
  const validTo = ReleaseApiUtils.todayAsAtlasString();
  const contactPointType = 'TICKET_COUNTER';
  const referencePointElementType = 'CONTACT_POINT';
  const openingHours = 'Öffnungszeiten: 08:00 - 20:00';
  const numberOfExpectedTicketCounters = 1;
  const inductionLoop = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );

  const validateCommonCompleteTicketCounterAttributes = (prmObject) => {
    validatePrmObject(prmObject, validFrom, validTo);
    expect(prmObject)
      .to.have.property('parentServicePointSloid')
      .that.is.a('string')
      .and.to.equal(parentServicePointSloid);
  };

  const validate = (ticketCounter) => {
    validateCommonCompleteTicketCounterAttributes(ticketCounter);
    expect(ticketCounter)
      .to.have.property('sloid')
      .that.is.a('string')
      .and.to.equal(ticketCounterSloid);
    expect(ticketCounter)
      .to.have.property('designation')
      .that.is.a('string')
      .and.to.equal(designation);
    expect(ticketCounter)
      .to.have.property('additionalInformation')
      .and.to.equal(additionalInformation);
    expect(ticketCounter)
      .to.have.property('inductionLoop')
      .and.to.equal(inductionLoop);
    expect(ticketCounter)
      .to.have.property('wheelchairAccess')
      .and.to.equal(wheelchairAccess);
    expect(ticketCounter)
      .to.have.property('openingHours')
      .and.to.equal(openingHours);
    expect(ticketCounter)
      .to.have.property('type')
      .that.is.a('string')
      .and.to.equal(contactPointType);
  };

  const validateRelation = (relation) => {
    validateCommonCompleteTicketCounterAttributes(relation);
    expect(relation)
      .to.have.property('referencePointSloid')
      .that.is.a('string')
      .and.to.equal(referencePointSloid);
    expect(relation)
      .to.have.property('elementSloid')
      .that.is.a('string')
      .and.to.equal(ticketCounterSloid);
    expect(relation)
      .to.have.property('parentServicePointSloid')
      .that.is.a('string')
      .and.to.equal(parentServicePointSloid);
    expect(relation)
      .to.have.property('referencePointElementType')
      .that.is.a('string')
      .and.to.equal(referencePointElementType);
    expect(relation)
      .to.have.property('tactileVisualMarks')
      .that.is.a('string')
      .and.to.equal(tactileVisualMarks);
    expect(relation)
      .to.have.property('contrastingAreas')
      .that.is.a('string')
      .and.to.equal(contrastingAreas);
    expect(relation)
      .to.have.property('stepFreeAccess')
      .that.is.a('string')
      .and.to.equal(stepFreeAccess);
  };

  it('Step-1: New complete Ticket Counter', () => {
    ticketCounterSloid = `${parentServicePointSloid}:${contactPointType}1`;

    // TODO: Export POST/PUT-bodies?
    CommonUtils.post('/prm-directory/v1/contact-points', {
      type: contactPointType,
      sloid: ticketCounterSloid,
      validFrom: validFrom,
      validTo: validTo,
      parentServicePointSloid: parentServicePointSloid,
      designation: designation,
      additionalInformation: additionalInformation,
      inductionLoop: inductionLoop,
      openingHours: openingHours,
      wheelchairAccess: wheelchairAccess,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
    }).then((response) => {
      expect(response.status).to.equal(
        CommonUtils.HTTP_REST_API_RESPONSE_CREATED
      );
      const ticketCounter = response.body;
      validate(ticketCounter);
      ticketCounterId = ticketCounter.id;
      etagVersionTicketCounter = ticketCounter.etagVersion;
    });
  });

  it('Step-2: Get complete Ticket Counter', () => {
    CommonUtils.get(
      `/prm-directory/v1/contact-points?sloids=${ticketCounterSloid}`
    ).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const ticketCounter = ReleaseApiUtils.getPrmObjectById(
        response.body,
        ticketCounterId,
        false,
        1
      );
      validate(ticketCounter);
      etagVersionTicketCounter = ticketCounter.etagVersion;
    });
  });

  const checkRelation = (queryParameters) => {
    CommonUtils.get(`/prm-directory/v1/relations?${queryParameters}`).then(
      (response) => {
        expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

        expect(response.body)
          .to.have.property('objects')
          .that.is.an('array')
          .and.to.have.property('length')
          .which.equals(numberOfExpectedTicketCounters);

        const relation =
          response.body.objects[numberOfExpectedTicketCounters - 1];
        validateRelation(relation);

        relationId = relation.id;
        etagVersionRelation = relation.etagVersion;
      }
    );
  };

  const checkRelations = () => {
    checkRelation(
      `parentServicePointSloids=${parentServicePointSloid}&referencePointElementTypes=${referencePointElementType}`
    );
    checkRelation(
      `referencePointSloids=${referencePointSloid}&referencePointElementTypes=${referencePointElementType}`
    );
    checkRelation(`sloids=${ticketCounterSloid}`);

    CommonUtils.get(`/prm-directory/v1/relations/${ticketCounterSloid}`).then(
      (response) => {
        expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);
        const relation = ReleaseApiUtils.getPrmObjectById(
          response.body,
          relationId,
          true,
          1
        );
        validateRelation(relation);
      }
    );
  };

  it('Step-3: Check relations after create', () => {
    checkRelations();
  });

  it('Step-4: Update complete ticket counter relation', () => {
    tactileVisualMarks = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.basicValuesAndNotApplicableAndWithRemoteControl()
    );
    contrastingAreas = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.basicValuesAndNotApplicableAndPartially()
    );
    stepFreeAccess = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.stepFreeAccessValues()
    );

    CommonUtils.put(`/prm-directory/v1/relations/${relationId}`, {
      sloid: ticketCounterSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersionRelation,
      parentServicePointSloid: parentServicePointSloid,
      tactileVisualMarks: tactileVisualMarks,
      contrastingAreas: contrastingAreas,
      stepFreeAccess: stepFreeAccess,
      referencePointElementType: referencePointElementType,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
      referencePointSloid: referencePointSloid,
    }).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const relation = ReleaseApiUtils.getPrmObjectById(
        response.body,
        relationId,
        true,
        1
      );
      validateRelation(relation);
      etagVersionRelation = relation.etagVersion;
      relationId = relation.id;
    });
  });

  it('Step-5: Check relations after update', () => {
    checkRelations();
  });

  it('Step-6: Update complete ticket counter', () => {
    designation = 'designation2';
    additionalInformation = 'additionalInformation2';
    wheelchairAccess = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.basicValuesAndNotApplicableAndPartially()
    );

    CommonUtils.put(`/prm-directory/v1/contact-points/${ticketCounterId}`, {
      type: contactPointType,
      sloid: ticketCounterSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersionTicketCounter,
      parentServicePointSloid: parentServicePointSloid,
      designation: designation,
      additionalInformation: additionalInformation,
      inductionLoop: inductionLoop,
      openingHours: openingHours,
      wheelchairAccess: wheelchairAccess,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
    }).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const ticketCounter = ReleaseApiUtils.getPrmObjectById(
        response.body,
        ticketCounterId,
        true,
        1
      );
      validate(ticketCounter);
      etagVersionTicketCounter = ticketCounter.etagVersion;
    });
  });

  it('Step-7: Re-Check relations after ticket counter update', () => {
    checkRelations();
  });
});

describe('PRM: Complete Platform', { testIsolation: false }, () => {
  let platformId = -1;
  let relationId = -1;
  let etagVersionPlatform = -1;
  let additionalInformation = 'additionalInformation';
  let boardingDevice = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.boardingDeviceValues()
  );

  let tactileVisualMarks = CommonUtils.TO_BE_COMPLETED;
  let contrastingAreas = CommonUtils.TO_BE_COMPLETED;
  let stepFreeAccess = CommonUtils.TO_BE_COMPLETED;

  const referencePointElementType = 'PLATFORM';
  const validFrom = ReleaseApiUtils.todayAsAtlasString();
  const validTo = ReleaseApiUtils.todayAsAtlasString();

  const adviceAccessInfo = 'adviceAccessInfo';

  const inclination = ReleaseApiUtils.getRoundedRandomFloat(0, 100, 3);
  const inclinationWidth = ReleaseApiUtils.getRoundedRandomFloat(0, 100, 3);
  const superelevation = ReleaseApiUtils.getRoundedRandomFloat(0, 10000, 3);

  const levelAccessWheelchair = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicable()
  );
  const contrastingAreasPlatform = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValues()
  );
  const dynamicAudio = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicable()
  );
  const dynamicVisual = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicable()
  );

  const validateCommonCompletePlatformAttributes = (prmObject) => {
    // TODO: Extract this method to the global scale, as it is for all complete PRM objects the same
    validatePrmObject(prmObject, validFrom, validTo);
    expect(prmObject)
      .to.have.property('parentServicePointSloid')
      .that.is.a('string')
      .and.to.equal(parentServicePointSloid);
  };

  const validate = (platform) => {
    validateCommonCompletePlatformAttributes(platform);
    expect(platform)
      .to.have.property('sloid')
      .that.is.a('string')
      .and.to.equal(trafficPointSloid);
    expect(platform)
      .to.have.property('additionalInformation')
      .and.to.equal(additionalInformation);
    expect(platform)
      .to.have.property('boardingDevice')
      .and.to.equal(boardingDevice);
    expect(platform)
      .to.have.property('adviceAccessInfo')
      .and.to.equal(adviceAccessInfo);
    expect(platform)
      .to.have.property('contrastingAreas')
      .and.to.equal(contrastingAreasPlatform);
    expect(platform)
      .to.have.property('dynamicAudio')
      .and.to.equal(dynamicAudio);
    expect(platform)
      .to.have.property('dynamicVisual')
      .and.to.equal(dynamicVisual);
    expect(platform).to.have.property('inclination').and.to.equal(inclination);
    expect(platform)
      .to.have.property('inclinationWidth')
      .and.to.equal(inclinationWidth);
    expect(platform)
      .to.have.property('levelAccessWheelchair')
      .and.to.equal(levelAccessWheelchair);
    expect(platform)
      .to.have.property('superelevation')
      .and.to.equal(superelevation);
  };

  const validateRelation = (relation) => {
    validateCommonCompletePlatformAttributes(relation);
    // TODO: Extract this method to the global scale, as it is for all complete PRM objects the same
    // Except the sloid/elementSloid
    expect(relation)
      .to.have.property('referencePointSloid')
      .that.is.a('string')
      .and.to.equal(referencePointSloid);
    expect(relation)
      .to.have.property('elementSloid')
      .that.is.a('string')
      .and.to.equal(trafficPointSloid);
    expect(relation)
      .to.have.property('parentServicePointSloid')
      .that.is.a('string')
      .and.to.equal(parentServicePointSloid);
    expect(relation)
      .to.have.property('referencePointElementType')
      .that.is.a('string')
      .and.to.equal(referencePointElementType);
    expect(relation)
      .to.have.property('tactileVisualMarks')
      .that.is.a('string')
      .and.to.equal(tactileVisualMarks);
    expect(relation)
      .to.have.property('contrastingAreas')
      .that.is.a('string')
      .and.to.equal(contrastingAreas);
    expect(relation)
      .to.have.property('stepFreeAccess')
      .that.is.a('string')
      .and.to.equal(stepFreeAccess);
  };

  it('Step-1: New complete Platform', () => {
    CommonUtils.post('/prm-directory/v1/platforms', {
      sloid: trafficPointSloid,
      validFrom: validFrom,
      validTo: validTo,
      parentServicePointSloid: parentServicePointSloid,
      boardingDevice: boardingDevice,
      adviceAccessInfo: adviceAccessInfo,
      additionalInformation: additionalInformation,
      contrastingAreas: contrastingAreasPlatform,
      dynamicAudio: dynamicAudio,
      dynamicVisual: dynamicVisual,
      inclination: inclination,
      inclinationWidth: inclinationWidth,
      levelAccessWheelchair: levelAccessWheelchair,
      superelevation: superelevation,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
    }).then((response) => {
      expect(response.status).to.equal(
        CommonUtils.HTTP_REST_API_RESPONSE_CREATED
      );
      const platform = response.body;
      validate(platform);
      platformId = platform.id;
      etagVersionPlatform = platform.etagVersion;
    });
  });

  it('Step-2: Get complete Platform', () => {
    CommonUtils.get(
      `/prm-directory/v1/platforms?sloids=${trafficPointSloid}`
    ).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const platform = ReleaseApiUtils.getPrmObjectById(
        response.body,
        platformId,
        false,
        1
      );
      validate(platform);
      etagVersionPlatform = platform.etagVersion;
    });
  });

  const checkRelation = (queryParameters) => {
    CommonUtils.get(`/prm-directory/v1/relations?${queryParameters}`).then(
      (response) => {
        expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);
        expect(response.body)
          .to.have.property('objects')
          .that.is.an('array')
          .and.to.have.property('length')
          .which.equals(1);
        const relation = response.body.objects[0];
        validateRelation(relation);
        relationId = relation.id;
        etagVersionRelation = relation.etagVersion;
      }
    );
  };

  const checkRelations = () => {
    checkRelation(
      `parentServicePointSloids=${parentServicePointSloid}&referencePointElementTypes=${referencePointElementType}`
    );
    checkRelation(
      `referencePointSloids=${referencePointSloid}&referencePointElementTypes=${referencePointElementType}`
    );
    checkRelation(`sloids=${trafficPointSloid}`);

    CommonUtils.get(`/prm-directory/v1/relations/${trafficPointSloid}`).then(
      (response) => {
        expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);
        const relation = ReleaseApiUtils.getPrmObjectById(
          response.body,
          relationId,
          true,
          1
        );
        validateRelation(relation);
      }
    );
  };

  it('Step-3: Check relations after create', () => {
    checkRelations();
  });

  it('Step-4: Update complete platform relation', () => {
    tactileVisualMarks = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.basicValuesAndNotApplicableAndWithRemoteControl()
    );
    contrastingAreas = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.basicValuesAndNotApplicableAndPartially()
    );
    stepFreeAccess = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.stepFreeAccessValues()
    );

    CommonUtils.put(`/prm-directory/v1/relations/${relationId}`, {
      sloid: trafficPointSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersionRelation,
      parentServicePointSloid: parentServicePointSloid,
      tactileVisualMarks: tactileVisualMarks,
      contrastingAreas: contrastingAreas,
      stepFreeAccess: stepFreeAccess,
      referencePointElementType: referencePointElementType,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
      referencePointSloid: referencePointSloid,
    }).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const relation = ReleaseApiUtils.getPrmObjectById(
        response.body,
        relationId,
        true,
        1
      );
      validateRelation(relation);
      etagVersionRelation = relation.etagVersion;
      relationId = relation.id;
    });
  });

  it('Step-5: Check relations after update', () => {
    checkRelations();
  });

  it('Step-6: Update complete Platform', () => {
    additionalInformation = 'additionalInformation2';
    boardingDevice = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.boardingDeviceValues()
    );

    CommonUtils.put(`/prm-directory/v1/platforms/${platformId}`, {
      sloid: trafficPointSloid,
      validFrom: validFrom,
      validTo: validTo,
      parentServicePointSloid: parentServicePointSloid,
      boardingDevice: boardingDevice,
      adviceAccessInfo: adviceAccessInfo,
      additionalInformation: additionalInformation,
      contrastingAreas: contrastingAreasPlatform,
      dynamicAudio: dynamicAudio,
      dynamicVisual: dynamicVisual,
      inclination: inclination,
      inclinationWidth: inclinationWidth,
      levelAccessWheelchair: levelAccessWheelchair,
      superelevation: superelevation,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
      etagVersion: etagVersionPlatform,
    }).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);
      const platform = ReleaseApiUtils.getPrmObjectById(
        response.body,
        platformId,
        true,
        1
      );
      validate(platform);
      etagVersionPlatform = platform.etagVersion;
    });
  });

  it('Step-7: Re-Check relations after Platform update', () => {
    checkRelations();
  });
});

describe('PRM: Complete Parking Lot', { testIsolation: false }, () => {
  let parkingLotId = -1;
  let parkingLotSloid = '';
  let relationId = -1;
  let etagVersionParkingLot = -1;

  let tactileVisualMarks = CommonUtils.TO_BE_COMPLETED;
  let contrastingAreas = CommonUtils.TO_BE_COMPLETED;
  let stepFreeAccess = CommonUtils.TO_BE_COMPLETED;
  let designation = 'öffentliches Parkplatz im Bf Basel Bad Bf';
  let additionalInformation = 'additionalInformation';

  let placesAvailable = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValues()
  );
  let prmPlacesAvailable = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValues()
  );

  const validFrom = ReleaseApiUtils.todayAsAtlasString();
  const validTo = ReleaseApiUtils.todayAsAtlasString();
  const referencePointElementType = 'PARKING_LOT';
  const openingHours = 'Öffnungszeiten: 08:00 - 20:00';

  const validateCommonCompleteParkingLotAttributes = (prmObject) => {
    validatePrmObject(prmObject, validFrom, validTo);
    expect(prmObject)
      .to.have.property('parentServicePointSloid')
      .that.is.a('string')
      .and.to.equal(parentServicePointSloid);
  };

  const validate = (parkingLot) => {
    validateCommonCompleteParkingLotAttributes(parkingLot);
    expect(parkingLot)
      .to.have.property('sloid')
      .that.is.a('string')
      .and.to.equal(parkingLotSloid);
    expect(parkingLot)
      .to.have.property('designation')
      .that.is.a('string')
      .and.to.equal(designation);
    expect(parkingLot)
      .to.have.property('additionalInformation')
      .and.to.equal(additionalInformation);
    expect(parkingLot)
      .to.have.property('placesAvailable')
      .and.to.equal(placesAvailable);
    expect(parkingLot)
      .to.have.property('prmPlacesAvailable')
      .and.to.equal(prmPlacesAvailable);
  };

  const validateRelation = (relation) => {
    validateCommonCompleteParkingLotAttributes(relation);
    expect(relation)
      .to.have.property('referencePointSloid')
      .that.is.a('string')
      .and.to.equal(referencePointSloid);
    expect(relation)
      .to.have.property('elementSloid')
      .that.is.a('string')
      .and.to.equal(parkingLotSloid);
    expect(relation)
      .to.have.property('parentServicePointSloid')
      .that.is.a('string')
      .and.to.equal(parentServicePointSloid);
    expect(relation)
      .to.have.property('tactileVisualMarks')
      .that.is.a('string')
      .and.to.equal(tactileVisualMarks);
    expect(relation)
      .to.have.property('contrastingAreas')
      .that.is.a('string')
      .and.to.equal(contrastingAreas);
    expect(relation)
      .to.have.property('stepFreeAccess')
      .that.is.a('string')
      .and.to.equal(stepFreeAccess);
  };

  const checkRelation = (queryParameters) => {
    CommonUtils.get(`/prm-directory/v1/relations?${queryParameters}`).then(
      (response) => {
        expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);
        expect(response.body)
          .to.have.property('objects')
          .that.is.an('array')
          .and.to.have.property('length')
          .which.equals(1);
        const relation = response.body.objects[0];
        validateRelation(relation);
        relationId = relation.id;
        etagVersionRelation = relation.etagVersion;
      }
    );
  };

  const checkRelations = () => {
    checkRelation(
      `parentServicePointSloids=${parentServicePointSloid}&referencePointElementTypes=${referencePointElementType}`
    );
    checkRelation(
      `referencePointSloids=${referencePointSloid}&referencePointElementTypes=${referencePointElementType}`
    );
    checkRelation(`sloids=${parkingLotSloid}`);

    CommonUtils.get(`/prm-directory/v1/relations/${parkingLotSloid}`).then(
      (response) => {
        expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);
        const relation = ReleaseApiUtils.getPrmObjectById(
          response.body,
          relationId,
          true,
          1
        );
        validateRelation(relation);
      }
    );
  };

  it('Step-1: New complete Parking Lot', () => {
    parkingLotSloid = `${parentServicePointSloid}:${referencePointElementType}1`;

    CommonUtils.post('/prm-directory/v1/parking-lots', {
      sloid: parkingLotSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: 0,
      parentServicePointSloid: parentServicePointSloid,
      designation: designation,
      additionalInformation: additionalInformation,
      placesAvailable: placesAvailable,
      prmPlacesAvailable: prmPlacesAvailable,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
    }).then((response) => {
      expect(response.status).to.equal(
        CommonUtils.HTTP_REST_API_RESPONSE_CREATED
      );
      const parkingLot = response.body;
      validate(parkingLot);
      parkingLotId = parkingLot.id;
    });
  });

  it('Step-2: Get complete Parking Lot', () => {
    CommonUtils.get(
      `/prm-directory/v1/parking-lots?sloids=${parkingLotSloid}`
    ).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const parkingLot = ReleaseApiUtils.getPrmObjectById(
        response.body,
        parkingLotId,
        false,
        1
      );
      validate(parkingLot);
      etagVersionParkingLot = parkingLot.etagVersion;
    });
  });

  it('Step-3: Check relations after create', () => {
    checkRelations();
  });

  it('Step-4: Update complete Parking Lot', () => {
    designation = 'designation2';
    additionalInformation = 'additionalInformation2';
    placesAvailable = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.basicValues()
    );
    prmPlacesAvailable = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.basicValues()
    );

    CommonUtils.put(`/prm-directory/v1/parking-lots/${parkingLotId}`, {
      sloid: parkingLotSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersionParkingLot,
      parentServicePointSloid: parentServicePointSloid,
      designation: designation,
      additionalInformation: additionalInformation,
      placesAvailable: placesAvailable,
      prmPlacesAvailable: prmPlacesAvailable,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
    }).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);
      const parkingLot = ReleaseApiUtils.getPrmObjectById(
        response.body,
        parkingLotId,
        true,
        1
      );
      validate(parkingLot);
      etagVersionParkingLot = parkingLot.etagVersion;
    });
  });

  it('Step-5: Check relations after parking lot update', () => {
    checkRelations();
  });

  it('Step-6: Update complete Parking Lot relation', () => {
    tactileVisualMarks = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.basicValuesAndNotApplicableAndWithRemoteControl()
    );
    contrastingAreas = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.basicValuesAndNotApplicableAndPartially()
    );
    stepFreeAccess = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.stepFreeAccessValues()
    );

    CommonUtils.put(`/prm-directory/v1/relations/${relationId}`, {
      sloid: parkingLotSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersionRelation,
      parentServicePointSloid: parentServicePointSloid,
      tactileVisualMarks: tactileVisualMarks,
      contrastingAreas: contrastingAreas,
      stepFreeAccess: stepFreeAccess,
      referencePointElementType: referencePointElementType,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
      referencePointSloid: referencePointSloid,
    }).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);
      const relation = ReleaseApiUtils.getPrmObjectById(
        response.body,
        relationId,
        true,
        1
      );
      validateRelation(relation);
      etagVersionRelation = relation.etagVersion;
      relationId = relation.id;
    });
  });

  it('Step-7: Re-Check relations after relation update', () => {
    checkRelations();
  });
});

describe('PRM: Complete Information Desk', { testIsolation: false }, () => {
  let informationDeskId = -1;
  let informationDeskSloid = '';
  let relationId = -1;
  let etagVersionInformationDesk = -1;

  let tactileVisualMarks = CommonUtils.TO_BE_COMPLETED;
  let contrastingAreas = CommonUtils.TO_BE_COMPLETED;
  let stepFreeAccess = CommonUtils.TO_BE_COMPLETED;

  let designation = 'öffentlicher Information-Desk im Bf Basel Bad Bf';
  let additionalInformation = 'additionalInformation';
  let wheelchairAccess = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );

  const validFrom = ReleaseApiUtils.todayAsAtlasString();
  const validTo = ReleaseApiUtils.todayAsAtlasString();
  const contactPointType = 'INFORMATION_DESK';
  const referencePointElementType = 'CONTACT_POINT';
  const openingHours = 'Öffnungszeiten: 08:00 - 20:00';
  const numberOfExpectedContactPoints = 2;
  const numberOfExpectedInformationDesks = 1;
  const inductionLoop = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );

  const validateCommonCompleteTicketCounterAttributes = (prmObject) => {
    validatePrmObject(prmObject, validFrom, validTo);
    expect(prmObject)
      .to.have.property('parentServicePointSloid')
      .that.is.a('string')
      .and.to.equal(parentServicePointSloid);
  };

  const validate = (informationDesk) => {
    validateCommonCompleteTicketCounterAttributes(informationDesk);
    expect(informationDesk)
      .to.have.property('sloid')
      .that.is.a('string')
      .and.to.equal(informationDeskSloid);
    expect(informationDesk)
      .to.have.property('designation')
      .that.is.a('string')
      .and.to.equal(designation);
    expect(informationDesk)
      .to.have.property('additionalInformation')
      .and.to.equal(additionalInformation);
    expect(informationDesk)
      .to.have.property('inductionLoop')
      .and.to.equal(inductionLoop);
    expect(informationDesk)
      .to.have.property('wheelchairAccess')
      .and.to.equal(wheelchairAccess);
    expect(informationDesk)
      .to.have.property('openingHours')
      .and.to.equal(openingHours);
    expect(informationDesk)
      .to.have.property('type')
      .that.is.a('string')
      .and.to.equal(contactPointType);
  };

  const validateRelation = (relation) => {
    validateCommonCompleteTicketCounterAttributes(relation);
    expect(relation)
      .to.have.property('referencePointSloid')
      .that.is.a('string')
      .and.to.equal(referencePointSloid);
    expect(relation)
      .to.have.property('elementSloid')
      .that.is.a('string')
      .and.to.equal(informationDeskSloid);
    expect(relation)
      .to.have.property('parentServicePointSloid')
      .that.is.a('string')
      .and.to.equal(parentServicePointSloid);
    expect(relation)
      .to.have.property('referencePointElementType')
      .that.is.a('string')
      .and.to.equal(referencePointElementType);
    expect(relation)
      .to.have.property('tactileVisualMarks')
      .that.is.a('string')
      .and.to.equal(tactileVisualMarks);
    expect(relation)
      .to.have.property('contrastingAreas')
      .that.is.a('string')
      .and.to.equal(contrastingAreas);
    expect(relation)
      .to.have.property('stepFreeAccess')
      .that.is.a('string')
      .and.to.equal(stepFreeAccess);
  };

  it('Step-1: New complete Information Desk', () => {
    informationDeskSloid = `${parentServicePointSloid}:${contactPointType}1`;

    // TODO: Export POST/PUT-bodies?
    CommonUtils.post('/prm-directory/v1/contact-points', {
      type: contactPointType,
      sloid: informationDeskSloid,
      validFrom: validFrom,
      validTo: validTo,
      parentServicePointSloid: parentServicePointSloid,
      designation: designation,
      additionalInformation: additionalInformation,
      inductionLoop: inductionLoop,
      openingHours: openingHours,
      wheelchairAccess: wheelchairAccess,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
    }).then((response) => {
      expect(response.status).to.equal(
        CommonUtils.HTTP_REST_API_RESPONSE_CREATED
      );
      const informationDesk = response.body;
      validate(informationDesk);
      informationDeskId = informationDesk.id;
    });
  });

  it('Step-2: Get complete Information Desk', () => {
    CommonUtils.get(
      `/prm-directory/v1/contact-points?sloids=${informationDeskSloid}`
    ).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const informationDesk = ReleaseApiUtils.getPrmObjectById(
        response.body,
        informationDeskId,
        false,
        1
      );
      validate(informationDesk);
      etagVersionInformationDesk = informationDesk.etagVersion;
    });
  });

  const checkRelation = (
    queryParameters,
    numberOfExpectedObjects: number,
    numberOfExpectedInformationDesks: number
  ) => {
    CommonUtils.get(`/prm-directory/v1/relations?${queryParameters}`).then(
      (response) => {
        expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

        expect(response.body)
          .to.have.property('objects')
          .that.is.an('array')
          .and.to.have.property('length')
          .which.equals(numberOfExpectedObjects);

        const relation = ReleaseApiUtils.getPrmObjectById(
          response.body,
          relationId,
          false,
          numberOfExpectedInformationDesks
        );

        validateRelation(relation);

        relationId = relation.id;
        etagVersionRelation = relation.etagVersion;
      }
    );
  };

  const checkRelations = () => {
    CommonUtils.get(`/prm-directory/v1/relations/${informationDeskSloid}`).then(
      (response) => {
        expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

        expect(response.body)
          .to.be.an('array')
          .and.to.have.property('length')
          .which.equals(numberOfExpectedInformationDesks);

        const relation = response.body[0];
        validateRelation(relation);
        relationId = relation.id;
      }
    );

    checkRelation(
      `parentServicePointSloids=${parentServicePointSloid}&referencePointElementTypes=${referencePointElementType}`,
      numberOfExpectedContactPoints,
      numberOfExpectedContactPoints
    );
    checkRelation(
      `referencePointSloids=${referencePointSloid}&referencePointElementTypes=${referencePointElementType}`,
      numberOfExpectedContactPoints,
      numberOfExpectedContactPoints
    );
    checkRelation(
      `sloids=${informationDeskSloid}`,
      numberOfExpectedInformationDesks,
      numberOfExpectedInformationDesks
    );
  };

  it('Step-3: Check relations after create', () => {
    checkRelations();
  });

  it('Step-4: Update complete Information Desk relation', () => {
    tactileVisualMarks = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.basicValuesAndNotApplicableAndWithRemoteControl()
    );
    contrastingAreas = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.basicValuesAndNotApplicableAndPartially()
    );
    stepFreeAccess = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.stepFreeAccessValues()
    );

    CommonUtils.put(`/prm-directory/v1/relations/${relationId}`, {
      sloid: informationDeskSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersionRelation,
      parentServicePointSloid: parentServicePointSloid,
      tactileVisualMarks: tactileVisualMarks,
      contrastingAreas: contrastingAreas,
      stepFreeAccess: stepFreeAccess,
      referencePointElementType: referencePointElementType,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
      referencePointSloid: referencePointSloid,
    }).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const relation = ReleaseApiUtils.getPrmObjectById(
        response.body,
        relationId,
        true,
        1
      );
      validateRelation(relation);
      etagVersionRelation = relation.etagVersion;
      relationId = relation.id;
    });
  });

  it('Step-5: Check relations after update', () => {
    checkRelations();
  });

  it('Step-6: Update complete information desk', () => {
    designation = 'designation2';
    additionalInformation = 'additionalInformation2';
    wheelchairAccess = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.basicValuesAndNotApplicableAndPartially()
    );

    CommonUtils.put(`/prm-directory/v1/contact-points/${informationDeskId}`, {
      type: contactPointType,
      sloid: informationDeskSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersionInformationDesk,
      parentServicePointSloid: parentServicePointSloid,
      designation: designation,
      additionalInformation: additionalInformation,
      inductionLoop: inductionLoop,
      openingHours: openingHours,
      wheelchairAccess: wheelchairAccess,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
    }).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const informationDesk = ReleaseApiUtils.getPrmObjectById(
        response.body,
        informationDeskId,
        true,
        1
      );
      validate(informationDesk);
      etagVersionInformationDesk = informationDesk.etagVersion;
    });
  });

  it('Step-7: Re-Check relations after information desk update', () => {
    checkRelations();
  });
});
