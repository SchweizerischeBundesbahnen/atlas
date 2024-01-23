import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { VersionsHandlingService } from '../../../../../core/versioning/versions-handling.service';
import {
  CompleteReferencePointFormGroup,
  ReferencePointFormGroupBuilder,
} from './form/reference-point-form-group';
import { Observable, of, take } from 'rxjs';
import { DetailFormComponent } from '../../../../../core/leave-guard/leave-dirty-form-guard.service';
import { DateRange } from '../../../../../core/versioning/date-range';
import { FormGroup } from '@angular/forms';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import {
  Country,
  PersonWithReducedMobilityService,
  ReadReferencePointVersion,
  ReadServicePointVersion,
  ReferencePointVersion,
} from '../../../../../api';
import { Countries } from '../../../../../core/country/Countries';

@Component({
  selector: 'app-reference-point',
  templateUrl: './reference-point-detail.component.html',
  styleUrls: ['./reference-point-detail.component.scss'],
})
export class ReferencePointDetailComponent implements OnInit, DetailFormComponent {
  isNew = false;
  referencePoint: ReadReferencePointVersion[] = [];
  selectedVersion!: ReadReferencePointVersion;

  servicePoint!: ReadServicePointVersion;
  maxValidity!: DateRange;

  form!: FormGroup<CompleteReferencePointFormGroup>;
  showVersionSwitch = false;
  selectedVersionIndex!: number;

  businessOrganisations: string[] = [];
  servicePointNumberPartForSloid: string[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private personWithReducedMobilityService: PersonWithReducedMobilityService,
    private notificationService: NotificationService,
    private dialogService: DialogService,
  ) {}

  ngOnInit(): void {
    this.initSePoDiData();

    this.referencePoint = this.route.snapshot.data.referencePoint;

    this.isNew = this.referencePoint.length === 0;

    if (!this.isNew) {
      VersionsHandlingService.addVersionNumbers(this.referencePoint);
      this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.referencePoint);
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.referencePoint);
      this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
        this.referencePoint,
      );
      this.selectedVersionIndex = this.referencePoint.indexOf(this.selectedVersion);
    }

    this.initForm();
  }

  private initForm() {
    this.form = ReferencePointFormGroupBuilder.buildCompleteFormGroup(this.selectedVersion);

    if (!this.isNew) {
      this.form.disable();
    }
  }

  private initSePoDiData() {
    const servicePointVersions: ReadServicePointVersion[] = this.route.snapshot.data.servicePoint;
    this.servicePoint =
      VersionsHandlingService.determineDefaultVersionByValidity(servicePointVersions);
    this.servicePointNumberPartForSloid = [this.buildServicePointNumberPartForSloid()];
    this.businessOrganisations = [
      ...new Set(servicePointVersions.map((value) => value.businessOrganisation)),
    ];
  }

  private buildServicePointNumberPartForSloid() {
    const numberAsString = String(this.servicePoint.number.number);
    if (numberAsString.startsWith(String(Countries.fromCountry(Country.Switzerland)!.uicCode!))) {
      return String(this.servicePoint.number.number % 100000);
    }
    return numberAsString;
  }

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.referencePoint[newIndex];
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
      const referencePointVersion = ReferencePointFormGroupBuilder.getWritableForm(
        this.form,
        this.servicePoint.sloid!,
      );
      if (this.isNew) {
        this.create(referencePointVersion);
      } else {
        this.update(referencePointVersion);
      }
    }
  }

  private create(referencePointVersion: ReferencePointVersion) {
    this.personWithReducedMobilityService
      .createReferencePoint(referencePointVersion)
      .subscribe((createdVersion) => {
        this.notificationService.success('PRM.REFERENCE_POINTS.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate(['..', createdVersion.sloid], {
            relativeTo: this.route,
          })
          .then(() => this.ngOnInit());
      });
  }

  private update(referencePointVersion: ReferencePointVersion) {
    this.personWithReducedMobilityService
      .updateReferencePoint(this.selectedVersion.id!, referencePointVersion)
      .subscribe(() => {
        this.notificationService.success('PRM.REFERENCE_POINTS.NOTIFICATION.EDIT_SUCCESS');
        this.router
          .navigate(['..', this.selectedVersion.sloid], {
            relativeTo: this.route,
          })
          .then(() => this.ngOnInit());
      });
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
