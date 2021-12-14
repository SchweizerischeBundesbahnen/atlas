import LidiUtils from '../../../support/util/lidi-utils';
import CommonUtils from '../../../support/util/common-utils';

describe('Linie', () => {
  const line = LidiUtils.getFirstLineVersion();

  const headerTitle = 'Linienverzeichnis';

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Navigate to Linienverzeichnis', () => {
    LidiUtils.navigateToLidi();
  });

  it('Step-3: Check the Linienverzeichnis Line Table is visible', () => {
    cy.contains(headerTitle);
    CommonUtils.assertTableHeader(1, 'CH-Liniennummer (CHLNR)');
    CommonUtils.assertTableHeader(2, 'Liniennummer');
    CommonUtils.assertTableHeader(3, 'Linienbezeichnung');
    CommonUtils.assertTableHeader(4, 'Status');
    CommonUtils.assertTableHeader(5, 'Geschäftsorganisation Konzessionär');
    CommonUtils.assertTableHeader(6, 'SLNID');
    CommonUtils.assertTableHeader(7, 'Gültig von');
    CommonUtils.assertTableHeader(8, 'Gültig bis');
  });

  it('Step-4: Go to page Add new Version', () => {
    LidiUtils.clickOnAddNewLinieVersion();
    LidiUtils.fillLineVersionForm(line);
    CommonUtils.saveVersion();
  });

  it('Step-5: Navigate to Linienverzeichnis', () => {
    CommonUtils.navigateToHome();
    LidiUtils.navigateToLidi();
    cy.contains(headerTitle);
  });

  it('Step-6: Check the added is present on the table result and navigate to it ', () => {
    cy.contains(line.swissLineNumber).parents('tr').click();
    cy.contains(line.swissLineNumber);
    LidiUtils.assertContainsLineVersion(line);
  });

  it('Step-7: Delete the item ', () => {
    CommonUtils.deleteItems();
    cy.contains(headerTitle);
  });
});
