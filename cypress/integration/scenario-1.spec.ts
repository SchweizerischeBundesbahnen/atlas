describe('Fahrplanfeldnummer', () => {
  const swissTimetableFieldNumber = 'aa.AAA';
  const ttfnid = 'ch:1:fpfnid:100000';
  const validFrom = '22.09.2021';
  const validTo = '22.09.2021';
  const businessOrganisation = 'SBB';
  const number = 'BEXsZs';
  const name =
    'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z';
  const comment = 'This is a comment';
  const headerTitle = 'Fahrplanfeldnummer';

  it('Step-1: Login on ATLAS', () => {
    cy.clearCookies();
    cy.clearLocalStorage();
    cy.login();
  });

  it('Step-2: Check the Fahrplanfeldnummer Table is visible', () => {
    cy.contains(headerTitle);
    cy.get('table').get('thead tr th').eq(1).get('div').contains('Fahrplannummer');
    cy.get('table').get('thead tr th').eq(2).get('div').contains('Bezeichnung');
    cy.get('table').get('thead tr th').eq(3).get('div').contains('Status');
    cy.get('table').get('thead tr th').eq(4).get('div').contains('Fahrplanfeldnummer');
    cy.get('table').get('thead tr th').eq(4).get('div').contains('Gültig von');
    cy.get('table').get('thead tr th').eq(4).get('div').contains('Gültig bis');
  });

  it('Step-3: Go to page Add new Version', () => {
    cy.get('[data-cy=new-item]').click();
    cy.get('[data-cy=save-item]').should('be.disabled');
    cy.get('[data-cy=edit-item]').should('not.exist');
    cy.get('[data-cy=delete-item]').should('not.exist');
    cy.contains('Neue Fahrplanfeldnummer');

    //add input values
    cy.get('[data-cy=swissTimetableFieldNumber]').type(swissTimetableFieldNumber);
    cy.get('[data-cy=ttfnid]').type(ttfnid);
    cy.get('[data-cy=validFrom]').type(validFrom);
    cy.get('[data-cy=validTo]').type(validTo);
    cy.get('[data-cy=businessOrganisation]').type(businessOrganisation);
    cy.get('[data-cy=number]').type(number);
    cy.get('[data-cy=name]').type(name);
    cy.get('[data-cy=comment]').type(comment);
    cy.get('[data-cy=save-item]').should('not.be.disabled');
    //save version
    cy.get('[data-cy=save-item]').click();
    cy.get('simple-snack-bar').should('be.visible');
    cy.get('simple-snack-bar').contains('Fahrplanfeldnummer erfolgreich hinzugefügt.');
    cy.get('snack-bar-container').should('have.class', 'success');
    cy.get('[data-cy=edit-item]').should('be.visible');
    cy.get('[data-cy=delete-item]').should('be.visible');
  });

  it('Step-4: Navigate to the home', () => {
    cy.get('#home').click();
    cy.contains(headerTitle);
  });

  it('Step-5: Check the item aa.AAA is present on the table result and navigate to it ', () => {
    cy.contains(swissTimetableFieldNumber).parents('tr').click();
    cy.contains(swissTimetableFieldNumber);
    cy.get('[data-cy=swissTimetableFieldNumber]')
      .invoke('val')
      .should('eq', swissTimetableFieldNumber);
    cy.get('[data-cy=ttfnid]').invoke('val').should('eq', ttfnid);
    cy.get('[data-cy=validFrom]').invoke('val').should('eq', validFrom);
    cy.get('[data-cy=validTo]').invoke('val').should('eq', validTo);
    cy.get('[data-cy=businessOrganisation]').invoke('val').should('eq', businessOrganisation);
    cy.get('[data-cy=number]').invoke('val').should('eq', number);
    cy.get('[data-cy=name]').invoke('val').should('eq', name);
    cy.get('[data-cy=comment]').invoke('val').should('eq', comment);
  });

  it('Step-6: Delete the item aa.AAA ', () => {
    cy.get('[data-cy=delete-item]').click();
    cy.get('#mat-dialog-0').contains('Warnung!');
    cy.get('[data-cy=dialog-confirm-button]').should('exist');
    cy.get('[data-cy=dialog-cancel-button]').should('exist');
    cy.get('[data-cy=dialog-confirm-button]').click();
    cy.contains(headerTitle);
  });
});
