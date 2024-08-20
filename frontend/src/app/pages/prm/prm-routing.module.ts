import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {Pages} from '../pages';
import {PrmHomeSearchComponent} from './prm-home-search/prm-home-search.component';
import {PrmPanelComponent} from './prm-panel/prm-panel.component';
import {StopPointDetailComponent} from './tabs/stop-point/detail/stop-point-detail.component';
import {ReferencePointTableComponent} from './tabs/reference-point/reference-point-table.component';
import {PlatformTableComponent} from './tabs/platform/platform-table.component';
import {ToiletComponent} from './tabs/toilet/toilet.component';
import {canLeaveDirtyForm} from '../../core/leave-guard/leave-dirty-form-guard.service';
import {stopPointResolver} from './prm-panel/resolvers/stop-point.resolver';
import {PrmTabs} from './prm-panel/prm-tabs';
import {prmPanelResolver} from './prm-panel/resolvers/prm-panel-resolver.service';
import {platformResolver} from './tabs/platform/detail/resolvers/platform.resolver';
import {trafficPointElementResolver} from './tabs/platform/detail/resolvers/traffic-point-element.resolver';
import {PlatformDetailPanelComponent} from './tabs/platform/detail/platform-detail-panel.component';
import {ReferencePointDetailComponent} from './tabs/reference-point/detail/reference-point-detail.component';
import {referencePointResolver} from './tabs/reference-point/detail/resolvers/reference-point.resolver';
import {ParkingLotDetailPanelComponent} from './tabs/parking-lot/detail/parking-lot-detail-panel.component';
import {parkingLotResolver} from './tabs/parking-lot/detail/resolvers/parking-lot.resolver';
import {ParkingLotTableComponent} from './tabs/parking-lot/parking-lot-table.component';
import {ContactPointTableComponent} from './tabs/contact-point/contact-point-table.component';
import {ContactPointDetailPanelComponent} from './tabs/contact-point/detail/contact-point-detail-panel.component';
import {contactPointResolver} from './tabs/contact-point/detail/resolvers/contact-point.resolver';
import {ToiletDetailPanelComponent} from './tabs/toilet/detail/toilet-detail-panel.component';
import {toiletResolver} from './tabs/toilet/detail/resolvers/toilet.resolver';
import {RelationTabDetailComponent} from './tabs/relation/tab-detail/relation-tab-detail.component';
import {PlatformDetailComponent} from './tabs/platform/detail/detail/platform-detail.component';
import {PRM_DETAIL_TAB_LINK, PRM_RELATIONS_TAB_LINK,} from './tabs/relation/tab/detail-with-relation-tab.component';
import {ParkingLotDetailComponent} from "./tabs/parking-lot/detail/detail/parking-lot-detail.component";
import {ContactPointDetailComponent} from "./tabs/contact-point/detail/detail/contact-point-detail.component";
import {ToiletDetailComponent} from "./tabs/toilet/detail/detail/toilet-detail.component";

const routes: Routes = [
  {
    path: '',
    component: PrmHomeSearchComponent,
    children: [
      {
        path: Pages.STOP_POINTS.path + '/:stopPointSloid/' + Pages.PLATFORMS.path + '/:sloid',
        component: PlatformDetailPanelComponent,
        runGuardsAndResolvers: 'always',
        canDeactivate: [canLeaveDirtyForm],
        resolve: {
          platform: platformResolver,
          servicePoint: prmPanelResolver,
          trafficPoint: trafficPointElementResolver,
          stopPoint: stopPointResolver,
        },
        children: [
          {
            path: PRM_DETAIL_TAB_LINK,
            component: PlatformDetailComponent,
            runGuardsAndResolvers: 'always',
            canDeactivate: [canLeaveDirtyForm],
          },
          {
            path: PRM_RELATIONS_TAB_LINK,
            component: RelationTabDetailComponent,
            runGuardsAndResolvers: 'always',
            canDeactivate: [canLeaveDirtyForm],
          },
          {path: '**', redirectTo: PRM_DETAIL_TAB_LINK},
        ],
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
        component: ParkingLotDetailPanelComponent,
        runGuardsAndResolvers: 'always',
        resolve: {
          parkingLot: parkingLotResolver,
          servicePoint: prmPanelResolver,
          stopPoint: stopPointResolver,
        },
        children: [
          {
            path: PRM_DETAIL_TAB_LINK,
            component: ParkingLotDetailComponent,
            runGuardsAndResolvers: 'always',
            canDeactivate: [canLeaveDirtyForm],
          },
          {
            path: PRM_RELATIONS_TAB_LINK,
            component: RelationTabDetailComponent,
            runGuardsAndResolvers: 'always',
            canDeactivate: [canLeaveDirtyForm],
          },
          {path: '**', redirectTo: PRM_DETAIL_TAB_LINK},
        ],
      },
      {
        path: Pages.STOP_POINTS.path + '/:stopPointSloid/' + Pages.CONTACT_POINT.path + '/:sloid',
        component: ContactPointDetailPanelComponent,
        runGuardsAndResolvers: 'always',
        resolve: {
          contactPoint: contactPointResolver,
          servicePoint: prmPanelResolver,
          stopPoint: stopPointResolver,
        },
        children: [
          {
            path: PRM_DETAIL_TAB_LINK,
            component: ContactPointDetailComponent,
            runGuardsAndResolvers: 'always',
            canDeactivate: [canLeaveDirtyForm],
          },
          {
            path: PRM_RELATIONS_TAB_LINK,
            component: RelationTabDetailComponent,
            runGuardsAndResolvers: 'always',
            canDeactivate: [canLeaveDirtyForm],
          },
          {path: '**', redirectTo: PRM_DETAIL_TAB_LINK},
        ],
      },
      {
        path: Pages.STOP_POINTS.path + '/:stopPointSloid/' + Pages.TOILET.path + '/:sloid',
        component: ToiletDetailPanelComponent,
        runGuardsAndResolvers: 'always',
        resolve: {
          toilet: toiletResolver,
          servicePoint: prmPanelResolver,
          stopPoint: stopPointResolver,
        },
        children: [
          {
            path: PRM_DETAIL_TAB_LINK,
            component: ToiletDetailComponent,
            runGuardsAndResolvers: 'always',
            canDeactivate: [canLeaveDirtyForm],
          },
          {
            path: PRM_RELATIONS_TAB_LINK,
            component: RelationTabDetailComponent,
            runGuardsAndResolvers: 'always',
            canDeactivate: [canLeaveDirtyForm],
          },
          {path: '**', redirectTo: PRM_DETAIL_TAB_LINK},
        ],
      },
      {
        path: Pages.STOP_POINTS.path + '/:stopPointSloid',
        component: PrmPanelComponent,
        resolve: {stopPoints: stopPointResolver, servicePoints: prmPanelResolver},
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
          {path: '**', redirectTo: Pages.PRM_STOP_POINT_TAB.path},
        ],
      },
    ]
  },
  // { path: '**', redirectTo: Pages.PRM.path },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PrmRoutingModule {}
