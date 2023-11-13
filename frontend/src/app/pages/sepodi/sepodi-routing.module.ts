import { inject, Injectable, NgModule } from '@angular/core';
import { Router, RouterModule, Routes, UrlTree } from '@angular/router';
import { SepodiMapviewComponent } from './mapview/sepodi-mapview.component';
import { ServicePointSidePanelComponent } from './service-point-side-panel/service-point-side-panel.component';
import { Pages } from '../pages';
import { servicePointResolver } from './service-point-side-panel/service-point-detail.resolver';
import { ServicePointDetailComponent } from './service-point-side-panel/service-point/service-point-detail.component';
import { TrafficPointElementsTableComponent } from './service-point-side-panel/traffic-point-elements/traffic-point-elements-table.component';
import { LoadingPointsDetailComponent } from './service-point-side-panel/loading-points/loading-points-detail.component';
import { FotCommentDetailComponent } from './service-point-side-panel/comment/fot-comment-detail.component';
import { canLeaveDirtyForm } from '../../core/leave-guard/leave-dirty-form-guard.service';
import { ServicePointCreationComponent } from './service-point-side-panel/service-point/service-point-creation/service-point-creation.component';
import { AuthService } from '../../core/auth/auth.service';
import { ApplicationType } from '../../api';
import { TrafficPointElementsDetailComponent } from './traffic-point-elements/traffic-point-elements-detail.component';
import { trafficPointResolver } from './traffic-point-elements/traffic-point-elements-detail-resolver.service';

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
            path: 'areas',
            component: TrafficPointElementsTableComponent,
            data: { isTrafficPointArea: true },
          },
          {
            path: 'traffic-point-elements',
            component: TrafficPointElementsTableComponent,
            data: { isTrafficPointArea: false },
          },
          {
            path: 'loading-points',
            component: LoadingPointsDetailComponent,
          },
          {
            path: 'comment',
            component: FotCommentDetailComponent,
          },
          {
            path: '**',
            redirectTo: 'service-point',
          },
        ],
      },
      {
        path: Pages.TRAFFIC_POINT_ELEMENTS.path + '/:id',
        component: TrafficPointElementsDetailComponent,
        resolve: { trafficPoint: trafficPointResolver },
        runGuardsAndResolvers: 'always',
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
