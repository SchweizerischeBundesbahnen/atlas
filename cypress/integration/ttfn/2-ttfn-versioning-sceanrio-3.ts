import TtfnUtils from '../../support/util/ttfn-utils';
import CommonUtils from '../../support/util/common-utils';

// Szenario 3: Update, dass über Versionsgrenze geht
// NEU:                      |______________|
// IST:      |----------------------|--------------------|
// Version:        1                          2
//
//
// RESULTAT: |----------------|______|______|-------------     NEUE VERSION EINGEFÜGT
// Version:        1              3     4         2

describe('Versioning: scenario 3', () => {
  const firstVersion = TtfnUtils.getFirstVersion();

  const secondVersion = TtfnUtils.getSecondVersion();

  const versionUpdate = {
    swissTimetableFieldNumber: '00.AAA',
    validFrom: '01.06.2001',
    validTo: '01.06.2002',
    businessOrganisation: 'SBB3',
    number: '1.1',
    name: 'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z',
    comment: 'A new comment',
  };

  const headerTitle = 'Fahrplanfeldnummer';

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Navigate to Fahrplanfeldnummer', () => {
    TtfnUtils.navigateToTimetableFieldNumber();
    cy.contains(headerTitle);
  });

  it('Step-3: Add first Version', () => {
    TtfnUtils.clickOnAddNewVersion();
    TtfnUtils.fillVersionForm(firstVersion);
    CommonUtils.saveVersion();
  });

  it('Step-4: Add second Version', () => {
    cy.get('[data-cy=edit-item]').click();
    TtfnUtils.fillVersionForm(secondVersion);
    CommonUtils.saveVersion();
  });

  it('Step-5: Add third Version', () => {
    cy.get('[data-cy=edit-item]').click();
    TtfnUtils.fillVersionForm(versionUpdate);
    CommonUtils.saveVersion();
  });

  it('Step-6: Check version display', () => {
    cy.get('[data-cy=switch-version-total-range]').contains(
      'Fahrplanfeldnummer von 01.01.2000 bis 31.12.2002'
    );
    cy.get('[data-cy=switch-version-navigation-items]').contains('4 / 4');
    cy.get('[data-cy=switch-version-current-range]').contains('02.06.2002 bis 31.12.2002');
  });

  it('Step-7: Assert fourth version (actual version)', () => {
    cy.get('[data-cy=switch-version-total-range]').contains(
      'Fahrplanfeldnummer von 01.01.2000 bis 31.12.2002'
    );
    cy.get('[data-cy=switch-version-navigation-items]').contains('4 / 4');
    cy.get('[data-cy=switch-version-current-range]').contains('02.06.2002 bis 31.12.2002');

    secondVersion.validFrom = '02.06.2002';
    secondVersion.validTo = '31.12.2002';
    TtfnUtils.assertContainsVersion(secondVersion);
  });

  it('Step-8: Assert third version', () => {
    cy.get('[data-cy=switch-version-total-range]').contains(
      'Fahrplanfeldnummer von 01.01.2000 bis 31.12.2002'
    );
    CommonUtils.switchLeft();
    cy.get('[data-cy=switch-version-navigation-items]').contains('3 / 4');
    cy.get('[data-cy=switch-version-current-range]').contains('01.06.2001 bis 01.06.2002');

    versionUpdate.validFrom = '01.06.2001';
    versionUpdate.validTo = '01.06.2002';
    TtfnUtils.assertContainsVersion(versionUpdate);
  });

  it('Step-9: Assert second version', () => {
    cy.get('[data-cy=switch-version-total-range]').contains(
      'Fahrplanfeldnummer von 01.01.2000 bis 31.12.2002'
    );
    CommonUtils.switchLeft();
    cy.get('[data-cy=switch-version-navigation-items]').contains('2 / 4');
    cy.get('[data-cy=switch-version-current-range]').contains('01.01.2001 bis 31.05.2001');

    secondVersion.validFrom = '01.01.2001';
    secondVersion.validTo = '31.05.2001';
    TtfnUtils.assertContainsVersion(secondVersion);
  });

  it('Step-10: Assert first version', () => {
    cy.get('[data-cy=switch-version-total-range]').contains(
      'Fahrplanfeldnummer von 01.01.2000 bis 31.12.2002'
    );
    CommonUtils.switchLeft();
    cy.get('[data-cy=switch-version-navigation-items]').contains('1 / 4');
    cy.get('[data-cy=switch-version-current-range]').contains('01.01.2000 bis 31.12.2000');

    firstVersion.validFrom = '01.01.2000';
    firstVersion.validTo = '31.12.2000';
    TtfnUtils.assertContainsVersion(firstVersion);
  });

  it('Step-11: Delete versions', () => {
    CommonUtils.deleteItems();
    cy.contains(headerTitle);
  });
});
