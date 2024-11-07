import LidiUtils from '../../../support/util/lidi-utils';
import CommonUtils from '../../../support/util/common-utils';
import {DataCy} from '../../../support/data-cy';
import BodiDependentUtils from '../../../support/util/bodi-dependent-utils';

describe('Teillinie', { testIsolation: false }, () => {
  const sublineVersion = LidiUtils.getFirstSublineVersion();
  let mainline: any;

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Dependent BusinessOrganisation Preparation Step', () => {
    BodiDependentUtils.createDependentBusinessOrganisation();
  });

  it('PreStep-2: check if subline and mainline already exists', () => {
    LidiUtils.navigateToLines()
    LidiUtils.checkIfSublineAlreadyExists(sublineVersion);
    LidiUtils.navigateToLines();
    LidiUtils.checkIfLineAlreadyExists(LidiUtils.getMainLineVersion());
  });

  it('Step-2: Add mainline', () => {
    mainline = LidiUtils.addMainLine();
  });

  it('Step-3: Navigate to Line', () => {
    LidiUtils.navigateToLines();
  });

  it('Step-4: Check the Linienverzeichnis Line Table is visible', () => {
    LidiUtils.checkLineTable();
  });

  it('Step-5: Go to page Add new Version', () => {
    LidiUtils.clickOnAddNewSublineVersion();
    LidiUtils.fillSublineVersionForm(sublineVersion);
    CommonUtils.saveSubline();
    LidiUtils.readSlnidFromForm(sublineVersion);
  });

  it('Step-6: Navigate to Sublines', () => {
    CommonUtils.fromDetailBackToLinesOverview();
    CommonUtils.navigateToHomeViaHomeLogo();
    LidiUtils.navigateToLines();
    LidiUtils.checkHeaderTitle();
  });

  it('Step-7: Search for added element on the table and navigate to it', () => {
    const pathToIntercept = '/line-directory/v1/lines?**';

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.LIDI_LINES + ' ' + DataCy.TABLE_FILTER_CHIP_INPUT,
      sublineVersion.swissSublineNumber
    );

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.LIDI_LINES + ' ' + DataCy.TABLE_FILTER_CHIP_INPUT,
      sublineVersion.slnid
    );

    CommonUtils.selectItemFromDropdownSearchItem(
      DataCy.LIDI_LINES + ' ' + DataCy.TABLE_FILTER_MULTI_SELECT(1, 2),
      'Aktiv'
    );

    CommonUtils.selectItemFromDropdownSearchItem(
      DataCy.LIDI_LINES + ' ' + DataCy.TABLE_FILTER_MULTI_SELECT(1, 1),
      sublineVersion.type
    );

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.LIDI_LINES + ' ' + DataCy.TABLE_FILTER_DATE_INPUT(1, 3),
      sublineVersion.validTo
    );
    // Check that the table contains 1 result
    cy.get(DataCy.LIDI_LINES + ' table tbody tr').should('have.length', 1);
    // Click on the item
    cy.contains('td', sublineVersion.swissSublineNumber).parents('tr').click({force: true});
    CommonUtils.getTotalRange().should('contain', '01.01.2000').should('contain', '31.12.2000');
    LidiUtils.assertContainsSublineVersion(sublineVersion);
  });

  it('Step-8: Delete the subline item', () => {
    CommonUtils.deleteItem();
    LidiUtils.assertIsOnLines();

    //clear search values added in previously step
    CommonUtils.clearSearchChip();
    CommonUtils.selectItemFromDropdownSearchItem(
      DataCy.LIDI_LINES + ' ' + DataCy.TABLE_FILTER_MULTI_SELECT(1, 1),
      sublineVersion.type
    );
  });

  it('Step-9: Navigate to the mainline item', () => {
    LidiUtils.searchAndNavigateToLine(mainline);
    CommonUtils.getTotalRange().should('contain', '01.01.2000').should('contain', '31.12.2002');
  });

  it('Step-10: Delete the mainline item', () => {
    CommonUtils.deleteItem();
    LidiUtils.assertIsOnLines();
  });

  it('Dependent BusinessOrganisation Cleanup Step', () => {
    BodiDependentUtils.deleteDependentBusinessOrganisation();
  });
});
