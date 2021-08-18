// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

import { AuthConfig } from 'angular-oauth2-oidc';
import 'angular-server-side-configuration/process';
import { Environment } from './environment.model';

// See https://confluence.sbb.ch/display/CLEW/Azure+AD
const authConfig: AuthConfig = {
  // This is the issuer URL for the SBB Azure AD organization
  issuer: 'https://login.microsoftonline.com/2cda5d11-f0ac-46b3-967d-af1b2e1bd01a/v2.0',
  // This is required, since Azure AD uses different domains in their issuer configuration
  strictDiscoveryDocumentValidation: false,
  clientId: '8184c96f-07a5-4ae1-b4a9-04a0e83cb578',
  redirectUri: location.origin,
  responseType: 'code',
  // TODO: Replace uuid with your own clientId or service id
  scope: `openid profile email offline_access 8184c96f-07a5-4ae1-b4a9-04a0e83cb578/.default`,
};

export const environment: Environment = {
  production: false,
  label: 'dev',
  // TODO: Replace url with your backend url
  backendUrl: process.env.BACKEND_URL!,
  authConfig,
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
