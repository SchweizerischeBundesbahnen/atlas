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
    this.saveAndCheckSnackMessage('Fahrplanfeldnummer erfolgreich hinzugefügt.');
  }

  static updateVersion() {
    this.saveAndCheckSnackMessage('Fahrplanfeldnummer erfolgreich gespeichert.');
  }

  static saveAndCheckSnackMessage(message: string) {
    cy.get('[data-cy=save-item]').click();
    cy.get('simple-snack-bar').should('be.visible');
    cy.get('simple-snack-bar').contains(message);
    cy.get('snack-bar-container').should('have.class', 'success');
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

  static deleteItem() {
    cy.get('[data-cy=delete-item]').click();
    cy.get('#mat-dialog-0').contains('Warnung!');
    cy.get('[data-cy=dialog-confirm-button]').should('exist');
    cy.get('[data-cy=dialog-cancel-button]').should('exist');
    cy.get('[data-cy=dialog-confirm-button]').click();
  }
}
