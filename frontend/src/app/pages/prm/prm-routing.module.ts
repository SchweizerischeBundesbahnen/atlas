import {Injectable, NgModule} from '@angular/core';
import {Router, RouterModule, Routes, UrlTree} from '@angular/router';
import {Pages} from '../pages';
import {AuthService} from '../../core/auth/auth.service';
import {ApplicationType} from '../../api';
import {PrmSearchOverviewComponent} from './prm-overview/prm-search-overview.component';
import {stopPointResolver} from "./stop-point/stop-point.resolver";
import {PrmPanelComponent} from "./prm-panel/prm-panel.component";
import {PrmDetailPanelComponent} from "./prm-detail-panel/prm-detail-panel.component";

@Injectable()
class CanActivatePrmCreationGuard {
  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
  ) {
  }

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
    component: PrmSearchOverviewComponent
  },
  {
    path: ':sloid',
    component: PrmPanelComponent,
    resolve: {stopPoint: stopPointResolver},
    runGuardsAndResolvers: 'always',
    children: [
      {
        path: Pages.PRM_STOP_POINT.path,
        component: PrmDetailPanelComponent,
        runGuardsAndResolvers: 'always'
      },
      {path: '**', redirectTo: Pages.PRM_STOP_POINT.path},
    ]
  },
  {path: '**', redirectTo: Pages.PRM.path},

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [CanActivatePrmCreationGuard],
})
export class PrmRoutingModule {
}
