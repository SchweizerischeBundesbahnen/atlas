import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReferencePointComponent } from './reference-point.component';

describe('ReferencePointComponent', () => {
  let component: ReferencePointComponent;
  let fixture: ComponentFixture<ReferencePointComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ReferencePointComponent],
    });
    fixture = TestBed.createComponent(ReferencePointComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
