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
  console.log('logging in');

  return cy
    .visit(`${Cypress.env('Host')}`)
    .request({
      method: 'POST',
      url: `https://login.microsoftonline.com/${Cypress.env('tenantId')}/oauth2/token`,
      form: true,
      body: {
        grant_type: 'client_credentials',
      },
      auth: {
        user: Cypress.env('clientId'),
        pass: Cypress.env('clientSecret'),
        sendImmediately: true,
      },
    })
    .then((response) => {
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
          iss: 'https://sso.sbb.ch/auth/realms/SBB_Public',
          aud: 'client-tms-ssp-prod',
          sub: '13e2b9c1-8521-4561-8ce7-4b7e54333d62',
          typ: 'ID',
          azp: 'client-tms-ssp-prod',
          acr: '1',
          upn: Cypress.env('clientId') + '@sbb.ch',
          email_verified: true,
          sbbuid_ad: 'ue0000000',
          name: 'Test User ',
          preferred_username: 'nt-sbb1\\ue85540',
          given_name: 'Test',
          sbbuid: 'ue0000000',
          family_name: Cypress.env('clientId'),
          email: Cypress.env('clientId') + '@sbb.ch',
        })
      );
    });
});
