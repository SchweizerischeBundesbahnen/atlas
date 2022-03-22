import {
  AfterContentChecked,
  AfterContentInit,
  AfterViewChecked,
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { Record } from '../detail-wrapper/record';
import { DateService } from '../../date/date.service';
import { Page } from '../../model/page';
import { Pages } from '../../../pages/pages';
import { Status } from '../../../api';

@Component({
  selector: 'app-switch-version',
  templateUrl: './switch-version.component.html',
  styleUrls: ['./switch-version.component.scss'],
})
export class SwitchVersionComponent implements AfterContentChecked {
  @Input() records!: Array<Record>;
  @Input() currentRecord!: Record;
  @Input() pageType!: Page;
  @Input() recordTitle: string | undefined;
  @Output() switchVersion = new EventEmitter<number>();

  currentIndex: number;
  tableColumns = [
    { translationKey: 'COMMON.VERSION_DESCRIPTION', identifier: 'description' },
    { translationKey: 'COMMON.VALID_FROM', identifier: 'validFrom' },
    { translationKey: 'COMMON.VALID_TO', identifier: 'validTo' },
    { translationKey: 'COMMON.STATUS', identifier: 'status' },
  ];

  constructor() {
    this.currentIndex = 0;
  }

  // TODO: ngOnChanges
  ngAfterContentChecked() {
    for (let i = 0; i < this.records.length; i++) {
      if (this.hasGapToNextRecord(this.records[i])) {
        this.records.splice(i + 1, 0, { placeholder: true });
        i++;
      }
    }
    this.records
      .filter((item) => !item.placeholder)
      .map((item, index) => (item.description = `Version ${index + 1}`));
  }

  get columnValues() {
    return this.tableColumns.map((el) => el.identifier);
  }

  getStartDate() {
    return this.formatDate(this.records[0].validFrom);
  }

  getEndDate() {
    return this.formatDate(this.records[this.records.length - 1].validTo);
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
    if (clickedRecord.placeholder) return;
    this.currentIndex = this.getIndexOfRecord(clickedRecord);
    this.switchVersion.emit(this.currentIndex);
  }

  isCurrentRecord(record: Record): boolean {
    this.getCurrentIndex();
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
