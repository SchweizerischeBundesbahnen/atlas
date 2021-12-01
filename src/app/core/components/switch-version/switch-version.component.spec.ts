import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SwitchVersionComponent } from './switch-version.component';

describe('SwitchVersionComponent', () => {
  let component: SwitchVersionComponent;
  let fixture: ComponentFixture<SwitchVersionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SwitchVersionComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SwitchVersionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
