import CommonUtils from '../common-utils';
import ReleaseApiUtils, { SePoDependentInfo } from './release-api-utils';
import Chainable = Cypress.Chainable;

const workflowEmail = 'joel.hofer@sbb.ch';

export type RestartStopPointWorkflowData = {
  stopPointWorkflowId: number;
  examinantIds: number[];
};

export default class StopPointWorkflow {
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

  static checkStatus(stopPointWorkflowId: number, expectedStatus: string) {
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

  static reject(stopPointWorkflowId: number) {
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

  static restart(
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

  static start(stopPointWorkflowId: number): Chainable<number[]> {
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

  static create = (
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

  static cancel(stopPointWorkflowId: number) {
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

  static overrideExaminantVote(
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
