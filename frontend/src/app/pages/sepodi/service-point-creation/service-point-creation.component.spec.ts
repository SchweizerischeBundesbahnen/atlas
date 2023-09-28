import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServicePointCreationComponent } from './service-point-creation.component';

describe('ServicePointCreationComponent', () => {
  let component: ServicePointCreationComponent;
  let fixture: ComponentFixture<ServicePointCreationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ServicePointCreationComponent],
    });
    fixture = TestBed.createComponent(ServicePointCreationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
