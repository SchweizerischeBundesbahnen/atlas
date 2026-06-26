import { ChangeDetectionStrategy, Component, inject, input, output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatIconButton } from '@angular/material/button';
import { HearingStatus, TimetableHearingStatementV2 } from '../../../../api';
import { Pages } from '../../../pages';
import { TranslatePipe } from '@ngx-translate/core';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { StatementShareService } from '../statement-share-service';
import { NgClass, NgOptimizedImage } from '@angular/common';
import { TableColumn } from '../../../../core/components/table/table-column';
import { DialogData } from '../../../../core/components/dialog/dialog.data';
import { ChangeCantonData } from '../tth-change-canton-dialog/model/change-canton-data';
import { TthChangeCantonDialogComponent } from '../tth-change-canton-dialog/tth-change-canton-dialog.component';
import {
  AddToDossierData,
  AddToDossierDialogComponent,
} from '../../dossier/add-to-dossier-dialog/add-to-dossier-dialog.component';

@Component({
  selector: 'atlas-statement-overview-menu',
  templateUrl: './statement-overview-menu.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
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

  readonly hearingStatus = input(HearingStatus.Active);
  readonly row = input.required<TimetableHearingStatementV2>();
  readonly column = input.required<TableColumn<TimetableHearingStatementV2>>();

  readonly reloadTable = output();

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
    const dialogData: AddToDossierData = {
      title: 'TTH.BUTTON.ADD_TO_DOSSIER',
      message: 'TTH.DOSSIER.ADD_TO_DOSSIER_INFO',
      cancelText: 'COMMON.CANCEL',
      confirmText: 'COMMON.SAVE',
      statement: statement,
    };

    this.dialogService
      .openDialogDataWithConfirmationResult(dialogData, AddToDossierDialogComponent)
      .subscribe((result) => {
        if (result) {
          this.reloadTable.emit();
        }
      });
  }

  switchCanton(statement: TimetableHearingStatementV2) {
    const dialogData: ChangeCantonData = {
      title: 'TTH.STATEMENT.DIALOG.TITLE',
      message: 'TTH.DIALOG.MULTIPLE_STATUS_CHANGE_MESSAGE',
      cancelText: 'TTH.DIALOG.BACK',
      confirmText: 'TTH.DIALOG.CANTON_CHANGE',
      tths: [statement],
    };
    this.dialogService
      .openDialogDataWithConfirmationResult(dialogData, TthChangeCantonDialogComponent)
      .subscribe((result) => {
        if (result) {
          this.reloadTable.emit();
        }
      });
  }
}
