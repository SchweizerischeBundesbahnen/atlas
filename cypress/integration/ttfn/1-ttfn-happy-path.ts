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
    cy.get('table').get('thead tr th').eq(1).get('div').contains('CH-Fahrplanfeldnummer');
    cy.get('table')
      .get('thead tr th')
      .eq(2)
      .get('div')
      .contains('CH-Fahrplanfeldnummer Bezeichnung');
    cy.get('table').get('thead tr th').eq(3).get('div').contains('Status');
    cy.get('table').get('thead tr th').eq(4).get('div').contains('Fahrplanfeldnummer-ID');
    cy.get('table').get('thead tr th').eq(4).get('div').contains('Gültig von');
    cy.get('table').get('thead tr th').eq(4).get('div').contains('Gültig bis');
  });

  it('Step-4: Go to page Add new Version', () => {
    TtfnUtils.clickOnAddNewVersion();
    TtfnUtils.fillVersionForm(firstVersion);
    CommonUtils.saveVersion();
  });

  it('Step-5: Navigate to the Fahrplanfeldnummer', () => {
    cy.get('#\\/timetable-field-number').click();
    cy.contains(headerTitle);
  });

  it('Step-6: Check the item aa.AAA is present on the table result and navigate to it ', () => {
    cy.contains(firstVersion.swissTimetableFieldNumber).parents('tr').click();
    cy.contains(firstVersion.swissTimetableFieldNumber);
    cy.get('[data-cy=swissTimetableFieldNumber]')
      .invoke('val')
      .should('eq', firstVersion.swissTimetableFieldNumber);
    TtfnUtils.assertContainsVersion(firstVersion);
  });

  it('Step-7: Delete the item aa.AAA ', () => {
    CommonUtils.deleteItems();
    cy.contains(headerTitle);
  });
});
