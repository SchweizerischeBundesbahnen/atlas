import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Pages } from '../pages';
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
import { canLeaveDirtyForm } from '../../core/leave-guard/leave-dirty-form-guard.service';
import { stopPointResolver } from './stop-point/stop-point.resolver';

const routes: Routes = [
  {
    path: '',
    component: PrmHomeSearchComponent,
  },
  {
    path: Pages.STOP_POINTS.path + '/:sloid',
    component: PrmPanelComponent,
    resolve: { stopPoints: stopPointResolver, servicePoints: prmOverviewResolver },
    runGuardsAndResolvers: 'always',
    children: [
      {
        path: Pages.PRM_STOP_POINT.path,
        component: StopPointDetailComponent,
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
})
export class PrmRoutingModule {}
