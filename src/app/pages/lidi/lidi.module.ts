import { NgModule } from '@angular/core';
import { CoreModule } from '../../core/module/core.module';
import { LinesComponent } from './lines/lines.component';
import { LidiRoutingModule } from './lidi.routing.module';
import { SublinesComponent } from './sublines/sublines.component';
import { LidiOverviewComponent } from './overview/lidi-overview.component';
import { LineDetailComponent } from './lines/detail/line-detail.component';

@NgModule({
  declarations: [LidiOverviewComponent, LinesComponent, LineDetailComponent, SublinesComponent],
  imports: [CoreModule, LidiRoutingModule],
})
export class LidiModule {}
