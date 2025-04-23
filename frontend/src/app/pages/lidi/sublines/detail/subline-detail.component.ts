import { Component, OnInit } from '@angular/core';
import {
  ApplicationRole,
  ApplicationType,
  CreateSublineVersionV2,
  ElementType,
  LidiElementType,
  Line,
  LineVersionV2,
  ReadSublineVersionV2,
  Status,
  SublineConcessionType,
  SublineType,
  SublineVersionV2,
} from '../../../../api';
import { catchError, EMPTY, Observable, of } from 'rxjs';
import { NotificationService } from '../../../../core/notification/notification.service';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Pages } from '../../../pages';
import { map } from 'rxjs/operators';
import { ValidationService } from '../../../../core/validation/validation.service';
import {
  SublineFormGroup,
  SublineFormGroupBuilder,
} from './subline-form-group';
import { ValidityService } from '../../../sepodi/validity/validity.service';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { DetailFormComponent } from '../../../../core/leave-guard/leave-dirty-form-guard.service';
import { VersionsHandlingService } from '../../../../core/versioning/versions-handling.service';
import { DateRange } from '../../../../core/versioning/date-range';
import {
  DetailHelperService,
  DetailWithCancelEdit,
} from '../../../../core/detail/detail-helper.service';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { SublineService } from '../../../../api/service/subline.service';
import { SublineInternalService } from '../../../../api/service/subline-internal.service';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { ScrollToTopDirective } from '../../../../core/scroll-to-top/scroll-to-top.directive';
import { DetailPageContentComponent } from '../../../../core/components/detail-page-content/detail-page-content.component';
import { DateRangeTextComponent } from '../../../../core/versioning/date-range-text/date-range-text.component';
import { NgIf } from '@angular/common';
import { SwitchVersionComponent } from '../../../../core/components/switch-version/switch-version.component';
import { SearchSelectComponent } from '../../../../core/form-components/search-select/search-select.component';
import { MatLabel } from '@angular/material/form-field';
import { LinkIconComponent } from '../../../../core/form-components/link-icon/link-icon.component';
import { SelectComponent } from '../../../../core/form-components/select/select.component';
import { TextFieldComponent } from '../../../../core/form-components/text-field/text-field.component';
import { DateRangeComponent } from '../../../../core/form-components/date-range/date-range.component';
import { BusinessOrganisationSelectComponent } from '../../../../core/form-components/bo-select/business-organisation-select.component';
import { UserDetailInfoComponent } from '../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { TranslatePipe } from '@ngx-translate/core';
import { MainlineDescriptionPipe } from './mainline-description.pipe';
import { LineService } from '../../../../api/service/line.service';

@Component({
  templateUrl: './subline-detail.component.html',
  styleUrls: ['./subline-detail.component.scss'],
  providers: [ValidityService],
  imports: [
    DetailPageContainerComponent,
    ScrollToTopDirective,
    DetailPageContentComponent,
    DateRangeTextComponent,
    NgIf,
    SwitchVersionComponent,
    SearchSelectComponent,
    ReactiveFormsModule,
    MatLabel,
    LinkIconComponent,
    SelectComponent,
    TextFieldComponent,
    DateRangeComponent,
    BusinessOrganisationSelectComponent,
    UserDetailInfoComponent,
    DetailFooterComponent,
    AtlasButtonComponent,
    TranslatePipe,
    MainlineDescriptionPipe,
  ],
})
export class SublineDetailComponent
  implements OnInit, DetailFormComponent, DetailWithCancelEdit
{
  protected readonly Pages = Pages;

  TYPE_OPTIONS: SublineType[] = [];
  CONCESSION_TYPE_OPTIONS = Object.values(SublineConcessionType);

  mainlines$: Observable<Line[]> = of([]);
  currentMainlineSelection?: LineVersionV2;

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
    private sublineService: SublineService,
    private sublineInternalService: SublineInternalService,
    private notificationService: NotificationService,
    private lineService: LineService,
    private permissionService: PermissionService,
    private activatedRoute: ActivatedRoute,
    private validityService: ValidityService,
    private detailHelperService: DetailHelperService,
    private dialogService: DialogService
  ) {}

  ngOnInit() {
    this.versions = this.activatedRoute.snapshot.data.sublineDetail;
    if (this.versions.length == 0) {
      this.isNew = true;
      this.form = SublineFormGroupBuilder.buildFormGroup();
    } else {
      this.isNew = false;
      VersionsHandlingService.addVersionNumbers(this.versions);
      this.maxValidity = VersionsHandlingService.getMaxValidity(this.versions);
      this.selectedVersion =
        VersionsHandlingService.determineDefaultVersionByValidity(
          this.versions
        );
      this.selectedVersionIndex = this.versions.indexOf(this.selectedVersion);

      this.initSelectedVersion();
      this.mainlines$ = this.lineService
        .getLine(this.selectedVersion.mainlineSlnid)
        .pipe(map((value) => [value]));

      this.lineService
        .getLineVersionsV2(this.selectedVersion.mainlineSlnid)
        .subscribe((mainline) => {
          this.currentMainlineSelection =
            VersionsHandlingService.determineDefaultVersionByValidity(mainline);
        });

      this.TYPE_OPTIONS = [this.form.controls.sublineType.value!];
    }
    this.initBoSboidRestriction();
  }

  private initSelectedVersion() {
    this.showVersionSwitch = VersionsHandlingService.hasMultipleVersions(
      this.versions
    );
    this.form = SublineFormGroupBuilder.buildFormGroup(this.selectedVersion);
    if (!this.isNew) {
      this.form.disable();
    }
  }

  initBoSboidRestriction() {
    if (!this.isNew || this.permissionService.isAdmin) {
      this.boSboidRestriction = [];
    } else {
      const permission = this.permissionService.getApplicationUserPermission(
        ApplicationType.Lidi
      );
      if (permission.role === ApplicationRole.Writer) {
        this.boSboidRestriction =
          PermissionService.getSboidRestrictions(permission);
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
      this.form.enable({ emitEvent: false });

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
      const sublineVersion =
        this.form.getRawValue() as unknown as CreateSublineVersionV2;
      this.form.disable();
      if (this.isNew) {
        this.createSubline(sublineVersion);
      } else {
        this.validityService.updateValidity(this.form);
        this.validityService.validate().subscribe((confirmed) => {
          if (confirmed) {
            this.form.disable();
            this.updateSubline(this.selectedVersion.id!, sublineVersion);
          }
        });
      }
    }
  }

  createSubline(sublineVersion: CreateSublineVersionV2): void {
    this.sublineService
      .createSublineVersionV2(sublineVersion)
      .pipe(catchError(this.handleError()))
      .subscribe((version) => {
        this.notificationService.success(
          'LIDI.SUBLINE.NOTIFICATION.ADD_SUCCESS'
        );
        this.router
          .navigate([Pages.LIDI.path, Pages.SUBLINES.path, version.slnid])
          .then(() => this.ngOnInit());
      });
  }

  updateSubline(id: number, sublineVersion: SublineVersionV2): void {
    this.sublineService
      .updateSublineVersionV2(id, sublineVersion)
      .pipe(catchError(this.handleError()))
      .subscribe(() => {
        this.notificationService.success(
          'LIDI.SUBLINE.NOTIFICATION.EDIT_SUCCESS'
        );
        this.router
          .navigate([
            Pages.LIDI.path,
            Pages.SUBLINES.path,
            sublineVersion.slnid,
          ])
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
            this.sublineInternalService
              .revokeSubline(this.selectedVersion.slnid)
              .subscribe(() => {
                this.notificationService.success(
                  'LIDI.SUBLINE.NOTIFICATION.REVOKE_SUCCESS'
                );
                this.router
                  .navigate([
                    Pages.LIDI.path,
                    Pages.SUBLINES.path,
                    this.selectedVersion.slnid,
                  ])
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
            this.sublineInternalService
              .deleteSublines(this.selectedVersion.slnid)
              .subscribe(() => {
                this.notificationService.success(
                  'LIDI.SUBLINE.NOTIFICATION.DELETE_SUCCESS'
                );
                this.back();
              });
          }
        }
      });
  }

  back() {
    this.router.navigate(['..'], { relativeTo: this.activatedRoute }).then();
  }

  searchMainlines(searchString: string) {
    this.mainlines$ = this.lineService
      .getLines(
        undefined,
        [searchString],
        [Status.Validated, Status.InReview, Status.Draft, Status.Withdrawn],
        undefined,
        [ElementType.Line],
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        ['swissLineNumber,ASC']
      )
      .pipe(map((value) => value.objects ?? []));
  }

  mainlineUrl(): string {
    return `${location.origin}/${Pages.LIDI.path}/${Pages.LINES.path}/${
      this.form.get(this.mainlineSlnidFormControlName)?.value
    }`;
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

      this.lineService.getLineVersionsV2(line.slnid!).subscribe((mainline) => {
        this.currentMainlineSelection =
          VersionsHandlingService.determineDefaultVersionByValidity(mainline);
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
        throw new Error('LineType not expected: ' + lineType);
    }
  }
}
