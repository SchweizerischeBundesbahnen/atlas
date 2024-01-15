import { DataCy } from '../data-cy';

export default class CommonUtils {
  static fromDetailBackToTtfnOverview() {
    this.fromDetailBackToOverview('timetable-field-number');
  }

  static fromDetailBackToLinesOverview() {
    this.fromDetailBackToOverview('line-directory/lines');
  }

  static fromDetailBackToSublinesOverview() {
    this.fromDetailBackToOverview('line-directory/sublines');
  }

  static fromDetailBackToOverview(overviewPath: string) {
    cy.get(DataCy.BACK_TO_OVERVIEW).click();
    cy.url().should('eq', Cypress.config().baseUrl + '/' + overviewPath);
  }

  static clickCancelOnDetailViewBackToTtfn() {
    this.clickCancelOnDetailView('timetable-field-number');
  }

  static clickCancelOnDetailViewBackToLines() {
    this.clickCancelOnDetailView('line-directory/lines');
  }

  static clickCancelOnDetailViewBackToSublines() {
    this.clickCancelOnDetailView('line-directory/sublines');
  }

  static clickFirstRowInTable(selector: string) {
    cy.get(selector + ' table tbody tr').click({ force: true });
  }

  static assertNumberOfTableRows(selector: string, numberOfRows: number) {
    cy.get(selector + ' table tbody tr').should('have.length', numberOfRows);
  }

  static assertNoItemsInTable(selector: string) {
    cy.get(selector + ' table tbody tr').should('have.length', 1);
    cy.get(selector + ' table tbody tr').should('have.text', 'Es wurden keine Daten gefunden.');
  }

  static navigateToHomeViaHomeLogo() {
    cy.get(DataCy.ATLAS_LOGO_HOME_LINK).click({ force: true });
    cy.url().should('contain', Cypress.config().baseUrl);
  }

  static navigateToHomepageViaSidemenu() {
    // Move to Home via the side-menu
    cy.get(DataCy.SIDEMENU_START).click();

    // Check that we are on the (german) home-page
    cy.contains('Home');
    cy.contains('Die SKI Business Plattform');
  }

  static navigateToTtfnViaSidemenu() {
    // Move to TTFN via the side-menu
    cy.get(DataCy.SIDEMENU_TTFN).click();

    // Check that we are on the TTFN-path
    cy.url().should('contain', '/timetable-field-number');
  }

  static navigateToLidiViaSidemenu() {
    // Move to LiDi via the side-menu
    cy.get(DataCy.SIDEMENU_LIDI).click();

    // Check that we are on the LiDi-path
    cy.url().should('contain', '/line-directory');
  }

  static assertHeaderTitle(title: string) {
    cy.get(DataCy.HEADER_TITLE).should('have.text', title);
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

  static getTotalRange() {
    return cy.get(DataCy.TOTAL_RANGE);
  }

  static saveVersionWithWait(urlToIntercept: string) {
    cy.intercept('GET', urlToIntercept).as('saveAndGetVersion');
    cy.get(DataCy.SAVE_ITEM).click();
    cy.wait('@saveAndGetVersion').its('response.statusCode').should('eq', 200);
    cy.get(DataCy.EDIT_ITEM).should('exist');
    cy.get(DataCy.DELETE_ITEM).should('exist');
  }

  static assertItemValue(selector: string, value: string) {
    cy.get(selector).invoke('val').should('eq', value);
  }

  static assertItemText(selector: string, value: string) {
    cy.get(selector).invoke('text').should('eq', value);
  }

  static deleteItem() {
    cy.get(DataCy.DELETE_ITEM).click();
    cy.get(DataCy.DIALOG).contains('Warnung!');
    cy.get(DataCy.DIALOG_CONFIRM_BUTTON).should('exist');
    cy.get(DataCy.DIALOG_CANCEL_BUTTON).should('exist');
    cy.get(DataCy.DIALOG_CONFIRM_BUTTON).click();
  }

  static switchToVersion(versionNumber: number) {
    cy.get(this.getVersionRowSelector(versionNumber)).click();
    this.assertSelectedVersion(versionNumber);
  }

  static assertVersionRange(versionNumber: number, validFrom: string, validTo: string) {
    const versionDataSelector = this.getVersionRowSelector(versionNumber) + ' > .cdk-column-';
    cy.get(versionDataSelector + 'validFrom').should('contain.text', validFrom);
    cy.get(versionDataSelector + 'validTo').should('contain.text', validTo);
  }

  static getVersionRowSelector(versionNumber: number) {
    return DataCy.VERSION_SWITCH + ' > tbody > :nth-child(' + (versionNumber * 2 - 1) + ')';
  }

  static assertTableHeader(
    tableNumber: number,
    columnHeaderNumber: number,
    columnHeaderContent: string,
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
    fieldLabelExpectation: string,
  ) {
    cy.get('app-table')
      .eq(tableNumber)
      .find('app-atlas-label-field')
      .eq(fieldNumber)
      .should('contain.text', fieldLabelExpectation);
  }

  static clickOnEdit() {
    cy.get(DataCy.EDIT_ITEM).click();
  }

  static selectItemFromDropDown(selector: string, value: string) {
    cy.get(selector).should('be.visible').first().click();
    // simulate click event on the drop down item (mat-option)
    CommonUtils.chooseMatOptionByText(value);
  }

  static selectFirstItemFromDropDown(selector: string) {
    cy.get(selector).should('be.visible');
    cy.get(selector).first().click();
    // simulate click event on the drop down item (mat-option)
    CommonUtils.chooseMatOptionByText(undefined);
  }

  static chooseMatOptionByText(value: string | undefined) {
    cy.get('mat-option > span')
      .should('be.visible')
      .should(($options) => {
        for (const option of $options) {
          if (value) {
            if (option.innerText === value) {
              option.click(); // this is jquery click() not cypress click()
            }
          } else {
            option.click();
          }
        }
      });
  }

  static chooseOneValueFromMultiselect(selector: string, value: string) {
    cy.get(selector).first().click();
    // deselect all

    // Deselect all selected dropDownElements
    cy.get('mat-option').should(($options) => {
      for (const option of $options) {
        if (option.getAttribute('aria-selected') == 'true') {
          option.click(); // this is jquery click() not cypress click()
        }
      }
    });

    // only select given value
    CommonUtils.chooseMatOptionByText(value);
    // Leave multiselect
    cy.get(selector).first().focus().type('{esc}');
  }

  /**
   * Cypress.Chainable<JQuery<HTMLElement>>.type() throws an exception
   * when an empty string ("") is passed. This method only calls type() when textToType is filled.
   */
  static getClearType(selector: string, textToType: string, force = false) {
    cy.get(selector)
      .clear()
      .then((e) => {
        // Workaround so that no exception is thrown when textToType="" (meaning an empty string)
        if (textToType) {
          cy.wrap(e).type(textToType, { delay: 0, force: force });
        }
      });
  }

  static typeSearchInput(pathToIntercept: string, searchSelector: string, value: string) {
    cy.intercept(pathToIntercept).as('searchItemUlrIntercept');
    cy.get(searchSelector).clear().type(value).type('{enter}').wait('@searchItemUlrIntercept');
  }

  static selectItemFromDropdownSearchItem(searchStatusSelector: string, value: string) {
    //Select status to search
    CommonUtils.selectItemFromDropDown(searchStatusSelector, value);
    cy.get('body').type('{esc}');
  }

  static assertSelectedVersion(number: number) {
    cy.get('.selected-row > .cdk-column-versionNumber').should('contain.text', 'Version ' + number);
  }

  /**
   * It is also checked if all options that are not given have the state unchecked.
   */
  static assertItemsFromDropdownAreChecked(selector: string, checkedOptionNames: string[]) {
    const dropDownElements = cy
      .get(selector)
      .click() // open dropdown menu
      .get('mat-option'); // get all dropdown-elements

    dropDownElements.should(($dropDownElement) => {
      for (const element of $dropDownElement) {
        const elementName = element.innerText.trim();
        const message = 'State of checkbox "' + elementName + '"';

        const checkBoxState = element.getAttribute('aria-selected') == 'true';
        if (checkedOptionNames.includes(elementName)) {
          expect(checkBoxState, message).to.be.true;
        } else {
          expect(checkBoxState, message).to.be.false;
        }
      }
    });
    // Workaround to close the dropDown-menu again after all checks are done
    cy.get(selector).type('{esc}');
  }

  static assertDatePickerIs(selector: string, date: string) {
    cy.get(selector)
      .invoke('val')
      .then((text) => {
        expect(date, selector + '-Date').to.equal(text);
      });
  }

  static unregisterServiceWorker() {
    if (window.navigator && navigator.serviceWorker) {
      navigator.serviceWorker.getRegistrations().then((registrations) => {
        registrations.forEach((registration) => {
          registration.unregister();
        });
      });
    }
  }

  static typeAndSelectItemFromDropDown(selector: string, value: string) {
    cy.intercept('GET', '*' + value + '*').as('searchIntercept');
    cy.get(selector)
      .should('have.value', '')
      .should(($el) => {
        expect(Cypress.dom.isFocusable($el)).to.be.true;
      })
      .should('be.enabled')
      .type(value, { delay: 0, force: true })
      .wait('@searchIntercept')
      .wait(100);
    cy.get(selector).type('{enter}');
  }

  static visit(itemToDeleteUrl: string) {
    CommonUtils.unregisterServiceWorker();
    cy.visit({ url: itemToDeleteUrl, method: 'GET' });
  }

  private static clickCancelOnDetailView(overviewPath: string) {
    cy.get(DataCy.CANCEL).click();
    cy.url().should('eq', Cypress.config().baseUrl + '/' + overviewPath);
  }
}
