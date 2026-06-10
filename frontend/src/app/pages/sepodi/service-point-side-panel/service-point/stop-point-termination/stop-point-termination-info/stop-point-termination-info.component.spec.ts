import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi, type Mocked } from 'vitest';

import { StopPointTerminationInfoComponent } from './stop-point-termination-info.component';
import { TranslatePipe } from '@ngx-translate/core';
import { StopPointTerminationWorkflowService } from '../../../../../../api/service/workflow/stop-point-termination-workflow.service';
import { TerminationInfo } from '../../../../../../api/model/terminationInfo';
import { of } from 'rxjs';
import { Router } from '@angular/router';
import { Pages } from '../../../../../pages';

const terminationInfo: TerminationInfo = {
  workflowId: 123,
  terminationDate: new Date('2021-06-01'),
};

describe('StopPointTerminationInfoComponent', () => {
  let component: StopPointTerminationInfoComponent;
  let fixture: ComponentFixture<StopPointTerminationInfoComponent>;
  let routerSpy: Mocked<Pick<Router, 'createUrlTree' | 'serializeUrl'>>;

  let workflowServiceSpy: Mocked<Pick<StopPointTerminationWorkflowService, 'getTerminationInfoBySloid'>>;

  beforeEach(async () => {
    workflowServiceSpy = {
      getTerminationInfoBySloid: vi.fn(),
    };
    workflowServiceSpy.getTerminationInfoBySloid.mockReturnValue(of(terminationInfo));

    routerSpy = {
      createUrlTree: vi.fn(),
      serializeUrl: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [StopPointTerminationInfoComponent],
      providers: [
        { provide: TranslatePipe },
        {
          provide: StopPointTerminationWorkflowService,
          useValue: workflowServiceSpy,
        },
        { provide: Router, useValue: routerSpy },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(StopPointTerminationInfoComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('sloid', 'ch:1:sloid:7000');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should init', () => {
    expect(component.terminationDate).toEqual('01.06.2021');
    expect(component.workflowId).toEqual(123);
  });

  it('should navigate', () => {
    const workflowId = 123;

    const expectedUrl = '/line-service-point-directory/termination-workflows/123';
    routerSpy.serializeUrl.mockReturnValue(expectedUrl);

    vi.spyOn(window, 'open').mockImplementation(() => null);

    component.navigate();

    expect(routerSpy.createUrlTree).toHaveBeenCalledWith([
      Pages.SEPODI.path,
      Pages.TERMINATION_STOP_POINT_WORKFLOWS.path,
      workflowId,
    ]);
    expect(window.open).toHaveBeenCalledWith(expectedUrl, '_blank');
  });
});
