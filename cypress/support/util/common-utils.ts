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
      .find('mat-form-field')
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

  static selectSearchStatus(searchStatusSelector: string, value: string) {
    //Select status to search
    CommonUtils.selectItemFromDropDown(searchStatusSelector, value);
    cy.get('body').type('{esc}');
  }
}
