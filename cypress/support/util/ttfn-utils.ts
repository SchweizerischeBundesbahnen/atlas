import CommonUtils from './common-utils';

export default class TtfnUtils {
  static navigateToTimetableFieldNumber() {
    cy.get('#\\timetable-field-number').click();
    cy.url().should('contain', '/timetable-field-number');
  }

  static clickOnAddNewVersion() {
    cy.get('[data-cy=new-item]').click();
    cy.get('[data-cy=save-item]').should('be.disabled');
    cy.get('[data-cy=edit-item]').should('not.exist');
    cy.get('[data-cy=delete-item]').should('not.exist');
    cy.contains('Neue Fahrplanfeldnummer');
  }

  static fillVersionForm(version: any) {
    cy.get('[data-cy=swissTimetableFieldNumber]').clear().type(version.swissTimetableFieldNumber);
    cy.get('[data-cy=validFrom]').clear().type(version.validFrom);
    cy.get('[data-cy=validTo]').clear().type(version.validTo);
    cy.get('[data-cy=businessOrganisation]').clear().type(version.businessOrganisation);
    cy.get('[data-cy=number]').clear().type(version.number);
    cy.get('[data-cy=name]').clear().type(version.name);
    cy.get('[data-cy=comment]').clear().type(version.comment);
    cy.get('[data-cy=save-item]').should('not.be.disabled');
  }

  static assertContainsVersion(version: any) {
    CommonUtils.assertItemValue('[data-cy=validFrom]', version.validFrom);
    CommonUtils.assertItemValue('[data-cy=validTo]', version.validTo);
    CommonUtils.assertItemValue('[data-cy=businessOrganisation]', version.businessOrganisation);
    CommonUtils.assertItemValue('[data-cy=number]', version.number);
    CommonUtils.assertItemValue('[data-cy=name]', version.name);
    CommonUtils.assertItemValue('[data-cy=comment]', version.comment);
  }

  static getFirstVersion() {
    return {
      swissTimetableFieldNumber: '00.AAA',
      validFrom: '01.01.2000',
      validTo: '31.12.2000',
      businessOrganisation: 'SBB',
      number: '1.1',
      name: 'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z',
      comment: 'This is a comment',
    };
  }

  static getSecondVersion() {
    return {
      swissTimetableFieldNumber: '00.AAA',
      validFrom: '01.01.2001',
      validTo: '31.12.2002',
      businessOrganisation: 'SBB1',
      number: '1.1',
      name: 'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z',
      comment: 'A new comment',
    };
  }
}
