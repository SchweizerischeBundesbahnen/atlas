import {Component, OnInit} from '@angular/core';
import {
  ApplicationType,
  Line,
  LinesService,
  SublineConcessionType,
  SublinesService,
  SublineType,
  SublineVersion, SublineVersionV2,
} from '../../../../api';
import {BaseDetailController} from '../../../../core/components/base-detail/base-detail-controller';
import {catchError, Observable, of} from 'rxjs';
import {DialogService} from '../../../../core/components/dialog/dialog.service';
import {NotificationService} from '../../../../core/notification/notification.service';
import {FormGroup} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {Page} from '../../../../core/model/page';
import {Pages} from '../../../pages';
import {map} from 'rxjs/operators';
import {ValidationService} from '../../../../core/validation/validation.service';
import {SublineFormGroupBuilder} from './subline-detail-form-group';
import {ValidityService} from "../../../sepodi/validity/validity.service";
import {PermissionService} from "../../../../core/auth/permission/permission.service";

@Component({
  templateUrl: './subline-detail.component.html',
  styleUrls: ['./subline-detail.component.scss'],
  providers: [ValidityService],
})
export class SublineDetailComponent extends BaseDetailController<SublineVersion> implements OnInit {
  TYPE_OPTIONS = Object.values(SublineType);
  CONCESSION_TYPE_OPTIONS= Object.values(SublineConcessionType);

  mainlines$: Observable<Line[]> = of([]);

  readonly mainlineSlnidFormControlName = 'mainlineSlnid';

  constructor(
    protected router: Router,
    private sublinesService: SublinesService,
    protected notificationService: NotificationService,
    private validationService: ValidationService,
    private linesService: LinesService,
    protected dialogService: DialogService,
    protected permissionService: PermissionService,
    protected activatedRoute: ActivatedRoute,
    protected validityService: ValidityService,
  ) {
    super(router, dialogService, notificationService, permissionService, activatedRoute, validityService);
  }

  ngOnInit() {
    super.ngOnInit();
    if (this.isExistingRecord()) {
      this.mainlines$ = this.linesService
        .getLine(this.record.mainlineSlnid)
        .pipe(map((value) => [value]));
    }
  }

  getPageType(): Page {
    return Pages.SUBLINES;
  }

  getApplicationType(): ApplicationType {
    return ApplicationType.Lidi;
  }

  readRecord(): SublineVersion {
    return this.activatedRoute.snapshot.data.sublineDetail;
  }

  getDetailHeading(record: SublineVersion): string {
    return `${record.number ?? ''} - ${record.description ?? ''}`;
  }

  getDetailSubheading(record: SublineVersion): string {
    return record.slnid!;
  }

  updateRecord(): void {
    this.form.disable();
    this.sublinesService
      .updateSublineVersion(this.getId(), this.form.value)
      .pipe(catchError(this.handleError))
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
      .pipe(catchError(this.handleError))
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
    return SublineFormGroupBuilder.buildFormGroup(version as SublineVersionV2);
  }

  getValidation(inputForm: string) {
    return this.validationService.getValidation(this.form?.controls[inputForm]?.errors);
  }

  getFormControlsToDisable(): string[] {
    return [this.mainlineSlnidFormControlName];
  }

  searchMainlines(searchString: string) {
    this.mainlines$ = this.linesService
      .getLines(undefined, [searchString], undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined,
        undefined, undefined, undefined, ['swissLineNumber,ASC'])
      .pipe(map((value) => value.objects ?? []));
  }

  mainlineUrl(): string {
    return `${location.origin}/${Pages.LIDI.path}/${Pages.LINES.path}/${this.form.get(
      this.mainlineSlnidFormControlName,
    )?.value}`;
  }
}
