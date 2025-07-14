import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopPointTerminationWorkflowDetail } from './stop-point-termination-workflow-detail';
import { ActivatedRoute, Router } from '@angular/router';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';
import { StopPointTerminationWorkflowDetailData } from './stop-point-termination-workflow-resolver';
import { TerminationStopPointAddWorkflow } from '../../../../api/model/terminationStopPointAddWorkflow';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { BoSelectionDisplayPipe } from '../../../../core/form-components/bo-select/bo-selection-display.pipe';
import { of } from 'rxjs';
import { TerminationDecisionDetailDialogService } from './decision/decision-detail/termination-decision-detail-dialog.service';

const workflow: TerminationStopPointAddWorkflow = {
  id: 10,
  sloid: 'ch:1sloid:700',
  versionId: 1000,
  boTerminationDate: new Date(),
  applicantMail: 'a@b.ch',
  workflowComment: 'Comment',
};

const workflowData: StopPointTerminationWorkflowDetailData = {
  workflow: workflow,
  servicePoint: [BERN_WYLEREGG],
};

const activatedRoute = {
  data: of({
    workflow: workflowData,
  }),
};

const terminationDecisionDetailDialogService = jasmine.createSpyObj(
  'TerminationDecisionDetailDialogService',
  { openDialog: of(true) }
);

describe('StopPointTerminationWorkflowDetail', () => {
  let component: StopPointTerminationWorkflowDetail;
  let fixture: ComponentFixture<StopPointTerminationWorkflowDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StopPointTerminationWorkflowDetail, TranslateModule.forRoot()],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TranslatePipe },
        { provide: BoSelectionDisplayPipe },
        { provide: Router },
        {
          provide: TerminationDecisionDetailDialogService,
          useValue: terminationDecisionDetailDialogService,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(StopPointTerminationWorkflowDetail);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.terminationPermission).toBeUndefined();
  });

  it('should go to atlas stoppoint', () => {
    spyOn(window, 'open');

    component.goToAtlasStopPoint();
    expect(window.open).toHaveBeenCalledWith(
      '/service-point-directory/service-points/8589008?id=1000',
      '_blank'
    );
  });

  it('should open decision', () => {
    component.onOpenDecision(component.form.controls.examinants.at(0));
    expect(
      terminationDecisionDetailDialogService.openDialog
    ).toHaveBeenCalled();
  });

  it('should open decision dialog', () => {
    component.openDecisionDialog();
    expect(
      terminationDecisionDetailDialogService.openDialog
    ).toHaveBeenCalled();
  });
});
