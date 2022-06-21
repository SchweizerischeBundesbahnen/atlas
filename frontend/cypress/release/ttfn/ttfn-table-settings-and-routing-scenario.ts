import CommonUtils from '../../support/util/common-utils';
import TtfnUtils from '../../support/util/ttfn-utils';
import { DataCy } from '../../support/data-cy';

describe('TTFN: TableSettings and Routing', () => {
  const ttfnBernThun = TtfnUtils.getTtfnBernThun();
  const churGrenze = TtfnUtils.getFirstVersion();

  const firstJune2000 = '01.06.2000';
  const statusAktiv = 'Aktiv';

  function assertAllTableFiltersAreFilled() {
    cy.get(DataCy.TABLE_SEARCH_STRINGS).contains(ttfnBernThun.swissTimetableFieldNumber);
    CommonUtils.assertItemsFromDropdownAreChecked(DataCy.TABLE_SEARCH_STATUS_INPUT, [statusAktiv]);
    CommonUtils.assertDatePickerIs(DataCy.TABLE_SEARCH_DATE_INPUT, firstJune2000);
    CommonUtils.assertNumberOfTableRows(DataCy.TTFN, 1);
  }

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Navigate to TTFN', () => {
    TtfnUtils.navigateToTimetableFieldNumber();
  });

  it('Step-3: Add new field number', () => {
    TtfnUtils.clickOnAddNewVersion();
    TtfnUtils.fillVersionForm(churGrenze);
    CommonUtils.saveTtfn();
    CommonUtils.fromDetailBackToTtfnOverview();
  });

  it('Step-4: Add another field number', () => {
    TtfnUtils.clickOnAddNewVersion();
    TtfnUtils.fillVersionForm(ttfnBernThun);
    CommonUtils.saveTtfn();
    CommonUtils.fromDetailBackToTtfnOverview();
  });

  it('Step-5: Look for TTFN Bern - Thun', () => {
    CommonUtils.typeSearchInput(
      '/line-directory/v1/field-numbers?**',
      DataCy.TABLE_SEARCH_CHIP_INPUT,
      ttfnBernThun.swissTimetableFieldNumber
    );

    CommonUtils.typeSearchInput(
      '/line-directory/v1/field-numbers?**',
      DataCy.TABLE_SEARCH_DATE_INPUT,
      firstJune2000
    );

    CommonUtils.selectItemFromDropdownSearchItem(DataCy.TABLE_SEARCH_STATUS_INPUT, statusAktiv);

    // Check that the table contains 1 result
    CommonUtils.assertNumberOfTableRows(DataCy.TTFN, 1);

    assertAllTableFiltersAreFilled();
  });

  it('Step-6: Click on add new TTFN Button and come back without actually creating it', () => {
    TtfnUtils.clickOnAddNewVersion();
    CommonUtils.clickCancelOnDetailViewBackToTtfn();

    assertAllTableFiltersAreFilled();
  });

  it('Step-7: Edit Bern-Thun to Bern-Thun-Interlaken', () => {
    CommonUtils.assertNumberOfTableRows(DataCy.TTFN, 1);
    CommonUtils.clickFirstRowInTable(DataCy.TTFN);

    cy.get(DataCy.EDIT_ITEM).click();
    const newDescription = 'Bern - Thun - Interlaken';
    cy.get(DataCy.DESCRIPTION).clear().type(newDescription, { force: true });
    CommonUtils.saveTtfn();

    cy.intercept('GET', '/line-directory/v1/field-numbers?**').as('getTtfns');
    CommonUtils.fromDetailBackToTtfnOverview();
    cy.wait('@getTtfns');

    // Search still present after edit
    assertAllTableFiltersAreFilled();
    // Change is already visible in table
    cy.get(DataCy.TTFN + ' .mat-row > .cdk-column-description').contains(newDescription);
  });

  it('Step-8: Delete Bern-Thun-Interlaken', () => {
    CommonUtils.clickFirstRowInTable(DataCy.TTFN);

    CommonUtils.deleteItem();
    cy.url().should('eq', Cypress.config().baseUrl + '/timetable-field-number');

    // Search still present after delete
    assertAllTableFiltersAreFilled();
    // No more items found
    CommonUtils.assertNoItemsInTable(DataCy.TTFN);
  });

  it('Step-9: Cleanup other TTFN', () => {
    // Get rid of search filter by reload
    CommonUtils.visit('/timetable-field-number');

    // Find other created item to clean up
    CommonUtils.typeSearchInput(
      '/line-directory/v1/field-numbers?**',
      DataCy.TABLE_SEARCH_CHIP_INPUT,
      churGrenze.swissTimetableFieldNumber
    );

    // Check that the table contains 1 result
    CommonUtils.assertNumberOfTableRows(DataCy.TTFN, 1);

    CommonUtils.clickFirstRowInTable(DataCy.TTFN);

    CommonUtils.deleteItem();
    cy.url().should('eq', Cypress.config().baseUrl + '/timetable-field-number');

    // Search still present after delete
    cy.get(DataCy.TABLE_SEARCH_STRINGS).contains(churGrenze.swissTimetableFieldNumber);
    // No more items found
    CommonUtils.assertNoItemsInTable(DataCy.TTFN);
  });
});
