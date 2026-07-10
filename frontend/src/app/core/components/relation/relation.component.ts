import { ChangeDetectionStrategy, Component, Input, input, output, ViewChild } from '@angular/core';
import { DateService } from '../../date/date.service';
import { TableColumn } from '../table/table-column';
import { MatSort, MatSortHeader, Sort } from '@angular/material/sort';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatNoDataRow,
  MatRow,
  MatRowDef,
  MatTable,
} from '@angular/material/table';
import { NgClass } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'atlas-relation',
  templateUrl: './relation.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./relation.component.scss'],
  imports: [
    MatTable,
    MatSort,
    NgClass,
    MatColumnDef,
    MatHeaderCellDef,
    MatHeaderCell,
    MatSortHeader,
    MatCellDef,
    MatCell,
    MatHeaderRowDef,
    MatHeaderRow,
    MatRowDef,
    MatRow,
    MatNoDataRow,
    TranslatePipe,
  ],
  providers: [TranslatePipe],
})
/* eslint-disable  @typescript-eslint/no-explicit-any */
export class RelationComponent<RECORD_TYPE> {
  @ViewChild(MatTable) table!: MatTable<any>;
  @ViewChild(MatSort) matSort!: MatSort;

  @Input() set records(value: RECORD_TYPE[] | null) {
    this._records = value ?? [];
    if (this.matSort?.active && this.matSort.direction) {
      this.sortChanged({
        active: this.matSort.active,
        direction: this.matSort.direction,
      });
    }
  }

  @Input() titleTranslationKey = '';
  readonly relationEditable = input(true);
  readonly editable = input(false);
  readonly tableColumns = input.required<TableColumn<RECORD_TYPE>[]>();
  readonly editMode = input(false);
  readonly selectedIndex = input(-1);
  readonly addBtnNameTranslationKey = input('RELATION.ADD');
  readonly deleteBtnNameTranslationKey = input('RELATION.DELETE');
  readonly updateBtnNameTranslationKey = input('RELATION.UPDATE');

  readonly addRelation = output<void>();
  readonly deleteRelation = output<void>();
  readonly updateRelation = output<void>();
  readonly editModeChanged = output<void>();
  readonly selectedIndexChanged = output<number>();

  _records: RECORD_TYPE[] = [];

  columnValues(): string[] {
    return this.tableColumns().map((item) => item.columnDef!);
  }

  formatDate(date: Date): string {
    return DateService.getDateFormatted(date);
  }

  getValue(row: RECORD_TYPE, column: TableColumn<RECORD_TYPE>): string | Date | number {
    if (column.formatAsDate) {
      return this.formatDate(this.readValueFromObject(row, column.value ?? column.valuePath!) as Date);
    }
    return this.readValueFromObject(row, column.value ?? column.valuePath!);
  }

  private readValueFromObject(obj: RECORD_TYPE, path: string): string | Date | number {
    const objectPath = path.split('.');
    return objectPath.reduce((prev, curr) => prev[curr], obj as any);
  }

  isRowSelected(row: RECORD_TYPE): boolean {
    return this.selectedIndex() === this._records.indexOf(row);
  }

  selectRecord(record: RECORD_TYPE): void {
    if (this.editable()) {
      this.selectedIndexChanged.emit(this._records.indexOf(record));
    }
  }

  sortChanged(sort: Sort): void {
    const valuePathToSort = this.getValuePathFromColumnName(sort.active);
    const nestedPath = valuePathToSort.split('.');

    this._records.sort((a, b) => {
      let i = 0;
      while (i < nestedPath.length) {
        a = (a as any)[nestedPath[i]];
        b = (b as any)[nestedPath[i]];
        i++;
      }

      switch (sort.direction) {
        case 'desc':
          return -1 * RelationComponent.compare(a, b);
        default:
          return RelationComponent.compare(a, b);
      }
    });

    this.table.renderRows();
  }

  onAddRelation() {
    this.addRelation.emit();
    this.editModeChanged.emit();
  }

  editRelation() {
    this.updateRelation.emit();
    this.editModeChanged.emit();
  }

  private getValuePathFromColumnName(column: string): string {
    const filteredColumn = this.tableColumns().find((tableColumn) => tableColumn.columnDef === column);
    if (!filteredColumn) {
      throw new Error(`Column with name ${column} not found in table columns`);
    }
    return filteredColumn.value ?? filteredColumn.valuePath!;
  }

  private static compare(a: any, b: any): number {
    if (typeof a === 'string' && typeof b === 'string') {
      return a.localeCompare(b);
    }
    return a > b ? 1 : -1;
  }
}
