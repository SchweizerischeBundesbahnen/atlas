//TODO kann gelöscht werden
import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { ReadTrafficPointElementVersion, TrafficPointElementsService } from 'src/app/api';
import { TableColumn } from 'src/app/core/components/table/table-column';
import { TablePagination } from 'src/app/core/components/table/table-pagination';
import { Pages } from 'src/app/pages/pages';

@Component({
  selector: 'app-service-point-areas',
  templateUrl: './areas-detail.component.html',
  styleUrls: ['./areas-detail.component.scss'],
})
export class AreasDetailComponent implements OnInit, OnDestroy {
  tableColumns: TableColumn<ReadTrafficPointElementVersion>[] = [
    { headerTitle: 'SEPODI.TRAFFIC_POINT_ELEMENTS.DESIGNATION', value: 'designation' },
    { headerTitle: 'SEPODI.SERVICE_POINTS.SLOID', value: 'sloid' },
    { headerTitle: 'COMMON.VALID_FROM', value: 'validFrom', formatAsDate: true },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  trafficPointElementRows: ReadTrafficPointElementVersion[] = [];
  totalCount$ = 0;
  private ngUnsubscribe = new Subject<void>();

  get servicePointNumber() {
    return this.route.parent!.snapshot.params['id'];
  }

  constructor(
    private trafficPointElementService: TrafficPointElementsService,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  ngOnDestroy(): void {}

  ngOnInit(): void {}

  getOverview(pagination: TablePagination) {
    this.trafficPointElementService
      .getAreasOfServicePoint(this.servicePointNumber, pagination.page, pagination.size, [
        pagination.sort ?? 'designation,asc',
      ])
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((container) => {
        this.trafficPointElementRows = container.objects!;
        this.totalCount$ = container.totalCount!;
      });
  }

  newTrafficPointElement() {
    this.router
      .navigate([Pages.SEPODI.path, Pages.TRAFFIC_POINT_ELEMENTS.path, 'add'], {
        state: { servicePointNumber: this.servicePointNumber },
      })
      .then();
  }

  editVersion($event: ReadTrafficPointElementVersion) {
    this.router
      .navigate([Pages.SEPODI.path, Pages.TRAFFIC_POINT_ELEMENTS.path, $event.sloid], {
        state: { servicePointNumber: this.servicePointNumber },
      })
      .then();
  }

  closeSidePanel() {
    this.router.navigate([Pages.SEPODI.path]).then();
  }
}
