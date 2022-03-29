import CommonUtils from './common-utils';

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
    cy.get('[data-cy=detail-subheading-id]')
      .invoke('text')
      .then((ttfnid) => (element.ttfnid = ttfnid ? ttfnid.toString() : ''));
  }

  static clickOnAddNewVersion() {
    cy.get('[data-cy=new-item]').click();
    cy.get('[data-cy=save-item]').should('be.disabled');
    cy.get('[data-cy=edit-item]').should('not.exist');
    cy.get('[data-cy=delete-item]').should('not.exist');
  }

  static fillVersionForm(version: any) {
    // workaround for disabled input field error with (https://github.com/cypress-io/cypress/issues/5830)
    cy.get('[data-cy=validFrom]').clear().type(version.validFrom);
    cy.get('[data-cy=validTo]').clear().type(version.validTo, { force: true });
    cy.get('[data-cy=swissTimetableFieldNumber]')
      .clear()
      .type(version.swissTimetableFieldNumber, { force: true });
    cy.get('[data-cy=businessOrganisation]').clear().type(version.businessOrganisation);
    cy.get('[data-cy=number]').clear().type(version.number);
    cy.get('[data-cy=description]').clear().type(version.description, { force: true });
    cy.get('[data-cy=comment]').clear().type(version.comment);
    cy.get('[data-cy=save-item]').should('not.be.disabled');
  }

  static assertContainsVersion(version: any) {
    CommonUtils.assertItemValue('[data-cy=validFrom]', version.validFrom);
    CommonUtils.assertItemValue('[data-cy=validTo]', version.validTo);
    CommonUtils.assertItemValue('[data-cy=businessOrganisation]', version.businessOrganisation);
    CommonUtils.assertItemValue('[data-cy=number]', version.number);
    CommonUtils.assertItemValue('[data-cy=description]', version.description);
    CommonUtils.assertItemValue('[data-cy=comment]', version.comment);
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
