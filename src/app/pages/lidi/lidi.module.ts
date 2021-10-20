import { NgModule } from '@angular/core';
import { CoreModule } from '../../core/module/core.module';
import { LinesComponent } from './lines/lines.component';
import { LidiRoutingModule } from './lidi.routing.module';
import { SublinesComponent } from './sublines/sublines.component';
import { LidiOverviewComponent } from './overview/lidi-overview.component';
import { LineDetailComponent } from './lines/detail/line-detail.component';
import { SublineDetailComponent } from './sublines/detail/subline-detail.component';
import { ColorPickerModule } from 'ngx-color-picker';
import { RgbPickerComponent } from './color-picker/rgb/rgb-picker.component';
import { CmykPickerComponent } from './color-picker/cmyk/cmyk-picker.component';
import { EmptyToNullDirective } from './color-picker/cmyk/empty-to-null';

@NgModule({
  declarations: [
    LidiOverviewComponent,
    LinesComponent,
    LineDetailComponent,
    RgbPickerComponent,
    CmykPickerComponent,
    EmptyToNullDirective,
    SublinesComponent,
    SublineDetailComponent,
  ],
  imports: [CoreModule, ColorPickerModule, LidiRoutingModule],
})
export class LidiModule {}
