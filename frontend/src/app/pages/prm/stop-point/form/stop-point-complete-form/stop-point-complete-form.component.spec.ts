import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopPointCompleteFormComponent } from './stop-point-complete-form.component';

describe('StopPointCompleteFormComponent', () => {
  let component: StopPointCompleteFormComponent;
  let fixture: ComponentFixture<StopPointCompleteFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StopPointCompleteFormComponent],
    });
    fixture = TestBed.createComponent(StopPointCompleteFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
