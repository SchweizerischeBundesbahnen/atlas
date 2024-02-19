import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {Pages} from '../pages';
import {PrmHomeSearchComponent} from './prm-home-search/prm-home-search.component';
import {PrmPanelComponent} from './prm-panel/prm-panel.component';
import {StopPointDetailComponent} from './tabs/stop-point/detail/stop-point-detail.component';
import {ReferencePointTableComponent} from './tabs/reference-point/reference-point-table.component';
import {PlatformTableComponent} from './tabs/platform/platform-table.component';
import {ToiletComponent} from './tabs/toilet/toilet.component';
import {ConnectionComponent} from './tabs/connection/connection.component';
import {canLeaveDirtyForm} from '../../core/leave-guard/leave-dirty-form-guard.service';
import {stopPointResolver} from './prm-panel/resolvers/stop-point.resolver';
import {PrmTabs} from './prm-panel/prm-tabs';
import {prmPanelResolver} from './prm-panel/resolvers/prm-panel-resolver.service';
import {platformResolver} from './tabs/platform/detail/resolvers/platform.resolver';
import {trafficPointElementResolver} from './tabs/platform/detail/resolvers/traffic-point-element.resolver';
import {PlatformDetailComponent} from './tabs/platform/detail/platform-detail.component';
import {ReferencePointDetailComponent} from './tabs/reference-point/detail/reference-point-detail.component';
import {referencePointResolver} from './tabs/reference-point/detail/resolvers/reference-point.resolver';
import {ParkingLotDetailComponent} from "./tabs/parking-lot/detail/parking-lot-detail.component";
import {parkingLotResolver} from "./tabs/parking-lot/detail/resolvers/parking-lot.resolver";
import {ParkingLotTableComponent} from "./tabs/parking-lot/parking-lot-table.component";
import {ContactPointTableComponent} from "./tabs/contact-point/contact-point-table.component";
import {ContactPointDetailComponent} from "./tabs/contact-point/detail/contact-point-detail.component";
import {contactPointResolver} from "./tabs/contact-point/detail/resolvers/contact-point.resolver";

const routes: Routes = [
  {
    path: '',
    component: PrmHomeSearchComponent,
  },
  {
    path: Pages.STOP_POINTS.path + '/:stopPointSloid/' + Pages.PLATFORMS.path + '/:platformSloid',
    component: PlatformDetailComponent,
    runGuardsAndResolvers: 'always',
    canDeactivate: [canLeaveDirtyForm],
    resolve: {
      platform: platformResolver,
      servicePoint: prmPanelResolver,
      trafficPoint: trafficPointElementResolver,
      stopPoint: stopPointResolver,
    },
  },
  {
    path: Pages.STOP_POINTS.path + '/:stopPointSloid/' + Pages.REFERENCE_POINT.path + '/:sloid',
    component: ReferencePointDetailComponent,
    runGuardsAndResolvers: 'always',
    canDeactivate: [canLeaveDirtyForm],
    resolve: {
      referencePoint: referencePointResolver,
      servicePoint: prmPanelResolver,
    },
  },
  {
    path: Pages.STOP_POINTS.path + '/:stopPointSloid/' + Pages.PARKING_LOT.path + '/:sloid',
    component: ParkingLotDetailComponent,
    runGuardsAndResolvers: 'always',
    canDeactivate: [canLeaveDirtyForm],
    resolve: {
      parkingLot: parkingLotResolver,
      servicePoint: prmPanelResolver,
    },
  },
  {
    path: Pages.STOP_POINTS.path + '/:stopPointSloid/' + Pages.CONTACT_POINT.path + '/:sloid',
    component: ContactPointDetailComponent,
    runGuardsAndResolvers: 'always',
    canDeactivate: [canLeaveDirtyForm],
    resolve: {
      contactPoint: contactPointResolver,
      servicePoint: prmPanelResolver,
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
        path: PrmTabs.REFERENCE_POINT.link,
        component: ReferencePointTableComponent,
        runGuardsAndResolvers: 'always',
      },
      {
        path: PrmTabs.PLATFORM.link,
        component: PlatformTableComponent,
        runGuardsAndResolvers: 'always',
      },
      {
        path: PrmTabs.CONTACT_POINT.link,
        component: ContactPointTableComponent,
        runGuardsAndResolvers: 'always',
      },
      {
        path: PrmTabs.TOILET.link,
        component: ToiletComponent,
        runGuardsAndResolvers: 'always',
      },
      {
        path: PrmTabs.PARKING_LOT.link,
        component: ParkingLotTableComponent,
        runGuardsAndResolvers: 'always',
      },
      {
        path: PrmTabs.CONNECTION.link,
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
