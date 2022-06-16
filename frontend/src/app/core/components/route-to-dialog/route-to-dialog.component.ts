import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Data, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { RouteToDialogService } from './route-to-dialog.service';
import { DialogReference } from './dialog-reference';

const DIALOG_WIDTH = '1440px';

@Component({
  template: '',
  styleUrls: ['route-to-dialog.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class RouteToDialogComponent implements OnInit, OnDestroy {
  private ngUnsubscribe = new Subject<void>();

  constructor(
    private readonly routeToDialogService: RouteToDialogService,
    private readonly dialog: MatDialog,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.route.data.pipe(takeUntil(this.ngUnsubscribe)).subscribe((data) => this.openDialog(data));
  }

  ngOnDestroy() {
    this.routeToDialogService.closeDialog();
    this.routeToDialogService.clearDialogRef();
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  private openDialog(data: Data): void {
    if (this.routeToDialogService.hasDialog()) {
      const dialogRef = this.routeToDialogService.getDialog();
      dialogRef.componentInstance.dialogData = data;
      dialogRef.componentInstance.ngOnInit();
    } else {
      const dialogRef = this.dialog.open(data.component, {
        data,
        panelClass: 'route-to-dialog-panel',
        disableClose: true,
        backdropClass: 'route-to-dialog-backdrop',
        width: DIALOG_WIDTH,
        minWidth: DIALOG_WIDTH,
      });
      dialogRef
        .afterClosed()
        .pipe(takeUntil(this.ngUnsubscribe))
        .subscribe(() => this.navigateBack());

      this.routeToDialogService.setDialogRef(dialogRef);

      this.setDialogWrapperClass(dialogRef);
    }
  }

  private setDialogWrapperClass(dialogRef: DialogReference) {
    // see how to override mat-dialog-wrapper: https://github.com/angular/components/issues/7471
    if (dialogRef._ref.overlayRef.overlayElement) {
      dialogRef._ref.overlayRef.overlayElement.parentElement.className +=
        ' route-to-dialog-wrapper';
    }
  }

  private navigateBack() {
    this.routeToDialogService.clearDialogRef();
    return this.router.navigate(['..'], { relativeTo: this.route });
  }
}
