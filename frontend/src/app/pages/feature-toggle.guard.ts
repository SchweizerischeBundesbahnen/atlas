import { CanActivateFn } from '@angular/router';
import { environment } from '../../environments/environment';

export const featureToggleGuard: CanActivateFn = () => {
  return environment.terminationWorkflowEnabled;
};
