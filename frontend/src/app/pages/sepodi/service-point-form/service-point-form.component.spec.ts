import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServicePointFormComponent } from './service-point-form.component';

describe('ServicePointFormComponent', () => {
  let component: ServicePointFormComponent;
  let fixture: ComponentFixture<ServicePointFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ServicePointFormComponent],
    });
    fixture = TestBed.createComponent(ServicePointFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
