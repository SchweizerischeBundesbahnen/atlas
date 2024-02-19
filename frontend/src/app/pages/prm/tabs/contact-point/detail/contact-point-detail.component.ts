import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {VersionsHandlingService} from '../../../../../core/versioning/versions-handling.service';
import {ContactPointFormGroup, ContactPointFormGroupBuilder,} from './form/contact-point-form-group';
import {Observable, of, take} from 'rxjs';
import {DetailFormComponent} from '../../../../../core/leave-guard/leave-dirty-form-guard.service';
import {DateRange} from '../../../../../core/versioning/date-range';
import {FormGroup} from '@angular/forms';
import {NotificationService} from '../../../../../core/notification/notification.service';
import {DialogService} from '../../../../../core/components/dialog/dialog.service';
import {
  ContactPointVersion,
  PersonWithReducedMobilityService,
  ReadContactPointVersion,
  ReadServicePointVersion,
} from '../../../../../api';

@Component({
  selector: 'app-contact-point',
  templateUrl: './contact-point-detail.component.html',
  styleUrls: ['./contact-point-detail.component.scss'],
})
export class ContactPointDetailComponent implements OnInit, DetailFormComponent {
  isNew = false;
  contactPoint: ReadContactPointVersion[] = [];
  selectedVersion!: ReadContactPointVersion;

  servicePoint!: ReadServicePointVersion;
  maxValidity!: DateRange;

  form!: FormGroup<ContactPointFormGroup>;
  showVersionSwitch = false;
  selectedVersionIndex!: number;

  businessOrganisations: string[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private personWithReducedMobilityService: PersonWithReducedMobilityService,
    private notificationService: NotificationService,
    private dialogService: DialogService,
  ) {}

  ngOnInit(): void {
    this.initSePoDiData();

    this.contactPoint = this.route.snapshot.data.contactPoint;

    this.isNew = this.contactPoint.length === 0;

    if (!this.isNew) {
      VersionsHandlingService.addVersionNumbers(this.contactPoint);
      this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.contactPoint);
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.contactPoint);
      this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(
        this.contactPoint,
      );
      this.selectedVersionIndex = this.contactPoint.indexOf(this.selectedVersion);
    }

    this.initForm();
  }

  private initForm() {
    this.form = ContactPointFormGroupBuilder.buildFormGroup(this.selectedVersion);

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

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.contactPoint[newIndex];
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
      const contactPointVersion = ContactPointFormGroupBuilder.getWritableForm(
        this.form,
        this.servicePoint.sloid!,
      );
      if (this.isNew) {
        this.create(contactPointVersion);
      } else {
        this.update(contactPointVersion);
      }
    }
  }

  private create(contactPointVersion: ContactPointVersion) {
    this.personWithReducedMobilityService
      .createContactPoint(contactPointVersion)
      .subscribe((createdVersion) => {
        this.notificationService.success('PRM.CONTACT_POINTS.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate(['..', createdVersion.sloid], {
            relativeTo: this.route,
          })
          .then(() => this.ngOnInit());
      });
  }

  private update(contactPointVersion: ContactPointVersion) {
    this.personWithReducedMobilityService
      .updateContactPoint(this.selectedVersion.id!, contactPointVersion)
      .subscribe(() => {
        this.notificationService.success('PRM.CONTACT_POINTS.NOTIFICATION.EDIT_SUCCESS');
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
