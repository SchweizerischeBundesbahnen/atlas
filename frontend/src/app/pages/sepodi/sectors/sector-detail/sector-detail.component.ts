import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Data, Router } from '@angular/router';
import { ValidityService } from '../../validity/validity.service';
import { ReadSectorVersion } from '../../../../api/model/readSectorVersion';
import { DateRange } from '../../../../core/versioning/date-range';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../core/components/detail-page-content/detail-page-content.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { DateRangeTextComponent } from '../../../../core/versioning/date-range-text/date-range-text.component';
import { TranslatePipe } from '@ngx-translate/core';
import { VersionsHandlingService } from '../../../../core/versioning/versions-handling.service';
import {
  ReadServicePointVersion,
  ReadTrafficPointElementVersion,
} from '../../../../api';
import { FormGroup } from '@angular/forms';
import {
  SectorDetailFormGroup,
  SectorFormGroupBuilder,
} from './sector-detail-form-group';
import { SwitchVersionComponent } from '../../../../core/components/switch-version/switch-version.component';
import { SectorMapService } from '../../map/sector-map.service';
import { TextFieldComponent } from '../../../../core/form-components/text-field/text-field.component';
import { DateRangeComponent } from '../../../../core/form-components/date-range/date-range.component';
import { GeographyComponent } from '../../geography/geography.component';
import { MatDivider } from '@angular/material/divider';
import { UserDetailInfoComponent } from '../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import {
  DetailHelperService,
  DetailWithCancelEdit,
} from '../../../../core/detail/detail-helper.service';
import { DetailFormComponent } from '../../../../core/leave-guard/leave-dirty-form-guard.service';
import { ValidationService } from '../../../../core/validation/validation.service';
import { CreateSectorVersion } from '../../../../api/model/createSectorVersion';
import { catchError, EMPTY } from 'rxjs';
import { SectorService } from '../../../../api/service/sepodi/sector.service';
import { NotificationService } from '../../../../core/notification/notification.service';
import { MapService } from '../../map/map.service';
import { filter } from 'rxjs/operators';
import { TrafficPointMapService } from '../../map/traffic-point-map.service';

@Component({
  selector: 'app-sector-detail',
  templateUrl: './sector-detail.component.html',
  styleUrls: ['./sector-detail.component.scss'],
  providers: [ValidityService],
  imports: [
    DetailPageContainerComponent,
    DetailPageContentComponent,
    DetailFooterComponent,
    DateRangeTextComponent,
    TranslatePipe,
    SwitchVersionComponent,
    TextFieldComponent,
    DateRangeComponent,
    GeographyComponent,
    MatDivider,
    UserDetailInfoComponent,
    AtlasButtonComponent,
  ],
})
export class SectorDetailComponent
  implements DetailFormComponent, DetailWithCancelEdit, OnInit, OnDestroy
{
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly sectorMapService = inject(SectorMapService);
  private readonly trafficPointMapService = inject(TrafficPointMapService);
  private readonly sectorService = inject(SectorService);
  private readonly validityService = inject(ValidityService);
  private readonly detailHelperService = inject(DetailHelperService);
  private readonly notificationService = inject(NotificationService);
  private readonly mapService = inject(MapService);

  sectorVersions!: ReadSectorVersion[];
  selectedVersion!: ReadSectorVersion;
  selectedVersionIndex!: number;
  maxValidity!: DateRange;
  servicePointDesignationOfficial!: string;
  trafficPoint!: ReadTrafficPointElementVersion;

  isNew = false;
  form!: FormGroup<SectorDetailFormGroup>;
  servicePointBusinessOrganisations: string[] = [];

  ngOnInit() {
    this.route.data.subscribe((next) => {
      this.sectorVersions = next.sector;
      this.initHeaderWithParentInfo(next);

      if (this.sectorVersions.length == 0) {
        this.isNew = true;
        this.form = SectorFormGroupBuilder.buildFormGroup();
        this.mapService.mapInitialized.pipe(filter((i) => i)).subscribe(() => {
          this.mapService.enterCoordinateSelectionMode();
        });
      } else {
        this.isNew = false;
        VersionsHandlingService.addVersionNumbers(this.sectorVersions);
        this.maxValidity = VersionsHandlingService.getMaxValidity(
          this.sectorVersions
        );
        this.selectedVersion =
          VersionsHandlingService.determineDefaultVersionByValidity(
            this.sectorVersions
          );
        this.initSelectedVersion();
      }
      this.form.controls.trafficPointSloid.setValue(this.trafficPoint.sloid);
    });
  }

  private initSelectedVersion(): void {
    this.form = SectorFormGroupBuilder.buildFormGroup(this.selectedVersion);
    this.selectedVersionIndex = this.sectorVersions.indexOf(
      this.selectedVersion
    );
    this.form.disable();
    this.sectorMapService.displayCurrentSector(
      this.selectedVersion.sectorGeolocation!.wgs84
    );
  }

  private initHeaderWithParentInfo(next: Data) {
    const servicePoint: ReadServicePointVersion[] = next.servicePoint;
    const servicePointVersion =
      VersionsHandlingService.determineDefaultVersionByValidity(servicePoint);
    this.servicePointDesignationOfficial =
      servicePointVersion.designationOfficial;
    this.servicePointBusinessOrganisations = servicePoint.map(
      (i) => i.businessOrganisation
    );
    this.trafficPointMapService.displayTrafficPointsOnMap(
      servicePointVersion.number.number
    );

    const trafficPoint: ReadTrafficPointElementVersion[] = next.trafficPoint;
    this.trafficPoint =
      VersionsHandlingService.determineDefaultVersionByValidity(trafficPoint);
    this.sectorMapService.displaySectorsOnMap(
      servicePointVersion.number.number,
      this.trafficPoint.sloid!
    );
  }

  ngOnDestroy() {
    this.sectorMapService.clearDisplayedSectors();
    this.sectorMapService.clearCurrentSector();
    this.trafficPointMapService.clearDisplayedTrafficPoints();
  }

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.sectorVersions[newIndex];
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
        this.form.getRawValue() as unknown as CreateSectorVersion;
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

  private create(sectorVersion: CreateSectorVersion): void {
    this.sectorService
      .createSector(sectorVersion)
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

  private update(id: number, sublineVersion: CreateSectorVersion): void {
    this.sectorService
      .updateSector(id, sublineVersion)
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
}
