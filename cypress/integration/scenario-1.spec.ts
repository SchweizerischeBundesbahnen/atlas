describe('Fahrplanfeldnummer Table is available', () => {
  it('Login on ATLAS', () => {
    cy.clearCookies();
    cy.clearLocalStorage();
    cy.login();
    cy.wait(2000);
  });

  it('Step 1: Check the Fahrplanfeldnummer Table is visible', () => {
    cy.contains('Fahrplanfeldnummer');
    cy.get('table').get('thead tr th').eq(1).get('div').contains('Fahrplannummer');
    cy.get('table').get('thead tr th').eq(2).get('div').contains('Bezeichnung');
    cy.get('table').get('thead tr th').eq(3).get('div').contains('Status');
    cy.get('table').get('thead tr th').eq(4).get('div').contains('Fahrplanfeldnummer');
    cy.get('table').get('thead tr th').eq(4).get('div').contains('Gültig von');
    cy.get('table').get('thead tr th').eq(4).get('div').contains('Gültig bis');
  });

  it('Step 2: Go to page Add new Version', () => {
    cy.get('[data-cy=new-item]').click();
    cy.get('[data-cy=save-item]').should('be.disabled');
    cy.get('[data-cy=edit-item]').should('not.exist');
    cy.get('[data-cy=delete-item]').should('not.exist');
    cy.contains('Neue Fahrplanfeldnummer');

    //add input values
    cy.get('[data-cy=swissTimetableFieldNumber]').type('aa.AAA');
    cy.get('[data-cy=ttfnid]').type('ch:1:fpfnid:100000');
    cy.get('[data-cy=validFrom]').type('22.09.2021');
    cy.get('[data-cy=validTo]').type('22.09.2021');
    cy.get('[data-cy=businessOrganisation]').type('SBB');
    cy.get('[data-cy=number]').type('BEXsZs');
    cy.get('[data-cy=name]').type(
      'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z'
    );
    cy.get('[data-cy=comment]').type('This is a comment');
    cy.get('[data-cy=save-item]').should('not.be.disabled');
    //save version
    cy.get('[data-cy=save-item]').click();
    cy.get('simple-snack-bar').should('be.visible');
    cy.get('simple-snack-bar').contains('Fahrplanfeldnummer erfolgreich hinzugefügt.');
    cy.get('snack-bar-container').should('have.class', 'success');
    cy.get('[data-cy=edit-item]').should('be.visible');
    cy.get('[data-cy=delete-item]').should('be.visible');
  });

  it('Navigate to the home', () => {
    cy.get('#home').click();
    cy.contains('Fahrplanfeldnummer');
  });

  it('Check the item aa.AAA is present on the table result and navigate to it ', () => {
    cy.contains('aa.AAA').parents('tr').click();
    cy.contains('aa.AAA');
    cy.get('[data-cy=swissTimetableFieldNumber]').invoke('val').should('eq', 'aa.AAA');
    cy.get('[data-cy=ttfnid]').invoke('val').should('eq', 'ch:1:fpfnid:100000');
    cy.get('[data-cy=validFrom]').invoke('val').should('eq', '22.09.2021');
    cy.get('[data-cy=validTo]').invoke('val').should('eq', '22.09.2021');
    cy.get('[data-cy=businessOrganisation]').invoke('val').should('eq', 'SBB');
    cy.get('[data-cy=number]').invoke('val').should('eq', 'BEXsZs');
    cy.get('[data-cy=name]')
      .invoke('val')
      .should(
        'eq',
        'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z'
      );
    cy.get('[data-cy=comment]').invoke('val').should('eq', 'This is a comment');
  });

  it('Delete the item aa.AAA ', () => {
    cy.get('[data-cy=delete-item]').click();
    cy.get('#mat-dialog-0').contains('Warnung!');
    cy.get('[data-cy=dialog-confirm-button]').should('exist');
    cy.get('[data-cy=dialog-cancel-button]').should('exist');
    cy.get('[data-cy=dialog-confirm-button]').click();
    cy.contains('Fahrplanfeldnummer');
  });
});
