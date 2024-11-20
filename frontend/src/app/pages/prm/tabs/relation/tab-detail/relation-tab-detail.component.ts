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
import { catchError, EMPTY, finalize, Observable, of, switchMap, take } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { NotificationService } from '../../../../../core/notification/notification.service';
import { map, tap } from 'rxjs/operators';
import { VersionsHandlingService } from '../../../../../core/versioning/versions-handling.service';
import { DetailFormComponent } from '../../../../../core/leave-guard/leave-dirty-form-guard.service';
import { RelationFormGroup, RelationFormGroupBuilder } from './relation-form-group';
import { MatSelectChange } from '@angular/material/select';
import { ValidityService } from '../../../../sepodi/validity/validity.service';

@Component({
  selector: 'app-relation-tab-detail',
  templateUrl: './relation-tab-detail.component.html',
  providers: [ValidityService],
})
export class RelationTabDetailComponent implements OnInit, DetailFormComponent {
  referencePoints: ReadReferencePointVersion[] = [];
  selectedReferencePointSloid?: string;
  elementSloid?: string;
  parentServicePointSloid?: string;

  relations$: Observable<ReadRelationVersion[]> = of();
  currentRelation?: ReadRelationVersion;

  form?: FormGroup<RelationFormGroup>;
  currentRelationId = -1;
  selectedRelationVersion = -1;

  stepFreeAccessOptions = Object.values(StepFreeAccessAttributeType);
  tactileVisualMarkOptions = Object.values(TactileVisualAttributeType);
  contrastingAreaOptions = Object.values(StandardAttributeType);

  businessOrganisations: string[] = [];
  editing = false;

  saving = false;

  readonly extractSloid = (option: ReadReferencePointVersion) => option.sloid;
  readonly displayExtractor = (option: ReadReferencePointVersion) =>
    `${option.designation} - ${option.sloid}`;

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly personWithReducedMobilityService: PersonWithReducedMobilityService,
    private readonly dialogService: DialogService,
    private readonly notificationService: NotificationService,
    private validityService: ValidityService,
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

  versionChanged(currentVersion: ReadRelationVersion, currentVersionIdx: number) {
    this.form = RelationFormGroupBuilder.buildFormGroup(currentVersion);
    this.currentRelationId = currentVersion.id!;
    this.currentRelation = currentVersion;
    this.selectedRelationVersion = currentVersionIdx + 1;
  }

  back() {
    this.router.navigate(['../..'], { relativeTo: this.route });
  }

  save(): void {
    this.saving = true;
    this.saveProcess()
      .pipe(
        take(1),
        tap(() => {
          this.notificationService.success('PRM.RELATIONS.NOTIFICATION.EDIT_SUCCESS');
          this.loadRelations(this.selectedReferencePointSloid!);
        }),
        catchError(() => {
          this.loadRelations(this.selectedReferencePointSloid!);
          return EMPTY;
        }),
        finalize(() => (this.saving = false)),
      )
      .subscribe();
  }

  private saveProcess(): Observable<ReadRelationVersion[]> {
    this.form?.markAllAsTouched();
    if (this.form?.valid) {
      let relationVersion = RelationFormGroupBuilder.getWritableForm(this.form);
      relationVersion = {
        ...relationVersion,
        parentServicePointSloid: this.parentServicePointSloid,
        referencePointSloid: this.selectedReferencePointSloid,
      };
      this.validityService.updateValidity(this.form);
      return this.validityService.validate().pipe(
        switchMap((dialogRes) => {
          if (dialogRes) {
            this.editing = false;
            this.form?.disable();
            return this.update(relationVersion);
          } else {
            return EMPTY;
          }
        }),
      );
    } else {
      return EMPTY;
    }
  }

  private update(relationVersion: RelationVersion) {
    return this.personWithReducedMobilityService.updateRelation(
      this.currentRelationId,
      relationVersion,
    );
  }

  toggleEdit() {
    if (this.editing) {
      this.showCancelEditDialog();
    } else {
      this.form?.enable();
      this.validityService.initValidity(this.form!);
      this.editing = true;
    }
  }

  private loadRelations(referencePointSloid: string) {
    this.relations$ = this.personWithReducedMobilityService
      .getRelationsBySloid(this.elementSloid!)
      .pipe(
        map((relationVersions) => {
          const relationsOfSelectedRP = relationVersions.filter(
            (relationVersion) => relationVersion.referencePointSloid === referencePointSloid,
          );
          VersionsHandlingService.addVersionNumbers(relationsOfSelectedRP);
          this.currentRelation =
            VersionsHandlingService.determineDefaultVersionByValidity(relationsOfSelectedRP);
          this.form = RelationFormGroupBuilder.buildFormGroup(this.currentRelation);
          this.currentRelationId = this.currentRelation.id!;
          this.selectedRelationVersion = relationsOfSelectedRP.indexOf(this.currentRelation) + 1;
          return relationsOfSelectedRP;
        }),
      );
  }

  private checkIfRelationsAvailable() {
    const stopPoint = this.route.parent!.snapshot.data.stopPoint;
    const reduced = PrmMeanOfTransportHelper.isReduced(stopPoint[0].meansOfTransport);
    if (reduced) {
      this.router.navigate([Pages.PRM.path, Pages.STOP_POINTS.path, stopPoint[0].sloid]).then();
    }
  }

  private showCancelEditDialog() {
    this.confirmLeave()
      .pipe(take(1))
      .subscribe(async (confirmed) => {
        if (confirmed) {
          this.form = RelationFormGroupBuilder.buildFormGroup(this.currentRelation);
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
}
