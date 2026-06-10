import { TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi, type Mocked } from 'vitest';
import {
  StopPointTerminationWorkflowDetailData,
  stopPointTerminationWorkflowResolver,
  StopPointTerminationWorkflowResolver,
} from './stop-point-termination-workflow-resolver';
import { firstValueFrom, Observable, of } from 'rxjs';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';
import { AppTestingModule } from '../../../../app.testing.module';
import { ActivatedRouteSnapshot, convertToParamMap, RouterStateSnapshot } from '@angular/router';
import { ServicePointService } from '../../../../api/service/sepodi/service-point.service';
import { StopPointTerminationWorkflowService } from '../../../../api/service/workflow/stop-point-termination-workflow.service';
import { TerminationStopPointWorkflowModel } from '../../../../api/model/terminationStopPointWorkflowModel';

describe('stopPointTerminationWorkflowResolverResolver', () => {
  let resolver: StopPointTerminationWorkflowResolver;

  let workflowServiceMock: Mocked<Pick<StopPointTerminationWorkflowService, 'getTerminationById'>>;
  let servicePointsServiceMock: Mocked<Pick<ServicePointService, 'getServicePointVersionsBySloid'>>;

  beforeEach(() => {
    const workflow: TerminationStopPointWorkflowModel = {
      versionId: 1,
      sloid: 'ch:1:sloid:1000',
      status: 'STARTED',
      workflowComment: 'comment',
      boTerminationDate: new Date('2020-03-31'),
      infoPlusTerminationDate: new Date('2020-03-31'),
      novaTerminationDate: new Date('2020-03-31'),
      applicantMail: 'asd@ab.ch',
      designationOfficial: 'test',
    };

    workflowServiceMock = {
      getTerminationById: vi.fn().mockReturnValue(of(workflow)),
    };

    servicePointsServiceMock = {
      getServicePointVersionsBySloid: vi.fn().mockReturnValue(of([BERN_WYLEREGG])),
    };

    TestBed.configureTestingModule({
      imports: [AppTestingModule],
      providers: [
        StopPointTerminationWorkflowResolver,
        {
          provide: StopPointTerminationWorkflowService,
          useValue: workflowServiceMock,
        },
        { provide: ServicePointService, useValue: servicePointsServiceMock },
      ],
    });
    resolver = TestBed.inject(StopPointTerminationWorkflowResolver);
  });

  it('should create', () => {
    expect(resolver).toBeTruthy();
  });

  it('should get workflow with service point', async () => {
    const mockRoute = {
      paramMap: convertToParamMap({ id: '1000' }),
    } as ActivatedRouteSnapshot;

    const resolvedVersion = TestBed.runInInjectionContext(() =>
      stopPointTerminationWorkflowResolver(mockRoute, {} as RouterStateSnapshot)
    ) as Observable<StopPointTerminationWorkflowDetailData>;

    const workflowData = await firstValueFrom(resolvedVersion);
    expect(workflowData?.workflow.versionId).toBe(1);
    expect(workflowData?.servicePoint[0].designationOfficial).toBe('Bern, Wyleregg');
  });
});
