import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SepodiRoutingModule } from './sepodi-routing.module';
import { SepodiOverviewComponent } from './overview/sepodi-overview.component';

@NgModule({
  declarations: [SepodiOverviewComponent],
  imports: [CommonModule, SepodiRoutingModule],
})
export class SepodiModule {}
