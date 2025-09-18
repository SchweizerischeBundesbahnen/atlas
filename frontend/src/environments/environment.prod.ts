import 'angular-server-side-configuration/process';
import { atlasReleaseNotes, authority } from './environment.common';
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
  authority,
  redirectUrl: location.origin,
  clientId: process.env.API_CLIENT_ID,
  // The `openid` scope is required by the library.
  // The `profile` scope is needed by the user menu in the header.
  // `offline_access` is needed to get a refresh token.
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
  postLogoutRedirectUri: location.origin,
};

const authConfig: PassedInitialConfig = {
  // If using multiple configurations, add them after the app configuration.
  config: [app],
};

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
  sectorsEnabled: process.env.SECTORS_ENABLED !== 'false',
};
