import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopPointTerminationInfoComponent } from './stop-point-termination-info.component';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { StopPointTerminationWorkflowService } from '../../../../../../api/service/workflow/stop-point-termination-workflow.service';
import { TerminationInfo } from '../../../../../../api/model/terminationInfo';
import { of } from 'rxjs';
import { Router } from '@angular/router';
import { Pages } from '../../../../../pages';

const terminationInfo: TerminationInfo = {
  workflowId: 123,
  terminationDate: new Date('2021-06-01'),
};

const workflowService = jasmine.createSpyObj('workflowService', [
  'getTerminationInfoBySloid',
]);

workflowService.getTerminationInfoBySloid.and.returnValue(of(terminationInfo));

describe('StopPointTerminationInfoComponent', () => {
  let component: StopPointTerminationInfoComponent;
  let fixture: ComponentFixture<StopPointTerminationInfoComponent>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const routerSpy = jasmine.createSpyObj('Router', [
      'createUrlTree',
      'serializeUrl',
    ]);

    await TestBed.configureTestingModule({
      imports: [StopPointTerminationInfoComponent, TranslateModule.forRoot()],
      providers: [
        { provide: TranslatePipe },
        {
          provide: StopPointTerminationWorkflowService,
          useValue: workflowService,
        },
        { provide: Router, useValue: routerSpy },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(StopPointTerminationInfoComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('sloid', 'ch:1:sloid:7000');
    fixture.detectChanges();
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
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

    const expectedUrl =
      '/line-service-point-directory/termination-workflows/123';
    router.serializeUrl.and.returnValue(expectedUrl);

    spyOn(window, 'open');

    component.navigate();

    expect(router.createUrlTree).toHaveBeenCalledWith([
      Pages.SEPODI.path,
      Pages.TERMINATION_STOP_POINT_WORKFLOWS.path,
      workflowId,
    ]);
    expect(window.open).toHaveBeenCalledWith(expectedUrl, '_blank');
  });
});
