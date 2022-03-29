import TtfnUtils from '../../support/util/ttfn-utils';
import CommonUtils from '../../support/util/common-utils';

describe('Fahrplanfeldnummer', () => {
  const firstVersion = TtfnUtils.getFirstVersion();

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Navigate to Fahrplanfeldnummer', () => {
    TtfnUtils.navigateToTimetableFieldNumber();
    TtfnUtils.checkHeaderTitle();
  });

  it('Step-3: Check the Fahrplanfeldnummer Table is visible', () => {
    CommonUtils.assertTableSearch(0, 0, 'Suche');
    CommonUtils.assertTableSearch(0, 1, 'Status');
    CommonUtils.assertTableSearch(0, 2, 'Gültig am');
    CommonUtils.assertTableHeader(0, 0, 'Fahrplanfeldnummer');
    CommonUtils.assertTableHeader(0, 1, 'CH-Fahrplanfeldnummer Bezeichnung');
    CommonUtils.assertTableHeader(0, 2, 'CH-Fahrplanfeldnummer');
    CommonUtils.assertTableHeader(0, 3, 'Gültig von');
    CommonUtils.assertTableHeader(0, 4, 'Gültig bis');
    CommonUtils.assertTableHeader(0, 5, 'Status');
    CommonUtils.assertTableHeader(0, 6, 'Geschäftsorganisation');
    CommonUtils.assertTableHeader(0, 7, 'Fahrplanfeldnummer-ID');
  });

  it('Step-4: Go to page Add new Version', () => {
    TtfnUtils.clickOnAddNewVersion();
    TtfnUtils.fillVersionForm(firstVersion);
    CommonUtils.saveTtfn();
    TtfnUtils.readTtfnidFromForm(firstVersion);
  });

  it('Step-5: Navigate to the Fahrplanfeldnummer', () => {
    CommonUtils.fromDetailBackToOverview();
    CommonUtils.navigateToHome();
    TtfnUtils.navigateToTimetableFieldNumber();
    TtfnUtils.checkHeaderTitle();
  });

  it('Step-6: search for added item in table and select it', () => {
    const pathToIntercept = '/line-directory/v1/field-numbers?**';

    CommonUtils.typeSearchInput(
      pathToIntercept,
      '[data-cy=table-search-chip-input]',
      firstVersion.swissTimetableFieldNumber
    );

    CommonUtils.typeSearchInput(
      pathToIntercept,
      '[data-cy=table-search-chip-input]',
      firstVersion.ttfnid
    );

    CommonUtils.selectItemFromDropdownSearchItem('[data-cy=table-search-status-input]', 'Aktiv');

    CommonUtils.typeSearchInput(
      pathToIntercept,
      '[data-cy=table-search-date-input]',
      firstVersion.validTo
    );

    // Check that the table contains 1 result
    cy.get('[data-cy="ttfn"] table tbody tr').should('have.length', 1);
    // Click on the item
    cy.contains('td', firstVersion.swissTimetableFieldNumber).parents('tr').click({ force: true });

    TtfnUtils.assertContainsVersion(firstVersion);
  });

  it('Step-7: Delete added item', () => {
    CommonUtils.deleteItems();
    cy.url().should('contain', '/timetable-field-number');
    TtfnUtils.checkHeaderTitle();
  });
});
