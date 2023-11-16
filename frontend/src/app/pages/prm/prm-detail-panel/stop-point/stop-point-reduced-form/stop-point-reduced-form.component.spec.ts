import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopPointReducedFormComponent } from './stop-point-reduced-form.component';

describe('StopPointReducedFormComponent', () => {
  let component: StopPointReducedFormComponent;
  let fixture: ComponentFixture<StopPointReducedFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StopPointReducedFormComponent],
    });
    fixture = TestBed.createComponent(StopPointReducedFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
