import { Component, inject, input, output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatIconButton } from '@angular/material/button';
import { TthChangeCantonDialogService } from '../tth-change-canton-dialog/service/tth-change-canton-dialog.service';
import { HearingStatus, TimetableHearingStatementV2 } from '../../../../api';
import { Pages } from '../../../pages';
import { TranslatePipe } from '@ngx-translate/core';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { StatementShareService } from '../statement-share-service';
import { NgClass, NgOptimizedImage } from '@angular/common';
import { TableColumn } from '../../../../core/components/table/table-column';
import { AddToDossierDialogService } from '../../dossier/add-to-dossier-dialog/add-to-dossier-dialog.service';
import { DialogData } from '../../../../core/components/dialog/dialog.data';

@Component({
  selector: 'atlas-statement-overview-menu',
  templateUrl: './statement-overview-menu.component.html',
  imports: [
    TranslatePipe,
    MatMenuTrigger,
    MatIconButton,
    MatMenu,
    MatMenuItem,
    TranslatePipe,
    NgClass,
    NgOptimizedImage,
  ],
})
export class StatementOverviewMenuComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly dialogService = inject(DialogService);
  private readonly statementShareService = inject(StatementShareService);
  private readonly tthChangeCantonDialogService = inject(
    TthChangeCantonDialogService
  );
  private readonly addToDossierDialogService = inject(
    AddToDossierDialogService
  );

  hearingStatus = input(HearingStatus.Active);
  row = input.required<TimetableHearingStatementV2>();
  column = input.required<TableColumn<TimetableHearingStatementV2>>();

  reloadTable = output();

  duplicate($event: TimetableHearingStatementV2) {
    this.dialogService
      .openDialogDataWithConfirmationResult({
        title: 'TTH.DUPLICATE.DIALOG.TITLE',
        message: 'TTH.DUPLICATE.DIALOG.MESSAGE',
        cancelText: 'TTH.DUPLICATE.DIALOG.CANCEL',
        confirmText: 'TTH.DUPLICATE.DIALOG.CONFIRM',
      } satisfies DialogData)
      .subscribe((confirmed) => {
        if (confirmed) {
          this.duplicateStatement($event);
        }
      });
  }

  duplicateStatement(statement: TimetableHearingStatementV2) {
    this.statementShareService.statement = statement;
    this.router
      .navigate(['add'], {
        relativeTo: this.route,
      })
      .then();
  }

  createDossier(statement: TimetableHearingStatementV2) {
    this.router
      .navigate(['..', Pages.TTH_DOSSIERS.path, 'add'], {
        relativeTo: this.route,
        queryParams: { statementIds: [statement.id!] },
      })
      .then();
  }

  addToDossier(statement: TimetableHearingStatementV2) {
    this.addToDossierDialogService.openDialog(statement).subscribe((result) => {
      if (result) {
        this.reloadTable.emit();
      }
    });
  }

  switchCanton(statement: TimetableHearingStatementV2) {
    this.tthChangeCantonDialogService
      .onClick(undefined, [statement])
      .subscribe((result) => {
        if (result) {
          this.reloadTable.emit();
        }
      });
  }
}
