import { Injectable, NgModule } from '@angular/core';
import { Router, RouterModule, Routes, UrlTree } from '@angular/router';
import { Pages } from '../pages';
import { AuthService } from '../../core/auth/auth.service';
import { ApplicationType } from '../../api';
import { PrmHomeSearchComponent } from './prm-home-search/prm-home-search.component';
import { PrmPanelComponent } from './prm-panel/prm-panel.component';
import { StopPointDetailComponent } from './stop-point/stop-point-detail.component';
import { ReferencePointComponent } from './reference-point/reference-point.component';
import { PlatformComponent } from './platform/platform.component';
import { TicketCounterComponent } from './ticket-counter/ticket-counter.component';
import { InformationDeskComponent } from './information-desk/information-desk.component';
import { ToiletteComponent } from './toilette/toilette.component';
import { ParkingLotComponent } from './parking-lot/parking-lot.component';
import { ConnectionComponent } from './connection/connection.component';
import { prmOverviewResolver } from './prm-panel/prm-overview-resolver.service';
import { stopPointResolver } from './stop-point/stop-point.resolver';
import { canLeaveDirtyForm } from '../../core/leave-guard/leave-dirty-form-guard.service';

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
    component: PrmHomeSearchComponent,
  },
  {
    path: Pages.STOP_POINTS.path + '/:sloid',
    component: PrmPanelComponent,
    resolve: { servicePoints: prmOverviewResolver },
    runGuardsAndResolvers: 'always',
    children: [
      {
        path: Pages.PRM_STOP_POINT.path,
        component: StopPointDetailComponent,
        resolve: { stopPoint: stopPointResolver },
        runGuardsAndResolvers: 'always',
        canDeactivate: [canLeaveDirtyForm],
      },
      {
        path: 'reference-point',
        component: ReferencePointComponent,
        runGuardsAndResolvers: 'always',
      },
      {
        path: 'platform',
        component: PlatformComponent,
        runGuardsAndResolvers: 'always',
      },
      {
        path: 'ticket-counter',
        component: TicketCounterComponent,
        runGuardsAndResolvers: 'always',
      },
      {
        path: 'information-desk',
        component: InformationDeskComponent,
        runGuardsAndResolvers: 'always',
      },
      {
        path: 'toilette',
        component: ToiletteComponent,
        runGuardsAndResolvers: 'always',
      },
      {
        path: 'parking-lot',
        component: ParkingLotComponent,
        runGuardsAndResolvers: 'always',
      },
      {
        path: 'connection',
        component: ConnectionComponent,
        runGuardsAndResolvers: 'always',
      },
      { path: '**', redirectTo: Pages.PRM_STOP_POINT.path },
    ],
  },
  { path: '**', redirectTo: Pages.PRM.path },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [CanActivatePrmCreationGuard],
})
export class PrmRoutingModule {}
