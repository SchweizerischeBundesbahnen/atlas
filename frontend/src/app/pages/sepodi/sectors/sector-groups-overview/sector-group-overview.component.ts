import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { of, Subject, switchMap } from 'rxjs';
import { SectorGroupInternalService } from '../../../../api/service/sepodi/sector-group-internal.service';
import { TranslatePipe } from '@ngx-translate/core';
import { DetailPageContentComponent } from '../../../../core/components/detail-page-content/detail-page-content.component';
import { TableComponent } from '../../../../core/components/table/table.component';
import { TableColumn } from '../../../../core/components/table/table-column';
import { TableFilter } from '../../../../core/components/table-filter/config/table-filter';
import { Pages } from '../../../pages';
import { TableService } from '../../../../core/components/table/table.service';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { ActivatedRoute, Router } from '@angular/router';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { SectorPermissionService } from '../sector-permission.service';
import { SectorInternalService } from '../../../../api/service/sepodi/sector-internal.service';
import { ReadSectorGroupVersion } from '../../../../api/model/readSectorGroupVersion';
import { SectorGroupService } from '../../../../api/service/sepodi/sector-group.service';
import { SectorMapService } from '../../map/sector-map.service';

@Component({
  selector: 'atlas-sector-group-overview',
  imports: [TranslatePipe, DetailPageContentComponent, TableComponent, AtlasButtonComponent, DetailFooterComponent],
  templateUrl: './sector-group-overview.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./sector-group-overview.component.scss'],
})
export class SectorGroupOverviewComponent implements OnInit, OnDestroy {
  private readonly sectorGroupInternalService = inject(SectorGroupInternalService);
  private readonly sectorGroupService = inject(SectorGroupService);
  private readonly sectorMapService = inject(SectorMapService);
  private readonly sectorInternalService = inject(SectorInternalService);
  private readonly tableService = inject(TableService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly sectorPermissionService = inject(SectorPermissionService);
  private readonly destroyRef = inject(DestroyRef);

  private readonly hoveredSectorGroupSloid$ = new Subject<string | null>();

  trafficPointSloid!: string;

  sectorGroups: ReadSectorGroupVersion[] = [];
  totalSectorGroups = 0;

  tableFilterConfig!: TableFilter<unknown>[][];

  tableColumnsSectorGroups: TableColumn<ReadSectorGroupVersion>[] = [
    {
      headerTitle: 'SEPODI.SECTORS.DESIGNATION',
      value: 'designation',
    },
    { headerTitle: 'SEPODI.SERVICE_POINTS.SLOID', value: 'sloid' },
    {
      headerTitle: 'COMMON.STATUS',
      value: 'status',
      translate: { withPrefix: 'COMMON.STATUS_TYPES.' },
    },
    {
      headerTitle: 'COMMON.VALID_FROM',
      value: 'validFrom',
      formatAsDate: true,
    },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  showCreateButtons = false;
  hasAtLeastTwoSectors = false;

  ngOnInit() {
    this.tableFilterConfig = this.tableService.initializeFilterConfig({}, Pages.SECTOR_GROUPS);
    this.trafficPointSloid = this.route.parent!.snapshot.params['trafficPointSloid']!;

    this.showCreateButtons = this.sectorPermissionService.showCreateButton(
      this.route.parent!.snapshot.data.servicePoint
    );

    this.sectorInternalService.getSectors(this.trafficPointSloid).subscribe((sectors) => {
      this.hasAtLeastTwoSectors = sectors.totalCount! >= 2;
    });

    this.hoveredSectorGroupSloid$
      .pipe(
        switchMap((sloid) => (sloid ? this.sectorGroupService.getSectorsBySectorGroupSloid(sloid) : of([]))),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe((sectors) => {
        this.sectorMapService.highlightSectors(
          sectors.map((sector) => sector.sectorGeolocation?.wgs84).filter((coordinates) => coordinates !== undefined)
        );
      });
  }

  editSectorGroup(clickedRow: ReadSectorGroupVersion) {
    this.router
      .navigate([clickedRow.sloid], {
        relativeTo: this.route,
      })
      .then();
  }

  onRowHovered(row: ReadSectorGroupVersion) {
    this.hoveredSectorGroupSloid$.next(row.sloid ?? null);
  }

  onRowHoverEnded() {
    this.hoveredSectorGroupSloid$.next(null);
  }

  ngOnDestroy() {
    this.sectorMapService.clearHighlightedSector();
  }

  getSectorGroupOverview(pagination: TablePagination) {
    this.sectorGroupInternalService
      .getSectorGroups(this.trafficPointSloid, pagination.page, pagination.size, [pagination.sort ?? 'designation,asc'])
      .subscribe((sectorGroups) => {
        this.sectorGroups = sectorGroups.objects!;
        this.totalSectorGroups = sectorGroups.totalCount!;
      });
  }

  addSectorGroup() {
    this.router.navigate(['add'], { relativeTo: this.route }).then();
  }

  backToServicePoint() {
    this.router.navigate(['../..'], { relativeTo: this.route }).then();
  }
}
