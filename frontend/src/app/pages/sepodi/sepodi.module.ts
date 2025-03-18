import { NgModule } from '@angular/core';

import { SepodiRoutingModule } from './sepodi-routing.module';
import { SepodiMapviewComponent } from './mapview/sepodi-mapview.component';
import { MapComponent } from './map/map.component';
import { ServicePointSidePanelComponent } from './service-point-side-panel/service-point-side-panel.component';
import { CoreModule } from '../../core/module/core.module';
import { ServicePointDetailComponent } from './service-point-side-panel/service-point/service-point-detail.component';
import { TrafficPointElementsTableComponent } from './service-point-side-panel/traffic-point-elements/traffic-point-elements-table.component';
import { LoadingPointsTableComponent } from './service-point-side-panel/loading-points/loading-points-table.component';
import { FormModule } from '../../core/module/form.module';
import { GeographyComponent } from './geography/geography.component';
import { KilometerMasterSearchComponent } from './service-point-side-panel/service-point/search/kilometer-master-search.component';
import { ServicePointCreationComponent } from './service-point-side-panel/service-point/service-point-creation/service-point-creation.component';
import { ServicePointFormComponent } from './service-point-side-panel/service-point/service-point-form/service-point-form.component';
import { TrafficPointElementsDetailComponent } from './traffic-point-elements/traffic-point-elements-detail.component';
import { LoadingPointsDetailComponent } from './loading-points/loading-points-detail.component';
import { FotCommentDetailComponent } from './service-point-side-panel/comment/fot-comment-detail.component';
import { AddStopPointWorkflowComponent } from './workflow/add-dialog/add-stop-point-workflow.component';
import { StopPointWorkflowDetailComponent } from './workflow/detail-page/stop-point-workflow-detail.component';
import { StopPointWorkflowOverviewComponent } from './workflow/overview/stop-point-workflow-overview.component';
import { StopPointRejectWorkflowDialogComponent } from './workflow/stop-point-reject-workflow-dialog/stop-point-reject-workflow-dialog.component';
import { StopPointWorkflowDetailFormComponent } from './workflow/detail-page/detail-form/stop-point-workflow-detail-form.component';
import { DecisionFormComponent } from './workflow/detail-page/decision/decision-form/decision-form.component';
import { DecisionOverrideComponent } from './workflow/detail-page/decision/decision-detail/override/decision-override.component';
import { DecisionStepperComponent } from './workflow/detail-page/decision/decision-stepper/decision-stepper.component';
import { DecisionDetailDialogComponent } from './workflow/detail-page/decision/decision-detail/decision-detail-dialog.component';
import { FormsModule } from '@angular/forms';
import { StopPointRestartWorkflowDialogComponent } from "./workflow/stop-point-restart-workflow-dialog/stop-point-restart-workflow-dialog.component";
import {
  StopPointWorkflowExaminantsTableComponent
} from "./workflow/detail-page/examinant-table/stop-point-workflow-examinants-table.component";
import {AddExaminantsComponent} from "./workflow/detail-page/add-examinants-dialog/add-examinants.component";

@NgModule({
    imports: [CoreModule, FormModule, FormsModule, SepodiRoutingModule, SepodiMapviewComponent,
        ServicePointSidePanelComponent,
        ServicePointDetailComponent,
        KilometerMasterSearchComponent,
        TrafficPointElementsTableComponent,
        LoadingPointsTableComponent,
        MapComponent,
        GeographyComponent,
        ServicePointCreationComponent,
        ServicePointFormComponent,
        TrafficPointElementsDetailComponent,
        LoadingPointsDetailComponent,
        FotCommentDetailComponent,
        AddStopPointWorkflowComponent,
        StopPointWorkflowDetailFormComponent,
        StopPointWorkflowDetailComponent,
        StopPointWorkflowOverviewComponent,
        StopPointRejectWorkflowDialogComponent,
        DecisionFormComponent,
        DecisionOverrideComponent,
        DecisionStepperComponent,
        DecisionDetailDialogComponent,
        StopPointRestartWorkflowDialogComponent,
        StopPointWorkflowExaminantsTableComponent,
        AddExaminantsComponent],
})
export class SepodiModule {}
