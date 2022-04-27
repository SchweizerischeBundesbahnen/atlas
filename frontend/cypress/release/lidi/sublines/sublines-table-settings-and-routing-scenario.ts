import CommonUtils from '../../../support/util/common-utils';
import { DataCy } from '../../../support/data-cy';
import LidiUtils from '../../../support/util/lidi-utils';

describe('Sublines: TableSettings and Routing', () => {
  const minimalSubline1 = LidiUtils.getFirstMinimalSubline();
  const minimalSubline2 = LidiUtils.getSecondMinimalSubline();

  const commonValidDate = '01.01.2000';
  const statusAktiv = 'Aktiv';

  const sublineDirectoryUrlPath = '/line-directory/sublines';
  const sublineDirectoryUrlPathToIntercept = '/line-directory/v1/sublines?**';

  function deleteFirstFoundSublineInTable() {
    CommonUtils.clickFirstRowInTable(DataCy.LIDI_SUBLINES);

    CommonUtils.deleteItem();
    cy.url().should('eq', Cypress.config().baseUrl + sublineDirectoryUrlPath);
  }

  function assertAllTableFiltersAreFilled() {
    cy.get(DataCy.TABLE_SEARCH_STRINGS).contains(minimalSubline1.swissSublineNumber);
    CommonUtils.assertItemsFromDropdownAreChecked(DataCy.TABLE_SEARCH_STATUS_INPUT, [statusAktiv]);

    CommonUtils.assertItemsFromDropdownAreChecked(DataCy.TABLE_SEARCH_SUBLINE_TYPE, [
      minimalSubline1.type,
    ]);
    CommonUtils.assertDatePickerIs(DataCy.TABLE_SEARCH_DATE_INPUT, commonValidDate);

    // Check that the table contains 1 result
    CommonUtils.assertNumberOfTableRows(DataCy.LIDI_SUBLINES, 1);
  }

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Navigate to Sublines', () => {
    LidiUtils.navigateToSublines();
  });

  it('Step-3: Add new subline', () => {
    LidiUtils.clickOnAddNewSublinesLinieVersion();
    LidiUtils.fillSublineVersionForm(minimalSubline1);
    CommonUtils.saveSubline();
    CommonUtils.fromDetailBackToSublinesOverview();
  });

  it('Step-4: Add another subline', () => {
    LidiUtils.clickOnAddNewSublinesLinieVersion();
    LidiUtils.fillSublineVersionForm(minimalSubline2);
    CommonUtils.saveSubline();
    CommonUtils.fromDetailBackToSublinesOverview();
  });

  it('Step-5: Look for subline 1', () => {
    CommonUtils.typeSearchInput(
      sublineDirectoryUrlPathToIntercept,
      DataCy.TABLE_SEARCH_CHIP_INPUT,
      minimalSubline1.swissSublineNumber
    );

    CommonUtils.selectItemFromDropdownSearchItem(DataCy.TABLE_SEARCH_STATUS_INPUT, statusAktiv);
    CommonUtils.selectItemFromDropdownSearchItem(
      DataCy.TABLE_SEARCH_SUBLINE_TYPE,
      minimalSubline1.type
    );

    CommonUtils.typeSearchInput(
      sublineDirectoryUrlPathToIntercept,
      DataCy.TABLE_SEARCH_DATE_INPUT,
      commonValidDate
    );

    assertAllTableFiltersAreFilled();
  });

  it('Step-6: Click on add new subline Button and come back without actually creating it', () => {
    LidiUtils.clickOnAddNewSublinesLinieVersion();
    CommonUtils.clickCancelOnDetailViewBackToSublines();

    assertAllTableFiltersAreFilled();
  });

  it('Step-7: Change CHTLNR of subline from r.31.001:x_ to r.31.001:x_-changed', () => {
    CommonUtils.assertNumberOfTableRows(DataCy.LIDI_SUBLINES, 1);
    CommonUtils.clickFirstRowInTable(DataCy.LIDI_SUBLINES);

    cy.get(DataCy.EDIT_ITEM).click();
    const newCHTLNR = 'r.31.001:x_-changed';
    cy.get(DataCy.SWISS_SUBLINE_NUMBER).clear().type(newCHTLNR, { force: true });
    CommonUtils.saveSubline();

    cy.intercept('GET', sublineDirectoryUrlPathToIntercept).as('getSublines');
    CommonUtils.fromDetailBackToSublinesOverview();
    cy.wait('@getSublines');

    // Search still present after edit
    assertAllTableFiltersAreFilled();
    // Change is already visible in table
    cy.get(DataCy.LIDI_SUBLINES + ' .mat-row > .cdk-column-swissSublineNumber').contains(newCHTLNR);
  });

  it('Step-8: Delete subline minimal1', () => {
    deleteFirstFoundSublineInTable();

    // Search still present after delete
    assertAllTableFiltersAreFilled();
    // No more items found
    CommonUtils.assertNoItemsInTable(DataCy.LIDI_SUBLINES);
  });

  it('Step-9: Cleanup other subline', () => {
    // Get rid of search filter by reload
    cy.reload();

    // Find other created item to clean up
    CommonUtils.typeSearchInput(
      sublineDirectoryUrlPathToIntercept,
      DataCy.TABLE_SEARCH_CHIP_INPUT,
      minimalSubline2.swissSublineNumber
    );

    // Check that the table contains 1 result
    CommonUtils.assertNumberOfTableRows(DataCy.LIDI_SUBLINES, 1);

    deleteFirstFoundSublineInTable();

    // Search still present after delete
    cy.get(DataCy.TABLE_SEARCH_STRINGS).contains(minimalSubline2.swissSublineNumber);
    // No more items found
    CommonUtils.assertNoItemsInTable(DataCy.LIDI_SUBLINES);
  });
});
