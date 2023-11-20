import { Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { DateService } from '../../date/date.service';
import { TableColumn } from '../table/table-column';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';

@Component({
  selector: 'app-relation',
  templateUrl: './relation.component.html',
  styleUrls: ['./relation.component.scss'],
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
  @Input() relationEditable = true;
  @Input() editable = false;
  @Input() tableColumns!: TableColumn<RECORD_TYPE>[];
  @Input() editMode = false;
  @Input() selectedIndex = -1;
  @Input() addBtnNameTranslationKey = 'RELATION.ADD';
  @Input() deleteBtnNameTranslationKey = 'RELATION.DELETE';
  @Input() updateBtnNameTranslationKey = 'RELATION.UPDATE';

  @Output() deleteRelation = new EventEmitter<void>();
  @Output() updateRelation = new EventEmitter<void>();
  @Output() editModeChanged = new EventEmitter<void>();
  @Output() selectedIndexChanged = new EventEmitter<number>();

  _records: RECORD_TYPE[] = [];

  columnValues(): string[] {
    return this.tableColumns.map((item) => item.columnDef!);
  }

  formatDate(date: Date): string {
    return DateService.getDateFormatted(date);
  }

  getValue(row: RECORD_TYPE, column: TableColumn<RECORD_TYPE>): string | Date | number {
    if (column.formatAsDate) {
      return this.formatDate(
        this.readValueFromObject(row, column.value ?? column.valuePath!) as Date,
      );
    }
    return this.readValueFromObject(row, column.value ?? column.valuePath!);
  }

  private readValueFromObject(obj: RECORD_TYPE, path: string): string | Date | number {
    const objectPath = path.split('.');
    return objectPath.reduce((prev, curr) => prev[curr], obj as any);
  }

  isRowSelected(row: RECORD_TYPE): boolean {
    return this.selectedIndex === this._records.indexOf(row);
  }

  selectRecord(record: RECORD_TYPE): void {
    if (this.editable) {
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

  editRelation() {
    this.updateRelation.emit();
    this.editModeChanged.emit();
  }

  private getValuePathFromColumnName(column: string): string {
    const filteredColumn = this.tableColumns.filter((i) => i.columnDef == column)[0];
    return filteredColumn.value ?? filteredColumn.valuePath!;
  }

  private static compare(a: any, b: any): number {
    if (typeof a === 'string' && typeof b === 'string') {
      return a.localeCompare(b);
    }
    return a > b ? 1 : -1;
  }
}
