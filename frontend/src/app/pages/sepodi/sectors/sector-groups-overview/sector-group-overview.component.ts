import { Component, inject, OnInit } from '@angular/core';
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
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { SectorPermissionService } from '../sector-permission.service';
import { SectorInternalService } from '../../../../api/service/sepodi/sector-internal.service';

@Component({
  selector: 'app-sector-group-overview',
  imports: [
    TranslatePipe,
    DetailPageContentComponent,
    TableComponent,
    AtlasButtonComponent,
    DetailFooterComponent,
  ],
  templateUrl: './sector-group-overview.component.html',
  styleUrls: ['./sector-group-overview.component.scss'],
})
export class SectorGroupOverviewComponent implements OnInit {
  sectorGroupInternalService = inject(SectorGroupInternalService);
  sectorInternalService = inject(SectorInternalService);
  tableService = inject(TableService);
  router = inject(Router);
  route = inject(ActivatedRoute);
  sectorPermissionService = inject(SectorPermissionService);

  trafficPointSloid!: string;

  sectorGroups: SectorGroupVersion[] = [];
  totalSectorGroups = 0;

  tableFilterConfig!: TableFilter<unknown>[][];

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

  showCreateButtons = false;
  hasAtLeastTwoSectors = false;

  ngOnInit() {
    this.tableFilterConfig = this.tableService.initializeFilterConfig(
      {},
      Pages.SECTOR_GROUPS
    );
    this.trafficPointSloid =
      this.route.parent!.snapshot.params['trafficPointSloid']!;

    this.showCreateButtons = this.sectorPermissionService.showCreateButton(
      this.route.parent!.snapshot.data.servicePoint
    );

    this.sectorInternalService
      .getSectors(this.trafficPointSloid)
      .subscribe((sectors) => {
        this.hasAtLeastTwoSectors = sectors.totalCount! >= 2;
      });
  }

  editSectorGroup(clickedRow: SectorGroupVersion) {
    this.router
      .navigate([clickedRow.sloid], {
        relativeTo: this.route,
      })
      .then();
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

  addSectorGroup() {
    this.router.navigate(['add'], { relativeTo: this.route }).then();
  }

  backToServicePoint() {
    this.router.navigate(['../..'], { relativeTo: this.route }).then();
  }
}
