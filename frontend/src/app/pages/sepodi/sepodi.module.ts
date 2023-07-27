import { NgModule } from '@angular/core';

import { SepodiRoutingModule } from './sepodi-routing.module';
import { SepodiOverviewComponent } from './overview/sepodi-overview.component';
import { MapComponent } from './map/map.component';
import { ServicePointSidePanelComponent } from './service-point-side-panel/service-point-side-panel.component';
import { CoreModule } from '../../core/module/core.module';
import { FormatServicePointNumber } from './number-pipe/service-point-number.pipe';
import { ServicePointDetailComponent } from './service-point-side-panel/service-point/service-point-detail.component';
import { AreasDetailComponent } from './service-point-side-panel/areas/areas-detail.component';
import { TrafficPointElementsDetailComponent } from './service-point-side-panel/traffic-point-elements/traffic-point-elements-detail.component';
import { LoadingPointsDetailComponent } from './service-point-side-panel/loading-points/loading-points-detail.component';

@NgModule({
  declarations: [
    SepodiOverviewComponent,
    ServicePointSidePanelComponent,
    ServicePointDetailComponent,
    AreasDetailComponent,
    TrafficPointElementsDetailComponent,
    LoadingPointsDetailComponent,
    MapComponent,
    FormatServicePointNumber,
  ],
  imports: [CoreModule, SepodiRoutingModule],
})
export class SepodiModule {}
