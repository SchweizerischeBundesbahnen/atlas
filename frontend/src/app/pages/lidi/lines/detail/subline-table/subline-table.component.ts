import {Component, Input, OnInit} from '@angular/core';
import { TableColumn } from '../../../../../core/components/table/table-column';
import { ElementType, Line, LinesService } from '../../../../../api';
import { TableFilter } from '../../../../../core/components/table-filter/config/table-filter';
import { Pages } from '../../../../pages';
import { Router } from '@angular/router';

@Component({
  selector: 'app-subline-table',
  templateUrl: './subline-table.component.html',
})
export class SublineTableComponent implements OnInit {
  @Input() mainLineSlnid!: string;

  tableColumns: TableColumn<Line>[] = [
    { headerTitle: 'LIDI.SUBLINE.DESCRIPTION', value: 'description' },
    { headerTitle: 'LIDI.SLNID', value: 'slnid' },
    {
      headerTitle: 'COMMON.VALID_FROM',
      value: 'validFrom',
      formatAsDate: true,
    },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];
  tableFilterConfig!: TableFilter<unknown>[][];
  sublines: Array<Line> = [];

  constructor(
    private linesService: LinesService,
    private router: Router
  ) {}

  ngOnInit() {
    this.getOverview();
  }

  rowClicked(subline: Line) {
    const url = this.router.serializeUrl(
      this.router.createUrlTree([
        Pages.LIDI.path,
        Pages.SUBLINES.path,
        subline.slnid,
      ])
    );
    window.open(url, '_blank');
  }

  getOverview() {
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
        50
      )
      .subscribe((sublines) => {
        this.sublines = sublines.objects!;
      });
  }
}
