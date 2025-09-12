import { Routes } from '@angular/router';
import { Pages } from '../pages';
import { servicePointResolver } from './service-point-side-panel/service-point-detail.resolver';
import { canLeaveDirtyForm } from '../../core/leave-guard/leave-dirty-form-guard.service';
import { trafficPointResolver } from './traffic-point-elements/form/traffic-point-elements-detail-resolver.service';
import { loadingPointResolver } from './loading-points/loading-points-detail-resolver.service';
import { canCreateServicePoint } from './service-point-creation-guard';
import { stopPointWorkflowDetailResolver } from './workflow/detail-page/stop-point-workflow-detail-resolver.service';
import { permissionsLoaded } from '../../core/auth/guards/permissions-loaded.guard';
import { featureToggleGuard } from '../feature-toggle.guard';
import { stopPointTerminationWorkflowResolver } from './termination-workflow/stop-point-termination-workflow-detail/stop-point-termination-workflow-resolver';

const workflowRoutes: Routes = [
  {
    path: Pages.SERVICE_POINT_WORKFLOWS.path + '/:id',
    loadComponent: () =>
      import(
        './workflow/detail-page/stop-point-workflow-detail.component'
      ).then((m) => m.StopPointWorkflowDetailComponent),
    canActivate: [permissionsLoaded],
    resolve: { workflow: stopPointWorkflowDetailResolver },
    runGuardsAndResolvers: 'always',
  },
  {
    path: Pages.SERVICE_POINT_WORKFLOWS.path,
    loadComponent: () =>
      import('./workflow/overview/stop-point-workflow-overview.component').then(
        (m) => m.StopPointWorkflowOverviewComponent
      ),
  },
];

const terminationWorkflowRoutes: Routes = [
  {
    path: Pages.TERMINATION_STOP_POINT_WORKFLOWS.path + '/:id',
    loadComponent: () =>
      import(
        './termination-workflow/stop-point-termination-workflow-detail/stop-point-termination-workflow-detail'
      ).then((m) => m.StopPointTerminationWorkflowDetail),
    canActivate: [featureToggleGuard, permissionsLoaded],
    resolve: { workflow: stopPointTerminationWorkflowResolver },
    runGuardsAndResolvers: 'always',
  },
  {
    path: Pages.TERMINATION_STOP_POINT_WORKFLOWS.path,
    loadComponent: () =>
      import(
        './termination-workflow/stop-point-termination-workflow-overview/stop-point-termination-workflow-overview.component'
      ).then((m) => m.StopPointTerminationWorkflowOverviewComponent),
    canActivate: [featureToggleGuard],
  },
];

const servicePointTabRoutes: Routes = [
  {
    path: Pages.SERVICE_POINT_TAB.path,
    loadComponent: () =>
      import(
        './service-point-side-panel/service-point/service-point-detail.component'
      ).then((m) => m.ServicePointDetailComponent),
    canDeactivate: [canLeaveDirtyForm],
  },
  {
    path: Pages.TRAFFIC_POINT_ELEMENTS_AREA.path,
    loadComponent: () =>
      import(
        './service-point-side-panel/traffic-point-elements/traffic-point-elements-table.component'
      ).then((m) => m.TrafficPointElementsTableComponent),
    data: { isTrafficPointArea: true },
  },
  {
    path: Pages.TRAFFIC_POINT_ELEMENTS_PLATFORM.path,
    loadComponent: () =>
      import(
        './service-point-side-panel/traffic-point-elements/traffic-point-elements-table.component'
      ).then((m) => m.TrafficPointElementsTableComponent),
    data: { isTrafficPointArea: false },
  },
  {
    path: 'loading-points',
    loadComponent: () =>
      import(
        './service-point-side-panel/loading-points/loading-points-table.component'
      ).then((m) => m.LoadingPointsTableComponent),
  },
  {
    path: 'comment',
    loadComponent: () =>
      import(
        './service-point-side-panel/comment/fot-comment-detail.component'
      ).then((m) => m.FotCommentDetailComponent),
    canDeactivate: [canLeaveDirtyForm],
  },
];

const trafficPointElementDetailRoutes: Routes = [
  {
    path:
      Pages.SERVICE_POINTS.path +
      '/:servicePointNumber/' +
      Pages.TRAFFIC_POINT_ELEMENTS_PLATFORM.path +
      '/:trafficPointSloid',
    loadComponent: () =>
      import(
        './traffic-point-elements/traffic-point-elements-side-panel.component'
      ).then((m) => m.TrafficPointElementsSidePanelComponent),
    resolve: {
      trafficPoint: trafficPointResolver,
      servicePoint: servicePointResolver,
    },
    data: { isTrafficPointArea: false },
    runGuardsAndResolvers: 'always',
    children: [
      {
        path: '',
        loadComponent: () =>
          import(
            './traffic-point-elements/form/traffic-point-elements-detail.component'
          ).then((m) => m.TrafficPointElementsDetailComponent),
        runGuardsAndResolvers: 'always',
        canDeactivate: [canLeaveDirtyForm],
        pathMatch: 'full',
      },
      {
        path: Pages.SECTORS.path,
        loadComponent: () =>
          import('./sectors/sector-overview/sector-overview.component').then(
            (m) => m.SectorOverviewComponent
          ),
        runGuardsAndResolvers: 'always',
      },
      {
        path: '**',
        redirectTo: '',
      },
    ],
  },
];

const trafficPointAreaDetailRoutes: Routes = [
  {
    path:
      Pages.SERVICE_POINTS.path +
      '/:servicePointNumber/' +
      Pages.TRAFFIC_POINT_ELEMENTS_AREA.path +
      '/:trafficPointSloid',
    loadComponent: () =>
      import(
        './traffic-point-elements/traffic-point-elements-side-panel.component'
      ).then((m) => m.TrafficPointElementsSidePanelComponent),
    resolve: { trafficPoint: trafficPointResolver },
    data: { isTrafficPointArea: true },
    runGuardsAndResolvers: 'always',
    children: [
      {
        path: '',
        loadComponent: () =>
          import(
            './traffic-point-elements/form/traffic-point-elements-detail.component'
          ).then((m) => m.TrafficPointElementsDetailComponent),
        runGuardsAndResolvers: 'always',
        canDeactivate: [canLeaveDirtyForm],
        pathMatch: 'full',
      },
      {
        path: '**',
        redirectTo: '',
      },
    ],
  },
];

export const routes: Routes = [
  ...workflowRoutes,
  ...terminationWorkflowRoutes,
  {
    path: '',
    loadComponent: () =>
      import('./mapview/sepodi-mapview.component').then(
        (m) => m.SepodiMapviewComponent
      ),
    children: [
      ...trafficPointElementDetailRoutes,
      ...trafficPointAreaDetailRoutes,
      {
        path: Pages.SERVICE_POINTS.path,
        loadComponent: () =>
          import(
            './service-point-side-panel/service-point/service-point-creation/service-point-creation.component'
          ).then((m) => m.ServicePointCreationComponent),
        canActivate: [canCreateServicePoint],
        canDeactivate: [canLeaveDirtyForm],
      },
      {
        path: Pages.SERVICE_POINTS.path + '/:servicePointNumber',
        loadComponent: () =>
          import(
            './service-point-side-panel/service-point-side-panel.component'
          ).then((m) => m.ServicePointSidePanelComponent),
        resolve: { servicePoint: servicePointResolver },
        runGuardsAndResolvers: 'always',
        children: [
          ...servicePointTabRoutes,
          {
            path: '**',
            redirectTo: Pages.SERVICE_POINT_TAB.path,
          },
        ],
      },
      {
        path: Pages.LOADING_POINTS.path + '/:servicePointNumber/:number',
        loadComponent: () =>
          import('./loading-points/loading-points-detail.component').then(
            (m) => m.LoadingPointsDetailComponent
          ),
        resolve: { loadingPoint: loadingPointResolver },
        runGuardsAndResolvers: 'always',
        canDeactivate: [canLeaveDirtyForm],
      },
    ],
  },
];
