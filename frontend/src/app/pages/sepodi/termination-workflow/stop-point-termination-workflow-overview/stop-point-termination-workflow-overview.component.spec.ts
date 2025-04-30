import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopPointTerminationWorkflowOverviewComponent } from './stop-point-termination-workflow-overview.component';

describe('StopPointTerminationWorkflowOverviewComponent', () => {
  let component: StopPointTerminationWorkflowOverviewComponent;
  let fixture: ComponentFixture<StopPointTerminationWorkflowOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [StopPointTerminationWorkflowOverviewComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StopPointTerminationWorkflowOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
