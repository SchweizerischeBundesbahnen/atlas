import { Component, EventEmitter, Input, OnChanges, Output } from '@angular/core';
import { Record } from '../detail-wrapper/record';
import { DateService } from '../../date/date.service';
import { Page } from '../../model/page';

@Component({
  selector: 'app-switch-version',
  templateUrl: './switch-version.component.html',
  styleUrls: ['./switch-version.component.scss'],
})
export class SwitchVersionComponent implements OnChanges {
  @Input() records!: Array<Record>;
  @Input() currentRecord!: Record;
  @Input() pageType!: Page;
  @Input() recordTitle: string | undefined;
  @Input() switchDisabled = false;
  @Output() switchVersion = new EventEmitter<number>();

  currentIndex: number;
  tableColumns = [
    { translationKey: 'COMMON.VERSION_DESCRIPTION', identifier: 'versionName' },
    { translationKey: 'COMMON.VALID_FROM', identifier: 'validFrom' },
    { translationKey: 'COMMON.VALID_TO', identifier: 'validTo' },
    { translationKey: 'COMMON.STATUS', identifier: 'status' },
  ];

  constructor() {
    this.currentIndex = 0;
  }

  ngOnChanges() {
    this.records.map((item, index) => (item.versionName = `Version ${index + 1}`));
    this.getCurrentIndex();
  }

  get columnValues() {
    return this.tableColumns.map((el) => el.identifier);
  }

  formatDate(date: Date | undefined) {
    return DateService.getDateFormatted(date);
  }

  format(input: Date | undefined, column: { translationKey: string; identifier: string }) {
    if (
      this.tableColumns
        .filter((el) => el.identifier === 'validFrom' || el.identifier === 'validTo')
        .includes(column)
    ) {
      return this.formatDate(input);
    }
    return input;
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
