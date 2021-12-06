import TtfnUtils from '../../support/util/ttfn-utils';

describe('Versioning', () => {
  const firstVersion = {
    swissTimetableFieldNumber: '00.AAA',
    validFrom: '01.01.2000',
    validTo: '31.12.2000',
    businessOrganisation: 'SBB',
    number: '1.1',
    name: 'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z',
    comment: 'This is a comment',
  };

  const secondVersion = {
    swissTimetableFieldNumber: '00.AAA',
    validFrom: '01.01.2001',
    validTo: '31.12.2002',
    businessOrganisation: 'SBB1',
    number: '1.1',
    name: 'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z',
    comment: 'A new comment',
  };

  const thirdVersion = {
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
  });

  it('Step-3: Check the Fahrplanfeldnummer Table is visible', () => {
    cy.contains(headerTitle);
    cy.get('table').get('thead tr th').eq(1).get('div').contains('CH-Fahrplanfeldnummer');
    cy.get('table')
      .get('thead tr th')
      .eq(2)
      .get('div')
      .contains('CH-Fahrplanfeldnummer Bezeichnung');
    cy.get('table').get('thead tr th').eq(3).get('div').contains('Status');
    cy.get('table').get('thead tr th').eq(4).get('div').contains('Fahrplanfeldnummer-ID');
    cy.get('table').get('thead tr th').eq(4).get('div').contains('Gültig von');
    cy.get('table').get('thead tr th').eq(4).get('div').contains('Gültig bis');
  });

  it('Step-4: Add first Version', () => {
    TtfnUtils.clickOnAddNewVersion();
    TtfnUtils.fillVersionForm(firstVersion);
    TtfnUtils.saveVersion();
  });

  it('Step-5: Add second Version', () => {
    cy.get('[data-cy=edit-item]').click();
    TtfnUtils.fillVersionForm(secondVersion);
    TtfnUtils.updateVersion();
  });

  it('Step-6: Add third Version', () => {
    cy.get('[data-cy=edit-item]').click();
    TtfnUtils.fillVersionForm(thirdVersion);
    TtfnUtils.updateVersion();
  });

  it('Step-7: check version display', () => {
    cy.get('[data-cy=switch-version-total-range]').contains(
      'Fahrplanfeldnummer von 01.01.2000 bis 31.12.2002'
    );
    cy.get('[data-cy=switch-version-navigation-items]').contains('4 / 4');
    cy.get('[data-cy=switch-version-current-range]').contains('02.06.2002 bis 31.12.2002');
  });

  it('Step-8: assert fourth version', () => {
    cy.get('[data-cy=switch-version-total-range]').contains(
      'Fahrplanfeldnummer von 01.01.2000 bis 31.12.2002'
    );
    cy.get('[data-cy=switch-version-navigation-items]').contains('4 / 4');
    cy.get('[data-cy=switch-version-current-range]').contains('02.06.2002 bis 31.12.2002');

    secondVersion.validFrom = '02.06.2002';
    secondVersion.validTo = '31.12.2002';
    TtfnUtils.assertContainsVersion(secondVersion);
  });

  it('Step-9: assert third version', () => {
    cy.get('[data-cy=switch-version-total-range]').contains(
      'Fahrplanfeldnummer von 01.01.2000 bis 31.12.2002'
    );
    cy.get('[data-cy=switch-version-left]').click();
    cy.get('[data-cy=switch-version-navigation-items]').contains('3 / 4');
    cy.get('[data-cy=switch-version-current-range]').contains('01.06.2001 bis 01.06.2002');

    thirdVersion.validFrom = '01.06.2001';
    thirdVersion.validTo = '01.06.2002';
    TtfnUtils.assertContainsVersion(thirdVersion);
  });

  it('Step-10: assert second version', () => {
    cy.get('[data-cy=switch-version-total-range]').contains(
      'Fahrplanfeldnummer von 01.01.2000 bis 31.12.2002'
    );
    cy.get('[data-cy=switch-version-left]').click();
    cy.get('[data-cy=switch-version-navigation-items]').contains('2 / 4');
    cy.get('[data-cy=switch-version-current-range]').contains('01.01.2001 bis 31.05.2001');

    secondVersion.validFrom = '01.01.2001';
    secondVersion.validTo = '31.05.2001';
    TtfnUtils.assertContainsVersion(secondVersion);
  });

  it('Step-11: assert first version', () => {
    cy.get('[data-cy=switch-version-total-range]').contains(
      'Fahrplanfeldnummer von 01.01.2000 bis 31.12.2002'
    );
    cy.get('[data-cy=switch-version-left]').click();
    cy.get('[data-cy=switch-version-navigation-items]').contains('1 / 4');
    cy.get('[data-cy=switch-version-current-range]').contains('01.01.2000 bis 31.12.2000');

    firstVersion.validFrom = '01.01.2000';
    firstVersion.validTo = '31.12.2000';
    TtfnUtils.assertContainsVersion(firstVersion);
  });
});
