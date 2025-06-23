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
} from './StopPointTerminationWorkflowDetailFormGroup';
import { FormGroup } from '@angular/forms';
import { TextFieldComponent } from '../../../../core/form-components/text-field/text-field.component';
import { CommentComponent } from '../../../../core/form-components/comment/comment.component';
import { LinkComponent } from '../../../../core/form-components/link/link.component';
import { Pages } from '../../../pages';

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
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  stopPoint!: ReadServicePointVersion;
  workflow!: TerminationStopPointAddWorkflow;
  form!: FormGroup<StopPointTerminationWorkflowDetailFormGroup>;

  ngOnInit(): void {
    const workflowData: StopPointTerminationWorkflowDetailData =
      this.route.snapshot.data.workflow;

    this.workflow = workflowData.workflow;

    const indexOfVersionInReview = workflowData.servicePoint.findIndex(
      (i) => i.id === this.workflow.versionId
    )!;
    this.stopPoint = workflowData.servicePoint[indexOfVersionInReview];

    this.form =
      StopPointTerminationWorkflowDetailFormGroupBuilder.buildFormGroup(
        workflowData.workflow
      );
    this.form.disable();
  }

  onOpenDecision($index: number) {
    window.alert($index);
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
}
