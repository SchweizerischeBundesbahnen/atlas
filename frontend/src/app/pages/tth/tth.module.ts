import { NgModule } from '@angular/core';
import { CoreModule } from '../../core/module/core.module';
import { TthRoutingModule } from './tth-routing.module';
import { FormModule } from '../../core/module/form.module';
import { TimetableHearingOverviewComponent } from './overview/timetable-hearing-overview.component';
import { CantonCardComponent } from './overview/canton-card/canton-card.component';
import { OverviewDetailComponent } from './overview-detail/overview-detail.component';
import { OverviewTabComponent } from './overview-tab/overview-tab.component';
import { OverviewTabHeadingComponent } from './overview-tab/overview-tab-heading/overview-tab-heading.component';
import { StatementDetailComponent } from './statement/statement-detail.component';
import { DialogManageTthComponent } from './dialog-manage-tth/dialog-manage-tth.component';
import { TthTableService } from './tth-table.service';
import { TableService } from '../../core/components/table/table.service';
import { NewTimetableHearingYearDialogComponent } from './new-timetable-hearing-year-dialog/new-timetable-hearing-year-dialog.component';
import { StatementDialogComponent } from './statement/statement-dialog/statement.dialog.component';
import { BaseChangeDialogComponent } from './overview-detail/base-change-dialog/base-change-dialog.component';
import { TthChangeCantonDialogComponent } from './overview-detail/tth-change-canton-dialog/tth-change-canton-dialog.component';

@NgModule({
  declarations: [
    TimetableHearingOverviewComponent,
    CantonCardComponent,
    OverviewDetailComponent,
    OverviewTabComponent,
    OverviewTabHeadingComponent,
    StatementDetailComponent,
    DialogManageTthComponent,
    TthChangeStatusDialogComponent,
    NewTimetableHearingYearDialogComponent,
    StatementDialogComponent,
    BaseChangeDialogComponent,
    TthChangeCantonDialogComponent,
  ],
  imports: [CoreModule, TthRoutingModule, FormModule],
  providers: [
    {
      provide: TthTableService,
      useClass: TthTableService,
    },
    {
      provide: TableService,
      useExisting: TthTableService,
    },
  ],
})
export class TthModule {}
