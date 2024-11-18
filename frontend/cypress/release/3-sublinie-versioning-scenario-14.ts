import LidiUtils from '../support/util/lidi-utils';
import CommonUtils from '../support/util/common-utils';
import { DataCy } from '../support/data-cy';
import BodiDependentUtils from '../support/util/bodi-dependent-utils';

/** Szenario 14: Linke Grenze ("Gültig von") auf gleichen Tag setzen, wie rechte Grenze ("Gültig bis")
 *           01.01.2000                                             31.12.2000
 * NEU:                                                             |
 * IST:      |------------------------------------------------------|
 * Version:                               1
 *
 * RESULTAT:                                                        |
 * Version:                                                         1
 */
describe('LiDi: Versioning Teillinie Scenario 14 - ATLAS-316', { testIsolation: false }, () => {
  const sublineVersion = LidiUtils.getFirstSublineVersion();
  const newValidFrom = '31.12.2000';
  let mainline: any;

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Dependent BusinessOrganisation Preparation Step', () => {
    BodiDependentUtils.createDependentBusinessOrganisation();
  });

  it('Step-2: Add mainline', () => {
    mainline = LidiUtils.addMainLine();
  });

  it('Step-4: Add Subline Version', () => {
    LidiUtils.clickOnAddNewSublineVersion();
    LidiUtils.fillSublineVersionForm(sublineVersion);
    CommonUtils.saveSubline();
    LidiUtils.readSlnidFromForm(sublineVersion);
  });

  it('Step-5: Update Subline Version', () => {
    CommonUtils.clickOnEdit();
    cy.get(DataCy.VALID_FROM).clear().type(newValidFrom);
    CommonUtils.saveSubline();
  });

  it('Step-6: Assert version (current version)', () => {
    CommonUtils.assertSelectedVersion(1);
    sublineVersion.validFrom = newValidFrom;
    LidiUtils.assertContainsSublineVersion(sublineVersion);
  });

  it('Step-7: Navigate to Lines', () => {
    CommonUtils.fromDetailBackToLinesOverview();
    CommonUtils.navigateToHomeViaHomeLogo();
    LidiUtils.navigateToLines();
  });

  it('Step-8: Check the added is present on the table result and navigate to it ', () => {
    LidiUtils.searchSublineAndNavigateToLines(sublineVersion);
  });

  it('Step-9: Delete the subline item ', () => {
    CommonUtils.deleteItem();
    LidiUtils.assertIsOnLines();
  });

  it('Step-10: Delete the mainline item ', () => {
    LidiUtils.clickOnLineInOverview(mainline);
    LidiUtils.assertContainsLineVersion(mainline);

    CommonUtils.deleteItem();
    LidiUtils.assertIsOnLines();
  });

  it('Dependent BusinessOrganisation Cleanup Step', () => {
    BodiDependentUtils.deleteDependentBusinessOrganisation();
  });
});
