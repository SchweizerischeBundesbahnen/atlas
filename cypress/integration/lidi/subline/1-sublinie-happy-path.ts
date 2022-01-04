import LidiUtils from '../../../support/util/lidi-utils';
import CommonUtils from '../../../support/util/common-utils';

describe('Teillinie', () => {
  const sublineVersion = LidiUtils.getFirstSublineVersion();
  let mainline: any;
  const breadcrumbTitle = 'Linienverzeichnis';

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Add mainline', () => {
    mainline = LidiUtils.addMainLine();
  });

  it('Step-3: Navigate to Linienverzeichnis', () => {
    LidiUtils.navigateToLidi();
    cy.contains(breadcrumbTitle);
    cy.get('[data-cy=sublines-title]').invoke('text').should('eq', 'Teillinien');
  });

  it('Step-4: Check the Linienverzeichnis Line Table is visible', () => {
    cy.contains('Teillinien');
    CommonUtils.assertTableSearch(1, 0, 'Suche');
    CommonUtils.assertTableSearch(1, 1, 'Status');
    CommonUtils.assertTableSearch(1, 2, 'Teillinientyp');
    CommonUtils.assertTableSearch(1, 3, 'Gültig am');
    CommonUtils.assertTableHeader(1, 0, 'CH-Teilliniennummer');
    CommonUtils.assertTableHeader(1, 1, 'Teillinienbezeichnung');
    CommonUtils.assertTableHeader(1, 2, 'CH-Liniennummer (CHLNR)');
    CommonUtils.assertTableHeader(1, 3, 'Status');
    CommonUtils.assertTableHeader(1, 4, 'Teillinientyp');
    CommonUtils.assertTableHeader(1, 5, 'Geschäftsorganisation Konzessionär');
    CommonUtils.assertTableHeader(1, 6, 'SLNID');
    CommonUtils.assertTableHeader(1, 7, 'Gültig von');
    CommonUtils.assertTableHeader(1, 8, 'Gültig bis');
  });

  it('Step-5: Go to page Add new Version', () => {
    LidiUtils.clickOnAddNewSublinesLinieVersion();
    LidiUtils.fillSublineVersionForm(sublineVersion);
    CommonUtils.saveSubline();
    LidiUtils.readSlnidFromForm(sublineVersion);
  });

  it('Step-6: Navigate to Linienverzeichnis', () => {
    CommonUtils.navigateToHome();
    LidiUtils.navigateToLidi();
    cy.contains(breadcrumbTitle);
  });

  it('Step-7: Search for added element on the table and navigate to it', () => {
    const itemToDeleteUrl = '/line-directory/sublines/' + sublineVersion.slnid;
    cy.visit({ url: itemToDeleteUrl, method: 'GET' });
    cy.contains(mainline.swissLineNumber);
    cy.contains(sublineVersion.swissSublineNumber);
    LidiUtils.assertContainsSublineVersion(sublineVersion);
  });

  it('Step-8: Delete the subline item', () => {
    CommonUtils.deleteItems();
    LidiUtils.assertIsOnLiDiHome();
  });

  it('Step-9: Navigate to the mainline item', () => {
    const itemToDeleteUrl = '/line-directory/lines/' + mainline.slnid;
    cy.visit({ url: itemToDeleteUrl, method: 'GET' });
    cy.contains(mainline.swissLineNumber);
    LidiUtils.assertContainsLineVersion(mainline);
  });

  it('Step-10: Delete the mainline item', () => {
    CommonUtils.deleteItems();
    LidiUtils.assertIsOnLiDiHome();
  });
});
