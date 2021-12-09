import LidiUtils from '../../../support/util/lidi-utils';

describe('Linienverzeichnis', () => {
  const firstVersion = LidiUtils.getFirstVersion();

  const headerTitle = 'Linienverzeichnis';

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Navigate to Linienverzeichnis', () => {
    LidiUtils.navigateToLidi();
  });

  it('Step-3: Check the Linienverzeichnis Line Table is visible', () => {
    cy.contains(headerTitle);
    cy.get('table').get('thead tr th').eq(1).get('div').contains('CH-Liniennummer (CHLNR)');
    cy.get('table').get('thead tr th').eq(2).get('div').contains('Liniennummer');
    cy.get('table').get('thead tr th').eq(3).get('div').contains('Linienbezeichnung');
    cy.get('table').get('thead tr th').eq(4).get('div').contains('Status');
    cy.get('table')
      .get('thead tr th')
      .eq(5)
      .get('div')
      .contains('Geschäftsorganisation Konzessionär');
    cy.get('table').get('thead tr th').eq(6).get('div').contains('SLNID');
    cy.get('table').get('thead tr th').eq(7).get('div').contains('Gültig bis');
    cy.get('table').get('thead tr th').eq(8).get('div').contains('Gültig bis');
  });

  it('Step-4: Go to page Add new Version', () => {
    LidiUtils.clickOnAddNewVersion();
    LidiUtils.fillVersionForm(firstVersion);
    LidiUtils.saveVersion();
  });

  it('Step-5: Navigate to Linienverzeichnis', () => {
    LidiUtils.navigateToHome();
    LidiUtils.navigateToLidi();
    cy.contains(headerTitle);
  });

  it('Step-6: Check the added is present on the table result and navigate to it ', () => {
    cy.contains(firstVersion.swissLineNumber).parents('tr').click();
    cy.contains(firstVersion.swissLineNumber);
    LidiUtils.assertContainsVersion(firstVersion);
  });

  it('Step-7: Delete the item ', () => {
    LidiUtils.deleteItems();
    cy.contains(headerTitle);
  });
});
