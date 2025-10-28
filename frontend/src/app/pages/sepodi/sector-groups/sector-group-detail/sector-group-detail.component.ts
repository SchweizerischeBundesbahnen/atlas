import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { DateRangeComponent } from '../../../../core/form-components/date-range/date-range.component';
import { DateRangeTextComponent } from '../../../../core/versioning/date-range-text/date-range-text.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../core/components/detail-page-content/detail-page-content.component';
import { MatDivider } from '@angular/material/divider';
import { SwitchVersionComponent } from '../../../../core/components/switch-version/switch-version.component';
import { TextFieldComponent } from '../../../../core/form-components/text-field/text-field.component';
import { TranslatePipe } from '@ngx-translate/core';
import { UserDetailInfoComponent } from '../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import { DetailFormComponent } from '../../../../core/leave-guard/leave-dirty-form-guard.service';
import {
  DetailDialogHelperService,
  DetailWithCancelEdit,
} from '../../../../core/detail/detail-dialog-helper.service';
import { ActivatedRoute, Data, Router } from '@angular/router';
import { ValidityService } from '../../validity/validity.service';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DateRange } from '../../../../core/versioning/date-range';
import {
  ReadServicePointVersion,
  ReadTrafficPointElementVersion,
} from '../../../../api';
import { FormGroup } from '@angular/forms';
import { VersionsHandlingService } from '../../../../core/versioning/versions-handling.service';
import { ValidationService } from '../../../../core/validation/validation.service';
import { catchError, EMPTY } from 'rxjs';
import { SectorGroupService } from '../../../../api/service/sepodi/sector-group.service';
import { SectorGroupVersion } from '../../../../api/model/sectorGroupVersion';
import {
  SectorGroupDetailFormGroup,
  SectorGroupFormGroupBuilder,
} from './sector-group-detail-form-group';
import { ReadSectorVersion } from '../../../../api/model/readSectorVersion';
import { TableComponent } from '../../../../core/components/table/table.component';
import { Pages } from '../../../pages';
import { TableColumn } from '../../../../core/components/table/table-column';
import { TableFilter } from '../../../../core/components/table-filter/config/table-filter';
import { CreateSectorGroupVersion } from '../../../../api/model/createSectorGroupVersion';
import {
  DisplayableSector,
  SectorMapService,
} from '../../map/sector-map.service';
import { TrafficPointMapService } from '../../map/traffic-point-map.service';
import { SelectComponent } from '../../../../core/form-components/select/select.component';
import { SectorInternalService } from '../../../../api/service/sepodi/sector-internal.service';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { AtlasFieldErrorComponent } from '../../../../core/form-components/atlas-field-error/atlas-field-error.component';

@Component({
  selector: 'app-sector-group-detail',
  imports: [
    AtlasButtonComponent,
    DateRangeComponent,
    DateRangeTextComponent,
    DetailFooterComponent,
    DetailPageContainerComponent,
    DetailPageContentComponent,
    MatDivider,
    SwitchVersionComponent,
    TextFieldComponent,
    TranslatePipe,
    UserDetailInfoComponent,
    TableComponent,
    SelectComponent,
    AtlasLabelFieldComponent,
    AtlasFieldErrorComponent,
  ],
  templateUrl: './sector-group-detail.component.html',
  styleUrls: ['./sector-group-detail.component.scss'],
  providers: [ValidityService],
})
export class SectorGroupDetailComponent
  implements DetailFormComponent, DetailWithCancelEdit, OnInit, OnDestroy
{
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly sectorMapService = inject(SectorMapService);
  private readonly trafficPointMapService = inject(TrafficPointMapService);
  private readonly validityService = inject(ValidityService);
  private readonly detailHelperService = inject(DetailDialogHelperService);
  private readonly notificationService = inject(NotificationService);
  private readonly sectorGroupService = inject(SectorGroupService);
  private readonly sectorInternalService = inject(SectorInternalService);

  tableColumns: TableColumn<ReadSectorVersion>[] = [
    { headerTitle: 'SEPODI.SECTORS.DESIGNATION', value: 'designation' },
    { headerTitle: 'SEPODI.SERVICE_POINTS.SLOID', value: 'sloid' },
    {
      headerTitle: 'COMMON.VALID_FROM',
      value: 'validFrom',
      formatAsDate: true,
    },
    { headerTitle: 'COMMON.VALID_TO', value: 'validTo', formatAsDate: true },
  ];
  tableFilterConfig!: TableFilter<unknown>[][];

  sectorGroupVersions!: SectorGroupVersion[];
  selectedVersion!: SectorGroupVersion;
  selectedVersionIndex!: number;
  sectorVersions: ReadSectorVersion[] = [];
  allSectorVersionsOfTrafficPoint: ReadSectorVersion[] = [];
  empty: string[] = ['It is empty'];
  maxValidity!: DateRange;
  servicePointDesignationOfficial!: string;
  trafficPoint!: ReadTrafficPointElementVersion;
  servicePointNumber!: number;

  isNew = false;
  form!: FormGroup<SectorGroupDetailFormGroup>;
  servicePointBusinessOrganisations: string[] = [];

  readonly displayExtractor = (option: ReadSectorVersion) =>
    `${option.designation} - ${option.sloid}`;

  readonly extractSloid = (option: ReadSectorVersion) => option.sloid;

  ngOnInit() {
    this.route.data.subscribe((next) => {
      this.sectorGroupVersions = next.sectorGroup;
      this.initHeaderWithParentInfo(next);

      if (this.sectorGroupVersions.length == 0) {
        this.isNew = true;
        this.form = SectorGroupFormGroupBuilder.buildFormGroupCreate();
      } else {
        this.isNew = false;
        VersionsHandlingService.addVersionNumbers(this.sectorGroupVersions);
        this.maxValidity = VersionsHandlingService.getMaxValidity(
          this.sectorGroupVersions
        );
        this.selectedVersion =
          VersionsHandlingService.determineDefaultVersionByValidity(
            this.sectorGroupVersions
          );
        this.initSelectedVersion();
      }
      this.form.controls.trafficPointSloid.setValue(this.trafficPoint.sloid);
    });
  }

  ngOnDestroy() {
    this.sectorMapService.clearDisplayedSectors();
  }

  private initSelectedVersion(): void {
    this.form = SectorGroupFormGroupBuilder.buildFormGroupUpdate(
      this.selectedVersion
    );
    this.selectedVersionIndex = this.sectorGroupVersions.indexOf(
      this.selectedVersion
    );
    this.form.disable();
  }

  private initHeaderWithParentInfo(next: Data) {
    const servicePoint: ReadServicePointVersion[] = next.servicePoint;
    const servicePointVersion =
      VersionsHandlingService.determineDefaultVersionByValidity(servicePoint);
    this.servicePointNumber = servicePointVersion.number.number;

    this.servicePointDesignationOfficial =
      servicePointVersion.designationOfficial;
    this.servicePointBusinessOrganisations = servicePoint.map(
      (i) => i.businessOrganisation
    );
    this.trafficPointMapService.displayTrafficPointsOnMap(
      this.servicePointNumber
    );
    const trafficPoint: ReadTrafficPointElementVersion[] = next.trafficPoint;
    this.trafficPoint =
      VersionsHandlingService.determineDefaultVersionByValidity(trafficPoint);
    this.getSectors(this.trafficPoint.sloid!);
  }

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.sectorGroupVersions[newIndex];
    this.initSelectedVersion();
  }

  back() {
    this.router.navigate(['..'], { relativeTo: this.route }).then();
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.detailHelperService.showCancelEditDialog(this);
    } else {
      this.validityService.initValidity(this.form);
      this.form.enable();
    }
  }

  save() {
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      const sectorVersion =
        this.form.getRawValue() as unknown as CreateSectorGroupVersion;
      this.form.disable();
      if (this.isNew) {
        this.create(sectorVersion);
      } else {
        this.validityService.updateValidity(this.form);
        this.validityService.validate().subscribe((confirmed) => {
          if (confirmed) {
            this.form.disable();
            this.update(this.selectedVersion.id!, sectorVersion);
          } else {
            this.form.enable();
          }
        });
      }
    }
  }

  private create(sectorVersion: CreateSectorGroupVersion): void {
    this.sectorGroupService
      .createSectorGroup(sectorVersion)
      .pipe(catchError(this.handleError()))
      .subscribe((version) => {
        this.notificationService.success(
          'SEPODI.SECTORS.NOTIFICATION.ADD_SUCCESS'
        );
        this.router
          .navigate(['..', version.sloid], { relativeTo: this.route })
          .then(() => this.ngOnInit());
      });
  }

  private update(id: number, sectorGroupVersion: SectorGroupVersion): void {
    this.sectorGroupService
      .updateSectorGroup(id, sectorGroupVersion)
      .pipe(catchError(this.handleError()))
      .subscribe(() => {
        this.notificationService.success(
          'SEPODI.SECTORS.NOTIFICATION.EDIT_SUCCESS'
        );
        this.router
          .navigate(['..', this.selectedVersion.sloid], {
            relativeTo: this.route,
          })
          .then(() => this.ngOnInit());
      });
  }

  private handleError() {
    return () => {
      this.form.enable();
      return EMPTY;
    };
  }

  rowClicked(sectorVersion: ReadSectorVersion) {
    const url = this.router.serializeUrl(
      this.router.createUrlTree([
        Pages.SEPODI.path,
        Pages.SERVICE_POINTS.path,
        this.servicePointNumber,
        Pages.TRAFFIC_POINT_ELEMENTS_PLATFORM.path,
        this.trafficPoint.sloid,
        Pages.SECTORS.path,
        sectorVersion.sloid,
      ])
    );
    window.open(url, '_blank');
  }

  getOverview(sectorGroupSloid: string) {
    this.sectorGroupService
      .getSectorsBySectorGroupSloid(sectorGroupSloid)
      .subscribe((sectorVersions) => {
        this.sectorVersions = sectorVersions;
        const sectors: DisplayableSector[] = sectorVersions.map((sector) => {
          return {
            sloid: sector.sloid!,
            designation: sector.designation,
            coordinates: sector.sectorGeolocation!.wgs84,
            trafficPointSloid: sector.trafficPointSloid,
            servicePointNumber: this.servicePointNumber,
          };
        });

        this.sectorMapService.setDisplayedSectors(sectors);
      });
  }

  getSectors(trafficPointSloid: string) {
    this.sectorInternalService
      .getSectors(trafficPointSloid)
      .subscribe((sectors) => {
        this.allSectorVersionsOfTrafficPoint = sectors.objects ?? [];
      });
  }
}
