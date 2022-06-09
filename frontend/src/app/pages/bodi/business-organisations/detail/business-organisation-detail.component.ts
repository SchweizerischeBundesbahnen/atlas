import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import {
  BusinessOrganisationsService,
  BusinessOrganisationVersion,
  BusinessType,
} from '../../../../api';
import { DetailWrapperController } from '../../../../core/components/detail-wrapper/detail-wrapper-controller';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { takeUntil } from 'rxjs/operators';
import { catchError, Subject } from 'rxjs';
import moment from 'moment/moment';
import { DateRangeValidator } from '../../../../core/validation/date-range/date-range-validator';
import { Pages } from '../../../pages';
import { Page } from 'src/app/core/model/page';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { WhitespaceValidator } from '../../../../core/validation/whitespace/whitespace-validator';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { Role } from '../../../../core/auth/role';

@Component({
  templateUrl: './business-organisation-detail.component.html',
  styleUrls: ['./business-organisation-detail.component.scss'],
})
export class BusinessOrganisationDetailComponent
  extends DetailWrapperController<BusinessOrganisationVersion>
  implements OnInit, OnDestroy
{
  private ngUnsubscribe = new Subject<void>();
  BUSINESS_TYPES = Object.values(BusinessType);

  constructor(
    @Inject(MAT_DIALOG_DATA) public dialogData: any,
    private router: Router,
    protected dialogRef: MatDialogRef<BusinessOrganisationDetailComponent>,
    private businessOrganisationsService: BusinessOrganisationsService,
    private formBuilder: FormBuilder,
    protected notificationService: NotificationService,
    protected dialogService: DialogService
  ) {
    super(dialogRef, dialogService, notificationService);
  }

  ngOnInit() {
    super.ngOnInit();
  }

  getPageType(): Page {
    return Pages.BUSINESS_ORGANISATIONS;
  }

  readRecord(): BusinessOrganisationVersion {
    return this.dialogData.businessOrganisationDetail;
  }

  getDetailHeading(record: BusinessOrganisationVersion): string {
    return `${record.abbreviationDe ?? ''} - ${record.organisationNumber ?? ''}`;
  }

  getDetailSubheading(record: BusinessOrganisationVersion): string {
    return record.sboid!;
  }

  updateRecord(): void {
    this.businessOrganisationsService
      .updateBusinessOrganisationVersion(this.getId(), this.form.value)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe(() => {
        this.notificationService.success('BODI.BUSINESS_ORGANISATION.NOTIFICATION.EDIT_SUCCESS');
        this.router
          .navigate([Pages.BODI.path, Pages.BUSINESS_ORGANISATIONS.path, this.record.sboid])
          .then(() => this.ngOnInit());
      });
  }

  createRecord(): void {
    this.businessOrganisationsService
      .createBusinessOrganisationVersion(this.form.value)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe((version) => {
        this.notificationService.success('BODI.BUSINESS_ORGANISATION.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate([Pages.BODI.path, Pages.BUSINESS_ORGANISATIONS.path, version.sboid])
          .then(() => this.ngOnInit());
      });
  }

  deleteRecord(): void {
    const selectedVersion: BusinessOrganisationVersion = this.getSelectedRecord();
    if (selectedVersion.sboid != null) {
      this.businessOrganisationsService
        .deleteBusinessOrganisation(selectedVersion.sboid)
        .subscribe(() => {
          this.notificationService.success(
            'BODI.BUSINESS_ORGANISATION.NOTIFICATION.DELETE_SUCCESS'
          );
          this.backToOverview();
        });
    }
  }

  getFormGroup(version: BusinessOrganisationVersion): FormGroup {
    return this.formBuilder.group(
      {
        descriptionDe: [
          version.descriptionDe,
          [
            Validators.required,
            AtlasFieldLengthValidator.length_60,
            WhitespaceValidator.blankOrEmptySpaceSurrounding,
            AtlasCharsetsValidator.iso88591,
          ],
        ],
        descriptionFr: [
          version.descriptionFr,
          [
            Validators.required,
            AtlasFieldLengthValidator.length_60,
            WhitespaceValidator.blankOrEmptySpaceSurrounding,
            AtlasCharsetsValidator.iso88591,
          ],
        ],
        descriptionIt: [
          version.descriptionIt,
          [
            Validators.required,
            AtlasFieldLengthValidator.length_60,
            WhitespaceValidator.blankOrEmptySpaceSurrounding,
            AtlasCharsetsValidator.iso88591,
          ],
        ],
        descriptionEn: [
          version.descriptionEn,
          [
            Validators.required,
            AtlasFieldLengthValidator.length_60,
            WhitespaceValidator.blankOrEmptySpaceSurrounding,
            AtlasCharsetsValidator.iso88591,
          ],
        ],
        abbreviationDe: [
          version.abbreviationDe,
          [
            Validators.required,
            AtlasFieldLengthValidator.length_10,
            AtlasCharsetsValidator.iso88591,
          ],
        ],
        abbreviationFr: [
          version.abbreviationFr,
          [
            Validators.required,
            AtlasFieldLengthValidator.length_10,
            AtlasCharsetsValidator.iso88591,
          ],
        ],
        abbreviationIt: [
          version.abbreviationIt,
          [
            Validators.required,
            AtlasFieldLengthValidator.length_10,
            AtlasCharsetsValidator.iso88591,
          ],
        ],
        abbreviationEn: [
          version.abbreviationEn,
          [
            Validators.required,
            AtlasFieldLengthValidator.length_10,
            AtlasCharsetsValidator.iso88591,
          ],
        ],
        organisationNumber: [
          version.organisationNumber,
          [Validators.required, Validators.min(0), Validators.max(99999)],
        ],
        contactEnterpriseEmail: [
          version.contactEnterpriseEmail,
          [AtlasFieldLengthValidator.length_255, AtlasCharsetsValidator.email],
        ],
        businessTypes: [version.businessTypes],
        validFrom: [
          version.validFrom ? moment(version.validFrom) : version.validFrom,
          [Validators.required],
        ],
        validTo: [
          version.validTo ? moment(version.validTo) : version.validTo,
          [Validators.required],
        ],
        etagVersion: version.etagVersion,
      },
      {
        validators: [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
      }
    );
  }

  getRolesAllowedToDelete(): Role[] {
    return [Role.BoAdmin];
  }

  getRolesAllowedToEdit(): Role[] {
    return [Role.BoWriter, Role.BoAdmin];
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }
}
