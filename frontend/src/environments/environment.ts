// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

import { AuthConfig } from 'angular-oauth2-oidc';
import { Environment } from './environment.model';
import packageJson from '../../package.json';

// See https://confluence.sbb.ch/display/CLEW/Azure+AD
const authConfig: AuthConfig = {
  // This is the issuer URL for the SBB Azure AD organization
  issuer: 'https://login.microsoftonline.com/2cda5d11-f0ac-46b3-967d-af1b2e1bd01a/v2.0',
  // This is required, since Azure AD uses different domains in their issuer configuration
  strictDiscoveryDocumentValidation: false,
  clientId: '18746f30-7978-48b5-b19b-0f871fb12e67',
  redirectUri: location.origin,
  responseType: 'code',
  scope: `openid profile email offline_access api://87e6e634-6ba1-4e7a-869d-3348b4c3eafc/.default`,
  preserveRequestedRoute: true,
};

export const environment: Environment = {
  production: false,
  label: 'dev',
  appVersion: packageJson.version,
  atlasApiUrl: 'http://localhost:8888',
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
