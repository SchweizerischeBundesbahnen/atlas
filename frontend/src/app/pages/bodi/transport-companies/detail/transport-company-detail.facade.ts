import { computed, inject, Injectable, signal } from '@angular/core';
import { TransportCompanyBoRelation } from '../../../../api';
import { Observable } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { required as requiredValue } from '../../../../core/util/values';
import moment from 'moment';
import { TransportCompanyRelationInternalService } from '../../../../api/service/bodi/transport-company-relation-internal.service';
import { TransportCompanyRelationFormModelValidated } from './transport-company-detail.component';

@Injectable()
export class TransportCompanyDetailFacade {
  // DEPS
  private readonly transportCompanyRelationInternalService = inject(TransportCompanyRelationInternalService);

  // STATE
  init(relations: TransportCompanyBoRelation[], transportCompanyId: number) {
    this.transportCompanyRelations.set(relations);
    this.transportCompanyId = transportCompanyId;
  }

  private transportCompanyId = 0;

  private readonly transportCompanyRelations = signal<TransportCompanyBoRelation[]>([]);
  readonly transportCompanyRelationsReadonly = this.transportCompanyRelations.asReadonly();

  private readonly selectedRelationId = signal<number | null>(null);
  readonly selectedRelationIndex = computed(() =>
    this.transportCompanyRelationsReadonly().findIndex((relation) => relation.id === this.selectedRelationId())
  );
  readonly isRelationSelected = computed(() => this.selectedRelationId() !== null);
  readonly selectedRelation = computed(
    () => this.transportCompanyRelationsReadonly().find((relation) => relation.id === this.selectedRelationId()) ?? null
  );
  selectRelation(index: number) {
    this.selectedRelationId.set(this.transportCompanyRelationsReadonly()[index].id!);
  }
  unselectRelation() {
    this.selectedRelationId.set(null);
  }

  private readonly editMode = signal(false);
  readonly isEditMode = this.editMode.asReadonly();
  leaveEditMode() {
    this.editMode.set(false);
  }
  toggleEditMode() {
    this.editMode.update((value) => !value);
  }

  // ACTIONS
  save(validatedForm: TransportCompanyRelationFormModelValidated) {
    if (this.isRelationSelected()) {
      return this.handleSave(this.updateExistingRelation(validatedForm));
    } else {
      return this.handleSave(this.createRelation(validatedForm));
    }
  }

  private handleSave(save$: Observable<TransportCompanyBoRelation | void>) {
    return save$.pipe(
      switchMap(() => this.reloadRelations()),
      tap(() => {
        this.leaveEditMode();
        this.unselectRelation();
      })
    );
  }

  private createRelation(validatedForm: TransportCompanyRelationFormModelValidated) {
    return this.transportCompanyRelationInternalService.createTransportCompanyRelation({
      transportCompanyId: validatedForm.transportCompanyId,
      sboid: validatedForm.businessOrganisation.sboid!,
      validFrom: moment(validatedForm.validFrom).toDate(),
      validTo: moment(validatedForm.validTo).toDate(),
    });
  }

  private updateExistingRelation(validatedForm: TransportCompanyRelationFormModelValidated) {
    return this.transportCompanyRelationInternalService.updateTransportCompanyRelation({
      id: this.selectedRelation()?.id,
      validFrom: moment(validatedForm.validFrom).toDate(),
      validTo: moment(validatedForm.validTo).toDate(),
    });
  }

  deleteRelation() {
    const relation = requiredValue(this.selectedRelation(), 'No relation selected');
    return this.transportCompanyRelationInternalService.deleteTransportCompanyRelation(relation.id!).pipe(
      switchMap(() =>
        this.reloadRelations().pipe(
          tap(() => {
            this.unselectRelation();
          })
        )
      )
    );
  }

  private reloadRelations() {
    return this.transportCompanyRelationInternalService
      .getTransportCompanyBoRelations(this.transportCompanyId)
      .pipe(tap((transportCompanyRelations) => this.transportCompanyRelations.set(transportCompanyRelations)));
  }
}
