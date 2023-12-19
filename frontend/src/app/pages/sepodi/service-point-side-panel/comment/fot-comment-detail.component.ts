import { Component, OnInit } from '@angular/core';
import { DetailFormComponent } from '../../../../core/leave-guard/leave-dirty-form-guard.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, EMPTY, Observable, of, take } from 'rxjs';
import { Pages } from '../../../pages';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { ValidationService } from '../../../../core/validation/validation.service';
import { NotificationService } from '../../../../core/notification/notification.service';
import { ServicePointFotComment, ServicePointsService } from '../../../../api';

export interface FotCommentFormGroup {
  fotComment: FormControl<string | null | undefined>;
  etagVersion: FormControl<number | null | undefined>;
  creationDate: FormControl<string | null | undefined>;
  editionDate: FormControl<string | null | undefined>;
  creator: FormControl<string | null | undefined>;
  editor: FormControl<string | null | undefined>;
}

@Component({
  selector: 'app-service-point-fot-comment',
  templateUrl: './fot-comment-detail.component.html',
  styleUrls: ['./fot-comment-detail.component.scss'],
})
export class FotCommentDetailComponent implements DetailFormComponent, OnInit {
  form!: FormGroup<FotCommentFormGroup>;

  constructor(
    private servicePointService: ServicePointsService,
    private route: ActivatedRoute,
    private router: Router,
    private dialogService: DialogService,
    private notificationService: NotificationService,
  ) {}

  ngOnInit() {
    this.initFormGroup();
    this.servicePointService
      .getFotComment(this.servicePointNumber)
      .subscribe((comment) => this.initFormGroup(comment));
  }

  get servicePointNumber() {
    return this.route.parent!.snapshot.params['id'];
  }

  isFormDirty(): boolean {
    return this.form.dirty;
  }

  initFormGroup(fotComment?: ServicePointFotComment) {
    this.form = new FormGroup<FotCommentFormGroup>({
      fotComment: new FormControl(fotComment?.fotComment, [Validators.maxLength(2000)]),
      etagVersion: new FormControl(fotComment?.etagVersion),
      creationDate: new FormControl(fotComment?.creationDate),
      editionDate: new FormControl(fotComment?.editionDate),
      editor: new FormControl(fotComment?.editor),
      creator: new FormControl(fotComment?.creator),
    });
    this.form.disable();
  }

  closeSidePanel() {
    this.router.navigate([Pages.SEPODI.path]).then();
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.showConfirmationDialog();
    } else {
      this.form.enable();
    }
  }

  showConfirmationDialog() {
    this.confirmLeave()
      .pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          this.ngOnInit();
        }
      });
  }

  confirmLeave(): Observable<boolean> {
    if (this.form.dirty) {
      return this.dialogService.confirm({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      });
    }
    return of(true);
  }

  save() {
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      this.servicePointService
        .saveFotComment(this.servicePointNumber, this.currentComment)
        .pipe(catchError(this.handleError))
        .subscribe((comment) => {
          this.notificationService.success('SEPODI.SERVICE_POINTS.NOTIFICATION.COMMENT_SAVED');
          this.initFormGroup(comment);
        });
    }
  }

  get currentComment(): ServicePointFotComment {
    return {
      fotComment: this.form.controls.fotComment.value
        ? this.form.controls.fotComment.value
        : undefined,
      etagVersion: this.form.controls.etagVersion.value!,
    };
  }

  private handleError = () => {
    this.form.enable();
    return EMPTY;
  };
}
