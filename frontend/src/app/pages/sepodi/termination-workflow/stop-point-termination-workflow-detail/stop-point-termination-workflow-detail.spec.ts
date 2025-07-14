import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopPointTerminationWorkflowDetail } from './stop-point-termination-workflow-detail';
import { ActivatedRoute } from '@angular/router';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';
import { StopPointTerminationWorkflowDetailData } from './stop-point-termination-workflow-resolver';
import { TerminationStopPointAddWorkflow } from '../../../../api/model/terminationStopPointAddWorkflow';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { BoSelectionDisplayPipe } from '../../../../core/form-components/bo-select/bo-selection-display.pipe';
import { of } from 'rxjs';

const workflow: TerminationStopPointAddWorkflow = {
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
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(StopPointTerminationWorkflowDetail);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
