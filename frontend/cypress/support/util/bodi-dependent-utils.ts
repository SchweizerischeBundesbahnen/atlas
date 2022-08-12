export default class BodiDependentUtils {

  static BO_DESCRIPTION = 'e2e-dependent-desc-de';

  static createDependentBusinessOrganisation() {
    cy.request({
      method: 'POST',
      url: Cypress.env('API_URL') + '/business-organisation-directory/v1/business-organisations/versions',
      body: BodiDependentUtils.getDependentBusinessOrganisation(),
      headers: {
        Authorization: `Bearer ${window.sessionStorage.getItem('access_token')}`
      },
    }).then((response) => {
      expect(response).property('status').to.equal(201);
      window.sessionStorage.setItem('sboid', response.body.sboid);
    });
  }

  static deleteDependentBusinessOrganisation() {
    cy.request({
      method: 'DELETE',
      url: Cypress.env('API_URL') + '/business-organisation-directory/v1/business-organisations/' + window.sessionStorage.getItem('sboid'),
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
      status: 'ACTIVE',
      businessTypes: ['STREET'],
      validFrom: '2000-01-01',
      validTo: '2000-12-31'
    };
  }
}
