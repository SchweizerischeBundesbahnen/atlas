import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopPointRestartWorkflowDialogComponent } from './stop-point-restart-workflow-dialog.component';

describe('StopPointRestartWorkflowDialogComponent', () => {
  let component: StopPointRestartWorkflowDialogComponent;
  let fixture: ComponentFixture<StopPointRestartWorkflowDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [StopPointRestartWorkflowDialogComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(StopPointRestartWorkflowDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
