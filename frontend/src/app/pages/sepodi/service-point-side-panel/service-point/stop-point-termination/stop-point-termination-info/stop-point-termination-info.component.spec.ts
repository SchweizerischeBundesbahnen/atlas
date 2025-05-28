import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopPointTerminationInfoComponent } from './stop-point-termination-info.component';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { WorkflowService } from '../../../../../../api/service/workflow/workflow.service';
import { TerminationInfo } from '../../../../../../api/model/terminationInfo';
import { of } from 'rxjs';

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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StopPointTerminationInfoComponent, TranslateModule.forRoot()],
      providers: [
        { provide: TranslatePipe },
        { provide: WorkflowService, useValue: workflowService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(StopPointTerminationInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should init', () => {
    expect(component.terminationDate).toEqual('01.06.2021');
    expect(component.workflowId).toEqual(123);
  });
});
