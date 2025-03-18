import { NgModule } from '@angular/core';
import { CoreModule } from '../../core/module/core.module';
import { LinesComponent } from './lines/lines.component';
import { LidiRoutingModule } from './lidi.routing.module';
import { LidiOverviewComponent } from './overview/lidi-overview.component';
import { LineDetailComponent } from './lines/detail/line-detail.component';
import { SublineDetailComponent } from './sublines/detail/subline-detail.component';
import { FormModule } from '../../core/module/form.module';
import { MainlineDescriptionPipe } from './sublines/detail/mainline-description.pipe';
import { LidiWorkflowOverviewComponent } from './workflow/overview/lidi-workflow-overview.component';
import { LineVersionSnapshotDetailComponent } from './workflow/detail/line-version-snapshot-detail.component';
import { LineDetailFormComponent } from './lines/detail/line-detail-form/line-detail-form.component';
import { SublineTableComponent } from './lines/detail/subline-table/subline-table.component';
import { SublineShorteningDialogComponent } from './dialog/subline-shortening-dialog/subline-shortening-dialog.component';
import { NgOptimizedImage } from '@angular/common';

@NgModule({
    imports: [CoreModule, LidiRoutingModule, FormModule, NgOptimizedImage, LineVersionSnapshotDetailComponent,
        LidiWorkflowOverviewComponent,
        LineDetailFormComponent,
        LidiOverviewComponent,
        LinesComponent,
        LineDetailComponent,
        SublineDetailComponent,
        MainlineDescriptionPipe,
        SublineTableComponent,
        SublineShorteningDialogComponent],
})
export class LidiModule {}
