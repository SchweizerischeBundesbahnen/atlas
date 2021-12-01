import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Record } from '../detail-wrapper/record';

@Component({
  selector: 'app-switch-version',
  templateUrl: './switch-version.component.html',
  styleUrls: ['./switch-version.component.scss'],
})
export class SwitchVersionComponent implements OnInit {
  @Input() records!: Array<Record>;
  @Input() currentRecord!: Record;
  @Output() switchVersion = new EventEmitter<number>();
  currentIndex: number;

  isArray: boolean | undefined;

  constructor() {
    this.currentIndex = 0;
  }

  ngOnInit(): void {
    this.getCurrentIndex();
    this.isArray = !!Array.isArray(this.records);
  }

  private getCurrentIndex() {
    this.records.forEach((record, index) => {
      if (record.id === this.currentRecord.id) {
        this.currentIndex = index;
      }
    });
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
}
