import CommonUtils from '../support/util/common-utils';

describe('Test the navigation of the homepage links', () => {


  it('Step-01: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  // Navigation via Start-Page and ATLAS-logo

  it('Step-02: Move to /timetable-field-number via Start-page (ATLAS-517)', () => {
    cy.get('#timetable-field-number').click();
    cy.url().should('contain', '/timetable-field-number');
  });

  it('Step-03: Move back to / (the homepage) via atlas-logo (ATLAS-516)', () => {
    CommonUtils.navigateToHome();
    cy.contains('Die SKI Business Plattform');
  });

  it('Step-04: Move to /line-directory via Start-page (ATLAS-517)', () => {
    cy.get('#line-directory').click();
    cy.url().should('contain', '/line-directory');
  });

  it('Step-05: Move back to / (the homepage) via atlas-logo (ATLAS-516)', () => {
    CommonUtils.navigateToHome();
    cy.contains('Die SKI Business Plattform');
  });

  // Navigation via Side-Menu

  it('Step-06: Move to /timetable-field-number via Sidemenu (ATLAS-516)', () => {
    CommonUtils.navigateToTtfnViaSidemenu();
  });

  it('Step-07: Move back to / (the homepage) via Sidemenu (ATLAS-516)', () => {
    CommonUtils.navigateToHomepageViaSidemenu();
  });

  it('Step-08: Move to /line-directory via Sidemenu (ATLAS-516)', () => {
    CommonUtils.navigateToLidiViaSidemenu();
  });

});
