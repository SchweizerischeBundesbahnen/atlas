import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Data, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  template: '',
})
export class RouteToDialogComponent implements OnInit, OnDestroy {
  private ngUnsubscribe = new Subject<void>();

  constructor(
    private readonly dialog: MatDialog,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.route.data.pipe(takeUntil(this.ngUnsubscribe)).subscribe((data) => this.openDialog(data));
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
  }

  private openDialog(data: Data): void {
    const dialogRef = this.dialog.open(data.component, {
      data,
      panelClass: 'route-to-dialog-panel',
    });
    dialogRef
      .afterClosed()
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe(() => this.navigateBack());
  }

  private navigateBack() {
    return this.router.navigate(['..'], { relativeTo: this.route });
  }
}
