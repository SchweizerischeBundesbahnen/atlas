import { NgModule } from '@angular/core';

import { SepodiRoutingModule } from './sepodi-routing.module';
import { SepodiMapviewComponent } from './mapview/sepodi-mapview.component';
import { MapComponent } from './map/map.component';
import { ServicePointSidePanelComponent } from './service-point-side-panel/service-point-side-panel.component';
import { CoreModule } from '../../core/module/core.module';
import { ServicePointDetailComponent } from './service-point-side-panel/service-point/service-point-detail.component';
import { AreasDetailComponent } from './service-point-side-panel/areas/areas-detail.component';
import { TrafficPointElementsTableComponent } from './service-point-side-panel/traffic-point-elements/traffic-point-elements-table.component';
import { LoadingPointsDetailComponent } from './service-point-side-panel/loading-points/loading-points-detail.component';
import { FormModule } from '../../core/module/form.module';
import { FormsModule } from '@angular/forms';
import { MeansOfTransportPickerComponent } from './means-of-transport-picker/means-of-transport-picker.component';
import { GeographyComponent } from './geography/geography.component';
import { SearchServicePointComponent } from './search-service-point/search-service-point.component';
import { SearchResultHighlightPipe } from './search-service-point/search-result-highlight.pipe';
import { SplitServicePointNumberPipe } from './search-service-point/split-service-point-number.pipe';
import { KilometerMasterSearchComponent } from './service-point-side-panel/service-point/search/kilometer-master-search.component';
import { ServicePointCreationComponent } from './service-point-side-panel/service-point/service-point-creation/service-point-creation.component';
import { ServicePointFormComponent } from './service-point-side-panel/service-point/service-point-form/service-point-form.component';
import { TrafficPointElementsDetailComponent } from './traffic-point-elements/traffic-point-elements-detail.component';

@NgModule({
  declarations: [
    SepodiMapviewComponent,
    ServicePointSidePanelComponent,
    ServicePointDetailComponent,
    KilometerMasterSearchComponent,
    AreasDetailComponent,
    TrafficPointElementsTableComponent,
    LoadingPointsDetailComponent,
    MapComponent,
    MeansOfTransportPickerComponent,
    GeographyComponent,
    SearchServicePointComponent,
    SearchResultHighlightPipe,
    SplitServicePointNumberPipe,
    ServicePointCreationComponent,
    ServicePointFormComponent,
    TrafficPointElementsDetailComponent,
  ],
  imports: [CoreModule, FormModule, FormsModule, SepodiRoutingModule],
})
export class SepodiModule {}
