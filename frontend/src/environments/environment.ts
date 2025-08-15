// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

// import { AuthConfig } from 'angular-oauth2-oidc';
import { Environment } from './environment.model';
import packageJson from '../../package.json';
import { atlasReleaseNotes } from './environment.common';
import {
  OpenIdConfiguration,
  PassedInitialConfig,
} from 'angular-auth-oidc-client';

// See https://confluence.sbb.ch/display/CLEW/Azure+AD
// const authConfig: AuthConfig = {
//   // This is the issuer URL for the SBB Azure AD organization
//   issuer,
//   // This is required, since Azure AD uses different domains in their issuer configuration
//   strictDiscoveryDocumentValidation: false,
//   clientId: '18746f30-7978-48b5-b19b-0f871fb12e67',
//   redirectUri: location.origin,
//   responseType: 'code',
//   scope: `openid profile email offline_access api://87e6e634-6ba1-4e7a-869d-3348b4c3eafc/.default`,
//   preserveRequestedRoute: true,
//   logoutUrl,
// };

const app: OpenIdConfiguration = {
  configId: 'app',
  authority:
    'https://login.microsoftonline.com/2cda5d11-f0ac-46b3-967d-af1b2e1bd01a/v2.0',
  redirectUrl: location.origin,
  clientId: '18746f30-7978-48b5-b19b-0f871fb12e67',
  // The `openid` scope is required by the library.
  // The `profile` scope is needed by the user menu in the header.
  // `offline_access` is needed to get a refresh token.
  // scope: 'openid profile email offline_access 8675c2fb-a1a4-4ad3-ac73-5e157d9a1744/.default',
  scope:
    'openid profile email offline_access api://87e6e634-6ba1-4e7a-869d-3348b4c3eafc/.default',
  silentRenew: true,
  useRefreshToken: true,
  autoUserInfo: false,
  maxIdTokenIatOffsetAllowedInSeconds: 600,
  ignoreNonceAfterRefresh: true, // see https://github.com/damienbod/angular-auth-oidc-client/issues/1947
  // An array of secure urls to which the token should be appended
  // if a request is made to one of these urls.
  // Docs: https://angular-auth-oidc-client.com/docs/documentation/interceptors
  secureRoutes: ['http://localhost:8888'],
  customParamsAuthRequest: {
    // Enable the following line to show the "Pick an account" dialog
    // prompt: 'select_account',
  },
  postLogoutRedirectUri: 'http://localhost:4200',
};

const authConfig: PassedInitialConfig = {
  // If using multiple configurations, add them after the app configuration.
  config: [app],
};

export const environment: Environment = {
  production: false,
  label: 'dev',
  appVersion: packageJson.version,
  atlasApiUrl: 'http://localhost:8888',
  atlasUnauthApiUrl: 'http://localhost:6969',
  authConfig,
  atlasReleaseNotes,
  journeyMapsApiKey: '6e28a0f7559988d0acf14d450ca29cf9',
  // feature toggles
  sepodiWorkflowBavActionEnabled: true,
  bulkImportEnabled: true,
  terminationWorkflowEnabled: true,
};
