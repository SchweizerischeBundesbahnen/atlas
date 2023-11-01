import CommonUtils from './common-utils';
import { DataCy } from '../data-cy';
import AngularMaterialConstants from './angular-material-constants';

export default class BodiUtils {
  private static BUSINESS_ORGANISATION_PATH_NO_LEADING_SLASH =
    'business-organisation-directory/business-organisations';
  private static BUSINESS_ORGANISATION_PATH =
    '/' + BodiUtils.BUSINESS_ORGANISATION_PATH_NO_LEADING_SLASH;

  static navigateToBusinessOrganisation() {
    CommonUtils.navigateToHomeViaHomeLogo();
    this.interceptBusinessOrganisations('#business-organisation-directory');
  }

  static checkHeaderTitle(title: string) {
    CommonUtils.assertHeaderTitle(title);
  }

  static assertIsOnBusinessOrganisation() {
    cy.url().should('contain', BodiUtils.BUSINESS_ORGANISATION_PATH);
    cy.get(DataCy.BODI_BUSINESS_ORGANISATION).should('exist');
  }

  static readSboidFromForm(element: { sboid: string }) {
    cy.get(DataCy.DETAIL_SUBHEADING_ID)
      .invoke('text')
      .then((sboid) => (element.sboid = sboid ? sboid.toString() : ''));
  }

  static clickOnAddBusinessOrganisationVersion() {
    cy.get(DataCy.NEW_BUSINESS_ORGANISATION).click();
    cy.get(DataCy.SAVE_ITEM).should('be.disabled');
    cy.get(DataCy.EDIT_ITEM).should('not.exist');
    cy.get(DataCy.DELETE_ITEM).should('not.exist');
    cy.contains('Neue Geschäftsorganisation');
  }

  static fillBusinessOrganisationVersionForm(version: any) {
    CommonUtils.getClearType(DataCy.ORGANISATION_NUMBER, version.organisationNumber, true);
    CommonUtils.getClearType(DataCy.DESCRIPTION_DE, version.descriptionDe);
    CommonUtils.getClearType(DataCy.DESCRIPTION_FR, version.descriptionFr);
    CommonUtils.getClearType(DataCy.DESCRIPTION_IT, version.descriptionIt);
    CommonUtils.getClearType(DataCy.DESCRIPTION_EN, version.descriptionEn);
    CommonUtils.getClearType(DataCy.ABBREVIATION_DE, version.abbreviationDe);
    CommonUtils.getClearType(DataCy.ABBREVIATION_FR, version.abbreviationFr);
    CommonUtils.getClearType(DataCy.ABBREVIATION_IT, version.abbreviationIt);
    CommonUtils.getClearType(DataCy.ABBREVIATION_EN, version.abbreviationEn);

    CommonUtils.getClearType(DataCy.VALID_FROM, version.validFrom, true);
    CommonUtils.getClearType(DataCy.VALID_TO, version.validTo, true);

    CommonUtils.getClearType(DataCy.CONTACT_ENTERPRISE_EMAIL, version.contactEnterpriseEmail);
    CommonUtils.selectItemFromDropdownSearchItem(DataCy.BUSINESS_TYPES, version.businessTypes[0]);
    CommonUtils.selectItemFromDropdownSearchItem(DataCy.BUSINESS_TYPES, version.businessTypes[1]);
    CommonUtils.selectItemFromDropdownSearchItem(DataCy.BUSINESS_TYPES, version.businessTypes[2]);
  }

  static searchBusinessOrganisation(businessOrganisation: any) {
    const pathToIntercept = '/business-organisation-directory/v1/business-organisations?**';

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.BODI_BUSINESS_ORGANISATION + ' ' + DataCy.TABLE_FILTER_CHIP_INPUT,
      businessOrganisation.organisationNumber,
    );

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.BODI_BUSINESS_ORGANISATION + ' ' + DataCy.TABLE_FILTER_CHIP_INPUT,
      businessOrganisation.descriptionDe,
    );

    CommonUtils.selectItemFromDropdownSearchItem(
      DataCy.BODI_BUSINESS_ORGANISATION + ' ' + DataCy.TABLE_FILTER_MULTI_SELECT(1, 0),
      'Aktiv',
    );

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.BODI_BUSINESS_ORGANISATION + ' ' + DataCy.TABLE_FILTER_DATE_INPUT(1, 1),
      businessOrganisation.validTo,
    );
  }

  static searchAndNavigateToBusinessOrganisation(businessOrganisation: any) {
    this.searchBusinessOrganisation(businessOrganisation);
    // Check that the table contains 1 result
    cy.get(DataCy.BODI_BUSINESS_ORGANISATION + ' table tbody tr').should('have.length', 1);
    // Click on the item
    cy.contains('td', businessOrganisation.descriptionDe).parents('tr').click({ force: true });
    this.assertContainsBusinessOrganisationVersion(businessOrganisation);
  }

  static assertContainsBusinessOrganisationVersion(version: any) {
    CommonUtils.assertItemValue(DataCy.VALID_FROM, version.validFrom);
    CommonUtils.assertItemValue(DataCy.VALID_TO, version.validTo);
    CommonUtils.assertItemValue(DataCy.ORGANISATION_NUMBER, version.organisationNumber.toString());
    CommonUtils.assertItemValue(DataCy.DESCRIPTION_DE, version.descriptionDe);
    CommonUtils.assertItemValue(DataCy.DESCRIPTION_FR, version.descriptionFr);
    CommonUtils.assertItemValue(DataCy.DESCRIPTION_IT, version.descriptionIt);
    CommonUtils.assertItemValue(DataCy.DESCRIPTION_EN, version.descriptionEn);
    CommonUtils.assertItemValue(DataCy.ABBREVIATION_DE, version.abbreviationDe);
    CommonUtils.assertItemValue(DataCy.ABBREVIATION_FR, version.abbreviationFr);
    CommonUtils.assertItemValue(DataCy.ABBREVIATION_IT, version.abbreviationIt);
    CommonUtils.assertItemValue(DataCy.ABBREVIATION_EN, version.abbreviationEn);
    CommonUtils.assertItemValue(DataCy.CONTACT_ENTERPRISE_EMAIL, version.contactEnterpriseEmail);
    CommonUtils.assertItemText(
      DataCy.BUSINESS_TYPES + AngularMaterialConstants.MAT_SELECT_TEXT_DEEP_SELECT,
      version.businessTypes[0] + ', ' + version.businessTypes[1] + ', ' + version.businessTypes[2],
    );

    cy.get(DataCy.EDIT_ITEM).should('not.be.disabled');
  }

  static fromDetailBackToBusinessOrganisationOverview() {
    CommonUtils.fromDetailBackToOverview(BodiUtils.BUSINESS_ORGANISATION_PATH_NO_LEADING_SLASH);
  }

  static fromDetailBackToTransportCompanyOverview() {
    CommonUtils.fromDetailBackToOverview('business-organisation-directory/transport-companies');
  }

  static saveBusinessOrganisation() {
    CommonUtils.saveVersionWithWait(
      'business-organisation-directory/v1/business-organisations/versions/*',
    );
  }

  static getBusinessOrganisationVersion() {
    return {
      sboid: 'ch:1:sboid:100000',
      said: 100000,
      descriptionDe: 'e2e-desc-de',
      descriptionFr: 'e2e-desc-fr',
      descriptionIt: 'e2e-desc-it',
      descriptionEn: 'e2e-desc-en',
      abbreviationDe: 'de',
      abbreviationFr: 'fr',
      abbreviationIt: 'it',
      abbreviationEn: 'en',
      organisationNumber: 91011,
      contactEnterpriseEmail: 'mail@mail.ch',
      status: 'VALIDATED',
      businessTypes: ['Strasse', 'Eisenbahn', 'Luft'],
      validFrom: '01.01.2000',
      validTo: '31.12.2000',
    };
  }

  static checkIfBoAlreadyExists(businessOrganisation: any) {
    this.searchBusinessOrganisation(businessOrganisation);
    cy.get('tbody')
      .find('tr')
      .should('have.length', 1)
      .then(($el) => {
        if (!$el.hasClass(AngularMaterialConstants.TABLE_NOW_DATA_ROW_CLASS)) {
          $el.trigger('click');
          BodiUtils.assertContainsBusinessOrganisationVersion(businessOrganisation);
          CommonUtils.deleteItem();
        }
      });
  }

  static switchTabToBusinessOrganisations() {
    this.interceptBusinessOrganisations('[data-cy=tab-business-organisations]');
  }

  static switchTabToTU() {
    cy.intercept('GET', '/business-organisation-directory/v1/transport-companies?**').as('getTUs');
    cy.contains('Transportunternehmen').click();
    cy.wait('@getTUs').then((interception) => {
      cy.wrap(interception.response?.statusCode).should('eq', 200);
      cy.url().should('contain', '/business-organisation-directory/transport-companies');
    });
  }

  static switchTabToCompany() {
    cy.intercept('GET', '/business-organisation-directory/v1/companies?**').as('getCompanies');
    cy.contains('Company Codes').click();
    cy.wait('@getCompanies').then((interception) => {
      cy.wrap(interception.response?.statusCode).should('eq', 200);
      cy.url().should('contain', '/business-organisation-directory/companies');
    });
  }

  static interceptGetTransportCompanyRelations(selector: string) {
    cy.intercept('GET', '/business-organisation-directory/v1/transport-company-relations/**').as(
      'loadRelations',
    );
    cy.get(selector).click();
    cy.wait('@loadRelations').its('response.statusCode').should('eq', 200);
  }

  private static interceptBusinessOrganisations(visitSelector: string) {
    const selector = cy.get(visitSelector);
    selector.scrollIntoView();

    selector.should('be.visible');
    selector.focus();
    selector.should('be.focused');

    cy.intercept('GET', '/business-organisation-directory/v1/business-organisations?**').as(
      'getBusinessOrganisations',
    );
    selector.click();
    cy.wait('@getBusinessOrganisations').its('response.statusCode').should('eq', 200);

    cy.url().should('contain', '/business-organisation-directory/business-organisations');
  }
}
