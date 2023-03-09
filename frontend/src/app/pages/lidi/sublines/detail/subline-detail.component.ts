import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import {
  ApplicationType,
  BusinessOrganisation,
  Line,
  LinesService,
  PaymentType,
  SublinesService,
  SublineType,
  SublineVersion,
} from '../../../../api';
import { DateService } from 'src/app/core/date/date.service';
import { BaseDetailController } from '../../../../core/components/base-detail/base-detail-controller';
import { catchError, EMPTY, Observable, of, Subject, takeUntil } from 'rxjs';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { NotificationService } from '../../../../core/notification/notification.service';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Page } from '../../../../core/model/page';
import { Pages } from '../../../pages';
import moment from 'moment';
import { DateRangeValidator } from '../../../../core/validation/date-range/date-range-validator';
import { map } from 'rxjs/operators';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { ValidationService } from '../../../../core/validation/validation.service';
import { WhitespaceValidator } from '../../../../core/validation/whitespace/whitespace-validator';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { SublineDetailFormGroup } from './subline-detail-form-group';
import { AuthService } from '../../../../core/auth/auth.service';
import { BusinessOrganisationSearchService } from '../../../../core/service/business-organisation-search.service';

@Component({
  templateUrl: './subline-detail.component.html',
  styleUrls: ['./subline-detail.component.scss'],
})
export class SublineDetailComponent
  extends BaseDetailController<SublineVersion>
  implements OnInit, OnDestroy
{
  TYPE_OPTIONS = Object.values(SublineType);
  PAYMENT_TYPE_OPTIONS = Object.values(PaymentType);

  private ngUnsubscribe = new Subject<void>();
  mainlines$: Observable<Line[]> = of([]);
  selectedBusinessOrganisation$: Observable<BusinessOrganisation> = EMPTY;

  readonly mainlineSlnidFormControlName = 'mainlineSlnid';

  constructor(
    @Inject(MAT_DIALOG_DATA) public dialogData: any,
    private router: Router,
    protected dialogRef: MatDialogRef<SublineDetailComponent>,
    private sublinesService: SublinesService,
    private formBuilder: FormBuilder,
    protected notificationService: NotificationService,
    private dateService: DateService,
    private validationService: ValidationService,
    private linesService: LinesService,
    protected dialogService: DialogService,
    protected authService: AuthService,
    protected activatedRoute: ActivatedRoute,
    private readonly businessOrganisationSearchService: BusinessOrganisationSearchService
  ) {
    super(dialogRef, dialogService, notificationService, authService, activatedRoute);
  }

  ngOnInit() {
    super.ngOnInit();
    if (this.isExistingRecord()) {
      this.mainlines$ = this.linesService
        .getLine(this.record.mainlineSlnid)
        .pipe(map((value) => [value]));
    }
    const sboid: string = this.form.get('businessOrganisation')?.value;
    if (sboid) {
      this.selectedBusinessOrganisation$ = this.businessOrganisationSearchService
        .searchByString(sboid)
        .pipe(map((businessOrganisations) => businessOrganisations[0]));
    } else {
      this.selectedBusinessOrganisation$ = of();
    }
  }

  onBusinessOrganisationChange(businessOrganisations: BusinessOrganisation | null) {
    this.form.patchValue({ businessOrganisations: businessOrganisations });
    this.selectedBusinessOrganisation$ = businessOrganisations ? of(businessOrganisations) : of();
  }

  getPageType(): Page {
    return Pages.SUBLINES;
  }

  getApplicationType(): ApplicationType {
    return ApplicationType.Lidi;
  }

  readRecord(): SublineVersion {
    return this.dialogData.sublineDetail;
  }

  getDetailHeading(record: SublineVersion): string {
    return `${record.number ?? ''} - ${record.description ?? ''}`;
  }

  getDetailSubheading(record: SublineVersion): string {
    return record.slnid!;
  }

  updateRecord(): void {
    this.sublinesService
      .updateSublineVersion(this.getId(), this.form.value)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError))
      .subscribe(() => {
        this.notificationService.success('LIDI.SUBLINE.NOTIFICATION.EDIT_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.SUBLINES.path, this.record.slnid])
          .then(() => this.ngOnInit());
      });
  }

  createRecord(): void {
    this.sublinesService
      .createSublineVersion(this.form.value)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError))
      .subscribe((version) => {
        this.notificationService.success('LIDI.SUBLINE.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.SUBLINES.path, version.slnid])
          .then(() => this.ngOnInit());
      });
  }

  revokeRecord(): void {
    const selectedRecord = this.getSelectedRecord();
    if (selectedRecord.slnid) {
      this.sublinesService.revokeSubline(selectedRecord.slnid).subscribe(() => {
        this.notificationService.success('LIDI.SUBLINE.NOTIFICATION.REVOKE_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.SUBLINES.path, selectedRecord.slnid])
          .then(() => this.ngOnInit());
      });
    }
  }

  deleteRecord(): void {
    const selectedSublineVersion = this.getSelectedRecord();
    if (selectedSublineVersion.slnid != null) {
      this.sublinesService.deleteSublines(selectedSublineVersion.slnid).subscribe(() => {
        this.notificationService.success('LIDI.SUBLINE.NOTIFICATION.DELETE_SUCCESS');
        this.backToOverview();
      });
    }
  }

  getFormGroup(version: SublineVersion): FormGroup {
    return new FormGroup<SublineDetailFormGroup>(
      {
        swissSublineNumber: new FormControl(version.swissSublineNumber, [
          Validators.required,
          AtlasFieldLengthValidator.length_50,
          AtlasCharsetsValidator.sid4pt,
        ]),
        [this.mainlineSlnidFormControlName]: new FormControl(version.mainlineSlnid, [
          Validators.required,
        ]),
        slnid: new FormControl(version.slnid),
        status: new FormControl(version.status),
        sublineType: new FormControl(version.sublineType, [Validators.required]),
        paymentType: new FormControl(version.paymentType, [Validators.required]),
        businessOrganisation: new FormControl(version.businessOrganisation, [
          Validators.required,
          AtlasFieldLengthValidator.length_50,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        number: new FormControl(version.number, [
          AtlasFieldLengthValidator.length_50,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        longName: new FormControl(version.longName, [
          AtlasFieldLengthValidator.length_255,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        description: new FormControl(version.description, [
          AtlasFieldLengthValidator.length_255,
          WhitespaceValidator.blankOrEmptySpaceSurrounding,
          AtlasCharsetsValidator.iso88591,
        ]),
        validFrom: new FormControl(
          version.validFrom ? moment(version.validFrom) : version.validFrom,
          [Validators.required]
        ),
        validTo: new FormControl(version.validTo ? moment(version.validTo) : version.validTo, [
          Validators.required,
        ]),
        etagVersion: new FormControl(version.etagVersion),
      },
      [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')]
    );
  }

  getValidation(inputForm: string) {
    return this.validationService.getValidation(this.form?.controls[inputForm]?.errors);
  }

  getFormControlsToDisable(): string[] {
    return [this.mainlineSlnidFormControlName];
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  searchMainlines(searchString: string) {
    this.mainlines$ = this.linesService
      .getLines(searchString, [], [], [], undefined, undefined, undefined, undefined, [
        'swissLineNumber,ASC',
      ])
      .pipe(map((value) => value.objects ?? []));
  }

  mainlineUrl(): string {
    return `${location.origin}/${Pages.LIDI.path}/${Pages.LINES.path}/${
      this.form.get(this.mainlineSlnidFormControlName)?.value
    }`;
  }
}
