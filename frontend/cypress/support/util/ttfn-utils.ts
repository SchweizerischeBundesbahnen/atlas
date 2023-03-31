import CommonUtils from './common-utils';
import { DataCy } from '../data-cy';
import BodiDependentUtils from './bodi-dependent-utils';
import AngularMaterialConstants from './angular-material-constants';

export default class TtfnUtils {
  static navigateToTimetableFieldNumber() {
    CommonUtils.navigateToHomeViaHomeLogo();
    cy.intercept('GET', '/line-directory/v1/field-numbers?**').as('getFieldnumbers');
    cy.get('#timetable-field-number')
      .should('be.visible')
      .should(($el) => expect(Cypress.dom.isFocusable($el)).to.be.true)
      .click();
    cy.wait('@getFieldnumbers').then((interception) => {
      cy.wrap(interception.response?.statusCode).should('eq', 200);
      cy.url().should('contain', '/timetable-field-number');
    });
  }

  static checkHeaderTitle() {
    CommonUtils.assertHeaderTitle('Fahrplanfeld-Nummern');
  }

  static readTtfnidFromForm(element: { ttfnid: string }) {
    cy.get(DataCy.DETAIL_SUBHEADING_ID)
      .invoke('text')
      .then((ttfnid) => (element.ttfnid = ttfnid ? ttfnid.toString() : ''));
  }

  static clickOnAddNewVersion() {
    cy.get(DataCy.NEW_ITEM).click();
    cy.get(DataCy.SAVE_ITEM).should('be.disabled');
    cy.get(DataCy.EDIT_ITEM).should('not.exist');
    cy.get(DataCy.DELETE_ITEM).should('not.exist');
  }

  static checkIfTtfnAlreadyExists(ttfn: any) {
    const pathToIntercept = '/line-directory/v1/field-numbers?**';

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.TABLE_FILTER_CHIP_INPUT,
      ttfn.swissTimetableFieldNumber
    );

    CommonUtils.selectItemFromDropdownSearchItem(DataCy.TABLE_FILTER_MULTI_SELECT(1, 1), 'Aktiv');

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.TABLE_FILTER_DATE_INPUT(1, 2),
      ttfn.validTo
    );

    cy.get('tbody')
      .find('tr')
      .should('have.length', 1)
      .then(($el) => {
        if (!$el.hasClass(AngularMaterialConstants.TABLE_NOW_DATA_ROW_CLASS)) {
          $el.trigger('click');
          CommonUtils.deleteItem();
        }
      });
  }

  static fillVersionForm(version: any) {
    // workaround for disabled input field error with (https://github.com/cypress-io/cypress/issues/5830)
    CommonUtils.getClearType(DataCy.VALID_FROM, version.validFrom, true);
    CommonUtils.getClearType(DataCy.VALID_TO, version.validTo, true);
    cy.get(DataCy.SWISS_TIMETABLE_FIELD_NUMBER).clear().type(version.swissTimetableFieldNumber);

    CommonUtils.typeAndSelectItemFromDropDown(
      DataCy.BUSINESS_ORGANISATION + ' ' + 'input',
      version.businessOrganisation
    );

    cy.get(DataCy.NUMBER).clear().type(version.number);
    cy.get(DataCy.DESCRIPTION).clear().type(version.description);
    cy.get(DataCy.COMMENT).clear().type(version.comment);
    cy.get(DataCy.SAVE_ITEM).should('not.be.disabled');
  }

  static assertContainsVersion(version: any) {
    CommonUtils.assertItemValue(DataCy.VALID_FROM, version.validFrom);
    CommonUtils.assertItemValue(DataCy.VALID_TO, version.validTo);
    cy.get(DataCy.BUSINESS_ORGANISATION).should('contain.text', version.businessOrganisation);
    CommonUtils.assertItemValue(DataCy.NUMBER, version.number);
    CommonUtils.assertItemValue(DataCy.DESCRIPTION, version.description);
    CommonUtils.assertItemValue(DataCy.COMMENT, version.comment);
  }

  static getFirstVersion() {
    return {
      ttfnid: '',
      swissTimetableFieldNumber: '00.AAA',
      validFrom: '01.01.2000',
      validTo: '31.12.2000',
      businessOrganisation: BodiDependentUtils.BO_DESCRIPTION,
      number: '1.1',
      description: 'First Version',
      comment: 'This is a comment',
    };
  }

  static getSecondVersion() {
    return {
      swissTimetableFieldNumber: '00.AAA',
      validFrom: '01.01.2001',
      validTo: '31.12.2002',
      businessOrganisation: BodiDependentUtils.BO_DESCRIPTION,
      number: '1.2',
      description: 'Second Version',
      comment: 'A new comment',
    };
  }

  static getTtfnBernThun() {
    return {
      ttfnid: '',
      swissTimetableFieldNumber: '01.AAA',
      validFrom: '01.01.2000',
      validTo: '31.12.2000',
      businessOrganisation: BodiDependentUtils.BO_DESCRIPTION,
      number: '0.1',
      description: 'Bern - Thun',
      comment: 'Beste',
    };
  }
}
