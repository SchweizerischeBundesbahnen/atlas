import {Component, Input, OnInit} from '@angular/core';
import {ControlContainer, FormArray, FormGroup, NgForm} from '@angular/forms';
import {
  SPECIAL_DECISION_TYPES,
  StopPointWorkflowDetailFormGroup,
  StopPointWorkflowDetailFormGroupBuilder,
} from './stop-point-workflow-detail-form-group';
import {Router} from '@angular/router';
import {
  Country,
  ReadServicePointVersion,
  ReadStopPointWorkflow,
  Status,
  StopPointPerson,
  StopPointWorkflowService,
  WorkflowStatus
} from 'src/app/api';
import {AtlasCharsetsValidator} from 'src/app/core/validation/charsets/atlas-charsets-validator';
import {AtlasFieldLengthValidator} from 'src/app/core/validation/field-lengths/atlas-field-length-validator';
import {DecisionDetailDialogService} from '../decision/decision-detail/decision-detail-dialog.service';
import {ValidationService} from 'src/app/core/validation/validation.service';
import {Pages} from 'src/app/pages/pages';
import {SloidHelper} from "../../../../../core/util/sloidHelper";

@Component({
  selector: 'stop-point-workflow-detail-form',
  templateUrl: './stop-point-workflow-detail-form.component.html',
  styleUrls: ['./stop-point-workflow-detail-form.component.scss'],
  viewProviders: [{provide: ControlContainer, useExisting: NgForm}],
})
export class StopPointWorkflowDetailFormComponent implements OnInit {
  readonly WorkflowStatus = WorkflowStatus;
  readonly emailValidator = [AtlasCharsetsValidator.email, AtlasFieldLengthValidator.length_100];

  @Input() stopPoint!: ReadServicePointVersion;
  @Input() oldDesignation?: string;
  @Input() form!: FormGroup<StopPointWorkflowDetailFormGroup>;
  @Input() currentWorkflow?: ReadStopPointWorkflow;
  disableDeleteButton: boolean = false;

  specialDecision?: StopPointPerson;

  constructor(
    private router: Router,
    private decisionDetailDialogService: DecisionDetailDialogService,
    private stopPointWorkflowService: StopPointWorkflowService
  ) {
  }

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
          number: SloidHelper.servicePointSloidToNumber(this.currentWorkflow.sloid),
          checkDigit: 1,
          numberShort: 1,
          uicCountryCode: 85
        },
      };
    }

    if(!this.currentWorkflow){
      this.stopPointWorkflowService.getExaminants(this.stopPoint.id!).subscribe((listOfExaminants: StopPointPerson[]) => {
        const emptyExaminant: StopPointPerson = {
          firstName: '',
          lastName: '',
          organisation: '',
          mail: ''
        };
        listOfExaminants.push(emptyExaminant);
        const examinantsFormArray = this.form.get('examinants') as FormArray;
        examinantsFormArray.clear();
        listOfExaminants.forEach(examinant => {
          examinantsFormArray.push(StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup(examinant));
        });

        this.disableFirstTwoExaminants(examinantsFormArray);
      });
    }

    if (this.currentWorkflow) {
      this.specialDecision = this.currentWorkflow.examinants?.find(examinant => SPECIAL_DECISION_TYPES.includes(examinant.decisionType!));
    }
  }

  public disableFirstTwoExaminants(examinantsFormArray: FormArray): void {
    this.disableDeleteButton = true;
    if (examinantsFormArray.length > 0) {
      examinantsFormArray.at(0).disable();
      if (examinantsFormArray.length > 1) {
        examinantsFormArray.at(1).disable();
      }
    }
  }

  disableDeleteButtonForTheFirstTwoExaminants(index: number): boolean {
    return (index < 2 && this.disableDeleteButton) || this.form.disabled;
  }


  addExaminant() {
    const examinantsControl = this.form.controls.examinants;
    ValidationService.validateForm(examinantsControl);
    if (examinantsControl.valid || this.allExaminantsDisabled(examinantsControl)) {
      examinantsControl.push(StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup());
    }
  }

  private allExaminantsDisabled(examinantsControl: FormArray): boolean {
    return examinantsControl.controls.every(control =>
      control.disabled
    );
  }

  removeExaminant(index: number) {
    this.form.controls.examinants.removeAt(index);
    this.form.markAsDirty();
  }

  goToSwissTopo() {
    const n = this.stopPoint.servicePointGeolocation!.lv95.north;
    const e = this.stopPoint.servicePointGeolocation!.lv95.east;
    window.open(
      `https://map.geo.admin.ch/?lang=de&topic=ech&bgLayer=ch.swisstopo.pixelkarte-farbe&layers=ch.swisstopo.swissboundaries3d-gemeinde-flaeche.fill,ch.swisstopo-vd.ortschaftenverzeichnis_plz,ch.swisstopo.amtliches-strassenverzeichnis,ch.bav.haltestellen-oev&layers_opacity=1,0.75,0.85,1&layers_timestamp=2024,,,&E=${e}&N=${n}&zoom=10&layers_visibility=false,true,false,true&crosshair=marker&E=${e}&N=${n}`,
      '_blank',
    );
  }

  goToAtlasStopPoint() {
    const url = this.router.serializeUrl(this.router.createUrlTree([Pages.SEPODI.path,
      Pages.SERVICE_POINTS.path,
      this.stopPoint?.number.number], {
      queryParams: {
        id: this.stopPoint?.id
      }
    }));
    window.open(url, '_blank');
  }

  goToWorkflow(id: number) {
    const url = this.router.serializeUrl(this.router.createUrlTree([Pages.SEPODI.path,
      Pages.WORKFLOWS.path, id]));
    window.open(url, '_blank');
  }

  openDecision(index: number) {
    const examinant = this.form.controls.examinants.at(index);
    this.decisionDetailDialogService.openDialog(this.currentWorkflow!.id!, this.currentWorkflow!.status!, examinant);
  }

  openStatusDecision() {
    this.decisionDetailDialogService.openDialog(this.currentWorkflow!.id!, this.currentWorkflow!.status!, StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup(this.specialDecision));
  }

}
