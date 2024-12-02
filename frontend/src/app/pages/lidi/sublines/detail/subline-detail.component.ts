import {Component, OnInit} from '@angular/core';
import {
  ApplicationRole,
  ApplicationType,
  ElementType,
  LidiElementType,
  Line,
  LinesService, LineVersionV2, ReadSublineVersionV2, Status,
  SublineConcessionType,
  SublinesService,
  SublineType,
  SublineVersionV2,
} from '../../../../api';
import {catchError, EMPTY, Observable, of} from 'rxjs';
import {NotificationService} from '../../../../core/notification/notification.service';
import {FormGroup} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {Pages} from '../../../pages';
import {map} from 'rxjs/operators';
import {ValidationService} from '../../../../core/validation/validation.service';
import {SublineFormGroup, SublineFormGroupBuilder} from './subline-form-group';
import {ValidityService} from "../../../sepodi/validity/validity.service";
import {PermissionService} from "../../../../core/auth/permission/permission.service";
import {DetailFormComponent} from "../../../../core/leave-guard/leave-dirty-form-guard.service";
import {VersionsHandlingService} from "../../../../core/versioning/versions-handling.service";
import {DateRange} from "../../../../core/versioning/date-range";
import {DetailHelperService, DetailWithCancelEdit} from "../../../../core/detail/detail-helper.service";
import {DialogService} from "../../../../core/components/dialog/dialog.service";

@Component({
  templateUrl: './subline-detail.component.html',
  styleUrls: ['./subline-detail.component.scss'],
  providers: [ValidityService],
})
export class SublineDetailComponent implements OnInit, DetailFormComponent, DetailWithCancelEdit {
  protected readonly Pages = Pages;

  TYPE_OPTIONS: SublineType[] = [];
  CONCESSION_TYPE_OPTIONS = Object.values(SublineConcessionType);

  mainlines$: Observable<Line[]> = of([]);
  currentMainlineSelection?:LineVersionV2;

  readonly mainlineSlnidFormControlName = 'mainlineSlnid';

  form!: FormGroup<SublineFormGroup>;
  isNew = false;
  showVersionSwitch = false;
  isSwitchVersionDisabled = false;
  selectedVersionIndex!: number;

  maxValidity!: DateRange;

  versions!: ReadSublineVersionV2[];
  selectedVersion!: ReadSublineVersionV2;

  boSboidRestriction: string[] = [];

  constructor(
    private router: Router,
    private sublinesService: SublinesService,
    private notificationService: NotificationService,
    private linesService: LinesService,
    private permissionService: PermissionService,
    private activatedRoute: ActivatedRoute,
    private validityService: ValidityService,
    private detailHelperService: DetailHelperService,
    private dialogService: DialogService,
  ) {
  }

  ngOnInit() {
    this.versions = this.activatedRoute.snapshot.data.sublineDetail;
    if (this.versions.length == 0) {
      this.isNew = true;
      this.form = SublineFormGroupBuilder.buildFormGroup();
    } else {
      this.isNew = false;
      VersionsHandlingService.addVersionNumbers(this.versions);
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.versions);
      this.selectedVersion = VersionsHandlingService.determineDefaultVersionByValidity(this.versions);
      this.selectedVersionIndex = this.versions.indexOf(this.selectedVersion);

      this.initSelectedVersion();
      this.mainlines$ = this.linesService
        .getLine(this.selectedVersion.mainlineSlnid)
        .pipe(map((value) => [value]));

      this.linesService.getLineVersionsV2(this.selectedVersion.mainlineSlnid).subscribe(mainline => {
        this.currentMainlineSelection = VersionsHandlingService.determineDefaultVersionByValidity(mainline);
      });

      this.TYPE_OPTIONS = [this.form.controls.sublineType.value!]
    }
    this.initBoSboidRestriction();
  }

  private initSelectedVersion() {
    this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.versions);
    this.form = SublineFormGroupBuilder.buildFormGroup(this.selectedVersion);
    if (!this.isNew) {
      this.form.disable();
    }
  }

  initBoSboidRestriction() {
    if (!this.isNew || this.permissionService.isAdmin) {
      this.boSboidRestriction = [];
    } else {
      const permission = this.permissionService.getApplicationUserPermission(ApplicationType.Lidi);
      if (permission.role === ApplicationRole.Writer) {
        this.boSboidRestriction = PermissionService.getSboidRestrictions(permission);
      } else {
        this.boSboidRestriction = [];
      }
    }
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.detailHelperService.showCancelEditDialog(this);
    } else {
      this.isSwitchVersionDisabled = true;
      this.validityService.initValidity(this.form);
      this.form.enable({emitEvent: false});

      this.form.controls.mainlineSlnid.disable();
      this.form.controls.sublineType.disable();
    }
  }

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.versions[newIndex];
    this.initSelectedVersion();
  }

  save() {
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      const sublineVersion = this.form.getRawValue() as unknown as SublineVersionV2;
      this.form.disable();
      if (this.isNew) {
        this.createSubline(sublineVersion);
      } else {
        this.validityService.updateValidity(this.form);
        this.validityService.validate().subscribe(confirmed => {
          if (confirmed) {
            this.form.disable();
            this.updateSubline(this.selectedVersion.id!, sublineVersion);
          }
        });
      }
    }
  }

  createSubline(sublineVersion: SublineVersionV2): void {
    this.sublinesService
      .createSublineVersionV2(sublineVersion)
      .pipe(catchError(this.handleError()))
      .subscribe((version) => {
        this.notificationService.success('LIDI.SUBLINE.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.SUBLINES.path, version.slnid])
          .then(() => this.ngOnInit());
      });
  }

  updateSubline(id: number, sublineVersion: SublineVersionV2): void {
    this.sublinesService
      .updateSublineVersionV2(id, sublineVersion)
      .pipe(catchError(this.handleError()))
      .subscribe(() => {
        this.notificationService.success('LIDI.SUBLINE.NOTIFICATION.EDIT_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.SUBLINES.path, sublineVersion.slnid])
          .then(() => this.ngOnInit());
      });
  }

  revoke(): void {
    this.dialogService
      .confirm({
        title: 'DIALOG.WARNING',
        message: 'DIALOG.REVOKE',
        cancelText: 'DIALOG.BACK',
        confirmText: 'DIALOG.CONFIRM_REVOKE',
      })
      .subscribe((confirmed) => {
        if (confirmed) {
          if (this.selectedVersion.slnid) {
            this.sublinesService.revokeSubline(this.selectedVersion.slnid).subscribe(() => {
              this.notificationService.success('LIDI.SUBLINE.NOTIFICATION.REVOKE_SUCCESS');
              this.router
                .navigate([Pages.LIDI.path, Pages.SUBLINES.path, this.selectedVersion.slnid])
                .then(() => this.ngOnInit());
            });
          }
        }
      });
  }

  delete(): void {
    this.dialogService
      .confirm({
        title: 'DIALOG.WARNING',
        message: 'DIALOG.DELETE',
        cancelText: 'DIALOG.BACK',
        confirmText: 'DIALOG.CONFIRM_DELETE',
      })
      .subscribe((confirmed) => {
        if (confirmed) {
          if (this.selectedVersion.slnid) {
            this.sublinesService.deleteSublines(this.selectedVersion.slnid).subscribe(() => {
              this.notificationService.success('LIDI.SUBLINE.NOTIFICATION.DELETE_SUCCESS');
              this.back();
            });
          }
        }
      });
  }

  back() {
    this.router.navigate(['..'], {relativeTo: this.activatedRoute}).then();
  }

  searchMainlines(searchString: string) {
    this.mainlines$ = this.linesService
      .getLines(undefined, [searchString], [Status.Validated, Status.InReview, Status.Draft, Status.Withdrawn], undefined, [ElementType.Line], undefined, undefined, undefined, undefined, undefined, undefined,
        undefined, undefined, undefined, ['swissLineNumber,ASC'])
      .pipe(map((value) => value.objects ?? []));
  }

  mainlineUrl(): string {
    return `${location.origin}/${Pages.LIDI.path}/${Pages.LINES.path}/${this.form.get(
      this.mainlineSlnidFormControlName,
    )?.value}`;
  }

  private handleError() {
    return () => {
      this.form.enable();
      return EMPTY;
    };
  }

  mainLineChanged(line?: Line) {
    if (line) {
      this.handleSublineType(line);

      this.linesService.getLineVersionsV2(line.slnid!).subscribe(mainline => {
        this.currentMainlineSelection = VersionsHandlingService.determineDefaultVersionByValidity(mainline);
      });
    } else {
      this.TYPE_OPTIONS = [];
      this.form.controls.sublineType.setValue(undefined);
      this.form.controls.sublineConcessionType.setValue(undefined);

      this.currentMainlineSelection = undefined;
    }

  }

  private handleSublineType(line: Line) {
    const lineType = line.lidiElementType;
    switch (lineType) {
      case LidiElementType.Orderly:
        this.TYPE_OPTIONS = [SublineType.Concession, SublineType.Technical];
        break;
      case LidiElementType.Disposition:
        this.TYPE_OPTIONS = [SublineType.Disposition];
        this.form.controls.sublineType.setValue(SublineType.Disposition);
        break;
      case LidiElementType.Temporary:
        this.TYPE_OPTIONS = [SublineType.Temporary];
        this.form.controls.sublineType.setValue(SublineType.Temporary);
        break;
      case LidiElementType.Operational:
        this.TYPE_OPTIONS = [SublineType.Operational];
        this.form.controls.sublineType.setValue(SublineType.Operational);
        break;
      default:
        console.error(line);
        throw new Error("LineType not expected: " + lineType);
    }
  }
}
