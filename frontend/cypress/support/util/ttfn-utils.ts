import CommonUtils from './common-utils';
import {DataCy} from "../data-cy";

export default class TtfnUtils {
  static navigateToTimetableFieldNumber() {
    cy.intercept('GET', '/line-directory/v1/field-numbers?**').as('getFieldnumbers');
    cy.get('#timetable-field-number').click();
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

  static fillVersionForm(version: any) {
    // workaround for disabled input field error with (https://github.com/cypress-io/cypress/issues/5830)
    cy.get(DataCy.VALID_FROM).clear().type(version.validFrom);
    cy.get(DataCy.VALID_TO).clear().type(version.validTo, { force: true });
    cy.get(DataCy.SWISS_TIMETABLE_FIELD_NUMBER)
      .clear()
      .type(version.swissTimetableFieldNumber, { force: true });
    cy.get(DataCy.BUSINESS_ORGANISATION).clear().type(version.businessOrganisation);
    cy.get(DataCy.NUMBER).clear().type(version.number);
    cy.get(DataCy.DESCRIPTION).clear().type(version.description, { force: true });
    cy.get(DataCy.COMMENT).clear().type(version.comment);
    cy.get(DataCy.SAVE_ITEM).should('not.be.disabled');
  }

  static assertContainsVersion(version: any) {
    CommonUtils.assertItemValue(DataCy.VALID_FROM, version.validFrom);
    CommonUtils.assertItemValue(DataCy.VALID_TO, version.validTo);
    CommonUtils.assertItemValue(DataCy.BUSINESS_ORGANISATION, version.businessOrganisation);
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
      businessOrganisation: 'SBB',
      number: '1.1',
      description:
        'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z',
      comment: 'This is a comment'
    };
  }

  static getSecondVersion() {
    return {
      swissTimetableFieldNumber: '00.AAA',
      validFrom: '01.01.2001',
      validTo: '31.12.2002',
      businessOrganisation: 'SBB1',
      number: '1.1',
      description:
        'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z',
      comment: 'A new comment'
    };
  }
}
