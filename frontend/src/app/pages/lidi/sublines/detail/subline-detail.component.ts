import {Component, OnInit} from '@angular/core';
import {
  ApplicationRole,
  ApplicationType,
  Line,
  LinesService,
  SublineConcessionType,
  SublinesService,
  SublineType,
  SublineVersion,
  SublineVersionV2,
} from '../../../../api';
import {catchError, EMPTY, Observable, of} from 'rxjs';
import {DialogService} from '../../../../core/components/dialog/dialog.service';
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

@Component({
  templateUrl: './subline-detail.component.html',
  styleUrls: ['./subline-detail.component.scss'],
  providers: [ValidityService],
})
export class SublineDetailComponent implements OnInit, DetailFormComponent, DetailWithCancelEdit {
  protected readonly Pages = Pages;

  TYPE_OPTIONS = Object.values(SublineType);
  CONCESSION_TYPE_OPTIONS = Object.values(SublineConcessionType);

  mainlines$: Observable<Line[]> = of([]);

  readonly mainlineSlnidFormControlName = 'mainlineSlnid';

  form!: FormGroup<SublineFormGroup>;
  isNew = false;
  showVersionSwitch = false;
  isSwitchVersionDisabled = false;
  selectedVersionIndex!: number;

  maxValidity!: DateRange;

  versions!: SublineVersion[];
  selectedVersion!: SublineVersion;

  boSboidRestriction: string[] = [];

  constructor(
    private router: Router,
    private sublinesService: SublinesService,
    private notificationService: NotificationService,
    private validationService: ValidationService,
    private linesService: LinesService,
    private dialogService: DialogService,
    private permissionService: PermissionService,
    private activatedRoute: ActivatedRoute,
    private validityService: ValidityService,
    private detailHelperService: DetailHelperService,
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
    }
    this.initBoSboidRestriction();
  }

  private initSelectedVersion() {
    this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(this.versions);
    // this.form = SublineFormGroupBuilder.buildFormGroup(this.selectedVersion);
    if (!this.isNew) {
      this.form.disable();
    }
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.detailHelperService.showCancelEditDialog(this);
    } else {
      this.isSwitchVersionDisabled = true;
      this.validityService.initValidity(this.form);
      this.form.enable({ emitEvent: false });
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

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.versions[newIndex];
    this.initSelectedVersion();
  }

  save() {
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      const sublineVersion = this.form.value as unknown as SublineVersionV2;
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

  createSubline(sublineVersion:SublineVersion): void {
    this.sublinesService
      .createSublineVersion(sublineVersion)
      .pipe(catchError(this.handleError()))
      .subscribe((version) => {
        this.notificationService.success('LIDI.SUBLINE.NOTIFICATION.ADD_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.SUBLINES.path, version.slnid])
          .then(() => this.ngOnInit());
      });
  }

  updateSubline(id:number, sublineVersion:SublineVersion): void {
    this.sublinesService
      .updateSublineVersion(id, sublineVersion)
      .pipe(catchError(this.handleError()))
      .subscribe(() => {
        this.notificationService.success('LIDI.SUBLINE.NOTIFICATION.EDIT_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.SUBLINES.path, sublineVersion.slnid])
          .then(() => this.ngOnInit());
      });
  }

  revoke(): void {
    if (this.selectedVersion.slnid) {
      this.sublinesService.revokeSubline(this.selectedVersion.slnid).subscribe(() => {
        this.notificationService.success('LIDI.SUBLINE.NOTIFICATION.REVOKE_SUCCESS');
        this.router
          .navigate([Pages.LIDI.path, Pages.SUBLINES.path, this.selectedVersion.slnid])
          .then(() => this.ngOnInit());
      });
    }
  }

  delete(): void {
    if (this.selectedVersion.slnid) {
      this.sublinesService.deleteSublines(this.selectedVersion.slnid).subscribe(() => {
        this.notificationService.success('LIDI.SUBLINE.NOTIFICATION.DELETE_SUCCESS');
        this.back();
      });
    }
  }

  back() {
    this.router.navigate(['..'], {relativeTo: this.activatedRoute}).then();
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

  private handleError() {
    return () => {
      this.form.enable();
      return EMPTY;
    };
  }

}
