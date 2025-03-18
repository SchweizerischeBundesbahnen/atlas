import { Component, OnInit } from '@angular/core';
import { VersionsHandlingService } from '../../../../../core/versioning/versions-handling.service';
import { ReferencePointFormGroupBuilder } from './form/reference-point-form-group';
import { DateRange } from '../../../../../core/versioning/date-range';
import {
  PersonWithReducedMobilityService,
  ReadReferencePointVersion,
  ReadServicePointVersion,
  ReferencePointVersion,
} from '../../../../../api';
import { ValidityService } from '../../../../sepodi/validity/validity.service';
import { EMPTY, Observable, switchMap } from 'rxjs';
import { map } from 'rxjs/operators';
import { PrmTabDetailBaseComponent } from '../../../shared/prm-tab-detail-base.component';
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { NgIf } from '@angular/common';
import { DateRangeTextComponent } from '../../../../../core/versioning/date-range-text/date-range-text.component';
import { DetailPageContentComponent } from '../../../../../core/components/detail-page-content/detail-page-content.component';
import { SloidComponent } from '../../../../../core/form-components/sloid/sloid.component';
import { ReactiveFormsModule } from '@angular/forms';
import { SwitchVersionComponent } from '../../../../../core/components/switch-version/switch-version.component';
import { ReferencePointCompleteFormComponent } from './form/reference-point-complete-form/reference-point-complete-form.component';
import { MatDivider } from '@angular/material/divider';
import { UserDetailInfoComponent } from '../../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import { DetailFooterComponent } from '../../../../../core/components/detail-footer/detail-footer.component';
import { AtlasButtonComponent } from '../../../../../core/components/button/atlas-button.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-reference-point',
    templateUrl: './reference-point-detail.component.html',
    providers: [ValidityService],
    imports: [DetailPageContainerComponent, NgIf, DateRangeTextComponent, DetailPageContentComponent, SloidComponent, ReactiveFormsModule, SwitchVersionComponent, ReferencePointCompleteFormComponent, MatDivider, UserDetailInfoComponent, DetailFooterComponent, AtlasButtonComponent, TranslatePipe]
})
export class ReferencePointDetailComponent
  extends PrmTabDetailBaseComponent<ReadReferencePointVersion>
  implements OnInit
{
  servicePoint!: ReadServicePointVersion;
  maxValidity!: DateRange;
  showVersionSwitch = false;
  businessOrganisations: string[] = [];
  protected readonly nbrOfBackPaths = 1;

  constructor(private readonly personWithReducedMobilityService: PersonWithReducedMobilityService) {
    super();
  }

  ngOnInit(): void {
    this.initSePoDiData();

    this.versions = this.route.snapshot.data.referencePoint;

    this.isNew = this.versions.length === 0;

    if (!this.isNew) {
      VersionsHandlingService.addVersionNumbers(this.versions);
      this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.versions);
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.versions);
      this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
        this.versions,
      );
      this.selectedVersionIndex = this.versions.indexOf(this.selectedVersion);
    }

    this.initForm();
  }

  protected initForm() {
    this.form = ReferencePointFormGroupBuilder.buildCompleteFormGroup(this.selectedVersion);

    if (!this.isNew) {
      this.form.disable();
    }
  }

  private initSePoDiData() {
    const servicePointVersions: ReadServicePointVersion[] = this.route.snapshot.data.servicePoint;
    this.servicePoint =
      VersionsHandlingService.determineDefaultVersionByValidity(servicePointVersions);
    this.businessOrganisations = [
      ...new Set(servicePointVersions.map((value) => value.businessOrganisation)),
    ];
  }

  protected saveProcess(): Observable<ReadReferencePointVersion | ReadReferencePointVersion[]> {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      const referencePointVersion = ReferencePointFormGroupBuilder.getWritableForm(
        this.form,
        this.servicePoint.sloid!,
      );
      if (this.isNew) {
        return this.create(referencePointVersion);
      } else {
        this.validityService.updateValidity(this.form);
        return this.validityService.validate().pipe(
          switchMap((dialogRes) => {
            if (dialogRes) {
              this.form.disable();
              return this.update(referencePointVersion);
            } else {
              return EMPTY;
            }
          }),
        );
      }
    } else {
      return EMPTY;
    }
  }

  private create(referencePointVersion: ReferencePointVersion) {
    return this.personWithReducedMobilityService.createReferencePoint(referencePointVersion).pipe(
      switchMap((createdVersion) => {
        return this.notificateAndNavigate(
          'PRM.REFERENCE_POINTS.NOTIFICATION.ADD_SUCCESS',
          createdVersion.sloid!,
        ).pipe(map(() => createdVersion));
      }),
    );
  }

  private update(referencePointVersion: ReferencePointVersion) {
    return this.personWithReducedMobilityService
      .updateReferencePoint(this.selectedVersion.id!, referencePointVersion)
      .pipe(
        switchMap((updatedVersions) => {
          return this.notificateAndNavigate(
            'PRM.REFERENCE_POINTS.NOTIFICATION.EDIT_SUCCESS',
            this.selectedVersion.sloid!,
          ).pipe(map(() => updatedVersions));
        }),
      );
  }
}
