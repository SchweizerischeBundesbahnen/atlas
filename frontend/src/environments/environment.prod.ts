import { AuthConfig } from 'angular-oauth2-oidc';
import 'angular-server-side-configuration/process';
import { atlasReleaseNotes, issuer, logoutUrl } from './environment.common';
import { Environment } from './environment.model';

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

// See https://confluence.sbb.ch/display/CLEW/Azure+AD
const authConfig: AuthConfig = {
  // This is the issuer URL for the SBB Azure AD organization
  issuer,
  // This is required, since Azure AD uses different domains in their issuer configuration
  strictDiscoveryDocumentValidation: false,
  clientId: process.env.API_CLIENT_ID!,
  redirectUri: location.origin,
  responseType: 'code',
  scope: process.env.API_SCOPE!,
  preserveRequestedRoute: true,
  logoutUrl,
};

export const environment: Environment = {
  production: process.env.PRODUCTION !== 'false',
  sepodiWorkflowBavActionEnabled:
    process.env.SEPODI_WORKFLOW_BAV_ACTION_ENABLED !== 'false',
  bulkImportEnabled: process.env.BULK_IMPORT_ENABLED !== 'false',
  terminationWorkflowEnabled:
    process.env.TERMINTAION_WORKFLOW_ENABLED !== 'false',
  label: process.env.ENVIRONMENT_LABEL!,
  appVersion: process.env.APP_VERSION!,
  atlasApiUrl: process.env.ATLAS_API_URL!,
  atlasUnauthApiUrl: process.env.ATLAS_UNAUTH_API_URL!,
  atlasReleaseNotes,
  authConfig,
  journeyMapsApiKey: process.env.JOURNEY_MAPS_API_KEY!,
};
