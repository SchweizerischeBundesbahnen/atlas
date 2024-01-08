import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Pages } from '../pages';
import { PrmHomeSearchComponent } from './prm-home-search/prm-home-search.component';
import { PrmPanelComponent } from './prm-panel/prm-panel.component';
import { StopPointDetailComponent } from './tabs/stop-point/detail/stop-point-detail.component';
import { ReferencePointComponent } from './tabs/reference-point/reference-point.component';
import { PlatformTableComponent } from './tabs/platform/platform-table.component';
import { TicketCounterComponent } from './tabs/ticket-counter/ticket-counter.component';
import { InformationDeskComponent } from './tabs/information-desk/information-desk.component';
import { ToiletComponent } from './tabs/toilet/toilet.component';
import { ParkingLotComponent } from './tabs/parking-lot/parking-lot.component';
import { ConnectionComponent } from './tabs/connection/connection.component';
import { canLeaveDirtyForm } from '../../core/leave-guard/leave-dirty-form-guard.service';
import { stopPointResolver } from './prm-panel/resolvers/stop-point.resolver';
import { PrmTab } from './prm-panel/prm-tab';
import { prmPanelResolver } from './prm-panel/resolvers/prm-panel-resolver.service';
import { PlatformComponent } from './platform/platform.component';
import { platformResolver } from './platform/platform.resolver';
import { trafficPointElementResolver } from './platform/traffic-point-element.resolver';

const routes: Routes = [
  {
    path: '',
    component: PrmHomeSearchComponent,
  },
  {
    path: Pages.STOP_POINTS.path + '/:stopPointSloid/' + Pages.PLATFORMS.path + '/:platformSloid',
    component: PlatformComponent,
    runGuardsAndResolvers: 'always',
    resolve: {
      platform: platformResolver,
      servicePoint: prmPanelResolver,
      trafficPoint: trafficPointElementResolver,
    },
  },
  {
    path: Pages.STOP_POINTS.path + '/:stopPointSloid',
    component: PrmPanelComponent,
    resolve: { stopPoints: stopPointResolver, servicePoints: prmPanelResolver },
    runGuardsAndResolvers: 'always',
    children: [
      {
        path: Pages.PRM_STOP_POINT_TAB.path,
        component: StopPointDetailComponent,
        runGuardsAndResolvers: 'always',
        canDeactivate: [canLeaveDirtyForm],
      },
      {
        path: PrmTab.REFERENCE_POINT.link,
        component: ReferencePointComponent,
        runGuardsAndResolvers: 'always',
      },
      {
        path: PrmTab.PLATFORM.link,
        component: PlatformTableComponent,
        runGuardsAndResolvers: 'always',
      },
      {
        path: PrmTab.TICKET_COUNTER.link,
        component: TicketCounterComponent,
        runGuardsAndResolvers: 'always',
      },
      {
        path: PrmTab.INFORMATION_DESK.link,
        component: InformationDeskComponent,
        runGuardsAndResolvers: 'always',
      },
      {
        path: PrmTab.TOILET.link,
        component: ToiletComponent,
        runGuardsAndResolvers: 'always',
      },
      {
        path: PrmTab.PARKING_LOT.link,
        component: ParkingLotComponent,
        runGuardsAndResolvers: 'always',
      },
      {
        path: PrmTab.CONNECTION.link,
        component: ConnectionComponent,
        runGuardsAndResolvers: 'always',
      },
      { path: '**', redirectTo: Pages.PRM_STOP_POINT_TAB.path },
    ],
  },
  { path: '**', redirectTo: Pages.PRM.path },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PrmRoutingModule {}
