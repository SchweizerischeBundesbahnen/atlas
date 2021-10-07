// Plugins enable you to tap into, modify, or extend the internal behavior of Cypress
// For more info, visit https://on.cypress.io/plugins-api
module.exports = (on, config) => {};

declare namespace Cypress {
  interface Chainable<Subject> {
    /**
     * Login with test user
     * @example
     * cy.login()
     */
    login(): Chainable<any>;
  }
}
