import { Component, EventEmitter, Input, OnChanges, OnInit, Output } from '@angular/core';
import { Record } from '../base-detail/record';
import { DateService } from '../../date/date.service';
import { TableColumn } from '../table/table-column';
import { Status } from '../../../api';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-switch-version',
  templateUrl: './switch-version.component.html',
  styleUrls: ['./switch-version.component.scss'],
})
export class SwitchVersionComponent implements OnInit, OnChanges {
  @Input() records!: Array<Record>;
  @Input() currentRecord!: Record;
  @Input() switchDisabled = false;
  @Input() showStatus = true;
  @Output() switchVersion = new EventEmitter<number>();

  currentIndex: number;
  tableColumns: TableColumn<Record>[] = [];

  constructor(private readonly translatePipe: TranslatePipe) {
    this.currentIndex = 0;
  }

  ngOnInit() {
    this.tableColumns = [
      {
        headerTitle: 'VERSION_TABLE.VERSION_DESCRIPTION',
        value: 'versionNumber',
        translate: { withKey: 'COMMON.VERSION' },
      },
      { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
      { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
    ];
    if (this.showStatus) {
      this.tableColumns = [
        ...this.tableColumns,
        {
          headerTitle: 'COMMON.STATUS',
          value: 'status',
          translate: { withPrefix: 'COMMON.STATUS_TYPES.' },
        },
      ];
    }
  }

  ngOnChanges() {
    this.getCurrentIndex();
  }

  get columnValues() {
    return this.tableColumns.map((item) => item.value);
  }

  formatDate(date: Date | undefined) {
    return DateService.getDateFormatted(date);
  }

  format(input: Date | Status | number | undefined, column: TableColumn<Record>): string | null {
    if (column.formatAsDate) {
      return this.formatDate(input as Date);
    }
    if (column.translate?.withKey) {
      return `${this.translatePipe.transform(column.translate.withKey)} ${input}`;
    }
    if (column.translate?.withPrefix) {
      return this.translatePipe.transform(column.translate.withPrefix + input);
    }
    return null;
  }

  setCurrentRecord(clickedRecord: Record) {
    if (this.switchDisabled) return;
    this.currentIndex = this.getIndexOfRecord(clickedRecord);
    this.switchVersion.emit(this.currentIndex);
  }

  isCurrentRecord(record: Record): boolean {
    return this.currentIndex == this.getIndexOfRecord(record);
  }

  getIndexOfRecord(record: Record) {
    return this.records.findIndex((element) => element === record);
  }

  hasGapToNextRecord(record: Record): boolean {
    const nextRecord = this.records[this.getIndexOfRecord(record) + 1];
    if (nextRecord) {
      return DateService.differenceInDays(record.validTo!, nextRecord.validFrom!) > 1;
    }
    return false;
  }

  getCurrentIndex() {
    this.records.forEach((record, index) => {
      if (record.id === this.currentRecord.id) {
        this.currentIndex = index;
      }
    });
  }
}
