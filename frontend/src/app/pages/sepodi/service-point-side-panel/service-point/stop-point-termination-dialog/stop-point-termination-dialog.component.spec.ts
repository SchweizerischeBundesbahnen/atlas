import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopPointTerminationDialogComponent } from './stop-point-termination-dialog.component';

describe('StopPointTerminationDialogComponent', () => {
  let component: StopPointTerminationDialogComponent;
  let fixture: ComponentFixture<StopPointTerminationDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [StopPointTerminationDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StopPointTerminationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
