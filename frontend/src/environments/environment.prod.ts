// import { AuthConfig } from 'angular-oauth2-oidc';
import 'angular-server-side-configuration/process';
import { atlasReleaseNotes } from './environment.common';
import { Environment } from './environment.model';
import {
  OpenIdConfiguration,
  PassedInitialConfig,
} from 'angular-auth-oidc-client';

/**
 * How to use angular-server-side-configuration:
 *
 * Use process.env.NAME_OF_YOUR_ENVIRONMENT_VARIABLE
 *
 * export const environment = {
 *   stringValue: process.env.STRING_VALUE,
 *   stringValueWithDefault: process.env.STRING_VALUE || 'defaultValue',
 *   numberValue: Number(process.env.NUMBER_VALUE),
 *   numberValueWithDefault: Number(process.env.NUMBER_VALUE || 10),
 *   booleanValue: Boolean(process.env.BOOLEAN_VALUE),
 *   booleanValueInverted: process.env.BOOLEAN_VALUE_INVERTED !== 'false',
 * };
 */

const app: OpenIdConfiguration = {
  configId: 'app',
  authority:
    'https://login.microsoftonline.com/2cda5d11-f0ac-46b3-967d-af1b2e1bd01a/v2.0',
  redirectUrl: location.origin,
  clientId: process.env.API_CLIENT_ID,
  // The `openid` scope is required by the library.
  // The `profile` scope is needed by the user menu in the header.
  // `offline_access` is needed to get a refresh token.
  // scope: 'openid profile email offline_access 8675c2fb-a1a4-4ad3-ac73-5e157d9a1744/.default',
  scope: process.env.API_SCOPE,
  silentRenew: true,
  useRefreshToken: true,
  autoUserInfo: false,
  maxIdTokenIatOffsetAllowedInSeconds: 600,
  ignoreNonceAfterRefresh: true, // see https://github.com/damienbod/angular-auth-oidc-client/issues/1947
  // An array of secure urls to which the token should be appended
  // if a request is made to one of these urls.
  // Docs: https://angular-auth-oidc-client.com/docs/documentation/interceptors
  secureRoutes: [process.env.ATLAS_API_URL!],
  customParamsAuthRequest: {
    // Enable the following line to show the "Pick an account" dialog
    // prompt: 'select_account',
  },
};

const authConfig: PassedInitialConfig = {
  // If using multiple configurations, add them after the app configuration.
  config: [app],
};

// See https://confluence.sbb.ch/display/CLEW/Azure+AD
// const authConfig: AuthConfig = {
//   // This is the issuer URL for the SBB Azure AD organization
//   issuer,
//   // This is required, since Azure AD uses different domains in their issuer configuration
//   strictDiscoveryDocumentValidation: false,
//   clientId: process.env.API_CLIENT_ID!,
//   redirectUri: location.origin,
//   responseType: 'code',
//   scope: process.env.API_SCOPE!,
//   preserveRequestedRoute: true,
//   logoutUrl,
// };

export const environment: Environment = {
  production: process.env.PRODUCTION !== 'false',
  label: process.env.ENVIRONMENT_LABEL!,
  appVersion: process.env.APP_VERSION!,
  atlasApiUrl: process.env.ATLAS_API_URL!,
  atlasUnauthApiUrl: process.env.ATLAS_UNAUTH_API_URL!,
  authConfig,
  atlasReleaseNotes,
  journeyMapsApiKey: process.env.JOURNEY_MAPS_API_KEY!,
  // feature toggles
  sepodiWorkflowBavActionEnabled:
    process.env.SEPODI_WORKFLOW_BAV_ACTION_ENABLED !== 'false',
  bulkImportEnabled: process.env.BULK_IMPORT_ENABLED !== 'false',
  terminationWorkflowEnabled:
    process.env.TERMINTAION_WORKFLOW_ENABLED !== 'false',
};
