import { Injectable, NgModule } from '@angular/core';
import { Router, RouterModule, Routes, UrlTree } from '@angular/router';
import { Pages } from '../pages';
import { AuthService } from '../../core/auth/auth.service';
import { ApplicationType } from '../../api';
import { PrmOverviewComponent } from './prm-overview/prm-overview.component';

@Injectable()
class CanActivatePrmCreationGuard {
  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
  ) {}

  canActivate(): true | UrlTree {
    if (this.authService.hasPermissionsToCreate(ApplicationType.Prm)) {
      return true;
    }
    return this.router.parseUrl(Pages.PRM.path);
  }
}

const routes: Routes = [
  {
    path: '',
    component: PrmOverviewComponent,
    children: [],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [CanActivatePrmCreationGuard],
})
export class PrmRoutingModule {}
