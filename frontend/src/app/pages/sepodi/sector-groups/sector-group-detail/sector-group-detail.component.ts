import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { DateRangeComponent } from '../../../../core/form-components/date-range/date-range.component';
import { DateRangeTextComponent } from '../../../../core/versioning/date-range-text/date-range-text.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../core/components/detail-page-content/detail-page-content.component';
import { GeographyComponent } from '../../geography/geography.component';
import { MatDivider } from '@angular/material/divider';
import { SwitchVersionComponent } from '../../../../core/components/switch-version/switch-version.component';
import { TextFieldComponent } from '../../../../core/form-components/text-field/text-field.component';
import { TranslatePipe } from '@ngx-translate/core';
import { UserDetailInfoComponent } from '../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import { DetailFormComponent } from '../../../../core/leave-guard/leave-dirty-form-guard.service';
import {
  DetailHelperService,
  DetailWithCancelEdit,
} from '../../../../core/detail/detail-helper.service';
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
import { CreateSectorVersion } from '../../../../api/model/createSectorVersion';
import { catchError, EMPTY } from 'rxjs';
import { SectorGroupService } from '../../../../api/service/sepodi/sector-group.service';
import { SectorGroupVersion } from '../../../../api/model/sectorGroupVersion';
import {
  SectorGroupDetailFormGroup,
  SectorGroupFormGroupBuilder,
} from './sector-group-detail-form-group';

@Component({
  selector: 'app-sector-group-detail',
  imports: [
    AtlasButtonComponent,
    DateRangeComponent,
    DateRangeTextComponent,
    DetailFooterComponent,
    DetailPageContainerComponent,
    DetailPageContentComponent,
    GeographyComponent,
    MatDivider,
    SwitchVersionComponent,
    TextFieldComponent,
    TranslatePipe,
    UserDetailInfoComponent,
  ],
  templateUrl: './sector-group-detail.component.html',
})
export class SectorGroupDetailComponent
  implements DetailFormComponent, DetailWithCancelEdit, OnInit, OnDestroy
{
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly validityService = inject(ValidityService);
  private readonly detailHelperService = inject(DetailHelperService);
  private readonly notificationService = inject(NotificationService);
  private readonly sectorGroupService = inject(SectorGroupService);
  //TODO sectorGroupService

  sectorGroupVersions!: SectorGroupVersion[];
  selectedVersion!: SectorGroupVersion;
  selectedVersionIndex!: number;
  maxValidity!: DateRange;
  servicePointDesignationOfficial!: string;
  trafficPoint!: ReadTrafficPointElementVersion;

  isNew = false;
  form!: FormGroup<SectorGroupDetailFormGroup>;
  servicePointBusinessOrganisations: string[] = [];

  ngOnInit() {
    this.route.data.subscribe((next) => {
      this.sectorGroupVersions = next.sector;
      this.initHeaderWithParentInfo(next);

      if (this.sectorGroupVersions.length == 0) {
        this.isNew = true;
        this.form = SectorGroupFormGroupBuilder.buildFormGroup();
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
    //Todo
  }

  private initSelectedVersion(): void {
    this.form = SectorGroupFormGroupBuilder.buildFormGroup(
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
    this.servicePointDesignationOfficial =
      servicePointVersion.designationOfficial;
    this.servicePointBusinessOrganisations = servicePoint.map(
      (i) => i.businessOrganisation
    );

    const trafficPoint: ReadTrafficPointElementVersion[] = next.trafficPoint;
    this.trafficPoint =
      VersionsHandlingService.determineDefaultVersionByValidity(trafficPoint);
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
          }
        });
      }
    }
  }

  private create(sectorVersion: CreateSectorVersion): void {
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

  private update(id: number, sublineVersion: CreateSectorVersion): void {
    this.sectorGroupService
      .updateSectorGroup(id, sublineVersion)
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
