export default class CommonUtils {
  static navigateToHome() {
    cy.get('#home').click();
  }

  static saveTtfn() {
    this.saveVersionWithWait('/timetable-field-number/v1/field-numbers/versions/*');
  }

  static saveLine() {
    this.saveVersionWithWait('line-directory/v1/lines/versions/*');
  }

  static saveSubline() {
    this.saveVersionWithWait('line-directory/v1/sublines/versions/*');
  }

  static saveVersionWithWait(urlToIntercept: string) {
    cy.intercept(urlToIntercept).as('saveVersion');
    cy.get('[data-cy=save-item]').click();
    cy.wait('@saveVersion');
    cy.get('[data-cy=edit-item]').should('be.visible');
    cy.get('[data-cy=delete-item]').should('be.visible');
  }

  static assertItemValue(selector: string, value: string) {
    cy.get(selector).invoke('val').should('eq', value);
  }

  static assertItemText(selector: string, value: string) {
    cy.get(selector).invoke('text').should('eq', value);
  }

  static deleteItems() {
    cy.get('[data-cy=delete-item]').click();
    cy.get('[data-cy=dialog]').contains('Warnung!');
    cy.get('[data-cy=dialog-confirm-button]').should('exist');
    cy.get('[data-cy=dialog-cancel-button]').should('exist');
    cy.get('[data-cy=dialog-confirm-button]').click();
  }

  static switchLeft() {
    cy.get('[data-cy=switch-version-left]').click();
  }

  static assertTableHeader(columnHeaderNumber: number, columnHeaderContent: string) {
    cy.get('table')
      .get('thead tr th')
      .eq(columnHeaderNumber)
      .get('div')
      .contains(columnHeaderContent);
  }

  static clickOnEdit() {
    cy.get('[data-cy=edit-item]').click();
  }
}
