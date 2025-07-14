import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { StopPointTerminationWorkflowDetailData } from './stop-point-termination-workflow-resolver';
import { ReadServicePointVersion } from '../../../../api';
import { TerminationStopPointAddWorkflow } from '../../../../api/model/terminationStopPointAddWorkflow';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../core/components/detail-page-content/detail-page-content.component';
import { TranslatePipe } from '@ngx-translate/core';
import { UserDetailInfoComponent } from '../../../../core/components/base-detail/user-edit-info/user-detail-info.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { BackButtonDirective } from '../../../../core/components/button/back-button/back-button.directive';
import { StopPointWorkflowBasicInfo } from '../../workflow/stop-point-workflow-basic-info/stop-point-workflow-basic-info';
import { AtlasSpacerComponent } from '../../../../core/components/spacer/atlas-spacer.component';
import {
  StopPointTerminationWorkflowDetailFormGroup,
  StopPointTerminationWorkflowDetailFormGroupBuilder,
  TerminationDecisionFormGroup,
} from './stop-point-termination-workflow-detail-form-group';
import { FormGroup } from '@angular/forms';
import { TextFieldComponent } from '../../../../core/form-components/text-field/text-field.component';
import { CommentComponent } from '../../../../core/form-components/comment/comment.component';
import { LinkComponent } from '../../../../core/form-components/link/link.component';
import { Pages } from '../../../pages';
import { TerminationDecisionDetailDialogService } from './decision/decision-detail/termination-decision-detail-dialog.service';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { TerminationDecision } from '../../../../api/model/terminationDecision';
import moment from 'moment';
import { TerminationWorkflowStatus } from '../../../../api/model/terminationWorkflowStatus';
import TerminationDecisionPersonEnum = TerminationDecision.TerminationDecisionPersonEnum;

@Component({
  selector: 'app-stop-point-termination-workflow-detail',
  imports: [
    DetailPageContainerComponent,
    DetailPageContentComponent,
    TranslatePipe,
    UserDetailInfoComponent,
    DetailFooterComponent,
    AtlasButtonComponent,
    BackButtonDirective,
    StopPointWorkflowBasicInfo,
    AtlasSpacerComponent,
    TextFieldComponent,
    CommentComponent,
    LinkComponent,
  ],
  templateUrl: './stop-point-termination-workflow-detail.html',
})
export class StopPointTerminationWorkflowDetail implements OnInit {
  protected readonly TerminationDecisionPersonEnum =
    TerminationDecisionPersonEnum;
  protected readonly TerminationWorkflowStatus = TerminationWorkflowStatus;

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly permissionService = inject(PermissionService);
  private readonly terminationDecisionDetailDialogService = inject(
    TerminationDecisionDetailDialogService
  );

  stopPoint!: ReadServicePointVersion;
  workflow!: TerminationStopPointAddWorkflow;
  form!: FormGroup<StopPointTerminationWorkflowDetailFormGroup>;
  terminationPermission?: TerminationDecisionPersonEnum;
  showDecisionButton = false;

  ngOnInit(): void {
    this.route.data.subscribe((data) => {
      const workflowData: StopPointTerminationWorkflowDetailData =
        data.workflow;

      this.workflow = workflowData.workflow;

      const indexOfVersionInReview = workflowData.servicePoint.findIndex(
        (i) => i.id === this.workflow.versionId
      );
      this.stopPoint = workflowData.servicePoint[indexOfVersionInReview];

      this.form =
        StopPointTerminationWorkflowDetailFormGroupBuilder.buildFormGroup(
          workflowData.workflow
        );
      this.form.disable();

      this.terminationPermission =
        this.permissionService.getTerminationPermission();

      if (this.terminationPermission) {
        if (
          this.terminationPermission === TerminationDecisionPersonEnum.InfoPlus
        ) {
          this.showDecisionButton = !this.workflow.infoPlusTerminationDate;
        }
        if (this.terminationPermission === TerminationDecisionPersonEnum.Nova) {
          this.showDecisionButton =
            this.workflow.status !=
            TerminationWorkflowStatus.TerminationApproved;
        }
      }
    });
  }

  onOpenDecision(examinantDecision: FormGroup<TerminationDecisionFormGroup>) {
    this.terminationDecisionDetailDialogService.openDialog(
      this.workflow.id!,
      true,
      examinantDecision.controls.terminationDecisionPerson.value!,
      examinantDecision
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

  openDecisionDialog() {
    const decisionForm =
      StopPointTerminationWorkflowDetailFormGroupBuilder.buildTerminationDecisionFormGroup();
    decisionForm.controls.terminationDecisionPerson.setValue(
      this.terminationPermission
    );
    decisionForm.controls.terminationDate.setValue(
      moment(
        this.workflow.infoPlusTerminationDate ??
          this.workflow.boTerminationDate!
      )
    );

    this.terminationDecisionDetailDialogService
      .openDialog(
        this.workflow.id!,
        false,
        this.terminationPermission!,
        decisionForm
      )
      .subscribe((result) => {
        if (result) {
          this.router
            .navigate(['..', this.workflow.id!], {
              relativeTo: this.route,
            })
            .then();
        }
      });
  }
}
