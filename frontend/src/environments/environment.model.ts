import { AuthConfig } from 'angular-oauth2-oidc';

export interface Environment {
  production: boolean;
  workflowEnabled: boolean;
  pageSepodiEnabled: boolean;
  label: string;
  appVersion: string;
  atlasApiUrl: string;
  authConfig: AuthConfig;
}
