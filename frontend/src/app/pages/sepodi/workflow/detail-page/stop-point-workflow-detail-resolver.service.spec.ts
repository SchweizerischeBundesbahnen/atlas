import {
  ActivatedRouteSnapshot,
  convertToParamMap,
  RouterStateSnapshot,
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { TestBed } from '@angular/core/testing';
import {
  StopPointWorkflowDetailData,
  stopPointWorkflowDetailResolver,
  StopPointWorkflowDetailResolver,
} from './stop-point-workflow-detail-resolver.service';
import {
  ReadStopPointWorkflow,
  ServicePointsService,
  StopPointWorkflowService,
} from '../../../../api';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';
import { AppTestingModule } from '../../../../app.testing.module';

const workflow: ReadStopPointWorkflow = {
  versionId: 1,
  sloid: 'ch:1:sloid:8000',
  workflowComment: 'No comment',
};

describe('StopPointWorkflowDetailResolver', () => {
  const stopPointWorkflowService = jasmine.createSpyObj(
    'stopPointWorkflowService',
    ['getStopPointWorkflow']
  );
  stopPointWorkflowService.getStopPointWorkflow.and.returnValue(of(workflow));

  const servicePointsService = jasmine.createSpyObj('servicePointsService', [
    'getServicePointVersionsBySloid',
  ]);
  servicePointsService.getServicePointVersionsBySloid.and.returnValue(
    of([BERN_WYLEREGG])
  );

  let resolver: StopPointWorkflowDetailResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        StopPointWorkflowDetailResolver,
        {
          provide: StopPointWorkflowService,
          useValue: stopPointWorkflowService,
        },
        { provide: ServicePointsService, useValue: servicePointsService },
      ],
    });
    resolver = TestBed.inject(StopPointWorkflowDetailResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get workflow with service point', () => {
    const mockRoute = {
      paramMap: convertToParamMap({ id: '1000' }),
    } as ActivatedRouteSnapshot;

    const resolvedVersion = TestBed.runInInjectionContext(() =>
      stopPointWorkflowDetailResolver(mockRoute, {} as RouterStateSnapshot)
    ) as Observable<StopPointWorkflowDetailData>;

    resolvedVersion.subscribe((workflowData) => {
      expect(workflowData?.workflow.versionId).toBe(1);
      expect(workflowData?.servicePoint[0].designationOfficial).toBe(
        'Bern, Wyleregg'
      );
    });
  });
});
