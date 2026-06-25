import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import {
  MeanOfTransport,
  PlatformVersion,
  ReadPlatformVersion,
  ReadServicePointVersion,
  ReadStopPointVersion,
  ReadTrafficPointElementVersion,
} from '../../../../../../api';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { PrmMeanOfTransportHelper } from '../../../../util/prm-mean-of-transport-helper';
import { VersionsHandlingService } from '../../../../../../core/versioning/versions-handling.service';
import {
  CompletePlatformFormGroup,
  PlatformFormGroupBuilder,
  ReducedPlatformFormGroup,
} from '../form/platform-form-group';
import { DateRange } from '../../../../../../core/versioning/date-range';
import { ValidityService } from '../../../../../sepodi/validity/validity.service';
import { PermissionService } from '../../../../../../core/auth/permission/permission.service';
import { EMPTY, Observable, switchMap } from 'rxjs';
import { map } from 'rxjs/operators';
import { PrmTabDetailBaseComponent } from '../../../../shared/prm-tab-detail-base.component';
import { DetailPageContentComponent } from '../../../../../../core/components/detail-page-content/detail-page-content.component';
import { SwitchVersionComponent } from '../../../../../../core/components/switch-version/switch-version.component';
import { NavigationSepodiPrmComponent } from '../../../../../../core/navigation-sepodi-prm/navigation-sepodi-prm.component';
import { DateRangeComponent } from '../../../../../../core/form-components/date-range/date-range.component';
import { PlatformReducedFormComponent } from '../form/platform-reduced-form/platform-reduced-form.component';
import { PlatformCompleteFormComponent } from '../form/platform-complete-form/platform-complete-form.component';
import { MatDivider } from '@angular/material/divider';
import { UserDetailInfoComponent } from '../../../../../../core/components/user-edit-info/user-detail-info.component';
import { DetailFooterComponent } from '../../../../../../core/components/detail-footer/detail-footer.component';
import { AtlasButtonComponent } from '../../../../../../core/components/button/atlas-button.component';
import { TranslatePipe } from '@ngx-translate/core';
import { Data } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { PlatformService } from '../../../../../../api/service/prm/platform/platform.service';
import moment from 'moment';
import { WheelchairAccessibilityComponent } from '../../../../../../core/components/wheelchair-accessibility/wheelchair-accessibility.component';

@Component({
  selector: 'atlas-platforms',
  templateUrl: './platform-detail.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  providers: [ValidityService, TranslatePipe],
  imports: [
    DetailPageContentComponent,
    SwitchVersionComponent,
    NavigationSepodiPrmComponent,
    DateRangeComponent,
    ReactiveFormsModule,
    PlatformReducedFormComponent,
    PlatformCompleteFormComponent,
    MatDivider,
    UserDetailInfoComponent,
    DetailFooterComponent,
    AtlasButtonComponent,
    TranslatePipe,
    WheelchairAccessibilityComponent,
  ],
})
export class PlatformDetailComponent extends PrmTabDetailBaseComponent<ReadPlatformVersion> implements OnInit {
  private readonly platformService = inject(PlatformService);
  private readonly permissionService = inject(PermissionService);
  private readonly destroyRef = inject(DestroyRef);

  servicePoint!: ReadServicePointVersion;
  meansOfTransport: MeanOfTransport[] = [];
  trafficPoint!: ReadTrafficPointElementVersion;
  maxValidity!: DateRange;
  stopPoint!: ReadStopPointVersion[];
  businessOrganisations: string[] = [];
  reduced = false;
  showVersionSwitch = false;
  mayCreate = true;

  get reducedForm(): FormGroup<ReducedPlatformFormGroup> {
    return this.form as FormGroup<ReducedPlatformFormGroup>;
  }

  get completeForm(): FormGroup<CompletePlatformFormGroup> {
    return this.form as FormGroup<CompletePlatformFormGroup>;
  }

  ngOnInit(): void {
    this.route.parent!.data.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((data) => {
      this.initSePoDiData(data);
      this.stopPoint = data.stopPoint;
      this.versions = data.platform;

      this.meansOfTransport = this.stopPoint.flatMap((i) => i.meansOfTransport);
      this.reduced = PrmMeanOfTransportHelper.isReduced(this.stopPoint[0].meansOfTransport);
      this.isNew = this.versions.length === 0;

      if (this.isNew) {
        this.mayCreate = this.hasPermissionToCreateNewStopPoint();
        this.showVersionSwitch = false;
        this.setupForm();
      } else {
        VersionsHandlingService.addVersionNumbers(this.versions);
        this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.versions);
        this.maxValidity = VersionsHandlingService.getMaxValidity(this.versions);
        this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(this.versions);
        this.selectedVersionIndex = this.versions.indexOf(this.selectedVersion);
        this.initForm();
      }
    });
  }

  isSelectedVersionValidToday(): boolean {
    if (!this.selectedVersion) {
      return false;
    }
    const today = moment();
    return today.isBetween(moment(this.selectedVersion.validFrom), moment(this.selectedVersion.validTo), 'day', '[]');
  }

  protected initForm() {
    this.setupForm(this.selectedVersion);
  }

  private setupForm(version?: ReadPlatformVersion) {
    if (this.reduced) {
      this.form = PlatformFormGroupBuilder.buildReducedFormGroup(version);
    } else {
      this.form = PlatformFormGroupBuilder.buildCompleteFormGroup(version);
    }
    this.form.controls.sloid.setValue(this.trafficPoint.sloid);

    if (!this.isNew) {
      this.form.disable();
    }
  }

  protected saveProcess(): Observable<ReadPlatformVersion | ReadPlatformVersion[]> {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      const platformVersion = PlatformFormGroupBuilder.getWritableForm(
        this.form,
        this.servicePoint.sloid!,
        this.reduced,
        this.stopPoint[0].meansOfTransport
      );
      if (this.isNew) {
        return this.create(platformVersion);
      } else {
        this.validityService.updateValidity(this.form);
        return this.validityService.validate().pipe(
          switchMap((dialogRes) => {
            if (dialogRes) {
              this.form.disable();
              return this.update(platformVersion);
            } else {
              return EMPTY;
            }
          })
        );
      }
    } else {
      return EMPTY;
    }
  }

  private initSePoDiData(data: Data) {
    const servicePointVersions: ReadServicePointVersion[] = data.servicePoint;
    this.servicePoint = VersionsHandlingService.determineDefaultVersionByValidity(servicePointVersions);
    this.businessOrganisations = [...new Set(servicePointVersions.map((value) => value.businessOrganisation))];
    this.trafficPoint = VersionsHandlingService.determineDefaultVersionByValidity(data.trafficPoint);
  }

  private hasPermissionToCreateNewStopPoint(): boolean {
    const sboidsPermissions = this.businessOrganisations.map((bo) =>
      this.permissionService.hasPermissionsToWrite('PRM', bo)
    );
    return sboidsPermissions.includes(true);
  }

  private create(platformVersion: PlatformVersion) {
    return this.platformService.createPlatform(platformVersion).pipe(
      switchMap((createdVersion) => {
        return this.notificateAndNavigate('PRM.PLATFORMS.NOTIFICATION.ADD_SUCCESS', this.trafficPoint.sloid!).pipe(
          map(() => createdVersion)
        );
      })
    );
  }

  private update(platformVersion: PlatformVersion) {
    return this.platformService.updatePlatform(this.selectedVersion!.id!, platformVersion).pipe(
      switchMap((updatedVersions) => {
        return this.notificateAndNavigate('PRM.PLATFORMS.NOTIFICATION.EDIT_SUCCESS', this.trafficPoint.sloid!).pipe(
          map(() => updatedVersions)
        );
      })
    );
  }
}
