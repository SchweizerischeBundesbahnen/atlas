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
    const itemToDeleteUrl = '/timetable-field-number/' + firstVersion.ttfnid;
    cy.visit({ url: itemToDeleteUrl, method: 'GET' });
    TtfnUtils.assertContainsVersion(firstVersion);
  });

  it('Step-7: Delete added item', () => {
    CommonUtils.deleteItems();
    cy.url().should('contain', '/timetable-field-number');
    cy.contains(headerTitle);
  });
});
