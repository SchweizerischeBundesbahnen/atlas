import { Component, computed, inject, input, output } from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { NgClass, NgOptimizedImage, NgTemplateOutlet } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { HearingStatus } from '../../../../api';
import { TableColumn } from '../../../../core/components/table/table-column';
import { DossierStatus } from '../../../../api/model/dossierStatus';
import { TthDossier } from '../../../../api/model/tthDossier';
import { DossierInternalService } from '../../../../api/service/workflow/dossier-internal.service';
import { NotificationService } from '../../../../core/notification/notification.service';

@Component({
  selector: 'atlas-tth-dossier-overview-menu',
  imports: [
    MatIconButton,
    MatMenu,
    MatMenuItem,
    NgOptimizedImage,
    TranslatePipe,
    MatMenuTrigger,
    NgClass,
    NgTemplateOutlet,
  ],
  templateUrl: './tth-dossier-overview-menu.component.html',
})
export class TthDossierOverviewMenuComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly dialogService = inject(DialogService);
  private readonly dossierInternalService = inject(DossierInternalService);
  private readonly notificationService = inject(NotificationService);

  readonly hearingStatus = input(HearingStatus.Active);
  readonly row = input.required<TthDossier>();
  readonly column = input.required<TableColumn<TthDossier>>();

  allowedDossierStatusForDissolve = [DossierStatus.Accepted, DossierStatus.Rejected, DossierStatus.Moved];

  isHearingStatusActive = computed(() => this.hearingStatus() === HearingStatus.Active);

  isDossierCancelable = computed(() => this.row().dossierStatus === DossierStatus.Added);

  isDossierDissolvable = computed(() => this.allowedDossierStatusForDissolve.includes(this.row().dossierStatus!));

  isDossierCompletable = computed(() => this.isDossierCancelable() || this.isDossierDissolvable());

  readonly reloadTable = output();

  completeDossier(status: DossierStatus) {
    this.dialogService
      .openDialogDataWithConfirmationResult({
        title: 'TTH.DOSSIER.NOTIFICATION.COMPLETE_TITLE',
        message: 'TTH.DOSSIER.NOTIFICATION.COMPLETE_MESSAGE',
        confirmText: 'DIALOG.OK',
        cancelText: 'DIALOG.CANCEL',
      })
      .subscribe((confirmed) => {
        if (confirmed) {
          this.dossierInternalService.completeDossier(this.row().id!, status).subscribe(() => {
            this.notificationService.success('TTH.DOSSIER.NOTIFICATION.EDIT_SUCCESS');
            this.router
              .navigate(['dossiers'], {
                relativeTo: this.route.parent,
              })
              .then();
          });
        }
      });
  }
}
