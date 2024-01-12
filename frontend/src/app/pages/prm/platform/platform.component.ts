import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  CreatePlatformVersion,
  PersonWithReducedMobilityService,
  ReadPlatformVersion,
  ReadServicePointVersion,
  ReadStopPointVersion,
  ReadTrafficPointElementVersion,
} from '../../../api';
import { VersionsHandlingService } from '../../../core/versioning/versions-handling.service';
import { DateRange } from '../../../core/versioning/date-range';
import {
  CompletePlatformFormGroup,
  PlatformFormGroupBuilder,
  ReducedPlatformFormGroup,
} from './form/platform-form-group';
import { FormGroup } from '@angular/forms';
import { NotificationService } from '../../../core/notification/notification.service';
import { Observable, of, take } from 'rxjs';
import { DetailFormComponent } from '../../../core/leave-guard/leave-dirty-form-guard.service';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { PrmMeanOfTransportHelper } from '../util/prm-mean-of-transport-helper';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-platforms',
  templateUrl: './platform.component.html',
  styleUrls: ['./platform.component.scss'],
})
export class PlatformComponent implements OnInit, DetailFormComponent {
  isNew = false;
  platform: ReadPlatformVersion[] = [];
  selectedVersion!: ReadPlatformVersion;

  servicePoint!: ReadServicePointVersion;
  trafficPoint!: ReadTrafficPointElementVersion;
  maxValidity!: DateRange;
  stopPoint!: ReadStopPointVersion[];
  businessOrganisations: string[] = [];

  reduced = false;
  form!: FormGroup<ReducedPlatformFormGroup> | FormGroup<CompletePlatformFormGroup>;
  showVersionSwitch = false;
  selectedVersionIndex!: number;
  mayCreate = true;

  get reducedForm(): FormGroup<ReducedPlatformFormGroup> {
    return this.form as FormGroup<ReducedPlatformFormGroup>;
  }

  get completeForm(): FormGroup<CompletePlatformFormGroup> {
    return this.form as FormGroup<CompletePlatformFormGroup>;
  }

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private personWithReducedMobilityService: PersonWithReducedMobilityService,
    private notificationService: NotificationService,
    private dialogService: DialogService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.initSePoDiData();
    this.stopPoint = this.route.snapshot.data.stopPoint;

    this.platform = this.route.snapshot.data.platform;
    this.reduced = PrmMeanOfTransportHelper.isReduced(this.stopPoint[0].meansOfTransport);

    this.isNew = this.platform.length === 0;

    if (this.isNew) {
      this.mayCreate = this.hasPermissionToCreateNewStopPoint();
    } else {
      VersionsHandlingService.addVersionNumbers(this.platform);
      this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.platform);
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.platform);
      this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
        this.platform,
      );
      this.selectedVersionIndex = this.platform.indexOf(this.selectedVersion);
    }

    this.initForm();
  }

  private initForm() {
    if (this.reduced) {
      this.form = PlatformFormGroupBuilder.buildReducedFormGroup(this.selectedVersion);
    } else {
      this.form = PlatformFormGroupBuilder.buildCompleteFormGroup(this.selectedVersion);
    }
    this.form.controls.sloid.setValue(this.trafficPoint.sloid);

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
    this.trafficPoint = VersionsHandlingService.determineDefaultVersionByValidity(
      this.route.snapshot.data.trafficPoint,
    );
  }

  hasPermissionToCreateNewStopPoint(): boolean {
    const sboidsPermissions = this.businessOrganisations.map((bo) =>
      this.authService.hasPermissionsToWrite('PRM', bo),
    );
    return sboidsPermissions.includes(true);
  }

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.platform[newIndex];
    this.initForm();
  }

  back() {
    this.router.navigate(['..'], { relativeTo: this.route }).then();
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.showCancelEditDialog();
    } else {
      this.form.enable();
    }
  }

  save() {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      const platformVersion = PlatformFormGroupBuilder.getWritableForm(
        this.form,
        this.servicePoint.sloid!,
        this.reduced,
      );
      if (this.isNew) {
        this.create(platformVersion);
      } else {
        this.update(platformVersion);
      }
    }
  }

  private create(platformVersion: CreatePlatformVersion) {
    this.personWithReducedMobilityService.createPlatform(platformVersion).subscribe(() => {
      this.notificationService.success('PRM.PLATFORMS.NOTIFICATION.ADD_SUCCESS');
      this.reloadPage();
    });
  }

  private update(platformVersion: CreatePlatformVersion) {
    this.personWithReducedMobilityService
      .updatePlatform(this.selectedVersion.id!, platformVersion)
      .subscribe(() => {
        this.notificationService.success('PRM.PLATFORMS.NOTIFICATION.EDIT_SUCCESS');
        this.reloadPage();
      });
  }

  reloadPage() {
    this.router
      .navigate(['..', this.trafficPoint.sloid], {
        relativeTo: this.route,
      })
      .then(() => this.ngOnInit());
  }

  private showCancelEditDialog() {
    this.confirmLeave()
      .pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          if (this.isNew) {
            this.form.reset();
            this.router.navigate(['..'], { relativeTo: this.route }).then();
          } else {
            this.form.disable();
          }
        }
      });
  }

  private confirmLeave(): Observable<boolean> {
    if (this.form.dirty) {
      return this.dialogService.confirm({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      });
    }
    return of(true);
  }

  //used in combination with canLeaveDirtyForm
  isFormDirty(): boolean {
    return this.form && this.form.dirty;
  }
}
