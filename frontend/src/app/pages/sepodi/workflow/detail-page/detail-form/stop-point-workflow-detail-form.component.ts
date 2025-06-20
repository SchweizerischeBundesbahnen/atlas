import { Component, Input, OnInit } from '@angular/core';
import {
  ControlContainer,
  FormGroup,
  NgForm,
  ReactiveFormsModule,
} from '@angular/forms';
import {
  SPECIAL_DECISION_TYPES,
  StopPointWorkflowDetailFormGroup,
  StopPointWorkflowDetailFormGroupBuilder,
} from './stop-point-workflow-detail-form-group';
import { Router } from '@angular/router';
import {
  Country,
  ReadServicePointVersion,
  ReadStopPointWorkflow,
  Status,
  StopPointPerson,
  StopPointWorkflowService,
  WorkflowStatus,
} from 'src/app/api';
import { AtlasCharsetsValidator } from 'src/app/core/validation/charsets/atlas-charsets-validator';
import { AtlasFieldLengthValidator } from 'src/app/core/validation/field-lengths/atlas-field-length-validator';
import { DecisionDetailDialogService } from '../decision/decision-detail/decision-detail-dialog.service';
import { Pages } from 'src/app/pages/pages';
import { SloidHelper } from '../../../../../core/util/sloidHelper';
import { LinkComponent } from '../../../../../core/form-components/link/link.component';
import { AtlasSpacerComponent } from '../../../../../core/components/spacer/atlas-spacer.component';
import { AtlasButtonComponent } from '../../../../../core/components/button/atlas-button.component';
import { TextFieldComponent } from '../../../../../core/form-components/text-field/text-field.component';
import { CommentComponent } from '../../../../../core/form-components/comment/comment.component';
import { InfoIconComponent } from '../../../../../core/form-components/info-icon/info-icon.component';
import { StopPointWorkflowExaminantsTableComponent } from '../examinant-table/stop-point-workflow-examinants-table.component';
import { StringListComponent } from '../../../../../core/form-components/string-list/string-list.component';
import { DisplayDatePipe } from '../../../../../core/pipe/display-date.pipe';
import { SplitServicePointNumberPipe } from '../../../../../core/search-service-point/split-service-point-number.pipe';
import { AsyncPipe } from '@angular/common';
import { TranslatePipe } from '@ngx-translate/core';
import { BoDisplayPipe } from '../../../../../core/form-components/bo-select/bo-display.pipe';

@Component({
  selector: 'stop-point-workflow-detail-form',
  templateUrl: './stop-point-workflow-detail-form.component.html',
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
  imports: [
    LinkComponent,
    AtlasSpacerComponent,
    AtlasButtonComponent,
    TextFieldComponent,
    ReactiveFormsModule,
    CommentComponent,
    InfoIconComponent,
    StopPointWorkflowExaminantsTableComponent,
    StringListComponent,
    DisplayDatePipe,
    SplitServicePointNumberPipe,
    AsyncPipe,
    TranslatePipe,
    BoDisplayPipe,
  ],
})
export class StopPointWorkflowDetailFormComponent implements OnInit {
  readonly WorkflowStatus = WorkflowStatus;
  readonly emailValidator = [
    AtlasCharsetsValidator.email,
    AtlasFieldLengthValidator.length_100,
  ];

  @Input() stopPoint!: ReadServicePointVersion;
  @Input() oldDesignation?: string;
  @Input() form!: FormGroup<StopPointWorkflowDetailFormGroup>;
  @Input() currentWorkflow?: ReadStopPointWorkflow;

  specialDecision?: StopPointPerson;

  constructor(
    private router: Router,
    private decisionDetailDialogService: DecisionDetailDialogService,
    private stopPointWorkflowService: StopPointWorkflowService
  ) {}

  ngOnInit() {
    if (!this.stopPoint && this.currentWorkflow) {
      this.stopPoint = {
        sloid: this.currentWorkflow.sloid,
        businessOrganisation: this.currentWorkflow.sboid!,
        validFrom: this.currentWorkflow.versionValidFrom!,
        validTo: this.currentWorkflow.versionValidTo!,
        designationOfficial: this.currentWorkflow.designationOfficial!,
        country: Country.Switzerland,
        status: Status.InReview,
        number: {
          number: SloidHelper.servicePointSloidToNumber(
            this.currentWorkflow.sloid
          ),
          checkDigit: 1,
          numberShort: 1,
          uicCountryCode: 85,
        },
      };
    }

    if (!this.currentWorkflow) {
      this.stopPointWorkflowService
        .getExaminants(this.stopPoint.id!)
        .subscribe({
          next: (defaultExaminants: StopPointPerson[]) => {
            defaultExaminants.forEach((examinant) => {
              this.form.controls.examinants.push(
                StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup(
                  examinant
                )
              );
            });
            this.form.controls.examinants.push(
              StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup()
            );
          },
          error: (error) => {
            console.error('Error occurred while fetching examinants:', error);
            this.form.disable();
          },
        });
    }

    if (this.currentWorkflow) {
      this.specialDecision = this.currentWorkflow!.examinants?.find(
        (examinant) => SPECIAL_DECISION_TYPES.includes(examinant.decisionType!)
      );
    }
  }

  goToSwissTopo() {
    const n = this.stopPoint.servicePointGeolocation!.lv95.north;
    const e = this.stopPoint.servicePointGeolocation!.lv95.east;
    window.open(
      `https://map.geo.admin.ch/?lang=de&topic=ech&bgLayer=ch.swisstopo.pixelkarte-farbe&layers=ch.swisstopo.swissboundaries3d-gemeinde-flaeche.fill,ch.swisstopo-vd.ortschaftenverzeichnis_plz,ch.swisstopo.amtliches-strassenverzeichnis,ch.bav.haltestellen-oev&layers_opacity=1,0.75,0.85,1&layers_timestamp=2024,,,&E=${e}&N=${n}&zoom=10&layers_visibility=false,true,false,true&crosshair=marker&E=${e}&N=${n}`,
      '_blank'
    );
  }

  goToAtlasStopPoint() {
    const url = this.router.serializeUrl(
      this.router.createUrlTree(
        [
          Pages.SEPODI.path,
          Pages.SERVICE_POINTS.path,
          this.stopPoint?.number.number,
        ],
        {
          queryParams: {
            id: this.stopPoint?.id,
          },
        }
      )
    );
    window.open(url, '_blank');
  }

  goToWorkflow(id: number) {
    const url = this.router.serializeUrl(
      this.router.createUrlTree([Pages.SEPODI.path, Pages.WORKFLOWS.path, id])
    );
    window.open(url, '_blank');
  }

  openDecision(index: number) {
    const examinant = this.form.controls.examinants.at(index);
    this.decisionDetailDialogService.openDialog(
      this.currentWorkflow!.id!,
      this.currentWorkflow!.status!,
      examinant
    );
  }

  openStatusDecision() {
    this.decisionDetailDialogService.openDialog(
      this.currentWorkflow!.id!,
      this.currentWorkflow!.status!,
      StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup(
        this.specialDecision
      )
    );
  }
}
