import { AuthConfig } from 'angular-oauth2-oidc';

export interface Environment {
  production: boolean;
  label: string;
  ttfnBackendUrl: string;
  lidiBackendUrl: string;
  authConfig: AuthConfig;
}
