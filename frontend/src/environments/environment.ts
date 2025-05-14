// This file can be replaced during build by using the `fileReplacements` array.
// `ng build` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

import { AuthConfig } from 'angular-oauth2-oidc';
import { Environment } from './environment.model';
import packageJson from '../../package.json';
import { issuer, logoutUrl } from './environment.prod';

// See https://confluence.sbb.ch/display/CLEW/Azure+AD
const authConfig: AuthConfig = {
  // This is the issuer URL for the SBB Azure AD organization
  issuer,
  // This is required, since Azure AD uses different domains in their issuer configuration
  strictDiscoveryDocumentValidation: false,
  clientId: '18746f30-7978-48b5-b19b-0f871fb12e67',
  redirectUri: location.origin,
  responseType: 'code',
  scope: `openid profile email offline_access api://87e6e634-6ba1-4e7a-869d-3348b4c3eafc/.default`,
  preserveRequestedRoute: true,
  logoutUrl,
};

export const environment: Environment = {
  production: false,
  sepodiWorkflowBavActionEnabled: true,
  bulkImportEnabled: true,
  terminationWorkflowEnabled: true,
  label: 'dev',
  appVersion: packageJson.version,
  atlasApiUrl: 'http://localhost:8888',
  atlasUnauthApiUrl: 'http://localhost:6969',
  atlasReleaseNotes:
    'https://atlas-info.app.sbb.ch/static/atlas-release-notes.html',
  authConfig,
  journeyMapsApiKey: '6e28a0f7559988d0acf14d450ca29cf9',
};
