import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ToiletComponent } from './toilet.component';

describe('ToiletteComponent', () => {
  let component: ToiletComponent;
  let fixture: ComponentFixture<ToiletComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ToiletComponent],
    });
    fixture = TestBed.createComponent(ToiletComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
