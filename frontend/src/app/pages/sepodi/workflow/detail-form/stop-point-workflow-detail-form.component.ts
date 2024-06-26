import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ApplicationType, ReadServicePointVersion, ReadStopPointWorkflow, WorkflowStatus} from '../../../../api';
import {ControlContainer, FormGroup, NgForm} from '@angular/forms';
import {AtlasCharsetsValidator} from '../../../../core/validation/charsets/atlas-charsets-validator';
import {AtlasFieldLengthValidator} from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import {
  StopPointWorkflowDetailFormGroup,
  StopPointWorkflowDetailFormGroupBuilder,
} from './stop-point-workflow-detail-form-group';
import {ValidationService} from '../../../../core/validation/validation.service';
import {Pages} from '../../../pages';
import {Router} from '@angular/router';
import {PermissionService} from '../../../../core/auth/permission/permission.service';
import {DecisionDetailDialogService} from "../detail-page/decision-detail/decision-detail-dialog.service";

@Component({
  selector: 'stop-point-workflow-detail-form',
  templateUrl: './stop-point-workflow-detail-form.component.html',
  styleUrls: ['./stop-point-workflow-detail-form.component.scss'],
  viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
})
export class StopPointWorkflowDetailFormComponent {
  protected readonly ApplicationType = ApplicationType;
  readonly WorkflowStatus = WorkflowStatus;
  readonly emailValidator = [AtlasCharsetsValidator.email, AtlasFieldLengthValidator.length_100];

  @Input() stopPoint!: ReadServicePointVersion;
  @Input() oldDesignation?: string;
  @Input() form!: FormGroup<StopPointWorkflowDetailFormGroup>;
  @Input() currentWorkflow?: ReadStopPointWorkflow;
  @Output() bavOverrideAction = new EventEmitter<number>();

  constructor(
    private router: Router,
    protected readonly permissionService: PermissionService,
    private decisionDetailDialogService: DecisionDetailDialogService) {
  }

  addExaminant() {
    const examinantsControl = this.form.controls.examinants;
    ValidationService.validateForm(examinantsControl);
    if (examinantsControl.valid) {
      examinantsControl.push(StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup());
    }
  }

  removeExaminant(index: number) {
    this.form.controls.examinants.removeAt(index);
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

  openDecision(index: number) {
    const examinant = this.form.controls.examinants.at(index);
    this.decisionDetailDialogService.openDialog(this.currentWorkflow!.id!, examinant);
  }
}
