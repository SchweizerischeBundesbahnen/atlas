import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApplicationType, TimetableFieldNumberVersion } from '../../../api';
import { BaseDetailController } from '../../../core/components/base-detail/base-detail-controller';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { NotificationService } from '../../../core/notification/notification.service';
import { catchError } from 'rxjs';
import { DialogService } from '../../../core/components/dialog/dialog.service';
import { Pages } from '../../pages';
import { Page } from '../../../core/model/page';
import { ValidityService } from '../../sepodi/validity/validity.service';
import { PermissionService } from '../../../core/auth/permission/permission.service';
import { TimetableFieldNumberInternalService } from '../../../api/service/lidi/timetable-field-number-internal.service';
import { TimetableFieldNumberService } from '../../../api/service/lidi/timetable-field-number.service';
import { BaseDetailComponent } from '../../../core/components/base-detail/base-detail.component';
import { NgIf } from '@angular/common';
import { TextFieldComponent } from '../../../core/form-components/text-field/text-field.component';
import { DateRangeComponent } from '../../../core/form-components/date-range/date-range.component';
import { BusinessOrganisationSelectComponent } from '../../../core/form-components/bo-select/business-organisation-select.component';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
  selector: 'app-timetable-field-number-detail',
  templateUrl: './timetable-field-number-detail.component.html',
  styleUrls: ['./timetable-field-number-detail.component.scss'],
  providers: [ValidityService],
  imports: [
    BaseDetailComponent,
    ReactiveFormsModule,
    NgIf,
    TextFieldComponent,
    DateRangeComponent,
    BusinessOrganisationSelectComponent,
    TranslatePipe,
  ],
})
export class TimetableFieldNumberDetailComponent
  extends BaseDetailController<TimetableFieldNumberVersion>
  implements OnInit
{
  constructor(
    protected router: Router,
    private timetableFieldNumberInternalService: TimetableFieldNumberInternalService,
    private timetableFieldNumberService: TimetableFieldNumberService,
    protected notificationService: NotificationService,
    protected dialogService: DialogService,
    protected permissionService: PermissionService,
    protected activatedRoute: ActivatedRoute,
    protected validityService: ValidityService
  ) {
    super(
      router,
      dialogService,
      notificationService,
      permissionService,
      activatedRoute,
      validityService
    );
  }

  ngOnInit() {
    super.ngOnInit();
  }

  readRecords(): TimetableFieldNumberVersion[] {
    return (
      this.activatedRoute.snapshot.data as {
        timetableFieldNumberDetail: TimetableFieldNumberVersion[];
      }
    ).timetableFieldNumberDetail;
  }

  getDetailHeading(record: TimetableFieldNumberVersion): string {
    return `${record.number} - ${record.descriptionOutwardLine1}`;
  }

  getDetailSubheading(record: TimetableFieldNumberVersion): string {
    return `${record.ttfnid}`;
  }

  updateRecord(): void {
    const id = this.getId();
    const ttfnid = this.record?.ttfnid;
    if (!id || !ttfnid) throw new Error('id and ttfnid are required');
    this.form.disable();
    this.timetableFieldNumberService
      .updateVersionWithVersioning(id, this.form.value)
      .pipe(catchError(this.handleError))
      .subscribe(() => {
        this.notificationService.success('TTFN.NOTIFICATION.EDIT_SUCCESS');
        this.router
          .navigate([Pages.TTFN.path, ttfnid])
          .then(() => this.ngOnInit());
      });
  }

  createRecord(): void {
    this.timetableFieldNumberService
      .createVersion(this.form.value)
      .pipe(catchError(this.handleError))
      .subscribe((version) => {
        this.notificationService.success('TTFN.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate([Pages.TTFN.path, version.ttfnid])
          .then(() => this.ngOnInit());
      });
  }

  revokeRecord(): void {
    const ttfnid = this.getSelectedRecord()?.ttfnid;
    if (!ttfnid) throw new Error('ttfnid is required');
    this.timetableFieldNumberInternalService
      .revokeTimetableFieldNumber(ttfnid)
      .subscribe(() => {
        this.notificationService.success('TTFN.NOTIFICATION.REVOKE_SUCCESS');
        this.router
          .navigate([Pages.TTFN.path, ttfnid])
          .then(() => this.ngOnInit());
      });
  }

  deleteRecord(): void {
    const ttfnid = this.getSelectedRecord()?.ttfnid;
    if (!ttfnid) throw new Error('ttfnid is required');
    this.timetableFieldNumberInternalService
      .deleteVersions(ttfnid)
      .subscribe(() => {
        this.notificationService.success('TTFN.NOTIFICATION.DELETE_SUCCESS');
        this.backToOverview();
      });
  }

  getFormGroup(version: TimetableFieldNumberVersion): FormGroup {
    return new FormGroup({});
    // todo
    /*return new FormGroup<TimetableFieldNumberDetailFormGroup>(
      {
        swissTimetableFieldNumber: new FormControl(
          version.swissTimetableFieldNumber,
          [
            Validators.required,
            AtlasFieldLengthValidator.length_50,
            AtlasCharsetsValidator.sid4pt,
          ]
        ),
        validFrom: new FormControl(
          version.validFrom ? moment(version.validFrom) : version.validFrom,
          [Validators.required]
        ),
        validTo: new FormControl(
          version.validTo ? moment(version.validTo) : version.validTo,
          [Validators.required]
        ),
        ttfnid: new FormControl(version.ttfnid),
        businessOrganisation: new FormControl(version.businessOrganisation, [
          Validators.required,
          AtlasFieldLengthValidator.length_50,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        number: new FormControl(version.number, [
          Validators.required,
          AtlasFieldLengthValidator.length_50,
          AtlasCharsetsValidator.numericWithDot,
        ]),
        descriptionOutwardLine1: new FormControl(
          version.descriptionOutwardLine1,{
            nonNullable: true,
            validators: [
              AtlasFieldLengthValidator.length_255,
              WhitespaceValidator.blankOrEmptySpaceSurrounding,
              AtlasCharsetsValidator.iso88591,
            ]
          }
        ),
        status: new FormControl(version.status),
        etagVersion: new FormControl(version.etagVersion),
        creationDate: new FormControl(version.creationDate),
        editionDate: new FormControl(version.editionDate),
        editor: new FormControl(version.editor),
        creator: new FormControl(version.creator),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
    );*/
  }

  getPageType(): Page {
    return Pages.TTFN;
  }

  getApplicationType(): ApplicationType {
    return ApplicationType.Ttfn;
  }
}
