import {Component, Input, OnInit} from '@angular/core';
import {TableColumn} from "../../../../../core/components/table/table-column";
import {ElementType, Line, LinesService} from "../../../../../api";
import {TablePagination} from "../../../../../core/components/table/table-pagination";
import {TableFilter} from "../../../../../core/components/table-filter/config/table-filter";

@Component({
  selector: 'app-subline-table',
  templateUrl: './subline-table.component.html',
})
export class SublineTableComponent implements OnInit {
  @Input() mainLineSlnid!: string;

  tableColumns: TableColumn<Line>[] = [
    { headerTitle: 'LIDI.LINE.NUMBER', value: 'number' },
    { headerTitle: 'LIDI.LINE.DESCRIPTION', value: 'description' },
    { headerTitle: 'LIDI.LINE.SLNID', value: 'slnid' },
  ];
  tableFilterConfig!: TableFilter<unknown>[][];
  sublines: Array<Line> = [];
  totalCount = 0;

  constructor(private linesService: LinesService) {}

  ngOnInit() {

  }

  rowClicked($event: Line) {

  }

  getOverview($event: TablePagination) {
    this.linesService
      .getLines(
        undefined,
        [this.mainLineSlnid + ':'],
        undefined,
        undefined,
        [ElementType.Subline],
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        0,
        50,
        undefined
      )
      .subscribe((sublines) => {
        this.sublines = sublines.objects!;
        this.totalCount = sublines.totalCount!;
      });
  }
}
