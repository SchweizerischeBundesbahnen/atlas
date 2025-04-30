import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { take } from 'rxjs';
import { DialogService } from '../../../../../../core/components/dialog/dialog.service';
import { Pages } from '../../../../../pages';

@Injectable({
  providedIn: 'root',
})
export class ReferencePointCreationHintService {
  constructor(
    private dialogService: DialogService,
    private router: Router
  ) {}

  showHint() {
    this.dialogService
      .confirm({
        title: 'PRM.REFERENCE_POINTS.HINT_DIALOG.TITLE',
        message: 'PRM.REFERENCE_POINTS.HINT_DIALOG.MESSAGE',
        cancelText: 'PRM.REFERENCE_POINTS.HINT_DIALOG.CANCEL_TEXT',
        confirmText: 'PRM.REFERENCE_POINTS.HINT_DIALOG.CONFIRM_TEXT',
      })
      .pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          const url = this.router.url.substring(
            0,
            this.router.url.lastIndexOf('/')
          );
          this.router.navigate([url, Pages.REFERENCE_POINT.path, 'add']).then();
        }
      });
  }
}
