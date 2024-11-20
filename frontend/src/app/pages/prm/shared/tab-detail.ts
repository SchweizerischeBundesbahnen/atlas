import { NotificationService } from '../../../core/notification/notification.service';
import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  DetailHelperService,
  DetailWithCancelEdit,
} from '../../../core/detail/detail-helper.service';
import { FormGroup } from '@angular/forms';
import { ValidityService } from '../../sepodi/validity/validity.service';
import { catchError, EMPTY, finalize, from, Observable, take } from 'rxjs';
import { tap } from 'rxjs/operators';

@Component({
  template: '',
})
export abstract class TabDetail<T> implements DetailWithCancelEdit, OnInit {
  protected readonly notificationService: NotificationService = inject(NotificationService);
  protected readonly router: Router = inject(Router);
  protected readonly route: ActivatedRoute = inject(ActivatedRoute);
  protected readonly detailHelperService: DetailHelperService = inject(DetailHelperService);
  protected readonly validityService: ValidityService = inject(ValidityService);

  form: FormGroup = new FormGroup({});
  isNew: boolean = true;

  protected versions: T[] = [];
  selectedVersionIndex: number = 0;
  selectedVersion!: T;
  protected saving = false;

  abstract initForm(): void;
  abstract saveProcess(): Observable<object>;
  abstract ngOnInit(): void;

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

  switchVersion(newIndex: number) {
    this.selectedVersionIndex = newIndex;
    this.selectedVersion = this.versions[newIndex];
    this.initForm();
  }

  save() {
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

  notificateAndNavigate(notification: string, routeParam: string) {
    this.notificationService.success(notification);
    return from(
      this.router.navigate(['..', routeParam], {
        relativeTo: this.route.parent,
      }),
    );
  }
}
