import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AreasDetailComponent } from './areas-detail.component';

describe('AreasDetailComponent', () => {
  let component: AreasDetailComponent;
  let fixture: ComponentFixture<AreasDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AreasDetailComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AreasDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
