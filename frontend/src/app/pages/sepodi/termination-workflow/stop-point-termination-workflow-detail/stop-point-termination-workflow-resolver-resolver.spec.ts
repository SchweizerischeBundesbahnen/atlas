import { TestBed } from '@angular/core/testing';

import {
  StopPointTerminationWorkflowDetailData,
  stopPointTerminationWorkflowResolver,
  StopPointTerminationWorkflowResolver,
} from './stop-point-termination-workflow-resolver';
import { TerminationStopPointAddWorkflow } from '../../../../api/model/terminationStopPointAddWorkflow';
import { Observable, of } from 'rxjs';
import { BERN_WYLEREGG } from 'src/test/data/service-point';
import { AppTestingModule } from '../../../../app.testing.module';
import {
  ActivatedRouteSnapshot,
  convertToParamMap,
  RouterStateSnapshot,
} from '@angular/router';
import { ServicePointService } from '../../../../api/service/sepodi/service-point.service';

const workflow: TerminationStopPointAddWorkflow = {
  versionId: 1,
  sloid: 'ch:1:sloid:1000',
  status: 'STARTED',
  workflowComment: 'comment',
  boTerminationDate: new Date('2020-03-31'),
  infoPlusTerminationDate: new Date('2020-03-31'),
  novaTerminationDate: new Date('2020-03-31'),
  applicantMail: 'asd@ab.ch',
};

describe('stopPointTerminationWorkflowResolverResolver', () => {
  const workflowService = jasmine.createSpyObj('workflowService', [
    'getTerminationById',
  ]);
  workflowService.getTerminationById.and.returnValue(of(workflow));

  const servicePointsService = jasmine.createSpyObj('servicePointsService', [
    'getServicePointVersionsBySloid',
  ]);
  servicePointsService.getServicePointVersionsBySloid.and.returnValue(
    of([BERN_WYLEREGG])
  );

  let resolver: StopPointTerminationWorkflowResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        StopPointTerminationWorkflowResolver,
        {
          provide: workflowService,
          useValue: workflowService,
        },
        { provide: ServicePointService, useValue: servicePointsService },
      ],
    });
    resolver = TestBed.inject(StopPointTerminationWorkflowResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get workflow with service point', () => {
    const mockRoute = {
      paramMap: convertToParamMap({ id: '1000' }),
    } as ActivatedRouteSnapshot;

    const resolvedVersion = TestBed.runInInjectionContext(() =>
      stopPointTerminationWorkflowResolver(mockRoute, {} as RouterStateSnapshot)
    ) as Observable<StopPointTerminationWorkflowDetailData>;

    resolvedVersion.subscribe((workflowData) => {
      expect(workflowData?.workflow.versionId).toBe(1);
      expect(workflowData?.servicePoint[0].designationOfficial).toBe(
        'Bern, Wyleregg'
      );
    });
  });
});
