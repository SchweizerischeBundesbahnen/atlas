import { NgModule } from '@angular/core';
import { CoreModule } from '../../core/module/core.module';
import { LinesComponent } from './lines/lines.component';
import { LidiRoutingModule } from './lidi.routing.module';
import { SublinesComponent } from './sublines/sublines.component';
import { LidiOverviewComponent } from './overview/lidi-overview.component';
import { LineDetailComponent } from './lines/detail/line-detail.component';
import { SublineDetailComponent } from './sublines/detail/subline-detail.component';
import { ColorModule } from './color-picker/color.module';
import { FormModule } from '../../core/module/form.module';
import { MainlineSelectOptionPipe } from './sublines/detail/mainline-select-option.pipe';
import { LidiWorkflowOverviewComponent } from './workflow/overview/lidi-workflow-overview.component';
import { LineVersionSnapshotDetailComponent } from './workflow/detail/line-version-snapshot-detail.component';
import { LineDetailFormComponent } from './lines/detail/line-detail-form/line-detail-form.component';
import { AtlasSearchSelectModule } from '../../core/form-components/atlas-search-select/atlas-search-select.module';
import { BoSelectionDisplayPipe } from '../../core/components/table-search/bo-selection-display.pipe';

@NgModule({
  declarations: [
    LineVersionSnapshotDetailComponent,
    LidiWorkflowOverviewComponent,
    LineDetailFormComponent,
    LidiOverviewComponent,
    LinesComponent,
    LineDetailComponent,
    SublinesComponent,
    SublineDetailComponent,
    MainlineSelectOptionPipe,
  ],
  imports: [CoreModule, ColorModule, LidiRoutingModule, FormModule, AtlasSearchSelectModule],
  providers: [BoSelectionDisplayPipe],
})
export class LidiModule {}
