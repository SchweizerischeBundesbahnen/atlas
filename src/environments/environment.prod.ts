import { AuthConfig } from 'angular-oauth2-oidc';
import 'angular-server-side-configuration/process';

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
  issuer: 'https://login.microsoftonline.com/2cda5d11-f0ac-46b3-967d-af1b2e1bd01a/v2.0',
  // This is required, since Azure AD uses different domains in their issuer configuration
  strictDiscoveryDocumentValidation: false,
  // TODO: Replace this with your own clientId
  clientId: '8184c96f-07a5-4ae1-b4a9-04a0e83cb578',
  // TODO: If you do not need multiple languages, replace the below code with the following line:
  // redirectUri: location.origin
  // Note that these URIs must also be added to allowed redirect URIs in azure-app-registration.yml
  // (e.g. https://your-domain/en/, https://your-domain/de/, ...)
  redirectUri:
    location.origin + location.pathname.substring(0, location.pathname.indexOf('/', 1) + 1),
  responseType: 'code',
  // TODO: Replace uuid with your own clientId or service id
  scope: `openid profile email offline_access 8184c96f-07a5-4ae1-b4a9-04a0e83cb578/.default`,
};

export const environment: Environment = {
  production: process.env.PRODUCTION !== 'false',
  label: process.env.ENVIRONMENT_LABEL!,
  backendUrl: 'https://timetable-field-number-backend-dev.apps.aws01t.sbb-aws-test.net',
  authConfig,
};
