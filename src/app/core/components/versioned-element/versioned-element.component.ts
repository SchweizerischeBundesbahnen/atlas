import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Record } from '../detail-wrapper/record';
import { DateService } from '../../date/date.service';
import { Page } from '../../model/page';
import { Pages } from '../../../pages/pages';

@Component({
  selector: 'app-versions-display',
  templateUrl: './versioned-element.component.html',
  styleUrls: ['./versioned-element.component.scss'],
})
export class VersionedElementComponent {
  @Input() records!: Array<Record>;
  @Input() currentRecord!: Record;
  @Input() pageType!: Page;
  @Input() recordTitle: string | undefined;
  @Output() switchVersion = new EventEmitter<number>();

  currentIndex: number;

  constructor() {
    this.currentIndex = 0;
  }

  displayPageTypeTitle() {
    this.getCurrentIndex();

    if (this.pageType === Pages.TTFN) {
      return Pages.TTFN.title;
    }
    if (this.pageType === Pages.LINES) {
      return 'LIDI.LINES';
    }
    if (this.pageType === Pages.SUBLINES) {
      return 'LIDI.SUBLINES';
    }
    return '';
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

  setCurrentRecord(clickedRecord: Record) {
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
