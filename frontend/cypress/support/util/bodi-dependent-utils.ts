export default class BodiDependentUtils {

  static BO_DESCRIPTION = 'e2e-dependent-desc-de';

  static createDependentBusinessOrganisation() {
    return cy.request({
      method: 'POST',
      failOnStatusCode: false,
      url: Cypress.env('API_URL') + '/business-organisation-directory/v1/business-organisations/versions',
      body: BodiDependentUtils.getDependentBusinessOrganisation(),
      headers: {
        Authorization: `Bearer ${window.sessionStorage.getItem('access_token')}`
      },
    }).then((response) => {
      if (response.status === 409) {
        const sboidParameters = response.body.details[0].displayInfo.parameters.filter((parameter: {
          key: string;
        }) => parameter.key == "sboid");
        const sboid = sboidParameters[0].value;
        window.sessionStorage.setItem('sboid', sboid);
      } else {
        expect(response).property('status').to.equal(201);
        window.sessionStorage.setItem('sboid', response.body.sboid);
      }
    });
  }

  static getDependentBusinessOrganisationSboid() {
    return window.sessionStorage.getItem('sboid');
  }

  static deleteDependentBusinessOrganisation() {
    cy.request({
      method: 'DELETE',
      url: Cypress.env('API_URL') + '/business-organisation-directory/v1/business-organisations/' + BodiDependentUtils.getDependentBusinessOrganisationSboid(),
      headers: {
        Authorization: `Bearer ${window.sessionStorage.getItem('access_token')}`
      }
    }).then((response) => {
      expect(response).property('status').to.equal(200);
    });
  }

  private static getDependentBusinessOrganisation() {
    return {
      descriptionDe: BodiDependentUtils.BO_DESCRIPTION,
      descriptionFr: 'e2e-dependent-desc-fr',
      descriptionIt: 'e2e-dependent-desc-it',
      descriptionEn: 'e2e-dependent-desc-en',
      abbreviationDe: 'de-dep',
      abbreviationFr: 'fr-dep',
      abbreviationIt: 'it-dep',
      abbreviationEn: 'en-dep',
      organisationNumber: 91019,
      contactEnterpriseEmail: 'mail@mail.ch',
      status: 'VALIDATED',
      businessTypes: ['STREET'],
      validFrom: '2000-01-01',
      validTo: '2000-12-31'
    };
  }
}
