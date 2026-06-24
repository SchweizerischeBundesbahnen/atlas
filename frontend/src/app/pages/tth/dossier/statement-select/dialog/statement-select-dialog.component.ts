import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogClose, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { StatementSelectComponent } from '../statement-select.component';
import { AtlasSpacerComponent } from '../../../../../core/components/spacer/atlas-spacer.component';
import { TimetableHearingStatementV2, TransportCompany } from '../../../../../api';
import { TablePagination } from '../../../../../core/components/table/table-pagination';
import { addElementsToArrayWhenNotUndefined } from '../../../../../core/util/arrays';
import { TimetableHearingStatementInternalService } from '../../../../../api/service/lidi/timetable-hearing-statement-internal.service';
import { TableService } from '../../../../../core/components/table/table.service';
import { TableColumn } from '../../../../../core/components/table/table-column';
import { TableFilter } from '../../../../../core/components/table-filter/config/table-filter';
import { TableComponent } from '../../../../../core/components/table/table.component';
import { TthTableFilterSettingsService } from '../../../tth-table-filter-settings.service';
import { Pages } from '../../../../pages';
import { AtlasButtonComponent } from '../../../../../core/components/button/atlas-button.component';
import { TthUtils } from '../../../util/tth-utils';
import { StatementSelectData } from '../../statement-select-data';
import { StatementTableHandler } from '../../../util/statement-table-handler';

@Component({
  selector: 'atlas-statement-select-dialog',
  templateUrl: './statement-select-dialog.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./statement-select-dialog.component.scss'],
  imports: [
    MatDialogClose,
    ReactiveFormsModule,
    MatDialogActions,
    TranslatePipe,
    StatementSelectComponent,
    AtlasSpacerComponent,
    TableComponent,
    AtlasButtonComponent,
  ],
  providers: [TranslatePipe, TableService],
})
export class StatementSelectDialogComponent extends StatementTableHandler implements OnInit {
  private readonly dialogRef = inject<MatDialogRef<StatementSelectDialogComponent, number[]>>(MatDialogRef);
  private readonly timetableHearingStatementsService = inject(TimetableHearingStatementInternalService);
  private readonly tableService = inject(TableService);

  readonly tableColumns: TableColumn<TimetableHearingStatementV2>[] = [
    ...this.defaultStatementColumns,
    {
      headerTitle: '',
      value: 'etagVersion',
      disabled: true,
      button: {
        icon: 'bi bi-file-earmark-plus',
        clickCallback: this.addStatement,
        applicationType: 'TIMETABLE_HEARING',
        buttonDataCy: 'addStatement',
        title: 'COMMON.ADD',
        buttonType: 'icon',
        disabled: this.selectedIncludes.bind(this),
      },
    },
  ];

  readonly data = inject<StatementSelectData>(MAT_DIALOG_DATA);
  selectedStatements: number[] = [];

  selectedIncludes(row: TimetableHearingStatementV2) {
    return this.selectedStatements.includes(row.id!);
  }

  statements: TimetableHearingStatementV2[] = [];
  totalCount$ = 0;
  tableFilterConfig!: TableFilter<unknown>[][];

  ngOnInit() {
    this.selectedStatements = this.data.selectedStatements;
    this.tableFilterConfig = this.tableService.initializeFilterConfig(
      TthTableFilterSettingsService.createSettings(),
      Pages.TTH_STATEMENTS
    );
  }

  confirm() {
    this.dialogRef.close(this.selectedStatements);
  }

  cancel() {
    this.dialogRef.close();
  }

  addStatement(row: TimetableHearingStatementV2) {
    if (this.selectedStatements.includes(row.id!)) {
      return;
    }
    this.selectedStatements = [...this.selectedStatements, row.id!];
  }

  getAdditionalStatements(pagination: TablePagination) {
    this.timetableHearingStatementsService
      .getStatements(
        this.data.timetableHearingYear,
        this.data.swissCanton,
        this.tableService.filter.chipSearch.getActiveSearch(),
        this.tableService.filter.multiSelectStatementStatus.getActiveSearch(),
        this.tableService.filter.searchSelectTTFN.getActiveSearch()?.ttfnid,
        TthUtils.toTransportCompanyIds(this.tableService.filter.searchSelectTU.getActiveSearch() as TransportCompany[]),
        false,
        pagination.page,
        pagination.size,
        addElementsToArrayWhenNotUndefined(pagination.sort, 'id,ASC')
      )
      .subscribe((container) => {
        this.statements = container.objects!;
        this.totalCount$ = container.totalCount!;
      });
  }
}
