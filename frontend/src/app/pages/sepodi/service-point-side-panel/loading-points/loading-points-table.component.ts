import { Component, OnInit } from '@angular/core';
import { Pages } from '../../../pages';
import { LoadingPointsService, ReadLoadingPointVersion } from '../../../../api';
import { ActivatedRoute, Router } from '@angular/router';
import { TableColumn } from '../../../../core/components/table/table-column';
import { Subject } from 'rxjs';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { takeUntil } from 'rxjs/operators';
import { TableService } from '../../../../core/components/table/table.service';
import { TableFilter } from '../../../../core/components/table-filter/config/table-filter';

@Component({
  selector: 'app-service-point-loading-points',
  templateUrl: './loading-points-table.component.html',
  styleUrls: ['./loading-points-table.component.scss'],
})
export class LoadingPointsTableComponent implements OnInit {
  tableColumns: TableColumn<ReadLoadingPointVersion>[] = [
    { headerTitle: 'SEPODI.LOADING_POINTS.NUMBER', value: 'number' },
    { headerTitle: 'SEPODI.LOADING_POINTS.DESIGNATION', value: 'designation' },
    { headerTitle: 'SEPODI.LOADING_POINTS.DESIGNATION_LONG', value: 'designationLong' },
    {
      headerTitle: 'SEPODI.LOADING_POINTS.CONNECTION_POINT',
      value: 'connectionPoint',
      translate: { withPrefix: 'SEPODI.LOADING_POINTS.CONNECTION_POINT_VALUE.' },
    },
    { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];
  tableFilterConfig!: TableFilter<unknown>[][];
  elements: ReadLoadingPointVersion[] = [];
  totalCount$ = 0;
  private ngUnsubscribe = new Subject<void>();

  constructor(
    private loadingPointsService: LoadingPointsService,
    private tableService: TableService,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnInit() {
    this.tableFilterConfig = this.tableService.initializeFilterConfig({}, Pages.LOADING_POINTS);
  }

  getOverview(pagination: TablePagination) {
    this.loadingPointsService
      .getLoadingPointOverview(this.servicePointNumber, pagination.page, pagination.size, [
        pagination.sort ?? 'designation,asc',
      ])
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((container) => {
        this.elements = container.objects!;
        this.totalCount$ = container.totalCount!;
      });
  }

  newLoadingPoint() {
    this.router
      .navigate([Pages.SEPODI.path, Pages.LOADING_POINTS.path, this.servicePointNumber, 'add'])
      .then();
  }

  editVersion($event: ReadLoadingPointVersion) {
    this.router
      .navigate([
        Pages.SEPODI.path,
        Pages.LOADING_POINTS.path,
        $event.servicePointNumber.number,
        $event.number,
      ])
      .then();
  }

  get servicePointNumber() {
    return this.route.parent!.snapshot.params['id'];
  }

  closeSidePanel() {
    this.router.navigate([Pages.SEPODI.path]).then();
  }
}
