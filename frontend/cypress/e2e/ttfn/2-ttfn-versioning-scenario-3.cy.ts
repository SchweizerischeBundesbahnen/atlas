import TtfnUtils from '../../support/util/ttfn-utils';
import CommonUtils from '../../support/util/common-utils';
import { DataCy } from '../../support/data-cy';
import BodiDependentUtils from '../../support/util/bodi-dependent-utils';

/**
 * Szenario 3: Update, dass über Versionsgrenze geht
 * NEU:                      |______________|
 * IST:      |----------------------|--------------------|
 * Version:        1                          2
 *
 * RESULTAT: |----------------|______|______|-------------     NEUE VERSION EINGEFÜGT
 * Version:        1              3     4         2
 */
describe('Versioning: scenario 3', {testIsolation: false}, () => {
  const firstVersion = TtfnUtils.getFirstVersion();
  const secondVersion = TtfnUtils.getSecondVersion();

  const versionUpdate = {
    swissTimetableFieldNumber: '00.AAA',
    validFrom: '01.06.2001',
    validTo: '01.06.2002',
    businessOrganisation: BodiDependentUtils.BO_DESCRIPTION,
    number: '1.1',
    description:
      'Update Description',
    comment: 'A new comment',
  };

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Dependent BusinessOrganisation Preparation Step', () => {
    BodiDependentUtils.createDependentBusinessOrganisation();
  });

  it('Step-2: Navigate to Fahrplanfeldnummer', () => {
    TtfnUtils.navigateToTimetableFieldNumber();
    TtfnUtils.checkHeaderTitle();
  });

  it('PreStep-3: check if ttfn already exists', () => {
    TtfnUtils.checkIfTtfnAlreadyExists(firstVersion);
  });

  it('Step-3: Add first Version', () => {
    TtfnUtils.clickOnAddNewVersion();
    TtfnUtils.fillVersionForm(firstVersion);
    CommonUtils.saveTtfn();
  });

  it('Step-4: Add second Version', () => {
    cy.get(DataCy.EDIT_ITEM).click();
    TtfnUtils.fillVersionForm(secondVersion);
    CommonUtils.saveTtfn();
  });

  it('Step-5: Add third Version', () => {
    cy.get(DataCy.EDIT_ITEM).click();
    TtfnUtils.fillVersionForm(versionUpdate);
    CommonUtils.saveTtfn();
    CommonUtils.getTotalRange().should('contain', '01.01.2000').should('contain', '31.12.2002');
  });

  it('Step-6: Assert fourth version (actual version)', () => {
    CommonUtils.assertSelectedVersion(4);
    CommonUtils.assertVersionRange(4, '02.06.2002', '31.12.2002');

    secondVersion.validFrom = '02.06.2002';
    secondVersion.validTo = '31.12.2002';
    TtfnUtils.assertContainsVersion(secondVersion);
  });

  it('Step-7: Assert third version', () => {
    CommonUtils.switchToVersion(3);
    CommonUtils.assertVersionRange(3, '01.06.2001', '01.06.2002');

    versionUpdate.validFrom = '01.06.2001';
    versionUpdate.validTo = '01.06.2002';
    TtfnUtils.assertContainsVersion(versionUpdate);
  });

  it('Step-8: Assert second version', () => {
    CommonUtils.switchToVersion(2);
    CommonUtils.assertVersionRange(2, '01.01.2001', '31.05.2001');

    secondVersion.validFrom = '01.01.2001';
    secondVersion.validTo = '31.05.2001';
    TtfnUtils.assertContainsVersion(secondVersion);
  });

  it('Step-9: Assert first version', () => {
    CommonUtils.switchToVersion(1);
    CommonUtils.assertVersionRange(1, '01.01.2000', '31.12.2000');

    firstVersion.validFrom = '01.01.2000';
    firstVersion.validTo = '31.12.2000';
    TtfnUtils.assertContainsVersion(firstVersion);
  });

  it('Step-10: Delete versions', () => {
    CommonUtils.deleteItem();
    TtfnUtils.checkHeaderTitle();
  });

  it('Dependent BusinessOrganisation Cleanup Step', () => {
    BodiDependentUtils.deleteDependentBusinessOrganisation();
  });
});
