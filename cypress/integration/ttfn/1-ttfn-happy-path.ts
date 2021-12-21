import TtfnUtils from '../../support/util/ttfn-utils';
import CommonUtils from '../../support/util/common-utils';

describe('Fahrplanfeldnummer', () => {
  const firstVersion = TtfnUtils.getFirstVersion();
  const headerTitle = 'Fahrplanfeldnummer';

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Navigate to Fahrplanfeldnummer', () => {
    TtfnUtils.navigateToTimetableFieldNumber();
  });

  it('Step-3: Check the Fahrplanfeldnummer Table is visible', () => {
    cy.contains(headerTitle);
    CommonUtils.assertTableSearch(0, 0, 'Suche');
    CommonUtils.assertTableSearch(0, 1, 'Status');
    CommonUtils.assertTableSearch(0, 2, 'Gültig am');
    CommonUtils.assertTableHeader(0, 0, 'CH-Fahrplanfeldnummer');
    CommonUtils.assertTableHeader(0, 1, 'CH-Fahrplanfeldnummer Bezeichnung');
    CommonUtils.assertTableHeader(0, 2, 'Status');
    CommonUtils.assertTableHeader(0, 3, 'Fahrplanfeldnummer-ID');
    CommonUtils.assertTableHeader(0, 4, 'Gültig von');
    CommonUtils.assertTableHeader(0, 5, 'Gültig bis');
  });

  it('Step-4: Go to page Add new Version', () => {
    TtfnUtils.clickOnAddNewVersion();
    TtfnUtils.fillVersionForm(firstVersion);
    CommonUtils.saveTtfn();
    TtfnUtils.readTtfnidFromForm(firstVersion);
  });

  it('Step-5: Navigate to the Fahrplanfeldnummer', () => {
    cy.get('#\\/timetable-field-number').click();
    cy.contains(headerTitle);
  });

  it('Step-6: search for added item in table and select it', () => {
    cy.intercept('GET', '/timetable-field-number/v1/field-numbers?**').as('searchTtFieldNumbers');
    cy.get('[data-cy=table-search-chip-input]')
      .clear()
      .type(firstVersion.swissTimetableFieldNumber)
      .type('{enter}');
    cy.wait('@searchTtFieldNumbers');
    cy.get('[data-cy=table-search-chip-input]').type(firstVersion.ttfnid).type('{enter}');
    cy.wait('@searchTtFieldNumbers');
    cy.get('table thead tr th').contains('Fahrplanfeldnummer-ID').click();
    cy.wait('@searchTtFieldNumbers');
    cy.get('table tbody tr').each(($el) => {
      cy.wrap($el)
        .should('contain.text', firstVersion.swissTimetableFieldNumber)
        .should('contain.text', firstVersion.ttfnid);
    });
    cy.get('table tbody tr').first().click();
    cy.contains(firstVersion.swissTimetableFieldNumber);
    cy.get('[data-cy=swissTimetableFieldNumber]')
      .invoke('val')
      .should('eq', firstVersion.swissTimetableFieldNumber);
    TtfnUtils.assertContainsVersion(firstVersion);
  });

  it('Step-7: Delete added item', () => {
    CommonUtils.deleteItems();
    cy.contains(headerTitle);
  });
});
