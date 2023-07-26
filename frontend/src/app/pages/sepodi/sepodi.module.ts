import { NgModule } from '@angular/core';

import { SepodiRoutingModule } from './sepodi-routing.module';
import { SepodiOverviewComponent } from './overview/sepodi-overview.component';
import { MapComponent } from './map/map.component';
import { ServicePointDetailComponent } from './service-points/service-point-detail.component';
import { CoreModule } from '../../core/module/core.module';
import { FormatServicePointNumber } from './number-pipe/service-point-number.pipe';

@NgModule({
  declarations: [
    SepodiOverviewComponent,
    ServicePointDetailComponent,
    MapComponent,
    FormatServicePointNumber,
  ],
  imports: [CoreModule, SepodiRoutingModule],
})
export class SepodiModule {}
