import {
  ChangeDetectionStrategy,
  AfterViewInit,
  Component,
  ElementRef,
  inject,
  input,
  OnChanges,
  OnInit,
  output,
  QueryList,
  ViewChildren,
} from '@angular/core';
import { Record } from '../../model/record';
import { DateService } from '../../date/date.service';
import { TableColumn } from '../table/table-column';
import { Status } from '../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
} from '@angular/material/table';
import { NgClass } from '@angular/common';

@Component({
  selector: 'atlas-switch-version',
  templateUrl: './switch-version.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./switch-version.component.scss'],
  imports: [
    MatTable,
    MatColumnDef,
    MatHeaderCellDef,
    MatHeaderCell,
    MatCellDef,
    MatCell,
    MatHeaderRowDef,
    MatHeaderRow,
    MatRowDef,
    MatRow,
    NgClass,
    TranslatePipe,
  ],
  providers: [TranslatePipe],
})
export class SwitchVersionComponent implements OnInit, OnChanges, AfterViewInit {
  readonly records = input.required<Array<Record>>();
  readonly currentRecord = input.required<Record>();
  readonly switchDisabled = input(false);
  readonly showStatus = input(true);
  readonly switchVersion = output<number>();

  @ViewChildren(MatRow, { read: ElementRef }) versionRows!: QueryList<ElementRef<HTMLTableRowElement>>;

  tableColumns: TableColumn<Record>[] = [];
  private readonly translatePipe = inject(TranslatePipe);
  currentIndex: number = 0;

  ngOnInit() {
    this.tableColumns = [
      {
        headerTitle: 'VERSION_TABLE.VERSION_DESCRIPTION',
        value: 'versionNumber',
        translate: { withKey: 'COMMON.VERSION' },
      },
      {
        headerTitle: 'COMMON.VALID_FROM',
        value: 'validFrom',
        formatAsDate: true,
      },
      { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
    ];
    if (this.showStatus()) {
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

  ngAfterViewInit() {
    this.scrollToCurrentRow();
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
    if (this.switchDisabled()) {
      return;
    }
    this.currentIndex = this.getIndexOfRecord(clickedRecord);
    this.switchVersion.emit(this.currentIndex);
  }

  scrollToCurrentRow() {
    this.versionRows?.get(this.currentIndex)?.nativeElement.scrollIntoView({
      behavior: 'instant',
      block: 'nearest',
      inline: 'center',
    });
  }

  isCurrentRecord(record: Record): boolean {
    return this.currentIndex == this.getIndexOfRecord(record);
  }

  getIndexOfRecord(record: Record) {
    return this.records().indexOf(record);
  }

  hasGapToNextRecord(record: Record): boolean {
    const nextRecord = this.records()[this.getIndexOfRecord(record) + 1];
    if (nextRecord) {
      return DateService.differenceInDays(record.validTo!, nextRecord.validFrom!) > 1;
    }
    return false;
  }

  getCurrentIndex() {
    this.records().forEach((record, index) => {
      if (record.id === this.currentRecord().id) {
        this.currentIndex = index;
        this.scrollToCurrentRow();
      }
    });
  }
}
