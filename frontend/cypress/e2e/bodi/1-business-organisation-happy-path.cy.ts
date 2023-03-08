import BodiUtils from '../../support/util/bodi-utils';
import CommonUtils from '../../support/util/common-utils';

describe('Business Organisation Directory', {testIsolation: false}, () => {
  const organisation = BodiUtils.getBusinessOrganisationVersion();

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Navigate to Geschäftsorganisationen', () => {
    BodiUtils.navigateToBusinessOrganisation();
  });

  it('Step-3: Check the Geschäftsorganisationen Table is visible', () => {
    BodiUtils.checkHeaderTitle('Geschäftsorganisationen');

    CommonUtils.assertTableSearch(0, 0, 'Suche');
    CommonUtils.assertTableSearch(0, 1, 'Status');
    CommonUtils.assertTableSearch(0, 2, 'Gültig am');

    CommonUtils.assertTableHeader(0, 0, 'Bezeichnung');
    CommonUtils.assertTableHeader(0, 1, 'Abkürzung');
    CommonUtils.assertTableHeader(0, 2, 'Swiss Business Organisation ID (SBOID)');
    CommonUtils.assertTableHeader(0, 3, 'GO-Nummer');
    CommonUtils.assertTableHeader(0, 4, 'Gültig von');
    CommonUtils.assertTableHeader(0, 5, 'Gültig bis');
  });

  it('Step-4: Go to page Add new Version', () => {
    BodiUtils.clickOnAddBusinessOrganisationVersion();
    BodiUtils.fillBusinessOrganisationVersionForm(organisation);
    BodiUtils.saveBusinessOrganisation();
    BodiUtils.readSboidFromForm(organisation);
  });

  it('Step-5: Navigate to Geschäftsorganisationen', () => {
    BodiUtils.fromDetailBackToBusinessOrganisationOverview();
    CommonUtils.navigateToHomeViaHomeLogo();
    BodiUtils.navigateToBusinessOrganisation();
    BodiUtils.checkHeaderTitle('Geschäftsorganisationen');
  });

  it('Step-6: Search added item in table and navigate to it', () => {
    BodiUtils.searchAndNavigateToBusinessOrganisation(organisation);
    CommonUtils.getTotalRange().should('contain', '01.01.2000').should('contain', '31.12.2000');
  });

  it('Step-7: Delete the item', () => {
    CommonUtils.deleteItem();
    BodiUtils.assertIsOnBusinessOrganisation();
    BodiUtils.checkHeaderTitle('Geschäftsorganisationen');
  });
});
