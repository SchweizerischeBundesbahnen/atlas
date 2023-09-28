import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SepodiMapviewComponent } from './mapview/sepodi-mapview.component';
import { ServicePointSidePanelComponent } from './service-point-side-panel/service-point-side-panel.component';
import { Pages } from '../pages';
import { servicePointResolver } from './service-point-side-panel/service-point-detail.resolver';
import { ServicePointDetailComponent } from './service-point-side-panel/service-point/service-point-detail.component';
import { AreasDetailComponent } from './service-point-side-panel/areas/areas-detail.component';
import { TrafficPointElementsDetailComponent } from './service-point-side-panel/traffic-point-elements/traffic-point-elements-detail.component';
import { LoadingPointsDetailComponent } from './service-point-side-panel/loading-points/loading-points-detail.component';
import { FotCommentDetailComponent } from './service-point-side-panel/comment/fot-comment-detail.component';
import { canLeaveDirtyForm } from '../../core/leave-guard/leave-dirty-form-guard.service';
import { ServicePointCreationComponent } from './service-point-creation/service-point-creation.component';

const routes: Routes = [
  {
    path: '',
    component: SepodiMapviewComponent,
    children: [
      {
        path: Pages.SERVICE_POINTS.path,
        component: ServicePointCreationComponent,
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
            component: AreasDetailComponent,
          },
          {
            path: 'traffic-point-elements',
            component: TrafficPointElementsDetailComponent,
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
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SepodiRoutingModule {}
