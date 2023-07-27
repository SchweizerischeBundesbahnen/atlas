import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServicePointDetailComponent } from './service-point-detail.component';

describe('ServicePointDetailComponent', () => {
  let component: ServicePointDetailComponent;
  let fixture: ComponentFixture<ServicePointDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ServicePointDetailComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ServicePointDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
