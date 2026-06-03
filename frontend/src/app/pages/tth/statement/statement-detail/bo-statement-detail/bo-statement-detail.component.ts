import { Component, OnInit } from '@angular/core';
import { StatementDataComponent } from '../../statement-data/statement-data.component';
import { FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TimetableHearingStatementDocumentGroup } from '../statement-detail-form-group';
import { CommentComponent } from '../../../../../core/form-components/comment/comment.component';
import { DetailPageContainerComponent } from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../../core/components/detail-page-content/detail-page-content.component';
import { AtlasButtonComponent } from '../../../../../core/components/button/atlas-button.component';
import { DetailFooterComponent } from '../../../../../core/components/detail-footer/detail-footer.component';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { FileComponent } from '../../../../../core/components/file-upload/file/file.component';
import { DetailFormComponent } from '../../../../../core/leave-guard/leave-dirty-form-guard.service';
import { StatementDetailBaseComponent } from '../statement-detail-base.component';
import { Pages } from '../../../../pages';

@Component({
  selector: 'atlas-bo-statement-detail',
  imports: [
    StatementDataComponent,
    CommentComponent,
    DetailPageContainerComponent,
    DetailPageContentComponent,
    AtlasButtonComponent,
    DetailFooterComponent,
    AtlasLabelFieldComponent,
    FileComponent,
    FormsModule,
    ReactiveFormsModule,
  ],
  templateUrl: './bo-statement-detail.component.html',
  styleUrls: ['./bo-statement-detail.component.scss'],
})
export class BoStatementDetailComponent extends StatementDetailBaseComponent implements OnInit, DetailFormComponent {
  private _anonymDocuments!: FormGroup<TimetableHearingStatementDocumentGroup>[];

  set anonymDocuments(value: FormGroup<TimetableHearingStatementDocumentGroup>[]) {
    this._anonymDocuments = value;
  }

  get anonymDocuments(): FormGroup<TimetableHearingStatementDocumentGroup>[] {
    return this._anonymDocuments;
  }

  ngOnInit() {
    this.statement = this.route.snapshot.data.statement;
    this.form = this.getFormGroup(this.statement);
    this.form.disable();
    this.hearingStatus = this.route.snapshot.data.hearingStatus;
    this.anonymDocuments = this.form.controls.documents.controls.filter(
      (control: FormGroup<TimetableHearingStatementDocumentGroup>) => {
        return control.getRawValue().anonymous === true;
      }
    );
    return this._anonymDocuments;
  }

  getStatementControlName(): string {
    const isStatementAnonymous = this.form.controls.statementAnonymous.getRawValue();
    if (isStatementAnonymous) {
      return 'statement';
    }
    return 'anonymousStatement';
  }

  goToDossier() {
    this.router
      .navigate(['../..', Pages.TTH_DOSSIERS.path, this.statement!.dossierId], {
        relativeTo: this.route,
      })
      .then();
  }
}
