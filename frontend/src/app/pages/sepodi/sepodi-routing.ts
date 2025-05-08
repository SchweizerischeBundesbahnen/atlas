import { Routes } from '@angular/router';
import { Pages } from '../pages';
import { servicePointResolver } from './service-point-side-panel/service-point-detail.resolver';
import { canLeaveDirtyForm } from '../../core/leave-guard/leave-dirty-form-guard.service';
import { trafficPointResolver } from './traffic-point-elements/traffic-point-elements-detail-resolver.service';
import { loadingPointResolver } from './loading-points/loading-points-detail-resolver.service';
import { canCreateServicePoint } from './service-point-creation-guard';
import { stopPointWorkflowDetailResolver } from './workflow/detail-page/stop-point-workflow-detail-resolver.service';
import { permissionsLoaded } from '../../core/auth/guards/permissions-loaded.guard';
import { featureToggleGuard } from '../feature-toggle.guard';

export const routes: Routes = [
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
  {
    path: Pages.TERMINATION_STOP_POINT_WORKFLOWS.path,
    loadComponent: () =>
      import(
        './termination-workflow/stop-point-termination-workflow-overview/stop-point-termination-workflow-overview.component'
      ).then((m) => m.StopPointTerminationWorkflowOverviewComponent),
    canActivate: [featureToggleGuard],
  },
  {
    path: '',
    loadComponent: () =>
      import('./mapview/sepodi-mapview.component').then(
        (m) => m.SepodiMapviewComponent
      ),
    children: [
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
        path: Pages.SERVICE_POINTS.path + '/:id',
        loadComponent: () =>
          import(
            './service-point-side-panel/service-point-side-panel.component'
          ).then((m) => m.ServicePointSidePanelComponent),
        resolve: { servicePoint: servicePointResolver },
        runGuardsAndResolvers: 'always',
        children: [
          {
            path: Pages.SEPODI_TAB.path,
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
          {
            path: '**',
            redirectTo: Pages.SEPODI_TAB.path,
          },
        ],
      },
      {
        path: Pages.TRAFFIC_POINT_ELEMENTS_PLATFORM.path + '/:id',
        loadComponent: () =>
          import(
            './traffic-point-elements/traffic-point-elements-detail.component'
          ).then((m) => m.TrafficPointElementsDetailComponent),
        resolve: { trafficPoint: trafficPointResolver },
        runGuardsAndResolvers: 'always',
        canDeactivate: [canLeaveDirtyForm],
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
