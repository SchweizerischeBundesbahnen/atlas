import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  PersonWithReducedMobilityService,
  ReadReferencePointVersion,
  ReadRelationVersion,
  ReadServicePointVersion,
  RelationVersion,
  StandardAttributeType,
  StepFreeAccessAttributeType,
  TactileVisualAttributeType,
} from '../../../../../api';
import { PrmMeanOfTransportHelper } from '../../../util/prm-mean-of-transport-helper';
import { Pages } from '../../../../pages';
import { lastValueFrom, Observable, of, take } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { map } from 'rxjs/operators';
import { VersionsHandlingService } from '../../../../../core/versioning/versions-handling.service';
import { DetailFormComponent } from '../../../../../core/leave-guard/leave-dirty-form-guard.service';
import { RelationFormGroup, RelationFormGroupBuilder } from './relation-form-group';
import { MatSelectChange } from '@angular/material/select';

@Component({
  selector: 'app-relation-tab-detail',
  templateUrl: './relation-tab-detail.component.html',
})
export class RelationTabDetailComponent implements OnInit, DetailFormComponent {
  referencePoints: ReadReferencePointVersion[] = [];
  selectedReferencePointSloid?: string;
  elementSloid?: string;
  parentServicePointSloid?: string;

  relations$: Observable<ReadRelationVersion[]> = of();
  currentRelation$: Observable<ReadRelationVersion> = of();

  form?: FormGroup<RelationFormGroup>;
  currentRelationId = -1;
  selectedRelationVersion = -1;

  stepFreeAccessOptions = Object.values(StepFreeAccessAttributeType);
  tactileVisualMarkOptions = Object.values(TactileVisualAttributeType);
  contrastingAreaOptions = Object.values(StandardAttributeType);

  businessOrganisations: string[] = [];
  editing = false;

  readonly extractSloid = (option: ReadReferencePointVersion) => option.sloid;
  readonly displayExtractor = (option: ReadReferencePointVersion) =>
    `${option.designation} - ${option.sloid}`;

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly personWithReducedMobilityService: PersonWithReducedMobilityService,
    private readonly dialogService: DialogService,
    private readonly notificationService: NotificationService,
  ) {}

  ngOnInit(): void {
    this.checkIfRelationsAvailable();
    this.elementSloid = this.route.parent!.snapshot.params.sloid!;
    this.parentServicePointSloid = this.route.parent!.snapshot.params.stopPointSloid;
    this.businessOrganisations = [
      ...new Set(
        (this.route.parent!.snapshot.data.servicePoint as ReadServicePointVersion[]).map(
          (version) => version.businessOrganisation,
        ),
      ),
    ];
    this.personWithReducedMobilityService
      .getReferencePointsOverview(this.parentServicePointSloid!)
      .subscribe((overviewRows) => {
        this.referencePoints = overviewRows;
        if (this.referencePoints.length === 1) {
          this.selectedReferencePointSloid = this.referencePoints[0].sloid!;
          this.loadRelations(this.selectedReferencePointSloid);
        }
      });
  }

  referencePointChanged(change: MatSelectChange) {
    this.selectedReferencePointSloid = change.value;
    this.loadRelations(this.selectedReferencePointSloid!);
  }

  versionChanged(currentVersion: ReadRelationVersion) {
    this.form = RelationFormGroupBuilder.buildFormGroup(currentVersion);
    this.currentRelationId = currentVersion.id!;
    this.currentRelation$ = of(currentVersion);
  }

  save() {
    this.form?.markAllAsTouched();
    if (this.form?.valid) {
      this.form.disable();
      this.editing = false;
      let relationVersion = RelationFormGroupBuilder.getWritableForm(this.form);
      relationVersion = {
        ...relationVersion,
        parentServicePointSloid: this.parentServicePointSloid,
        referencePointSloid: this.selectedReferencePointSloid,
      };
      this.update(relationVersion);
    }
  }

  toggleEdit() {
    if (this.editing) {
      this.showCancelEditDialog();
    } else {
      this.form?.enable();
      this.editing = true;
    }
  }

  private checkIfRelationsAvailable() {
    const stopPoint = this.route.parent!.snapshot.data.stopPoint;
    const reduced = PrmMeanOfTransportHelper.isReduced(stopPoint[0].meansOfTransport);
    if (reduced) {
      this.router.navigate([Pages.PRM.path, Pages.STOP_POINTS.path, stopPoint[0].sloid]).then();
    }
  }

  private update(relationVersion: RelationVersion) {
    this.personWithReducedMobilityService
      .updateRelation(this.currentRelationId, relationVersion)
      .subscribe(() => {
        this.notificationService.success('PRM.RELATIONS.NOTIFICATION.EDIT_SUCCESS');
        this.loadRelations(this.selectedReferencePointSloid!);
      });
  }

  private loadRelations(referencePointSloid: string) {
    this.relations$ = this.personWithReducedMobilityService
      .getRelationsBySloid(this.elementSloid!) // todo: backend endpoint sort validFrom
      .pipe(
        map((relationVersions) => {
          const relationsOfSelectedRP = relationVersions.filter(
            (relationVersion) => relationVersion.referencePointSloid === referencePointSloid,
          );
          VersionsHandlingService.addVersionNumbers(relationsOfSelectedRP);
          return relationsOfSelectedRP;
        }),
      );
    this.currentRelation$ = this.relations$.pipe(
      map((relations) => {
        const currentRelationVersion =
          VersionsHandlingService.determineDefaultVersionByValidity(relations);
        this.form = RelationFormGroupBuilder.buildFormGroup(currentRelationVersion);
        this.currentRelationId = currentRelationVersion.id!;
        this.selectedRelationVersion = relations.indexOf(currentRelationVersion) + 1;
        return currentRelationVersion;
      }),
    );
  }

  private showCancelEditDialog() {
    this.confirmLeave()
      .pipe(take(1))
      .subscribe(async (confirmed) => {
        if (confirmed) {
          this.form = RelationFormGroupBuilder.buildFormGroup(
            await lastValueFrom(this.currentRelation$),
          );
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
    return this.form?.dirty ?? false;
  }
}
// todo: on error (flag on loadRelations for @if conditions)
