import { ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit, inject, input } from '@angular/core';
import { TableColumn } from '../../../../../core/components/table/table-column';
import { ElementType, Line } from '../../../../../api';
import { TableFilter } from '../../../../../core/components/table-filter/config/table-filter';
import { Pages } from '../../../../pages';
import { Router } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { TableComponent } from '../../../../../core/components/table/table.component';
import { TranslatePipe } from '@ngx-translate/core';
import { LineInternalService } from '../../../../../api/service/lidi/line-internal.service';

@Component({
  selector: 'atlas-subline-table',
  templateUrl: './subline-table.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [TableComponent, TranslatePipe],
})
export class SublineTableComponent implements OnInit, OnDestroy {
  readonly mainLineSlnid = input.required<string>();

  @Input() eventSubject!: Observable<boolean>;

  private onDestroy$ = new Subject<boolean>();

  tableColumns: TableColumn<Line>[] = [
    { headerTitle: 'LIDI.SUBLINE.DESCRIPTION', value: 'description' },
    { headerTitle: 'LIDI.SLNID', value: 'slnid' },
    {
      headerTitle: 'COMMON.VALID_FROM',
      value: 'validFrom',
      formatAsDate: true,
    },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
    {
      headerTitle: 'COMMON.STATUS',
      value: 'status',
      translate: { withPrefix: 'COMMON.STATUS_TYPES.' },
    },
  ];
  tableFilterConfig!: TableFilter<unknown>[][];
  sublines: Array<Line> = [];

  private readonly lineInternalService = inject(LineInternalService);
  private readonly router = inject(Router);

  ngOnInit() {
    this.getOverview();
    this.subscribeToParent();
  }

  subscribeToParent(): void {
    this.eventSubject.pipe(takeUntil(this.onDestroy$)).subscribe((refreshTable: boolean) => {
      if (refreshTable) {
        this.getOverview();
      }
    });
  }

  rowClicked(subline: Line) {
    const url = this.router.serializeUrl(
      this.router.createUrlTree([Pages.LIDI.path, Pages.SUBLINES.path, subline.slnid])
    );
    window.open(url, '_blank');
  }

  getOverview() {
    this.lineInternalService
      .getLines(
        undefined,
        [this.mainLineSlnid() + ':'],
        undefined,
        undefined,
        [ElementType.Subline],
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        0,
        50
      )
      .pipe(takeUntil(this.onDestroy$))
      .subscribe((sublines) => {
        this.sublines = sublines.objects!;
      });
  }

  ngOnDestroy(): void {
    this.onDestroy$.next(true);
    this.onDestroy$.complete();
  }
}
