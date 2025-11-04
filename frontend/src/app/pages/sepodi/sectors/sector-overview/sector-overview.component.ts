import { Component, inject, OnInit } from '@angular/core';
import { SectorInternalService } from '../../../../api/service/sepodi/sector-internal.service';
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
import { ReadSectorVersion } from '../../../../api/model/readSectorVersion';

@Component({
  selector: 'app-sector-overview',
  imports: [
    TranslatePipe,
    DetailPageContentComponent,
    TableComponent,
    AtlasButtonComponent,
    DetailFooterComponent,
  ],
  templateUrl: './sector-overview.component.html',
  styleUrls: ['./sector-overview.component.scss'],
})
export class SectorOverviewComponent implements OnInit {
  sectorInternalService = inject(SectorInternalService);
  tableService = inject(TableService);
  router = inject(Router);
  route = inject(ActivatedRoute);
  sectorPermissionService = inject(SectorPermissionService);

  trafficPointSloid!: string;

  sectors: ReadSectorVersion[] = [];
  totalSectors = 0;

  tableFilterConfig!: TableFilter<unknown>[][];
  tableColumnsSectors: TableColumn<ReadSectorVersion>[] = [
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

  ngOnInit() {
    this.tableFilterConfig = this.tableService.initializeFilterConfig(
      {},
      Pages.SECTORS
    );
    this.trafficPointSloid =
      this.route.parent!.snapshot.params['trafficPointSloid']!;
    this.showCreateButtons = this.sectorPermissionService.showCreateButton(
      this.route.parent!.snapshot.data.servicePoint
    );
  }

  editSector(clickedRow: ReadSectorVersion) {
    this.router.navigate([clickedRow.sloid], { relativeTo: this.route }).then();
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

  backToServicePoint() {
    this.router.navigate(['../..'], { relativeTo: this.route }).then();
  }

  addSector() {
    this.router.navigate(['add'], { relativeTo: this.route }).then();
  }
}
