import {
  AbstractControl,
  FormArray,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { TimetableHearingStatementV2 } from '../../../../../../api';
import { TimetableHearingStatementDataProtection } from '../../../../../../api/model/timetableHearingStatementDataProtection';
import { AtlasFieldLengthValidator } from '../../../../../../core/validation/field-lengths/atlas-field-length-validator';

export interface StatementDataProtectionFormGroup {
  id: FormControl<number | null | undefined>;
  anonymousStatement: FormControl<string | null | undefined>;
  hasStatementPersonalInformation: FormControl<boolean | null | undefined>;
}

export interface StatementDocumentDataProtectionFormGroup {
  documents: FormArray<FormGroup<DocumentDataProtectionFormGroup>>;
}

export interface DocumentDataProtectionFormGroup {
  id: FormControl<number | null | undefined>;
  hasDocumentPersonalInformation: FormControl<boolean | null | undefined>;
}

export class StatementDataProtectionFormGroupBuilder {
  static buildStatementGroup(
    timetableHearingStatement: TimetableHearingStatementV2
  ): FormGroup<StatementDataProtectionFormGroup> {
    return new FormGroup<StatementDataProtectionFormGroup>(
      {
        id: new FormControl(timetableHearingStatement.id),
        anonymousStatement: new FormControl(
          timetableHearingStatement.anonymousStatement
            ? timetableHearingStatement.anonymousStatement
            : timetableHearingStatement.statement,
          [AtlasFieldLengthValidator.statement]
        ),
        hasStatementPersonalInformation: new FormControl(
          timetableHearingStatement.statementAnonymous !== null
            ? !timetableHearingStatement.statementAnonymous
            : undefined,
          [Validators.required]
        ),
      },
      [this.anonymousStatementChanged(timetableHearingStatement.statement)]
    );
  }

  static anonymousStatementChanged(originalStatement: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const formGroup = control as FormGroup<StatementDataProtectionFormGroup>;

      const hasPersonalInformation = formGroup.controls.hasStatementPersonalInformation.value;
      const anonymizedStatement = formGroup.controls.anonymousStatement.value;

      if (hasPersonalInformation && anonymizedStatement === originalStatement) {
        return {
          NO_ANONYMIZATION_DETECTED: true,
        };
      }
      return null;
    };
  }

  static buildDocumentGroup(
    timetableHearingStatement: TimetableHearingStatementV2
  ): FormGroup<StatementDocumentDataProtectionFormGroup> {
    return new FormGroup<StatementDocumentDataProtectionFormGroup>({
      documents: new FormArray(
        timetableHearingStatement.documents?.map(
          (document) =>
            new FormGroup<DocumentDataProtectionFormGroup>({
              id: new FormControl(document.id),
              hasDocumentPersonalInformation: new FormControl(
                document.anonymous !== null ? !document.anonymous : undefined,
                [Validators.required]
              ),
            })
        ) ?? []
      ),
    });
  }

  static toModel(
    documentFormGroup: FormGroup<StatementDocumentDataProtectionFormGroup>,
    statementFormGroup: FormGroup<StatementDataProtectionFormGroup>
  ): TimetableHearingStatementDataProtection {
    const statementHasPersonalInformation = statementFormGroup.controls.hasStatementPersonalInformation.value!;
    return {
      id: statementFormGroup.controls.id.value!,
      statementAnonymous: !statementHasPersonalInformation,
      anonymousStatement: statementHasPersonalInformation
        ? statementFormGroup.controls.anonymousStatement.value!
        : undefined,
      documents: documentFormGroup.value.documents!.map((formDocument) => {
        return {
          id: formDocument.id!,
          fileName: '',
          fileSize: 0,
          anonymous: !formDocument.hasDocumentPersonalInformation,
        };
      }),
    };
  }
}
