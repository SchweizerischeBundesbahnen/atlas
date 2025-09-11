import { Component, inject, OnInit } from '@angular/core';
import { SectorInternalService } from '../../../../api/service/sepodi/sector-internal.service';
import { SectorVersion } from '../../../../api/model/sectorVersion';
import { SectorGroupInternalService } from '../../../../api/service/sepodi/sector-group-internal.service';
import { SectorGroupVersion } from '../../../../api/model/sectorGroupVersion';
import { TranslatePipe } from '@ngx-translate/core';
import { DetailPageContentComponent } from '../../../../core/components/detail-page-content/detail-page-content.component';
import { TableComponent } from '../../../../core/components/table/table.component';
import { TableColumn } from '../../../../core/components/table/table-column';
import { TableFilter } from '../../../../core/components/table-filter/config/table-filter';
import { Pages } from '../../../pages';
import { TableService } from '../../../../core/components/table/table.service';
import { TablePagination } from '../../../../core/components/table/table-pagination';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-sector-overview',
  imports: [TranslatePipe, DetailPageContentComponent, TableComponent],
  templateUrl: './sector-overview.html',
})
export class SectorOverview implements OnInit {
  sectorInternalService = inject(SectorInternalService);
  sectorGroupInternalService = inject(SectorGroupInternalService);
  tableService = inject(TableService);
  router = inject(Router);
  route = inject(ActivatedRoute);

  trafficPointSloid!: string;

  sectors: SectorVersion[] = [];
  totalSectors = 0;
  sectorGroups: SectorGroupVersion[] = [];
  totalSectorGroups = 0;

  tableFilterConfig!: TableFilter<unknown>[][];
  tableColumnsSectors: TableColumn<SectorVersion>[] = [
    {
      headerTitle: 'SEPODI.SECTORS.DESIGNATION',
      value: 'designation',
    },
    { headerTitle: 'SEPODI.SERVICE_POINTS.SLOID', value: 'sloid' },
    {
      headerTitle: 'COMMON.VALID_FROM',
      value: 'validFrom',
      formatAsDate: true,
    },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  tableColumnsSectorGroups: TableColumn<SectorGroupVersion>[] = [
    {
      headerTitle: 'SEPODI.SECTORS.DESIGNATION',
      value: 'designation',
    },
    { headerTitle: 'SEPODI.SERVICE_POINTS.SLOID', value: 'sloid' },
    {
      headerTitle: 'COMMON.VALID_FROM',
      value: 'validFrom',
      formatAsDate: true,
    },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];

  ngOnInit() {
    this.tableFilterConfig = this.tableService.initializeFilterConfig(
      {},
      Pages.SECTORS
    );
    this.trafficPointSloid =
      this.route.parent!.snapshot.params['trafficPointSloid']!;
  }

  editSector(clickedRow: SectorVersion) {
    this.router.navigate([clickedRow.sloid]).then();
  }

  getSectorOverview(pagination: TablePagination) {
    this.sectorInternalService
      .getSectors(this.trafficPointSloid, pagination.page, pagination.size, [
        pagination.sort ?? 'designation,asc',
      ])
      .subscribe((sectors) => {
        this.sectors = sectors.objects!;
        this.totalSectors = sectors.totalCount!;
      });
  }

  editSectorGroup(clickedRow: SectorGroupVersion) {
    this.router.navigate(['../sector-groups', clickedRow.sloid]).then();
  }

  getSectorGroupOverview(pagination: TablePagination) {
    this.sectorGroupInternalService
      .getSectorGroups(
        this.trafficPointSloid,
        pagination.page,
        pagination.size,
        [pagination.sort ?? 'designation,asc']
      )
      .subscribe((sectorGroups) => {
        this.sectorGroups = sectorGroups.objects!;
        this.totalSectorGroups = sectorGroups.totalCount!;
      });
  }
}
