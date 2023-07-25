import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SepodiRoutingModule } from './sepodi-routing.module';
import { SepodiOverviewComponent } from './overview/sepodi-overview.component';
import { MapComponent } from './map/map.component';
import { ServicePointDetailComponent } from './service-points/service-point-detail.component';

@NgModule({
  declarations: [SepodiOverviewComponent, ServicePointDetailComponent, MapComponent],
  imports: [CommonModule, SepodiRoutingModule],
})
export class SepodiModule {}
