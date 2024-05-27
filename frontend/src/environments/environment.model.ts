import { AuthConfig } from 'angular-oauth2-oidc';

export interface Environment {
  production: boolean;
  sepodiWorkflowEnabled: boolean;
  label: string;
  appVersion: string;
  atlasApiUrl: string;
  authConfig: AuthConfig;
  atlasReleaseNotes: string;
}
