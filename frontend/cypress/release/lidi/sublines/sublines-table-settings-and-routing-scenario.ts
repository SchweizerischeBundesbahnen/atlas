import CommonUtils from '../../../support/util/common-utils';
import { DataCy } from '../../../support/data-cy';
import LidiUtils from '../../../support/util/lidi-utils';
import BodiDependentUtils from '../../../support/util/bodi-dependent-utils';

describe('Sublines: TableSettings and Routing', { testIsolation: false }, () => {
  const minimalLine1 = LidiUtils.getFirstMinimalLineVersion();
  const minimalSubline1 = LidiUtils.getFirstMinimalSubline();
  const minimalSubline2 = LidiUtils.getSecondMinimalSubline();

  const commonValidDate = '01.01.2000';
  const statusValidiert = 'Validiert';

  const sublineDirectoryUrlPath = '/line-directory/sublines';
  const sublineDirectoryUrlPathToIntercept = '/line-directory/v1/sublines?**';

  function deleteFirstFoundSublineInTable() {
    CommonUtils.clickFirstRowInTable(DataCy.LIDI_SUBLINES);

    CommonUtils.deleteItem();
    cy.url().should('eq', Cypress.config().baseUrl + sublineDirectoryUrlPath);
  }

  function assertAllTableFiltersAreFilled() {
    cy.get(DataCy.TABLE_FILTER_CHIP_DIV).contains(minimalSubline1.swissSublineNumber);
    CommonUtils.assertItemsFromDropdownAreChecked(DataCy.TABLE_FILTER_MULTI_SELECT(1, 2), [
      statusValidiert,
    ]);

    CommonUtils.assertItemsFromDropdownAreChecked(DataCy.TABLE_FILTER_MULTI_SELECT(1, 1), [
      minimalSubline1.type,
    ]);
    CommonUtils.assertDatePickerIs(DataCy.TABLE_FILTER_DATE_INPUT(1, 3), commonValidDate);

    // Check that the table contains 1 result
    CommonUtils.assertNumberOfTableRows(DataCy.LIDI_SUBLINES, 1);
  }

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Dependent BusinessOrganisation Preparation Step', () => {
    BodiDependentUtils.createDependentBusinessOrganisation();
  });

  it('Step-2: Navigate to Sublines', () => {
    LidiUtils.navigateToSublines();
  });

  it('Step-3: Add new line', () => {
    LidiUtils.addLine(minimalLine1);
  });

  it('Step-4: Add new subline', () => {
    LidiUtils.clickOnAddNewSublineVersion();
    LidiUtils.fillSublineVersionForm(minimalSubline1);
    CommonUtils.saveSubline();
    CommonUtils.fromDetailBackToSublinesOverview();
  });

  it('Step-5: Add another subline', () => {
    LidiUtils.clickOnAddNewSublineVersion();
    LidiUtils.fillSublineVersionForm(minimalSubline2);
    CommonUtils.saveSubline();
    CommonUtils.fromDetailBackToSublinesOverview();
  });

  it('Step-6: Look for subline 1', () => {
    CommonUtils.typeSearchInput(
      sublineDirectoryUrlPathToIntercept,
      DataCy.TABLE_FILTER_CHIP_INPUT,
      minimalSubline1.swissSublineNumber
    );

    CommonUtils.chooseOneValueFromMultiselect(
      DataCy.TABLE_FILTER_MULTI_SELECT(1, 2),
      statusValidiert
    );
    CommonUtils.selectItemFromDropdownSearchItem(
      DataCy.TABLE_FILTER_MULTI_SELECT(1, 1),
      minimalSubline1.type
    );

    CommonUtils.typeSearchInput(
      sublineDirectoryUrlPathToIntercept,
      DataCy.TABLE_FILTER_DATE_INPUT(1, 3),
      commonValidDate
    );

    assertAllTableFiltersAreFilled();
  });

  it('Step-7: Click on add new subline Button and come back without actually creating it', () => {
    LidiUtils.clickOnAddNewSublineVersion();
    CommonUtils.clickCancelOnDetailViewBackToSublines();

    assertAllTableFiltersAreFilled();
  });

  it('Step-8: Change CHTLNR of subline from r.31.001:x_ to r.31.001:x_-changed', () => {
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
    cy.get(DataCy.LIDI_SUBLINES + ' .cdk-column-swissSublineNumber').contains(newCHTLNR);
  });

  it('Step-9: Delete subline minimal1', () => {
    deleteFirstFoundSublineInTable();

    // Search still present after delete
    assertAllTableFiltersAreFilled();
    // No more items found
    CommonUtils.assertNoItemsInTable(DataCy.LIDI_SUBLINES);
  });

  it('Step-10: Cleanup other subline', () => {
    // Get rid of search filter by reload
    CommonUtils.visit('/line-directory/sublines');

    // Find other created item to clean up
    CommonUtils.typeSearchInput(
      sublineDirectoryUrlPathToIntercept,
      DataCy.TABLE_FILTER_CHIP_INPUT,
      minimalSubline2.swissSublineNumber
    );

    // Check that the table contains 1 result
    CommonUtils.assertNumberOfTableRows(DataCy.LIDI_SUBLINES, 1);

    deleteFirstFoundSublineInTable();

    // Search still present after delete
    cy.get(DataCy.TABLE_FILTER_CHIP_DIV).contains(minimalSubline2.swissSublineNumber);
    // No more items found
    CommonUtils.assertNoItemsInTable(DataCy.LIDI_SUBLINES);
  });

  it('Step-11: Delete line minimal1', () => {
    LidiUtils.changeLiDiTabToLines();
    LidiUtils.searchAndNavigateToLine(minimalLine1);
    CommonUtils.deleteItem();
    cy.url().should('eq', Cypress.config().baseUrl + '/line-directory/lines');

    // No more items found
    CommonUtils.assertNoItemsInTable(DataCy.LIDI_LINES);
  });

  it('Dependent BusinessOrganisation Cleanup Step', () => {
    BodiDependentUtils.deleteDependentBusinessOrganisation();
  });
});
