import { Component, EventEmitter, Input, Output } from '@angular/core';
import { DateService } from '../../date/date.service';
import { TableColumn } from '../table/table-column';

@Component({
  selector: 'app-relation',
  templateUrl: './relation.component.html',
  styleUrls: ['./relation.component.scss'],
})
export class RelationComponent<RECORD_TYPE> {
  @Input() records: RECORD_TYPE[] = [];
  @Input() titleTranslationKey = '';
  @Input() editable = false;
  @Input() tableColumns!: TableColumn<RECORD_TYPE>[];
  @Input() editMode = false;

  @Output() deleteRelation = new EventEmitter<void>();
  @Output() editModeChanged = new EventEmitter<void>();
  @Output() selectedIndexChanged = new EventEmitter<number>();

  @Input() selectedIndex = -1;

  columnValues(): string[] {
    return this.tableColumns.map((item) => item.columnDef!);
  }

  formatDate(date: Date): string {
    return DateService.getDateFormatted(date);
  }

  getValue(row: RECORD_TYPE, column: TableColumn<RECORD_TYPE>): string | Date | number {
    if (column.formatAsDate) {
      return this.formatDate(
        this.readValueFromObject(row, column.value ?? column.valuePath!) as Date
      );
    }
    return this.readValueFromObject(row, column.value ?? column.valuePath!);
  }

  private readValueFromObject(obj: RECORD_TYPE, path: string): string | Date | number {
    const objectPath = path.split('.');
    return objectPath.reduce((prev, curr) => prev[curr], obj as any);
  }

  isRowSelected(row: RECORD_TYPE): boolean {
    return this.selectedIndex === this.records.indexOf(row);
  }

  selectRecord(record: RECORD_TYPE): void {
    if (this.editable) {
      this.selectedIndexChanged.emit(this.records.indexOf(record));
    }
  }

  sortChanged(sortData: any): void {
    console.log(sortData);
  }
}
