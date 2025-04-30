import { Component, OnInit } from '@angular/core';
import {
  MeanOfTransport,
  PersonWithReducedMobilityService,
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
import { NgIf } from '@angular/common';
import { SwitchVersionComponent } from '../../../../../../core/components/switch-version/switch-version.component';
import { NavigationSepodiPrmComponent } from '../../../../../../core/navigation-sepodi-prm/navigation-sepodi-prm.component';
import { DateRangeComponent } from '../../../../../../core/form-components/date-range/date-range.component';
import { PlatformReducedFormComponent } from '../form/platform-reduced-form/platform-reduced-form.component';
import { PlatformCompleteFormComponent } from '../form/platform-complete-form/platform-complete-form.component';
import { MatDivider } from '@angular/material/divider';
import { UserDetailInfoComponent } from '../../../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import { DetailFooterComponent } from '../../../../../../core/components/detail-footer/detail-footer.component';
import { AtlasButtonComponent } from '../../../../../../core/components/button/atlas-button.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-platforms',
  templateUrl: './platform-detail.component.html',
  providers: [ValidityService],
  imports: [
    DetailPageContentComponent,
    NgIf,
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
  ],
})
export class PlatformDetailComponent
  extends PrmTabDetailBaseComponent<ReadPlatformVersion>
  implements OnInit
{
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

  constructor(
    private readonly personWithReducedMobilityService: PersonWithReducedMobilityService,
    private readonly permissionService: PermissionService
  ) {
    super();
  }

  ngOnInit(): void {
    this.initSePoDiData();
    this.stopPoint = this.route.snapshot.parent!.data.stopPoint;
    this.versions = this.route.snapshot.parent!.data.platform;

    this.meansOfTransport = this.stopPoint.flatMap((i) => i.meansOfTransport);
    this.reduced = PrmMeanOfTransportHelper.isReduced(
      this.stopPoint[0].meansOfTransport
    );
    this.isNew = this.versions.length === 0;

    if (this.isNew) {
      this.mayCreate = this.hasPermissionToCreateNewStopPoint();
    } else {
      VersionsHandlingService.addVersionNumbers(this.versions);
      this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(
        this.versions
      );
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.versions);
      this.selectedVersion =
        VersionsHandlingService.determineDefaultVersionByValidity(
          this.versions
        );
      this.selectedVersionIndex = this.versions.indexOf(this.selectedVersion);
    }

    this.initForm();
  }

  protected initForm() {
    if (this.reduced) {
      this.form = PlatformFormGroupBuilder.buildReducedFormGroup(
        this.selectedVersion
      );
    } else {
      this.form = PlatformFormGroupBuilder.buildCompleteFormGroup(
        this.selectedVersion
      );
    }
    this.form.controls.sloid.setValue(this.trafficPoint.sloid);

    if (!this.isNew) {
      this.form.disable();
    }
  }

  protected saveProcess(): Observable<
    ReadPlatformVersion | ReadPlatformVersion[]
  > {
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

  private initSePoDiData() {
    const servicePointVersions: ReadServicePointVersion[] =
      this.route.snapshot.parent!.data.servicePoint;
    this.servicePoint =
      VersionsHandlingService.determineDefaultVersionByValidity(
        servicePointVersions
      );
    this.businessOrganisations = [
      ...new Set(
        servicePointVersions.map((value) => value.businessOrganisation)
      ),
    ];
    this.trafficPoint =
      VersionsHandlingService.determineDefaultVersionByValidity(
        this.route.snapshot.parent!.data.trafficPoint
      );
  }

  private hasPermissionToCreateNewStopPoint(): boolean {
    const sboidsPermissions = this.businessOrganisations.map((bo) =>
      this.permissionService.hasPermissionsToWrite('PRM', bo)
    );
    return sboidsPermissions.includes(true);
  }

  private create(platformVersion: PlatformVersion) {
    return this.personWithReducedMobilityService
      .createPlatform(platformVersion)
      .pipe(
        switchMap((createdVersion) => {
          return this.notificateAndNavigate(
            'PRM.PLATFORMS.NOTIFICATION.ADD_SUCCESS',
            this.trafficPoint.sloid!
          ).pipe(map(() => createdVersion));
        })
      );
  }

  private update(platformVersion: PlatformVersion) {
    return this.personWithReducedMobilityService
      .updatePlatform(this.selectedVersion!.id!, platformVersion)
      .pipe(
        switchMap((updatedVersions) => {
          return this.notificateAndNavigate(
            'PRM.PLATFORMS.NOTIFICATION.EDIT_SUCCESS',
            this.trafficPoint.sloid!
          ).pipe(map(() => updatedVersions));
        })
      );
  }
}
