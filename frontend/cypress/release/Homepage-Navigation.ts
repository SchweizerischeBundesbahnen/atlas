describe('Test the navigation of the homepage links', () => {


  it('Step-01: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-02: Move to /timetable-field-number via Start-page (ATLAS-517)', () => {
    cy.get('#timetable-field-number').click();
    cy.url().should('contain', '/timetable-field-number');
  });

  it('Step-03: Move back to / (the homepage) via atlas-logo (ATLAS-516)', () => {
    cy.get('[data-cy="atlas-logo-home-link"]').click();
    cy.contains('Die SKI Business Plattform');
  });

  it('Step-04: Move to /line-directory via Start-page (ATLAS-517)', () => {
    cy.get('#line-directory').click();
    cy.url().should('contain', '/line-directory');
  });

  it('Step-05: Move back to / (the homepage) via atlas-logo (ATLAS-516)', () => {
    cy.get('[data-cy="atlas-logo-home-link"]').click();
    cy.contains('Die SKI Business Plattform');
  });

  it('Step-06: Move to /timetable-field-number via Menu (ATLAS-516)', () => {
    cy.get('.sidenav-menu-btn').click();
    cy.get(':nth-child(2) > .mat-list-item > .mat-list-item-content').click();
    cy.url().should('contain', '/timetable-field-number');
  });

  it('Step-07: Move back to / (the homepage) via Menu (ATLAS-516)', () => {
    cy.get('.sidenav-menu-btn').click();
    cy.get(':nth-child(1) > .mat-list-item > .mat-list-item-content').click();
    cy.url().should('contain', '/timetable-field-number');
  });


  // ATLAS-516 - Header & Seitenmenu
  // ATLAS-517 - Design Atlas: Startseite

  });
