import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ToiletDetailComponent } from './toilet-detail.component';

describe('ToiletDetailComponent', () => {
  let component: ToiletDetailComponent;
  let fixture: ComponentFixture<ToiletDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ToiletDetailComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ToiletDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
