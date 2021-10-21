import { NgModule } from '@angular/core';
import { CoreModule } from '../../core/module/core.module';
import { LinesComponent } from './lines/lines.component';
import { LidiRoutingModule } from './lidi.routing.module';
import { SublinesComponent } from './sublines/sublines.component';
import { LidiOverviewComponent } from './overview/lidi-overview.component';
import { LineDetailComponent } from './lines/detail/line-detail.component';
import { SublineDetailComponent } from './sublines/detail/subline-detail.component';
import { ColorModule } from './color-picker/color.module';

@NgModule({
  declarations: [
    LidiOverviewComponent,
    LinesComponent,
    LineDetailComponent,
    SublinesComponent,
    SublineDetailComponent,
  ],
  imports: [CoreModule, ColorModule, LidiRoutingModule],
})
export class LidiModule {}
