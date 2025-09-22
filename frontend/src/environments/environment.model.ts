import { PassedInitialConfig } from 'angular-auth-oidc-client';

export interface Environment {
  production: boolean;
  label: string;
  appVersion: string;
  atlasApiUrl: string;
  atlasUnauthApiUrl: string;
  authConfig: PassedInitialConfig;
  atlasReleaseNotes: string;
  journeyMapsApiKey: string;
  // feature toggles
  sepodiWorkflowBavActionEnabled: boolean;
  bulkImportEnabled: boolean;
  terminationWorkflowEnabled: boolean;
  sectorsEnabled: boolean;
}
