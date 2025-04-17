import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MaintenanceIconComponent } from './maintenance-icon.component';

describe('MaintenanceIconComponent', () => {
  let component: MaintenanceIconComponent;
  let fixture: ComponentFixture<MaintenanceIconComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MaintenanceIconComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(MaintenanceIconComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
