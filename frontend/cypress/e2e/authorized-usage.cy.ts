import {DataCy} from "../support/data-cy";

describe('Authorized usage', () => {

  it('Step-01: Check home', () => {
    cy.intercept('GET', '/user-administration/v1/users/current').as('loadPermissions');
    cy.atlasLogin();
    cy.wait('@loadPermissions').then(() => {
      // Login button is not available
      cy.get('#login').should('not.exist');

      // Username is displayed
      cy.get(DataCy.USER_NAME).should('contain.text', 'ATLAS / LIDI / FPFN Admin User');

      // Pages for admin should be 8
      cy.get('.card').should('have.length', 8);
    });
  });

});
