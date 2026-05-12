import { Component, effect, inject, input, model } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TimetableHearingStatementV2 } from '../../../../api';
import { TimetableHearingStatementInternalService } from '../../../../api/service/lidi/timetable-hearing-statement-internal.service';
import { TableComponent } from '../../../../core/components/table/table.component';
import { TableColumn } from '../../../../core/components/table/table-column';
import { Router } from '@angular/router';
import { Pages } from '../../../pages';
import { Cantons } from '../../../../core/cantons/Cantons';
import { TranslatePipe } from '@ngx-translate/core';
import { forkJoin } from 'rxjs';
import { StatementTableHandler } from '../../util/statement-table-handler';

@Component({
  selector: 'atlas-statement-select',
  imports: [FormsModule, ReactiveFormsModule, TableComponent, TranslatePipe],
  templateUrl: './statement-select.component.html',
})
export class StatementSelectComponent extends StatementTableHandler {
  readonly selectedStatements = model.required<number[]>();
  readonly removeOptionEnabled = input(true);
  readonly showRemoveOption = input(true);

  private readonly timetableHearingStatementInternalService = inject(TimetableHearingStatementInternalService);
  private readonly router = inject(Router);

  defaultTableColumns: TableColumn<TimetableHearingStatementV2>[] = [
    ...this.defaultStatementColumns,
    {
      headerTitle: '',
      value: 'etagVersion',
      disabled: true,
      button: {
        icon: 'bi bi-trash',
        clickCallback: this.removeStatement,
        applicationType: 'TIMETABLE_HEARING',
        buttonDataCy: 'removeStatement',
        title: 'COMMON.DELETE',
        buttonType: 'icon',
        disabled: () => !this.removeOptionEnabled(),
      },
    },
  ];
  statements: TimetableHearingStatementV2[] = [];

  get tableColumns(): TableColumn<TimetableHearingStatementV2>[] {
    if (this.showRemoveOption()) {
      return this.defaultTableColumns;
    }
    return this.defaultTableColumns.slice(0, -1);
  }

  constructor() {
    super();
    effect(() => {
      this.loadStatementsToTable();
    });
  }

  removeStatement(statement: TimetableHearingStatementV2) {
    const updatedStatementIds = this.selectedStatements().filter((id) => id !== statement.id);
    this.selectedStatements.set(updatedStatementIds);
  }

  goToStatement(statement: TimetableHearingStatementV2) {
    const url = this.router.serializeUrl(
      this.router.createUrlTree([
        Pages.TTH.path,
        Cantons.fromSwissCanton(statement.swissCanton)?.path,
        Pages.TTH_ACTIVE.path,
        Pages.TTH_STATEMENTS.path,
        statement.id,
      ])
    );

    window.open(url, '_blank');
  }

  loadStatementsToTable() {
    if (this.selectedStatements().length === 0) {
      this.statements = [];
    } else {
      forkJoin(
        this.selectedStatements().map((id) => this.timetableHearingStatementInternalService.getStatement(id))
      ).subscribe((statements) => {
        this.statements = statements;
      });
    }
  }
}
