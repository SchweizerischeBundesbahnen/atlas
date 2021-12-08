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

  static saveVersion() {
    cy.get('[data-cy=save-item]').click();
    cy.get('[data-cy=edit-item]').should('be.visible');
    cy.get('[data-cy=delete-item]').should('be.visible');
  }

  static assertContainsVersion(version: any) {
    cy.get('[data-cy=validFrom]').invoke('val').should('eq', version.validFrom);
    cy.get('[data-cy=validTo]').invoke('val').should('eq', version.validTo);
    cy.get('[data-cy=businessOrganisation]')
      .invoke('val')
      .should('eq', version.businessOrganisation);
    cy.get('[data-cy=number]').invoke('val').should('eq', version.number);
    cy.get('[data-cy=name]').invoke('val').should('eq', version.name);
    cy.get('[data-cy=comment]').invoke('val').should('eq', version.comment);
  }

  static deleteItems() {
    cy.get('[data-cy=delete-item]').click();
    cy.get('#mat-dialog-0').contains('Warnung!');
    cy.get('[data-cy=dialog-confirm-button]').should('exist');
    cy.get('[data-cy=dialog-cancel-button]').should('exist');
    cy.get('[data-cy=dialog-confirm-button]').click();
  }

  static switchLeft() {
    cy.get('[data-cy=switch-version-left]').click();
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
