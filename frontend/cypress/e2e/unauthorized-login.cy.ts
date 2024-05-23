import {DataCy} from "../support/data-cy";

describe('Unauthorized navigation and login', () => {

  it('Step-01: Unauthorized - navigate to ATLAS', () => {
    cy.visit('/');

    // Check that login button is available
    cy.get('#login').should('contain.text', 'Login');

    // Pages for unauthorized user should be 4
    cy.get('.card').should('have.length', 4);
  });

  it('Step-02: Login to ATLAS', () => {
    cy.intercept('GET', '/user-administration/v1/users/current').as('loadPermissions');

    cy.atlasLogin();

    cy.wait('@loadPermissions').then(() => {
      // Login button is not available
      cy.get('#login').should('not.exist');

      // Username is displayed
      cy.get(DataCy.USER_NAME).should('contain.text', 'ATLAS / LIDI / FPFN Admin User');

      // Pages for admin should be 7
      cy.get('.card').should('have.length', 7);
    });

  });

});
