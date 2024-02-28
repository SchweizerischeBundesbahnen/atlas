import { Component, Input, OnInit } from '@angular/core';
import { DetailFormComponent } from '../../../../../core/leave-guard/leave-dirty-form-guard.service';
import {
  PersonWithReducedMobilityService,
  ReadRelationVersion,
  RelationVersion,
  StandardAttributeType,
  StepFreeAccessAttributeType,
  TactileVisualAttributeType,
} from '../../../../../api';
import { Observable, of, take } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { VersionsHandlingService } from '../../../../../core/versioning/versions-handling.service';
import { FormGroup } from '@angular/forms';
import { RelationFormGroup, RelationFormGroupBuilder } from './relation-form-group';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { NotificationService } from '../../../../../core/notification/notification.service';

@Component({
  selector: 'app-relation-detail',
  templateUrl: './relation-detail.component.html',
})
export class RelationDetailComponent implements OnInit, DetailFormComponent {
  @Input() elementSloid!: string;
  @Input() referencePointSloid!: string; // todo: on refpoint change

  relations$: Observable<Array<ReadRelationVersion>> = of();
  currentRelation$: Observable<ReadRelationVersion> = of();

  form?: FormGroup<RelationFormGroup>;
  currentRelationId = -1;

  stepFreeAccessOptions = Object.values(StepFreeAccessAttributeType);
  tactileVisualMarkOptions = Object.values(TactileVisualAttributeType);
  contrastingAreaOptions = Object.values(StandardAttributeType);

  businessOrganisations = ['ch:1:sboid:100015']; // todo

  editing = false;

  constructor(
    private readonly personWithReducedMobilityService: PersonWithReducedMobilityService,
    private readonly dialogService: DialogService,
    private readonly notificationService: NotificationService,
  ) {}

  ngOnInit(): void {
    this.loadRelations(this.referencePointSloid);
  }

  private loadRelations(rpSloid: string) {
    this.relations$ = this.personWithReducedMobilityService
      .getRelationsBySloid(this.elementSloid)
      .pipe(map((array) => array.filter((item) => item.referencePointSloid === rpSloid)));
    this.currentRelation$ = this.relations$.pipe(
      map((array) => VersionsHandlingService.determineDefaultVersionByValidity(array)),
      tap(
        (currentVersion) => (
          (this.form = RelationFormGroupBuilder.buildFormGroup(currentVersion)),
          (this.currentRelationId = currentVersion.id!)
        ),
      ),
    );
  }

  toggleEdit() {
    if (this.editing) {
      this.showCancelEditDialog();
    } else {
      this.form?.enable();
      this.editing = true;
    }
  }

  save() {
    this.form?.markAllAsTouched();
    if (this.form?.valid) {
      this.form.disable();
      this.editing = false;
      let relationVersion = RelationFormGroupBuilder.getWritableForm(this.form);
      relationVersion = {
        ...relationVersion,
        parentServicePointSloid: 'ch:1:sloid:3', // todo
        referencePointSloid: this.referencePointSloid,
      };
      this.update(relationVersion);
    }
    // todo: load saved element after save
  }

  private update(relationVersion: RelationVersion) {
    this.personWithReducedMobilityService
      .updateRelation(this.currentRelationId, relationVersion)
      .subscribe(() => {
        this.notificationService.success('PRM.RELATIONS.NOTIFICATION.EDIT_SUCCESS');
      });
  }

  private showCancelEditDialog() {
    this.confirmLeave()
      .pipe(take(1))
      .subscribe((confirmed) => {
        if (confirmed) {
          this.form?.disable();
          this.editing = false;
        }
      });
  }

  private confirmLeave(): Observable<boolean> {
    if (this.form?.dirty) {
      return this.dialogService.confirm({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      });
    }
    return of(true);
  }

  // used in combination with canLeaveDirtyForm
  isFormDirty(): boolean {
    // TODO
    return false;
  }
}
