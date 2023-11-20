import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateStopPointComponent } from './create-stop-point.component';

describe('CreateStopPointComponent', () => {
  let component: CreateStopPointComponent;
  let fixture: ComponentFixture<CreateStopPointComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateStopPointComponent],
    });
    fixture = TestBed.createComponent(CreateStopPointComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
