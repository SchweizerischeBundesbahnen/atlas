import { inject, Injectable, signal } from '@angular/core';
import { BusinessOrganisation, TransportCompanyBoRelation } from '../../../../api';
import { Observable } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { DialogData } from '../../../../core/components/dialog/dialog.data';
import { Moment } from 'moment/moment';
import { form, required, validateTree } from '@angular/forms/signals';
import { required as requiredValue } from '../../../../core/util/values';
import moment from 'moment';
import { NotificationService } from '../../../../core/notification/notification.service';
import { TransportCompanyRelationInternalService } from '../../../../api/service/bodi/transport-company-relation-internal.service';
import { DialogService } from '../../../../core/components/dialog/dialog.service';

type TransportCompanyRelationFormModel = {
  businessOrganisation: BusinessOrganisation | null;
  validFrom: Moment | null;
  validTo: Moment | null;
};

type TransportCompanyRelationFormModelValidated = {
  businessOrganisation: BusinessOrganisation;
  validFrom: Moment;
  validTo: Moment;
};

@Injectable()
export class TransportCompanyDetailFacade {
  // deps
  private readonly notificationService = inject(NotificationService);
  private readonly transportCompanyRelationInternalService = inject(TransportCompanyRelationInternalService);
  private readonly dialogService = inject(DialogService);

  // state
  private readonly transportCompanyRelations = signal<TransportCompanyBoRelation[]>([]);
  readonly transportCompanyRelationsReadonly = this.transportCompanyRelations.asReadonly();
  private readonly selectedTransportCompanyRelationIndex = signal(-1);
  readonly selectedTransportCompanyRelationIndexReadonly = this.selectedTransportCompanyRelationIndex.asReadonly();
  readonly selectRelation = (index: number) => {
    this.selectedTransportCompanyRelationIndex.set(index);
  };
  private readonly unselectRelation = () => {
    this.selectedTransportCompanyRelationIndex.set(-1);
  };
  private readonly isRelationSelected = () => this.selectedTransportCompanyRelationIndex() !== -1;
  private readonly getSelectedRelation = () =>
    this.isRelationSelected() ? this.transportCompanyRelations()[this.selectedTransportCompanyRelationIndex()] : null;

  private readonly editMode = signal(false);
  private readonly leaveEditMode = () => {
    if (!this.editMode()) {
      throw new Error('Cannot leave edit mode when in read only mode');
    }
    this.toggleEditMode();
  };
  readonly isEditMode = this.editMode.asReadonly();
  readonly toggleEditMode = () => {
    this.editMode.update((value) => !value);
  };

  private readonly emptyFormValue = {
    businessOrganisation: null,
    validFrom: null,
    validTo: null,
  };
  private readonly transportCompanyRelationFormModel = signal<TransportCompanyRelationFormModel>({
    ...this.emptyFormValue,
  });
  // todo: can dates be null?
  readonly transportCompanyRelationForm = form(this.transportCompanyRelationFormModel, (schemaPath) => {
    required(schemaPath.businessOrganisation);
    validateTree(schemaPath, (ctx) => {
      const validFrom = ctx.valueOf(schemaPath.validFrom);
      const validTo = ctx.valueOf(schemaPath.validTo);
      if (validFrom !== null && validTo !== null && validFrom.isAfter(validTo)) {
        return [
          {
            kind: 'dateRange',
            message: 'ValidFrom must be before validTo', // todo: translate
            fieldTree: ctx.fieldTree.validFrom,
          },
          {
            kind: 'dateRange',
            message: 'ValidTo must be after validFrom', // todo: translate
            fieldTree: ctx.fieldTree.validTo,
          },
        ];
      }
      return null;
    });
  });

  // orchestration
  save(): void {
    this.transportCompanyRelationForm().markAsTouched();
    if (this.transportCompanyRelationForm().invalid()) {
      return;
    }

    const validatedForm: TransportCompanyRelationFormModelValidated = {
      businessOrganisation: this.transportCompanyRelationForm.businessOrganisation().value()!,
      validFrom: this.transportCompanyRelationForm.validFrom().value()!,
      validTo: this.transportCompanyRelationForm.validTo().value()!,
    };

    if (this.isRelationSelected()) {
      this.handleSave(this.updateExistingRelation(validatedForm));
    } else {
      this.handleSave(this.createRelation(validatedForm));
    }
  }

  private handleSave(save$: Observable<TransportCompanyBoRelation | void>) {
    save$
      .pipe(
        switchMap(() => this.reloadRelations()),
        tap(() => {
          this.leaveEditMode();
          this.transportCompanyRelationForm().reset({ ...this.emptyFormValue });
          const successMsg = this.isRelationSelected() ? 'RELATION.UPDATE_SUCCESS_MSG' : 'RELATION.ADD_SUCCESS_MSG';
          this.notificationService.success(successMsg);
          this.unselectRelation();
        })
      )
      .subscribe();
  }

  private createRelation(validatedForm: TransportCompanyRelationFormModelValidated) {
    return this.transportCompanyRelationInternalService.createTransportCompanyRelation({
      transportCompanyId: this.transportCompanyForm.id().value(), // todo: include form into facade
      sboid: validatedForm.businessOrganisation.sboid!,
      validFrom: moment(validatedForm.validFrom).toDate(),
      validTo: moment(validatedForm.validTo).toDate(),
    });
  }

  private updateExistingRelation(validatedForm: TransportCompanyRelationFormModelValidated) {
    return this.transportCompanyRelationInternalService.updateTransportCompanyRelation({
      id: this.relationId, // todo: find from relations list and selected index
      validFrom: moment(validatedForm.validFrom).toDate(),
      validTo: moment(validatedForm.validTo).toDate(),
    });
  }

  updateRelation() {
    const relation = requiredValue(this.getSelectedRelation(), 'No relation selected');
    this.transportCompanyRelationFormModel.set({
      businessOrganisation: relation.businessOrganisation ?? null,
      validFrom: moment(relation.validFrom),
      validTo: moment(relation.validTo),
    });
  }

  deleteRelation(): void {
    const relation = requiredValue(this.getSelectedRelation(), 'No relation selected');
    this.transportCompanyRelationInternalService
      .deleteTransportCompanyRelation(relation.id!)
      .pipe(
        switchMap(() =>
          this.reloadRelations().pipe(
            tap(() => {
              this.unselectRelation();
              this.notificationService.success('RELATION.DELETE_SUCCESS_MSG');
            })
          )
        )
      )
      .subscribe();
  }

  private reloadRelations(): Observable<TransportCompanyBoRelation[]> {
    return this.transportCompanyRelationInternalService
      .getTransportCompanyBoRelations(this.transportCompanyForm.id().value())
      .pipe(tap((transportCompanyRelations) => this.transportCompanyRelations.set(transportCompanyRelations)));
  }

  leaveEditModeWithDialog() {
    if (!this.transportCompanyRelationForm().dirty()) {
      this.cancelEdit();
      return;
    }

    this.dialogService
      .openDialogDataWithConfirmationResult({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      } satisfies DialogData)
      .subscribe((result) => {
        if (result) {
          this.cancelEdit();
        }
      });
  }

  private cancelEdit() {
    this.leaveEditMode();
    this.transportCompanyRelationForm().reset({ ...this.emptyFormValue });
  }
}
