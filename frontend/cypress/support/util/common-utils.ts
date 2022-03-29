export default class CommonUtils {

  static fromDetailBackToOverview() {
    cy.get('[data-cy=back-to-overview]').click();
  }

  static navigateToHome() {
    cy.get('[data-cy=atlas-logo-home-link]').click();
  }

  static assertHeaderTitle(title: string) {
    cy.get('[data-cy=header-title]').should('have.text', title);
  }

  static saveTtfn() {
    this.saveVersionWithWait('/line-directory/v1/field-numbers/versions/*');
  }

  static saveLine() {
    this.saveVersionWithWait('line-directory/v1/lines/versions/*');
  }

  static saveSubline() {
    this.saveVersionWithWait('line-directory/v1/sublines/versions/*');
  }

  static getTotalRange(){
    return cy.get('[data-cy="total-range"]')
  }

  static saveVersionWithWait(urlToIntercept: string) {
    cy.intercept('GET', urlToIntercept).as('saveAndGetVersion');
    cy.get('[data-cy=save-item]').click();
    cy.wait('@saveAndGetVersion').its('response.statusCode').should('eq', 200);
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

  static switchToVersion(versionNumber: number) {
    cy.get(this.getVersionRowSelector(versionNumber)).click();
    this.assertSelectedVersion(versionNumber);
  }

  static assertVersionRange(versionNumber: number, validFrom: string, validTo: string) {
    const versionDataSelector = this.getVersionRowSelector(versionNumber) + ' > .cdk-column-';
    cy.get(versionDataSelector + "validFrom").should('contain.text', validFrom);
    cy.get(versionDataSelector + "validTo").should('contain.text', validTo);
  }

  static getVersionRowSelector(versionNumber: number) {
    return '[data-cy="version-switch"] > tbody > :nth-child(' + (versionNumber * 2 - 1) + ')';
  }

  static assertTableHeader(
    tableNumber: number,
    columnHeaderNumber: number,
    columnHeaderContent: string
  ) {
    cy.get('table')
      .eq(tableNumber)
      .find('thead tr th')
      .eq(columnHeaderNumber)
      .find('div')
      .contains(columnHeaderContent);
  }

  static assertTableSearch(
    tableNumber: number,
    fieldNumber: number,
    fieldLabelExpectation: string
  ) {
    cy.get('app-table')
      .eq(tableNumber)
      .find('mat-label')
      .eq(fieldNumber)
      .contains(fieldLabelExpectation);
  }

  static clickOnEdit() {
    cy.get('[data-cy=edit-item]').click();
  }

  static selectItemFromDropDown(selector: string, value: string) {
    cy.get(selector).first().click();
    // simulate click event on the drop down item (mat-option)
    cy.get('.mat-option-text').then((options) => {
      for (const option of options) {
        if (option.innerText === value) {
          option.click(); // this is jquery click() not cypress click()
        }
      }
    });
  }

  static typeSearchInput(pathToIntercept: string, searchSelector: string, value: string) {
    cy.intercept('GET', pathToIntercept).as('searchItemUlrIntercept');
    cy.get(searchSelector).clear().type(value).type('{enter}').wait('@searchItemUlrIntercept');
  }

  static selectItemFromDropdownSearchItem(searchStatusSelector: string, value: string) {
    //Select status to search
    CommonUtils.selectItemFromDropDown(searchStatusSelector, value);
    cy.get('body').type('{esc}');
  }

  static assertSelectedVersion(number: number) {
    cy.get('.selected-row > .cdk-column-versionNumber').should('contain.text', 'Version ' + number)
  }
}
