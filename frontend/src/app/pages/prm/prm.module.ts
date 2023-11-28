import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PrmHomeSearchComponent } from './prm-home-search/prm-home-search.component';
import { FormModule } from '../../core/module/form.module';
import { FormsModule } from '@angular/forms';
import { PrmRoutingModule } from './prm-routing.module';
import { StopPointDetailComponent } from './tabs/stop-point/detail/stop-point-detail.component';
import { CoreModule } from '../../core/module/core.module';
import { PrmPanelComponent } from './prm-panel/prm-panel.component';
import { StopPointReducedFormComponent } from './tabs/stop-point/form/stop-point-reduced-form/stop-point-reduced-form.component';
import { StopPointCompleteFormComponent } from './tabs/stop-point/form/stop-point-complete-form/stop-point-complete-form.component';
import { ReferencePointComponent } from './tabs/reference-point/reference-point.component';
import { PlatformComponent } from './tabs/platform/platform.component';
import { TicketCounterComponent } from './tabs/ticket-counter/ticket-counter.component';
import { InformationDeskComponent } from './tabs/information-desk/information-desk.component';
import { ToiletComponent } from './tabs/toilet/toilet.component';
import { ParkingLotComponent } from './tabs/parking-lot/parking-lot.component';
import { ConnectionComponent } from './tabs/connection/connection.component';
import { CreateStopPointComponent } from './tabs/stop-point/create-stop-point/create-stop-point.component';
import { PrmInfoBoxComponent } from './prm-home-search/prm-info-box/prm-info-box.component';

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
    PrmInfoBoxComponent,
  ],
  imports: [CommonModule, FormModule, FormsModule, PrmRoutingModule, CoreModule],
})
export class PrmModule {}
