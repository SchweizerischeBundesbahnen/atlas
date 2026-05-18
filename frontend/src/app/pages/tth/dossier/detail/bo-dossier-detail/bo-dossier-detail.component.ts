import { Component, inject, OnInit } from '@angular/core';
import { AtlasButtonComponent } from '../../../../../core/components/button/atlas-button.component';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { AtlasSpacerComponent } from '../../../../../core/components/spacer/atlas-spacer.component';
import { DetailFooterComponent } from '../../../../../core/components/detail-footer/detail-footer.component';
import {
  DetailPageContainerComponent
} from '../../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../../core/components/detail-page-content/detail-page-content.component';
import { ScrollToTopDirective } from '../../../../../core/scroll-to-top/scroll-to-top.directive';
import { StatementSelectComponent } from '../../statement-select/statement-select.component';
import { TextFieldComponent } from '../../../../../core/form-components/text-field/text-field.component';
import { TranslatePipe } from '@ngx-translate/core';
import { DetailFormComponent } from '../../../../../core/leave-guard/leave-dirty-form-guard.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormGroup } from '@angular/forms';
import { BoAnswerFormGroupBuilder, DossierDetailFormGroup, DossierFormGroupBuilder, } from '../dossier-detail-form-group';
import { TthDossier } from '../../../../../api/model/tthDossier';
import { DateComponent } from '../../../../../core/form-components/date/date.component';
import { CommentComponent } from '../../../../../core/form-components/comment/comment.component';
import { DossierInternalService } from '../../../../../api/service/workflow/dossier-internal.service';
import { BoAnswer } from '../../../../../api/model/boAnswer';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { ValidationService } from '../../../../../core/validation/validation.service';
import { OpenBoDossierInMailService } from './open-bo-dossier-in-mail.service';

@Component({
  selector: 'atlas-bo-dossier-detail',
  imports: [
    AtlasButtonComponent,
    AtlasLabelFieldComponent,
    AtlasSpacerComponent,
    DetailFooterComponent,
    DetailPageContainerComponent,
    DetailPageContentComponent,
    ScrollToTopDirective,
    StatementSelectComponent,
    TextFieldComponent,
    TranslatePipe,
    DateComponent,
    CommentComponent,
  ],
  providers: [OpenBoDossierInMailService, TranslatePipe],
  templateUrl: './bo-dossier-detail.component.html',
  styleUrls: ['./bo-dossier-detail.component.scss'],
})
export class BoDossierDetailComponent implements DetailFormComponent, OnInit {
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly dossierInternalService = inject(DossierInternalService);
  private readonly notificationService = inject(NotificationService);
  private readonly openBoDossierInMailService = inject(OpenBoDossierInMailService);

  dossierForm!: FormGroup<DossierDetailFormGroup>;
  form = BoAnswerFormGroupBuilder.buildFormGroup(null);
  currentDossier?: TthDossier;

  private _selectedStatements!: number[];
  private _cantonQuestion!: string;
  private _isDossierStatusBoCheck = false;

  get selectedStatements(): number[] {
    return this._selectedStatements;
  }

  set selectedStatements(value: number[]) {
    this._selectedStatements = value;
    this.dossierForm.controls.statementIds.setValue(value);
  }

  get cantonQuestion(): string {
    return this._cantonQuestion;
  }

  set cantonQuestion(value: string) {
    this._cantonQuestion = value;
  }

  get isDossierStatusBoCheck(): boolean {
    return this._isDossierStatusBoCheck;
  }

  set isDossierStatusBoCheck(value: boolean) {
    this._isDossierStatusBoCheck = value;
  }

  ngOnInit() {
    this.initDossierForm();
    if (this.currentDossier) {
      this.selectedStatements = this.currentDossier.statementIds;
      const tthDossierQuestion = this.currentDossier.questions[0];
      this.cantonQuestion = tthDossierQuestion.question ?? '';
      this.dossierForm.disable();
      if (this.currentDossier.dossierStatus !== 'DOSSIER_BO_CHECK') {
        this.form.disable();
        this.isDossierStatusBoCheck = false;
        this.form.controls.answerToCanton.setValue(tthDossierQuestion.answerToCanton!);
      } else {
        this.form.enable();
        this.isDossierStatusBoCheck = true;
      }
    }
  }

  openInMail() {
    this.openBoDossierInMailService.openDossierInMail({
      id: this.currentDossier!.id!,
      topic: this.currentDossier!.topic,
      statementIds: this.currentDossier!.statementIds,
      cantonQuestion: this.cantonQuestion,
      swissCanton: this.currentDossier!.swissCanton,
    });
  }

  back() {
    this.router.navigate(['../..'], { relativeTo: this.activatedRoute }).then();
  }

  sendAnswer() {
    ValidationService.validateForm(this.form);
    if (this.form.valid) {
      const questionId = this.currentDossier?.questions[0].id;
      const boAnswer: BoAnswer = {
        answerToCanton: this.form.controls.answerToCanton.getRawValue()!,
      };
      this.dossierInternalService.answerQuestion(questionId!, boAnswer).subscribe(() => {
        this.notificationService.success('TTH.DOSSIER.NOTIFICATION.SENT_TO_CANTON');
        this.form.disable();
        this.form.markAsPristine();
        this.isDossierStatusBoCheck = false;
      });
    }
  }

  private initDossierForm() {
    this.currentDossier = this.activatedRoute.snapshot.data.dossier;
    this.dossierForm = DossierFormGroupBuilder.buildFormGroup(this.currentDossier);
  }
}
