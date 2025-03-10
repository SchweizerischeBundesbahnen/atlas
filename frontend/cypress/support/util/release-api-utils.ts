import CommonUtils from './common-utils';
import Chainable = Cypress.Chainable;

export type SePoDependentInfo = {
  parentServicePointSloid: string;
  parentServicePointId: number;
  numberWithoutCheckDigit: number;
};

const workflowEmail = 'joel.hofer@sbb.ch';

export type RestartStopPointWorkflowData = {
  stopPointWorkflowId: number;
  examinantIds: number[];
};

export default class ReleaseApiUtils {
  static FIRST_ATLAS_DATE = '1700-01-01';
  static LAST_ATLAS_DATE = '9999-12-31';

  /**
   * Give back a day in the format 'DD-MM-YYYY'
   * with today as the default day.
   * If needed dayIncrement can be supplied which can be positive or negative.
   * @param dayIncrement Number of days added to today.
   */
  static atlasDay(dayIncrement: number = 0) {
    return ReleaseApiUtils.date(dayIncrement).toISOString().split('T')[0]; // TODO: Use new Date().toLocaleDateString('en-CA'); ?
  }

  static date(dayIncrement: number = 0) {
    const date = new Date();
    date.setDate(date.getDate() + dayIncrement);
    return date;
  }

  static today() {
    return ReleaseApiUtils.date();
  }

  static tomorrow() {
    return ReleaseApiUtils.date(1);
  }

  static todayAsAtlasString() {
    return ReleaseApiUtils.atlasDay();
  }

  static tomorrowAsAtlasString() {
    return ReleaseApiUtils.atlasDay(1);
  }

  static makeCommonChecks = (
    response: Cypress.Response<any>,
    slnid: string,
    lineVersionId: number
  ) => {
    // Check the status code
    expect(response.status).to.equal(200);

    // Check if the response is an array
    const lineVersions = response.body;
    expect(Array.isArray(lineVersions)).to.be.true; // Verify that it is an array
    expect(lineVersions.length).to.equal(1); // Verify the length of the array

    const lineVersionsFirst = lineVersions[0];

    // Check the values of the first element in the array
    expect(lineVersionsFirst).to.have.property('slnid').that.equals(slnid);
    expect(lineVersionsFirst)
      .to.have.property('id')
      .that.is.a('number')
      .and.equals(lineVersionId);
    return lineVersionsFirst;
  };

  static getPrmObjectById = (
    body,
    prmId: number,
    arePrmObjectsInBodyDirectly: boolean,
    expectedNumberOfObjects: number
  ) => {
    let objects;
    if (arePrmObjectsInBodyDirectly) {
      expect(body).is.an('array');
      objects = body;
    } else {
      expect(body).to.have.property('objects').that.is.an('array');
      objects = body.objects;
    }
    expect(objects.length).to.equal(expectedNumberOfObjects);
    return objects.find((obj) => obj.id === prmId);
  };

  static extractOneRandomValue = (values) => {
    return values[Math.floor(Math.random() * values.length)];
  };

  static getRoundedRandomFloat(min, max, fractionDigits) {
    const float = Math.random() * (max - min) + min;
    return parseFloat(float.toFixed(fractionDigits));
  }

  static createDependentServicePoint(
    sboid: string, // The service-point needs to have a meansOfTransport, so that a PRM-stopPoint can be created.
    // The meansOfTransport BUS leads to the reduced and TRAIN to the complete variant
    // Code:
    // https://code.sbb.ch/projects/KI_ATLAS/repos/atlas/browse/base-atlas/src/main/java/ch/sbb/atlas/servicepoint/enumeration/MeanOfTransport.java
    meansOfTransport: string[],
    additionalAttributes: object = {}
  ): Chainable<SePoDependentInfo> {
    const body = Object.assign(
      {
        country: 'SWITZERLAND',
        // Mark designationOfficial to be able to filter the emails in Outlook
        designationOfficial: `${new Date().toISOString()}API`,
        businessOrganisation: sboid,
        meansOfTransport: meansOfTransport,
        validFrom: ReleaseApiUtils.FIRST_ATLAS_DATE,
        validTo: ReleaseApiUtils.LAST_ATLAS_DATE,
      },
      additionalAttributes
    );
    return CommonUtils.post(
      '/service-point-directory/v1/service-points',
      body
    ).then((response) => {
      expect(response)
        .property('status')
        .to.equal(CommonUtils.HTTP_REST_API_RESPONSE_CREATED);

      expect(response).property('body').property('sloid').to.be.a('string');
      expect(response)
        .property('body')
        .property('number')
        .property('number')
        .to.be.a('number');

      expect(response).property('body').property('id').to.be.a('number');

      return {
        parentServicePointSloid: response.body.sloid,
        numberWithoutCheckDigit: response.body.number.number,
        parentServicePointId: response.body.id,
      };
    });
  }

  static createDependentStopPointObjects(): Chainable<SePoDependentInfo> {
    return CommonUtils.createDependentBusinessOrganisation(
      ReleaseApiUtils.today(),
      ReleaseApiUtils.today()
    ).then((sboidOfBO: string) => {
      const sboid = sboidOfBO;
      const dateInMonth: string = new Date()
        .getMonth()
        .toString()
        .padStart(2, '0');
      // The service-point need to have a meansOfTransport,
      // so that a StopPointWorkflow can be created and started.
      return ReleaseApiUtils.createDependentServicePoint(sboid, ['UNKNOWN'], {
        servicePointGeolocation: {
          spatialReference: 'LV95',
          north: `12051${dateInMonth}`,
          east: `26520${dateInMonth}`,
          height: 1,
        },
      }).then((sePoDependentInfo: SePoDependentInfo) => {
        return sePoDependentInfo;
      });
    });
  }

  static createDependentTrafficPoint(
    trafficPointSloid: string,
    info: SePoDependentInfo
  ) {
    CommonUtils.post('/service-point-directory/v1/traffic-point-elements', {
      numberWithoutCheckDigit: info.numberWithoutCheckDigit,
      sloid: trafficPointSloid,
      parentSloid: info.parentServicePointSloid,
      validFrom: ReleaseApiUtils.FIRST_ATLAS_DATE,
      validTo: ReleaseApiUtils.FIRST_ATLAS_DATE,
      trafficPointElementType: 'BOARDING_PLATFORM',
    }).then((response) => {
      expect(response.status).to.equal(
        CommonUtils.HTTP_REST_API_RESPONSE_CREATED
      );
      expect(response.body.parentSloid).to.equal(info.parentServicePointSloid);
      expect(response.body)
        .to.have.property('sloid')
        .that.is.a('string')
        .and.to.equal(trafficPointSloid);

      expect(response.body)
        .property('servicePointNumber')
        .property('number')
        .that.is.a('number')
        .and.equals(info.numberWithoutCheckDigit);
    });
  }

  static checkWorkflowStatus(
    stopPointWorkflowId: number,
    expectedStatus: string
  ) {
    CommonUtils.get(
      `/workflow/v1/stop-point/workflows/${stopPointWorkflowId}`
    ).then((response) => {
      expect(response)
        .property('status')
        .to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      expect(response)
        .property('body')
        .property('status')
        .to.equal(expectedStatus);
      expect(response)
        .property('body')
        .property('id')
        .to.equal(stopPointWorkflowId);
    });
  }

  static rejectWorkflow(stopPointWorkflowId: number) {
    CommonUtils.post(
      `/workflow/v1/stop-point/workflows/reject/${stopPointWorkflowId}`,
      {
        mail: workflowEmail,
        organisation: 'The Fot',
      }
    ).then((response) => {
      expect(response)
        .property('status')
        .to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      expect(response).property('body').property('status').to.equal('REJECTED');
    });
  }

  static restartStopPointWorkflow(
    stopPointWorkflowId: number,
    examinantIds: number[]
  ): Cypress.Chainable<RestartStopPointWorkflowData> {
    return CommonUtils.post(
      `/workflow/v1/stop-point/workflows/restart/${stopPointWorkflowId}`,
      {
        // Mark designationOfficial to be able to filter the emails in Outlook
        designationOfficial: `${new Date().toISOString()}API`,
        mail: workflowEmail,
        organisation: 'The Fot',
      }
    ).then((response) => {
      expect(response)
        .property('status')
        .to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      expect(response).property('body').property('status').to.equal('HEARING');

      expect(response)
        .property('body')
        .property('examinants')
        .to.be.an('array')
        .of.length(3);

      response.body.examinants.forEach((examinant) => {
        expect(examinantIds).to.not.include(examinant.id);
      });

      expect(response).property('body').property('id').to.be.a('number');
      return {
        stopPointWorkflowId: response.body.id,
        examinantIds: response.body.examinants.map((examinant) => examinant.id),
      };
    });
  }

  static startStopPointWorkflow(
    stopPointWorkflowId: number
  ): Chainable<number[]> {
    return CommonUtils.post(
      `/workflow/v1/stop-point/workflows/start/${stopPointWorkflowId}`
    ).then((response) => {
      expect(response)
        .property('status')
        .to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      expect(response).property('body').property('status').to.equal('HEARING');

      expect(response)
        .property('body')
        .property('examinants')
        .to.be.an('array')
        .of.length(3);
      return response.body.examinants.map((examinant) => examinant.id);
    });
  }

  static createStopPointWorkflow = (
    parentServicePointId: number,
    parentServicePointSloid: string
  ): Chainable<number> => {
    return CommonUtils.post('/workflow/v1/stop-point/workflows', {
      applicantMail: 'joel.hofer@sbb.ch',
      versionId: parentServicePointId,
      sloid: parentServicePointSloid,
      ccEmails: [],
      workflowComment: 'workflowComment',
      examinants: [
        {
          firstName: 'Vorname',
          lastName: 'Nachname',
          judgement: null,
          decisionType: null,
          organisation: 'Empfänger',
          personFunction: 'Funktion',
          mail: 'joel.hofer@sbb.ch',
        },
      ],
    }).then((response) => {
      expect(response)
        .property('status')
        .to.equal(CommonUtils.HTTP_REST_API_RESPONSE_CREATED);

      expect(response).property('body').property('id').to.be.a('number');
      return response.body.id;
    });
  };

  static cancelStopPointWorkflow(stopPointWorkflowId: number) {
    CommonUtils.post(
      `/workflow/v1/stop-point/workflows/cancel/${stopPointWorkflowId}`,
      {
        mail: workflowEmail,
        organisation: 'The Fot',
      }
    ).then((response) => {
      expect(response)
        .property('status')
        .to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      expect(response).property('body').property('status').to.equal('CANCELED');
    });
  }

  static overrideExaminantOfStopPointWorkflow(
    stopPointWorkflowId: number,
    examinantId: number,
    fotJudgement: string,
    fotMotivation: string
  ) {
    CommonUtils.post(
      `/workflow/v1/stop-point/workflows/override-vote/${stopPointWorkflowId}/${examinantId}`,
      {
        firstName: 'VornameBAV',
        lastName: 'NachnameBAV',
        fotJudgement: fotJudgement,
        fotMotivation: fotMotivation,
      }
    ).then((response) => {
      expect(response)
        .property('status')
        .to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);
    });
  }
}
