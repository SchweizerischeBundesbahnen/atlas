// ***********************************************
// This example namespace declaration will help
// with Intellisense and code completion in your
// IDE or Text Editor.
// ***********************************************
// declare namespace Cypress {
//   interface Chainable<Subject = any> {
//     customCommand(param: any): typeof customCommand;
//   }
// }
//
// function customCommand(param: any): void {
//   console.warn(param);
// }
//
// NOTE: You can use it like so:
// Cypress.Commands.add('customCommand', customCommand);
//
// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

Cypress.Commands.add('loginClientCredentials', () => {
  console.log('alternative login');
  return cy
    .visit(`${Cypress.env('Host')}/`)
    .request({
      url: Cypress.env('authority') + '/oauth2/v2.0/token',
      method: 'POST',
      body: {
        grant_type: 'password',
        client_id: Cypress.env('clientId'),
        client_secret: Cypress.env('clientSecret'),
        scope: Cypress.env('apiScopes')[0],
        username: Cypress.env('username'),
        password: Cypress.env('password'),
      },
      form: true,
    })
    .then((response) => {
      console.log(response);
    });
});

Cypress.Commands.add('loginWithCredentials', () => {
  Cypress.log({
    name: 'loginViaAuth0',
  });

  const options = {
    method: 'POST',
    url: `https://login.microsoftonline.com/2cda5d11-f0ac-46b3-967d-af1b2e1bd01a/oauth2/token`,
    body: {
      grant_type: 'password',
      username: 'fxatl_r@sbb.ch',
      password: 'ATLAS%r3ader',
      resource: 'http://localhost:4200',
      scope: Cypress.env('apiScopes')[0],
      client_id: Cypress.env('clientId'),
      client_secret: Cypress.env('clientSecret'),
    },
    form: true,
  };
  cy.request(options);
});

Cypress.Commands.add('login', () => {
  return cy
    .visit(`${Cypress.env('Host')}`)
    .request({
      method: 'POST',
      url: `https://login.microsoftonline.com/${Cypress.env('tenantId')}/oauth2/token`,
      form: true,
      body: {
        grant_type: 'client_credentials',
        client_id: Cypress.env('clientId'),
        client_secret: Cypress.env('clientSecret'),
      },
    })
    .then((response) => {
      console.log(response);
      const accessToken = response.body.access_token;
      const expiresOn = response.body.expires_on;
      sessionStorage.setItem('access_token', accessToken);
      sessionStorage.setItem('auth_token', accessToken);
      sessionStorage.setItem('expires_at', expiresOn);
      sessionStorage.setItem('access_token_stored_at', expiresOn);
    });
});
