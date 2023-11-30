import { inject, Injectable, NgModule } from '@angular/core';
import { Router, RouterModule, Routes, UrlTree } from '@angular/router';
import { SepodiMapviewComponent } from './mapview/sepodi-mapview.component';
import { ServicePointSidePanelComponent } from './service-point-side-panel/service-point-side-panel.component';
import { Pages } from '../pages';
import { servicePointResolver } from './service-point-side-panel/service-point-detail.resolver';
import { ServicePointDetailComponent } from './service-point-side-panel/service-point/service-point-detail.component';
import { TrafficPointElementsTableComponent } from './service-point-side-panel/traffic-point-elements/traffic-point-elements-table.component';
import { LoadingPointsTableComponent } from './service-point-side-panel/loading-points/loading-points-table.component';
import { FotCommentDetailComponent } from './service-point-side-panel/comment/fot-comment-detail.component';
import { canLeaveDirtyForm } from '../../core/leave-guard/leave-dirty-form-guard.service';
import { ServicePointCreationComponent } from './service-point-side-panel/service-point/service-point-creation/service-point-creation.component';
import { AuthService } from '../../core/auth/auth.service';
import { ApplicationType } from '../../api';
import { TrafficPointElementsDetailComponent } from './traffic-point-elements/traffic-point-elements-detail.component';
import { trafficPointResolver } from './traffic-point-elements/traffic-point-elements-detail-resolver.service';
import { loadingPointResolver } from './loading-points/loading-points-detail-resolver.service';
import { LoadingPointsDetailComponent } from './loading-points/loading-points-detail.component';

@Injectable()
class CanActivateServicePointCreationGuard {
  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
  ) {}

  canActivate(): true | UrlTree {
    if (this.authService.hasPermissionsToCreate(ApplicationType.Sepodi)) {
      return true;
    }
    return this.router.parseUrl(Pages.SEPODI.path);
  }
}

const routes: Routes = [
  {
    path: '',
    component: SepodiMapviewComponent,
    children: [
      {
        path: Pages.SERVICE_POINTS.path,
        component: ServicePointCreationComponent,
        canActivate: [() => inject(CanActivateServicePointCreationGuard).canActivate()],
      },
      {
        path: Pages.SERVICE_POINTS.path + '/:id',
        component: ServicePointSidePanelComponent,
        resolve: { servicePoint: servicePointResolver },
        runGuardsAndResolvers: 'always',
        children: [
          {
            path: 'service-point',
            component: ServicePointDetailComponent,
            canDeactivate: [canLeaveDirtyForm],
          },
          {
            path: Pages.TRAFFIC_POINT_ELEMENTS_AREA.path,
            component: TrafficPointElementsTableComponent,
            data: { isTrafficPointArea: true },
          },
          {
            path: Pages.TRAFFIC_POINT_ELEMENTS_PLATFORM.path,
            component: TrafficPointElementsTableComponent,
            data: { isTrafficPointArea: false },
          },
          {
            path: 'loading-points',
            component: LoadingPointsTableComponent,
          },
          {
            path: 'comment',
            component: FotCommentDetailComponent,
            canDeactivate: [canLeaveDirtyForm],
          },
          {
            path: '**',
            redirectTo: 'service-point',
          },
        ],
      },
      {
        path: Pages.TRAFFIC_POINT_ELEMENTS_PLATFORM.path + '/:id',
        component: TrafficPointElementsDetailComponent,
        resolve: { trafficPoint: trafficPointResolver },
        runGuardsAndResolvers: 'always',
        canDeactivate: [canLeaveDirtyForm],
      },
      {
        path: Pages.LOADING_POINTS.path + '/:servicePointNumber/:number',
        component: LoadingPointsDetailComponent,
        resolve: { loadingPoint: loadingPointResolver },
        runGuardsAndResolvers: 'always',
        canDeactivate: [canLeaveDirtyForm],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [CanActivateServicePointCreationGuard],
})
export class SepodiRoutingModule {}
