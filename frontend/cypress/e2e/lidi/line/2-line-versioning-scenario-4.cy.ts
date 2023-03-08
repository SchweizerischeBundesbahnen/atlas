import LidiUtils from '../../../support/util/lidi-utils';
import CommonUtils from '../../../support/util/common-utils';
import { DataCy } from '../../../support/data-cy';
import BodiDependentUtils from '../../../support/util/bodi-dependent-utils';

/**
 * Szenario 4: Update, das über eine ganze Version hinausragt
 * NEU:             |___________________________________|
 * IST:      |-----------|----------------------|--------------------
 * Version:        1                 2                  3
 *
 *
 * RESULTAT: |------|_____|______________________|______|------------     NEUE VERSION EINGEFÜGT
 * Version:      1     4              2              5        3
 */

describe('LiDi: Versioning Linie Scenario 4', {testIsolation: false}, () => {
  const firstLinieVersion = LidiUtils.getFirstLineVersion();
  const secondLineVersion = LidiUtils.getSecondLineVersion();
  const thirdLineVersion = LidiUtils.getThirdLineVersion();
  const editedLineVersion = LidiUtils.getEditedLineVersion();

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Dependent BusinessOrganisation Preparation Step', () => {
    BodiDependentUtils.createDependentBusinessOrganisation();
  });

  it('Step-2: Navigate to Linien', () => {
    LidiUtils.navigateToLines();
  });

  it('PreStep-3: check if line already exists', () => {
    LidiUtils.checkIfLineAlreadyExists(firstLinieVersion);
  });

  it('Step-3: Add first Linie Version', () => {
    LidiUtils.clickOnAddNewLineVersion();
    LidiUtils.fillLineVersionForm(firstLinieVersion);
    CommonUtils.saveLine();
  });

  it('Step-4: Add second Linie Version', () => {
    CommonUtils.clickOnEdit();
    LidiUtils.fillLineVersionForm(secondLineVersion);
    CommonUtils.saveLine();
  });

  it('Step-5: Add third Linie Version', () => {
    CommonUtils.clickOnEdit();
    LidiUtils.fillLineVersionForm(thirdLineVersion);
    CommonUtils.saveLine();
  });

  it('Step-6: Add edited Linie Version to trigger versioning Scenario 4', () => {
    CommonUtils.clickOnEdit();
    cy.get(DataCy.VALID_FROM).clear().type(editedLineVersion.validFrom);
    cy.get(DataCy.VALID_TO).clear().type(editedLineVersion.validTo);
    cy.get(DataCy.ALTERNATIVE_NAME).clear().type(editedLineVersion.alternativeName);
    CommonUtils.saveLine();
    CommonUtils.getTotalRange().should('contain', '01.01.2000').should('contain', '31.12.2002');
  });

  it('Step-7: Assert fifth version (actual version)', () => {
    CommonUtils.assertSelectedVersion(5);
    CommonUtils.assertVersionRange(5, '02.06.2002', '31.12.2002');

    thirdLineVersion.validFrom = '02.06.2002';
    thirdLineVersion.validTo = '31.12.2002';
    LidiUtils.assertContainsLineVersion(thirdLineVersion);
  });

  it('Step-8: Assert fourth version', () => {
    CommonUtils.switchToVersion(4);
    CommonUtils.assertVersionRange(4, '01.01.2002', '01.06.2002');

    thirdLineVersion.validFrom = '01.01.2002';
    thirdLineVersion.validTo = '01.06.2002';
    thirdLineVersion.alternativeName = editedLineVersion.alternativeName;
    LidiUtils.assertContainsLineVersion(thirdLineVersion);
  });

  it('Step-9: Assert third version', () => {
    CommonUtils.switchToVersion(3);
    CommonUtils.assertVersionRange(3, '01.01.2001', '31.12.2001');

    thirdLineVersion.validFrom = '01.01.2001';
    thirdLineVersion.validTo = '31.12.2001';
    thirdLineVersion.alternativeName = editedLineVersion.alternativeName;
    thirdLineVersion.comment = 'Kommentar-1';
    LidiUtils.assertContainsLineVersion(thirdLineVersion);
  });

  it('Step-10: Assert second version', () => {
    CommonUtils.switchToVersion(2);
    CommonUtils.assertVersionRange(2, '01.06.2000', '31.12.2000');

    secondLineVersion.validFrom = '01.06.2000';
    secondLineVersion.validTo = '31.12.2000';
    secondLineVersion.alternativeName = editedLineVersion.alternativeName;
    secondLineVersion.comment = firstLinieVersion.comment;
    LidiUtils.assertContainsLineVersion(secondLineVersion);
  });

  it('Step-11: Assert first version', () => {
    CommonUtils.switchToVersion(1);
    CommonUtils.assertVersionRange(1, '01.01.2000', '31.05.2000');

    firstLinieVersion.validTo = '31.05.2000';
    LidiUtils.assertContainsLineVersion(firstLinieVersion);
  });

  it('Step-12: Delete the item ', () => {
    CommonUtils.deleteItem();
    LidiUtils.checkHeaderTitle();
  });

  it('Dependent BusinessOrganisation Cleanup Step', () => {
    BodiDependentUtils.deleteDependentBusinessOrganisation();
  });
});
