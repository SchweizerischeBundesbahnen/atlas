import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TableColumn } from '../table/table-column';
import { DateService } from '../../date/date.service';

@Component({
  selector: 'app-relation',
  templateUrl: './relation.component.html',
  styleUrls: ['./relation.component.scss'],
})
export class RelationComponent<RECORD_TYPE extends { [prop: string]: any }> {
  @Input() records: RECORD_TYPE[] = [];
  @Input() titleTranslationKey = '';
  @Input() editable = false;
  @Input() tableColumns: TableColumn<RECORD_TYPE>[] = [];

  @Output() createRelation = new EventEmitter<void>();
  @Output() deleteRelation = new EventEmitter<{ record: RECORD_TYPE; callbackFn: () => void }>();

  private selectedIndex = 0;

  columnValues(): string[] {
    return this.tableColumns.map((item) => item.value as string);
  }

  test(row: RECORD_TYPE, path: string): any {
    const splitted = path.split('.');
    let currentValAccessor = row;
    splitted.forEach(
      (pathProp) =>
        (currentValAccessor = currentValAccessor ? currentValAccessor[pathProp] : undefined)
    );
    return currentValAccessor;
  }

  formatDate(date: Date): string {
    return DateService.getDateFormatted(date);
  }

  isRowSelected(row: RECORD_TYPE): boolean {
    return this.selectedIndex === this.records.indexOf(row);
  }

  selectRecord(record: RECORD_TYPE): void {
    if (this.editable) {
      this.selectedIndex = this.records.indexOf(record);
    }
  }

  delete(): void {
    if (this.records.length === 0) return;

    this.deleteRelation.emit({
      record: this.records[this.selectedIndex],
      callbackFn: () => (this.selectedIndex = 0),
    });
  }

  create(): void {
    this.createRelation.emit();
  }
}
