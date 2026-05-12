import { ActivatedRoute, Router } from '@angular/router';
import { TimetableHearingStatementInternalService } from '../../../../api/service/lidi/timetable-hearing-statement-internal.service';
import { inject } from '@angular/core';
import { HearingStatus, TimetableHearingStatementV2 } from '../../../../api';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import {
  StatementDetailFormGroup,
  StatementSenderFormGroup,
  TimetableHearingStatementBuilder,
  TimetableHearingStatementDocumentGroup,
} from './statement-detail-form-group';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { WhitespaceValidator } from '../../../../core/validation/whitespace/whitespace-validator';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { Pages } from '../../../pages';
import { FileDownloadService } from '../../../../core/components/file-upload/file/file-download.service';

export abstract class StatementDetailBaseComponent {
  protected readonly router = inject(Router);
  protected readonly route = inject(ActivatedRoute);
  protected readonly timetableHearingStatementsService = inject(TimetableHearingStatementInternalService);

  protected ttfnValidOn: Date | undefined = undefined;
  statement: TimetableHearingStatementV2 | undefined;
  protected hearingStatus!: HearingStatus;
  form!: FormGroup<StatementDetailFormGroup>;
  uploadedFiles: File[] = [];

  protected backToOverview() {
    this.router
      .navigate([Pages.TTH.path, this.route.snapshot.params.canton.toLowerCase(), this.hearingStatus.toLowerCase()], {
        queryParams: {
          year: this.statement?.timetableYear,
        },
      })
      .then();
  }

  protected downloadFile(fileName: string) {
    this.timetableHearingStatementsService
      .getStatementDocument(this.statement!.id!, fileName)
      .subscribe((response) => FileDownloadService.downloadFile(fileName, response));
  }

  protected getFormGroup(statement: TimetableHearingStatementV2 | undefined): FormGroup {
    return new FormGroup<StatementDetailFormGroup>({
      id: new FormControl(statement?.id),
      timetableYear: new FormControl(statement?.timetableYear, [Validators.required]),
      statementStatus: new FormControl(statement?.statementStatus, [Validators.required]),
      ttfnid: new FormControl(statement?.ttfnid),
      responsibleTransportCompanies: new FormControl(statement?.responsibleTransportCompanies ?? []),
      oldSwissCanton: new FormControl(statement?.oldSwissCanton),
      swissCanton: new FormControl(statement?.swissCanton, [Validators.required]),
      stopPlace: new FormControl(statement?.stopPlace, [
        AtlasFieldLengthValidator.length_255,
        WhitespaceValidator.blankOrEmptySpaceSurrounding,
      ]),
      statementSender: new FormGroup<StatementSenderFormGroup>({
        firstName: new FormControl(statement?.statementSender?.firstName, [AtlasFieldLengthValidator.length_100]),
        lastName: new FormControl(statement?.statementSender?.lastName, [AtlasFieldLengthValidator.length_100]),
        organisation: new FormControl(statement?.statementSender?.organisation, [AtlasFieldLengthValidator.length_100]),
        zip: new FormControl(statement?.statementSender?.zip, [
          AtlasCharsetsValidator.numeric,
          Validators.min(1000),
          Validators.max(99999),
        ]),
        city: new FormControl(statement?.statementSender?.city, [AtlasFieldLengthValidator.length_50]),
        street: new FormControl(statement?.statementSender?.street, [AtlasFieldLengthValidator.length_100]),
        emails: new FormControl(Array.from(statement?.statementSender?.emails ?? []), [Validators.required]),
      }),
      statement: new FormControl(statement?.statement, [Validators.required, AtlasFieldLengthValidator.statement]),
      anonymousStatement: new FormControl(statement?.anonymousStatement, [AtlasFieldLengthValidator.statement]),
      statementAnonymous: new FormControl(statement?.statementAnonymous),
      publicComment: new FormControl(statement?.publicComment, [AtlasFieldLengthValidator.statement]),
      internalComment: new FormControl(statement?.internalComment, [AtlasFieldLengthValidator.statement]),
      cantonTransferComment: new FormControl(statement?.cantonTransferComment, [AtlasFieldLengthValidator.length_280]),
      topic: new FormControl(statement?.topic, [AtlasFieldLengthValidator.length_255]),
      documents: new FormArray<FormGroup<TimetableHearingStatementDocumentGroup>>(
        statement?.documents
          ?.map((document) => TimetableHearingStatementBuilder.buildTimetableHearingStatementDocumentGroup(document))
          .sort((a, b) => a.getRawValue().fileName!.localeCompare(b.getRawValue().fileName!)) ?? []
      ),
      etagVersion: new FormControl(statement?.etagVersion),
      editor: new FormControl(statement?.editor),
    });
  }
}
