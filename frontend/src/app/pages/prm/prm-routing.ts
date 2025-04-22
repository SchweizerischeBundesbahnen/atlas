import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Pages } from '../pages';

import { canLeaveDirtyForm } from '../../core/leave-guard/leave-dirty-form-guard.service';
import { stopPointResolver } from './prm-panel/resolvers/stop-point.resolver';
import { PrmTabs } from './prm-panel/prm-tabs';
import { prmPanelResolver } from './prm-panel/resolvers/prm-panel-resolver.service';
import { platformResolver } from './tabs/platform/detail/resolvers/platform.resolver';
import { trafficPointElementResolver } from './tabs/platform/detail/resolvers/traffic-point-element.resolver';

import { referencePointResolver } from './tabs/reference-point/detail/resolvers/reference-point.resolver';

import { parkingLotResolver } from './tabs/parking-lot/detail/resolvers/parking-lot.resolver';

import { contactPointResolver } from './tabs/contact-point/detail/resolvers/contact-point.resolver';

import { toiletResolver } from './tabs/toilet/detail/resolvers/toilet.resolver';

import {
  PRM_DETAIL_TAB_LINK,
  PRM_RELATIONS_TAB_LINK,
} from './tabs/relation/tab/detail-with-relation-tab.component';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./prm-home-search/prm-home-search.component').then(
        (m) => m.PrmHomeSearchComponent
      ),
    children: [
      {
        path:
          Pages.STOP_POINTS.path +
          '/:stopPointSloid/' +
          Pages.PLATFORMS.path +
          '/:sloid',
        loadComponent: () =>
          import('./tabs/platform/detail/platform-detail-panel.component').then(
            (m) => m.PlatformDetailPanelComponent
          ),
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
            loadComponent: () =>
              import(
                './tabs/platform/detail/detail/platform-detail.component'
              ).then((m) => m.PlatformDetailComponent),
            runGuardsAndResolvers: 'always',
            canDeactivate: [canLeaveDirtyForm],
          },
          {
            path: PRM_RELATIONS_TAB_LINK,
            loadComponent: () =>
              import(
                './tabs/relation/tab-detail/relation-tab-detail.component'
              ).then((m) => m.RelationTabDetailComponent),
            runGuardsAndResolvers: 'always',
            canDeactivate: [canLeaveDirtyForm],
          },
          { path: '**', redirectTo: PRM_DETAIL_TAB_LINK },
        ],
      },
      {
        path:
          Pages.STOP_POINTS.path +
          '/:stopPointSloid/' +
          Pages.REFERENCE_POINT.path +
          '/:sloid',
        loadComponent: () =>
          import(
            './tabs/reference-point/detail/reference-point-detail.component'
          ).then((m) => m.ReferencePointDetailComponent),
        runGuardsAndResolvers: 'always',
        canDeactivate: [canLeaveDirtyForm],
        resolve: {
          referencePoint: referencePointResolver,
          servicePoint: prmPanelResolver,
        },
      },
      {
        path:
          Pages.STOP_POINTS.path +
          '/:stopPointSloid/' +
          Pages.PARKING_LOT.path +
          '/:sloid',
        loadComponent: () =>
          import(
            './tabs/parking-lot/detail/parking-lot-detail-panel.component'
          ).then((m) => m.ParkingLotDetailPanelComponent),
        runGuardsAndResolvers: 'always',
        resolve: {
          parkingLot: parkingLotResolver,
          servicePoint: prmPanelResolver,
          stopPoint: stopPointResolver,
        },
        children: [
          {
            path: PRM_DETAIL_TAB_LINK,
            loadComponent: () =>
              import(
                './tabs/parking-lot/detail/detail/parking-lot-detail.component'
              ).then((m) => m.ParkingLotDetailComponent),
            runGuardsAndResolvers: 'always',
            canDeactivate: [canLeaveDirtyForm],
          },
          {
            path: PRM_RELATIONS_TAB_LINK,
            loadComponent: () =>
              import(
                './tabs/relation/tab-detail/relation-tab-detail.component'
              ).then((m) => m.RelationTabDetailComponent),
            runGuardsAndResolvers: 'always',
            canDeactivate: [canLeaveDirtyForm],
          },
          { path: '**', redirectTo: PRM_DETAIL_TAB_LINK },
        ],
      },
      {
        path:
          Pages.STOP_POINTS.path +
          '/:stopPointSloid/' +
          Pages.CONTACT_POINT.path +
          '/:sloid',
        loadComponent: () =>
          import(
            './tabs/contact-point/detail/contact-point-detail-panel.component'
          ).then((m) => m.ContactPointDetailPanelComponent),
        runGuardsAndResolvers: 'always',
        resolve: {
          contactPoint: contactPointResolver,
          servicePoint: prmPanelResolver,
          stopPoint: stopPointResolver,
        },
        children: [
          {
            path: PRM_DETAIL_TAB_LINK,
            loadComponent: () =>
              import(
                './tabs/contact-point/detail/detail/contact-point-detail.component'
              ).then((m) => m.ContactPointDetailComponent),
            runGuardsAndResolvers: 'always',
            canDeactivate: [canLeaveDirtyForm],
          },
          {
            path: PRM_RELATIONS_TAB_LINK,
            loadComponent: () =>
              import(
                './tabs/relation/tab-detail/relation-tab-detail.component'
              ).then((m) => m.RelationTabDetailComponent),
            runGuardsAndResolvers: 'always',
            canDeactivate: [canLeaveDirtyForm],
          },
          { path: '**', redirectTo: PRM_DETAIL_TAB_LINK },
        ],
      },
      {
        path:
          Pages.STOP_POINTS.path +
          '/:stopPointSloid/' +
          Pages.TOILET.path +
          '/:sloid',
        loadComponent: () =>
          import('./tabs/toilet/detail/toilet-detail-panel.component').then(
            (m) => m.ToiletDetailPanelComponent
          ),
        runGuardsAndResolvers: 'always',
        resolve: {
          toilet: toiletResolver,
          servicePoint: prmPanelResolver,
          stopPoint: stopPointResolver,
        },
        children: [
          {
            path: PRM_DETAIL_TAB_LINK,
            loadComponent: () =>
              import(
                './tabs/toilet/detail/detail/toilet-detail.component'
              ).then((m) => m.ToiletDetailComponent),
            runGuardsAndResolvers: 'always',
            canDeactivate: [canLeaveDirtyForm],
          },
          {
            path: PRM_RELATIONS_TAB_LINK,
            loadComponent: () =>
              import(
                './tabs/relation/tab-detail/relation-tab-detail.component'
              ).then((m) => m.RelationTabDetailComponent),
            runGuardsAndResolvers: 'always',
            canDeactivate: [canLeaveDirtyForm],
          },
          { path: '**', redirectTo: PRM_DETAIL_TAB_LINK },
        ],
      },
      {
        path: Pages.STOP_POINTS.path + '/:stopPointSloid',
        loadComponent: () =>
          import('./prm-panel/prm-panel.component').then(
            (m) => m.PrmPanelComponent
          ),
        resolve: {
          stopPoints: stopPointResolver,
          servicePoints: prmPanelResolver,
        },
        runGuardsAndResolvers: 'always',
        children: [
          {
            path: Pages.PRM_STOP_POINT_TAB.path,
            loadComponent: () =>
              import(
                './tabs/stop-point/detail/stop-point-detail.component'
              ).then((m) => m.StopPointDetailComponent),
            runGuardsAndResolvers: 'always',
            canDeactivate: [canLeaveDirtyForm],
          },
          {
            path: PrmTabs.REFERENCE_POINT.link,
            loadComponent: () =>
              import(
                './tabs/reference-point/reference-point-table.component'
              ).then((m) => m.ReferencePointTableComponent),
            runGuardsAndResolvers: 'always',
          },
          {
            path: PrmTabs.PLATFORM.link,
            loadComponent: () =>
              import('./tabs/platform/platform-table.component').then(
                (m) => m.PlatformTableComponent
              ),
            runGuardsAndResolvers: 'always',
          },
          {
            path: PrmTabs.CONTACT_POINT.link,
            loadComponent: () =>
              import('./tabs/contact-point/contact-point-table.component').then(
                (m) => m.ContactPointTableComponent
              ),
            runGuardsAndResolvers: 'always',
          },
          {
            path: PrmTabs.TOILET.link,
            loadComponent: () =>
              import('./tabs/toilet/toilet.component').then(
                (m) => m.ToiletComponent
              ),
            runGuardsAndResolvers: 'always',
          },
          {
            path: PrmTabs.PARKING_LOT.link,
            loadComponent: () =>
              import('./tabs/parking-lot/parking-lot-table.component').then(
                (m) => m.ParkingLotTableComponent
              ),
            runGuardsAndResolvers: 'always',
          },
          { path: '**', redirectTo: Pages.PRM_STOP_POINT_TAB.path },
        ],
      },
    ],
  },
  { path: '**', redirectTo: Pages.PRM.path },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PrmRouting {}
