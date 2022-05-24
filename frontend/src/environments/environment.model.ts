import { AuthConfig } from 'angular-oauth2-oidc';

export interface Environment {
  production: boolean;
  pageBodiEnabled: boolean;
  label: string;
  appVersion: string;
  atlasApiUrl: string;
  authConfig: AuthConfig;
}
