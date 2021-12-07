import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Record } from '../detail-wrapper/record';
import moment from 'moment';
import { DATE_PATTERN } from '../../date/date.service';

@Component({
  selector: 'app-switch-version',
  templateUrl: './switch-version.component.html',
  styleUrls: ['./switch-version.component.scss'],
})
export class SwitchVersionComponent implements OnInit {
  @Input() records!: Array<Record>;
  @Input() currentRecord!: Record;
  @Input() type!: string;
  @Input() recordTitle: string | undefined;
  @Output() switchVersion = new EventEmitter<number>();

  currentIndex: number;

  constructor() {
    this.currentIndex = 0;
  }

  ngOnInit(): void {
    this.getCurrentIndex();
  }

  displayRecordType() {
    if (this.type === 'TTFN') {
      return 'PAGES.TTFN.TITLE';
    }
    return 'PAGES.LIDI.TITLE';
  }

  displayVersionsItems() {
    this.getCurrentIndex();
    return this.currentIndex + 1 + ' / ' + this.records.length;
  }

  switchLeft() {
    this.currentIndex = this.currentIndex - 1;
    this.changeSelected(this.currentIndex);
  }

  switchRight() {
    this.currentIndex = this.currentIndex + 1;
    this.changeSelected(this.currentIndex);
  }

  changeSelected(number: number) {
    this.switchVersion.emit(number);
  }

  isLeftSwitchDisabled() {
    return this.currentIndex === 0;
  }

  isRightSwitchDisabled() {
    return this.currentIndex === this.records.length - 1;
  }

  getInitialDataRage() {
    return this.formatDate(this.records[0].validFrom);
  }

  getEndDataRage() {
    return this.formatDate(this.records[this.records.length - 1].validTo);
  }

  getInitialCurrentDataRage() {
    return this.formatDate(this.currentRecord.validFrom);
  }

  getEndCurrentDataRage() {
    return this.formatDate(this.currentRecord.validTo);
  }

  private formatDate(date: Date | undefined) {
    return moment(date).format(DATE_PATTERN);
  }

  getCurrentIndex() {
    this.records.forEach((record, index) => {
      if (record.id === this.currentRecord.id) {
        this.currentIndex = index;
      }
    });
  }
}
