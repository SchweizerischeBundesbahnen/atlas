import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PrmHomeSearchComponent } from './prm-home-search/prm-home-search.component';
import { FormModule } from '../../core/module/form.module';
import { FormsModule } from '@angular/forms';
import { PrmRoutingModule } from './prm-routing.module';
import { SepodiModule } from '../sepodi/sepodi.module';
import { StopPointDetailComponent } from './stop-point/stop-point-detail.component';
import { CoreModule } from '../../core/module/core.module';
import { PrmPanelComponent } from './prm-panel/prm-panel.component';
import { StopPointReducedFormComponent } from './stop-point/form/stop-point-reduced-form/stop-point-reduced-form.component';
import { StopPointCompleteFormComponent } from './stop-point/form/stop-point-complete-form/stop-point-complete-form.component';
import { ReferencePointComponent } from './reference-point/reference-point.component';
import { PlatformComponent } from './platform/platform.component';
import { TicketCounterComponent } from './ticket-counter/ticket-counter.component';
import { InformationDeskComponent } from './information-desk/information-desk.component';
import { ToiletComponent } from './toilet/toilet.component';
import { ParkingLotComponent } from './parking-lot/parking-lot.component';
import { ConnectionComponent } from './connection/connection.component';
import { CreateStopPointComponent } from './stop-point/create-stop-point/create-stop-point.component';

@NgModule({
  declarations: [
    PrmHomeSearchComponent,
    StopPointDetailComponent,
    PrmPanelComponent,
    StopPointReducedFormComponent,
    StopPointCompleteFormComponent,
    ReferencePointComponent,
    PlatformComponent,
    TicketCounterComponent,
    InformationDeskComponent,
    ToiletComponent,
    ParkingLotComponent,
    ConnectionComponent,
    CreateStopPointComponent,
  ],
  imports: [CommonModule, FormModule, FormsModule, PrmRoutingModule, SepodiModule, CoreModule],
})
export class PrmModule {}
