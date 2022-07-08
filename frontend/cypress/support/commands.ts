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
import CommonUtils from './util/common-utils';

declare global {
  // eslint-disable-next-line @typescript-eslint/no-namespace
  namespace Cypress {
    interface Chainable {
      /**
       * Login with test user
       * @example
       * cy.login()
       */
      login: () => void;

      atlasLogin: () => void;
    }
  }
}

Cypress.Commands.add('atlasLogin', () => {
  cy.clearCookies();
  cy.clearLocalStorage();
  CommonUtils.unregisterServiceWorker();
  cy.login();
  cy.visit('/');
});

Cypress.Commands.add('login', () => {
  cy.request({
    method: 'POST',
    url: `https://login.microsoftonline.com/2cda5d11-f0ac-46b3-967d-af1b2e1bd01a/oauth2/v2.0/token`,
    form: true,
    body: {
      grant_type: 'password',
      client_id: Cypress.env('CLIENT_ID'),
      client_secret: Cypress.env('CLIENT_SECRET'),
      username: Cypress.env('USERNAME'),
      password: Cypress.env('PASSWORD'),
      scope: Cypress.env('SCOPE'),
    },
  }).then((response) => {
    expect(response).property('status').to.equal(200);
    expect(response.body).property('access_token').to.not.be.oneOf([null, '']);
    const body = response.body;
    const now = new Date().getTime();
    const expiresAt = JSON.stringify(body.expires_in * 1000 + now);

    window.sessionStorage.removeItem('refresh_token');
    window.sessionStorage.removeItem('none');
    window.sessionStorage.setItem('id_token_expires_at', expiresAt);
    window.sessionStorage.setItem('expires_at', expiresAt);
    window.sessionStorage.setItem('id_token', body.access_token);
    window.sessionStorage.setItem('access_token', body.access_token);
    window.sessionStorage.setItem(
      'id_token_claims_obj',
      JSON.stringify({
        exp: expiresAt,
        iat: now,
        auth_time: now,
        jti: '48a3f9da-67d6-456f-8427-a041eac454a7',
        iss: 'https://login.microsoftonline.com/2cda5d11-f0ac-46b3-967d-af1b2e1bd01a/v2.0',
        aud: 'client-tms-ssp-prod',
        sub: '13e2b9c1-8521-4561-8ce7-4b7e54333d62',
        typ: 'ID',
        azp: 'client-tms-ssp-prod',
        acr: '1',
        upn: Cypress.env('CLIENT_ID') + '@sbb.ch',
        email_verified: true,
        sbbuid_ad: 'ue0000000',
        name: 'Atlas User',
        preferred_username: 'nt-sbb1\\ue01234',
        given_name: 'Test',
        sbbuid: 'ue0000000',
        family_name: Cypress.env('CLIENT_ID'),
        email: Cypress.env('CLIENT_ID') + '@sbb.ch',
      })
    );
  });
});
