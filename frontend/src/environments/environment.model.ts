import { AuthConfig } from 'angular-oauth2-oidc';

export interface Environment {
  production: boolean;
  sepodiWorkflowBavActionEnabled: boolean;
  bulkImportEnabled: boolean;
  label: string;
  appVersion: string;
  atlasApiUrl: string;
  atlasUnauthApiUrl: string;
  authConfig: AuthConfig;
  atlasReleaseNotes: string;
  journeyMapsApiKey: string;
}
