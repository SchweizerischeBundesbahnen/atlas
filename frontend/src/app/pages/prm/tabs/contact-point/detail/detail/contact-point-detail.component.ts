import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DetailFormComponent } from '../../../../../../core/leave-guard/leave-dirty-form-guard.service';
import {
  ContactPointVersion,
  PersonWithReducedMobilityService,
  ReadContactPointVersion,
  ReadServicePointVersion,
} from '../../../../../../api';
import { FormGroup } from '@angular/forms';
import { NotificationService } from '../../../../../../core/notification/notification.service';
import { VersionsHandlingService } from '../../../../../../core/versioning/versions-handling.service';
import {
  ContactPointFormGroup,
  ContactPointFormGroupBuilder,
} from '../form/contact-point-form-group';
import { DateRange } from '../../../../../../core/versioning/date-range';
import {
  DetailHelperService,
  DetailWithCancelEdit,
} from '../../../../../../core/detail/detail-helper.service';
import { ValidityService } from '../../../../../sepodi/validity/validity.service';
import { catchError, EMPTY, finalize, from, Observable, switchMap, take } from 'rxjs';
import { map, tap } from 'rxjs/operators';

// todo: map this comp to tabDetail and look at pr comments, check jasmine tests and e2e-tests with cypress:run
@Component({
  selector: 'app-contact-point-detail',
  templateUrl: './contact-point-detail.component.html',
  providers: [ValidityService],
})
export class ContactPointDetailComponent
  implements OnInit, DetailFormComponent, DetailWithCancelEdit
{
  isNew = false;
  contactPoint: ReadContactPointVersion[] = [];
  selectedVersion!: ReadContactPointVersion;

  servicePoint!: ReadServicePointVersion;
  maxValidity!: DateRange;

  form!: FormGroup<ContactPointFormGroup>;
  showVersionSwitch = false;
  selectedVersionIndex!: number;

  businessOrganisations: string[] = [];

  saving = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private personWithReducedMobilityService: PersonWithReducedMobilityService,
    private notificationService: NotificationService,
    private detailHelperService: DetailHelperService,
    private validityService: ValidityService,
  ) {}

  ngOnInit(): void {
    this.initSePoDiData();

    this.contactPoint = this.route.snapshot.parent!.data.contactPoint;

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
    const servicePointVersions: ReadServicePointVersion[] =
      this.route.snapshot.parent!.data.servicePoint;
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
    this.router.navigate(['..'], { relativeTo: this.route.parent }).then();
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.detailHelperService.showCancelEditDialog(this);
    } else {
      this.validityService.initValidity(this.form);
      this.form.enable();
    }
  }

  save(): void {
    this.saving = true;
    this.saveProcess()
      .pipe(
        take(1),
        tap(() => this.ngOnInit()),
        catchError(() => {
          this.ngOnInit();
          return EMPTY;
        }),
        finalize(() => (this.saving = false)),
      )
      .subscribe();
  }

  private saveProcess(): Observable<ReadContactPointVersion | ReadContactPointVersion[]> {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      const contactPointVersion = ContactPointFormGroupBuilder.getWritableForm(
        this.form,
        this.servicePoint.sloid!,
      );
      if (this.isNew) {
        return this.create(contactPointVersion);
      } else {
        this.validityService.updateValidity(this.form);
        return this.validityService.validate().pipe(
          switchMap((dialogRes) => {
            if (dialogRes) {
              this.form.disable();
              return this.update(contactPointVersion);
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

  private create(contactPointVersion: ContactPointVersion) {
    return this.personWithReducedMobilityService.createContactPoint(contactPointVersion).pipe(
      switchMap((createdVersion) => {
        return this.notificateAndNavigate(
          'PRM.CONTACT_POINTS.NOTIFICATION.ADD_SUCCESS',
          createdVersion.sloid!,
        ).pipe(map(() => createdVersion));
      }),
    );
  }

  private update(contactPointVersion: ContactPointVersion) {
    return this.personWithReducedMobilityService
      .updateContactPoint(this.selectedVersion.id!, contactPointVersion)
      .pipe(
        switchMap((updatedVersions) => {
          return this.notificateAndNavigate(
            'PRM.CONTACT_POINTS.NOTIFICATION.EDIT_SUCCESS',
            this.selectedVersion.sloid!,
          ).pipe(map(() => updatedVersions));
        }),
      );
  }

  private notificateAndNavigate = (notification: string, routeParam: string) => {
    this.notificationService.success(notification);
    return from(
      this.router.navigate(['..', routeParam], {
        relativeTo: this.route.parent,
      }),
    );
  };
}
