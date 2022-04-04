import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { LinesService, LineType, LineVersion, PaymentType } from '../../../../api';
import { DetailWrapperController } from '../../../../core/components/detail-wrapper/detail-wrapper-controller';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { takeUntil } from 'rxjs/operators';
import { catchError, EMPTY, Subject } from 'rxjs';
import moment from 'moment/moment';
import { DateRangeValidator } from '../../../../core/validation/date-range/date-range-validator';
import { Pages } from '../../../pages';
import { Page } from 'src/app/core/model/page';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { WhitespaceValidator } from '../../../../core/validation/whitespace/whitespace-validator';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';

@Component({
  templateUrl: './line-detail.component.html',
})
export class LineDetailComponent
  extends DetailWrapperController<LineVersion>
  implements OnInit, OnDestroy
{
  readonly LINE_NUMBER_EXAMPLE = 50.391;
  readonly LINE_DESCRIPTION_EXAMPLE = 'Lenzburg Langsamstig - Bahnhof - Lenzburg Schloss';
  readonly SWISS_LINE_NUMBER_EXAMPLE = 'r.50.391';
  readonly COLOR_RGB_EXAMPLE = '#4AC44F';
  readonly FONT_CMYK_EXAMPLE = '0,0,0,100';
  readonly BACKGROUND_CMYK_EXAMPLE = '85,10,65,0';

  TYPE_OPTIONS = Object.values(LineType);
  PAYMENT_TYPE_OPTIONS = Object.values(PaymentType);

  private ngUnsubscribe = new Subject<void>();

  constructor(
    @Inject(MAT_DIALOG_DATA) public dialogData: any,
    private router: Router,
    private linesService: LinesService,
    private formBuilder: FormBuilder,
    protected notificationService: NotificationService,
    protected dialogService: DialogService
  ) {
    super(dialogService, notificationService);
  }

  ngOnInit() {
    super.ngOnInit();
  }

  getPageType(): Page {
    return Pages.LINES;
  }

  readRecord(): LineVersion {
    return this.dialogData.lineDetail;
  }

  getTitle(record: LineVersion): string | undefined {
    return record.swissLineNumber;
  }

  getDetailHeading(record: LineVersion): string {
    return `${record.number} - ${record.description}`;
  }

  getDetailSubheading(record: LineVersion): string {
    return record.slnid!;
  }

  updateRecord(): void {
    this.linesService
      .updateLineVersion(this.getId(), this.form.value)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe(() => {
        this.notificationService.success('LIDI.LINE.NOTIFICATION.EDIT_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.LINES.path, this.record.slnid])
          .then(() => this.ngOnInit());
      });
  }

  createRecord(): void {
    this.linesService
      .createLineVersion(this.form.value)
      .pipe(takeUntil(this.ngUnsubscribe), catchError(this.handleError()))
      .subscribe((version) => {
        this.notificationService.success('LIDI.LINE.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.LINES.path, version.slnid])
          .then(() => this.ngOnInit());
      });
  }

  deleteRecord(): void {
    const selectedLineVersion: LineVersion = this.getSelectedRecord();
    if (selectedLineVersion.slnid != null) {
      this.linesService
        .deleteLines(selectedLineVersion.slnid)
        .pipe(
          takeUntil(this.ngUnsubscribe),
          catchError((err) => {
            this.notificationService.error(err, 'LIDI.LINE.NOTIFICATION.DELETE_ERROR');
            return EMPTY;
          })
        )
        .subscribe(() => {
          this.notificationService.success('LIDI.LINE.NOTIFICATION.DELETE_SUCCESS');
          this.backToOverview();
        });
    }
  }

  backToOverview(): void {
    this.router.navigate([Pages.LIDI.path]).then();
  }

  getFormGroup(version: LineVersion): FormGroup {
    return this.formBuilder.group(
      {
        swissLineNumber: [
          version.swissLineNumber,
          [Validators.required, Validators.maxLength(50), AtlasCharsetsValidator.sid4pt],
        ],
        lineType: [version.lineType, [Validators.required]],
        paymentType: [version.paymentType, [Validators.required]],
        businessOrganisation: [
          version.businessOrganisation,
          [
            Validators.required,
            AtlasFieldLengthValidator.small,
            WhitespaceValidator.blankOrEmptySpaceSurrounding,
          ],
        ],
        number: [
          version.number,
          [
            AtlasFieldLengthValidator.small,
            WhitespaceValidator.blankOrEmptySpaceSurrounding,
            AtlasCharsetsValidator.iso88591,
          ],
        ],
        alternativeName: [
          version.alternativeName,
          [
            AtlasFieldLengthValidator.small,
            WhitespaceValidator.blankOrEmptySpaceSurrounding,
            AtlasCharsetsValidator.iso88591,
          ],
        ],
        combinationName: [
          version.combinationName,
          [
            AtlasFieldLengthValidator.small,
            WhitespaceValidator.blankOrEmptySpaceSurrounding,
            AtlasCharsetsValidator.iso88591,
          ],
        ],
        longName: [
          version.longName,
          [
            AtlasFieldLengthValidator.mid,
            WhitespaceValidator.blankOrEmptySpaceSurrounding,
            AtlasCharsetsValidator.iso88591,
          ],
        ],
        icon: [
          version.icon,
          [
            AtlasFieldLengthValidator.mid,
            WhitespaceValidator.blankOrEmptySpaceSurrounding,
            AtlasCharsetsValidator.iso88591,
          ],
        ],
        colorFontRgb: [version.colorFontRgb || '#000000', [Validators.required]],
        colorBackRgb: [version.colorBackRgb || '#FFFFFF', [Validators.required]],
        colorFontCmyk: [version.colorFontCmyk || '100,100,100,100', [Validators.required]],
        colorBackCmyk: [version.colorBackCmyk || '0,0,0,0', [Validators.required]],
        description: [
          version.description,
          [
            AtlasFieldLengthValidator.mid,
            WhitespaceValidator.blankOrEmptySpaceSurrounding,
            AtlasCharsetsValidator.iso88591,
          ],
        ],
        validFrom: [
          version.validFrom ? moment(version.validFrom) : version.validFrom,
          [Validators.required],
        ],
        validTo: [
          version.validTo ? moment(version.validTo) : version.validTo,
          [Validators.required],
        ],
        comment: [
          version.comment,
          [
            AtlasFieldLengthValidator.comments,
            WhitespaceValidator.blankOrEmptySpaceSurrounding,
            AtlasCharsetsValidator.iso88591,
          ],
        ],
        etagVersion: version.etagVersion,
      },
      {
        validators: [DateRangeValidator.fromGreaterThenTo('validFrom', 'validTo')],
      }
    );
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }
}
