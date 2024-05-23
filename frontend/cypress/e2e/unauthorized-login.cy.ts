import CommonUtils from '../support/util/common-utils';

describe('Unauthorized navigation and login', () => {

  it('Step-01: Unauthorized - navigate to ATLAS', () => {
    cy.visit('/');

    // Check that login button is available
    cy.get('#login').should('contain.text', 'Login');

    // Pages for unauthorized user should be 4
    cy.get('.card').should('have.length', 4);
  });

  it('Step-02: Login to ATLAS', () => {
    cy.atlasLogin();
    cy.visit('/');

    // Login button is not available
    cy.get('#login').should('not.exist');

    // Pages for admin should be 7
    cy.get('.card').should('have.length', 7);
  });

});
