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
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { Countries } from '../../../../core/country/Countries';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import {
  ApplicationType,
  MeanOfTransport,
  ReadServicePointVersion,
} from '../../../../api';

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
  sectorGroupInternalService = inject(SectorGroupInternalService);
  tableService = inject(TableService);
  router = inject(Router);
  route = inject(ActivatedRoute);
  permissionService = inject(PermissionService);

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

  showCreateButtons = false;

  ngOnInit() {
    this.tableFilterConfig = this.tableService.initializeFilterConfig(
      {},
      Pages.SECTORS
    );
    this.trafficPointSloid =
      this.route.parent!.snapshot.params['trafficPointSloid']!;

    this.initCreateButtons();
  }

  editSector(clickedRow: SectorVersion) {
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

  editSectorGroup(clickedRow: SectorGroupVersion) {
    this.router
      .navigate(['../sector-groups', clickedRow.sloid], {
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

  backToServicePoint() {
    this.router.navigate(['..', '..'], { relativeTo: this.route }).then();
  }

  private initCreateButtons() {
    const servicePoint: ReadServicePointVersion[] =
      this.route.parent!.snapshot.data.servicePoint;

    const hasVersionWithMotTrain = servicePoint.some((i) =>
      i.meansOfTransport?.includes(MeanOfTransport.Train)
    );

    const hasPermissionsForOneVersion = servicePoint.some((i) => {
      return (
        this.permissionService.hasPermissionsToWrite(
          ApplicationType.Sepodi,
          i.businessOrganisation
        ) &&
        this.permissionService.hasPermissionsToWrite(
          ApplicationType.Sepodi,
          Countries.fromUicCode(i.number.uicCountryCode).enumCountry
        )
      );
    });

    this.showCreateButtons =
      hasVersionWithMotTrain && hasPermissionsForOneVersion;
  }
}
