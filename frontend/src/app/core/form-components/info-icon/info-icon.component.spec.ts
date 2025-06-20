import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InfoIconComponent } from './info-icon.component';

describe('DateIconComponent', () => {
  let component: InfoIconComponent;
  let fixture: ComponentFixture<InfoIconComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InfoIconComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InfoIconComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
