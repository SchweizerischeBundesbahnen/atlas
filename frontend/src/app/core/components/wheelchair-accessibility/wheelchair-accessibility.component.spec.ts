import {ComponentFixture, TestBed} from '@angular/core/testing';
import {beforeEach, describe, expect, it} from 'vitest';

import {WheelchairAccessibilityComponent} from './wheelchair-accessibility.component';

describe('WheelchairAccessibilityComponent', () => {
  let component: WheelchairAccessibilityComponent;
  let fixture: ComponentFixture<WheelchairAccessibilityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WheelchairAccessibilityComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(WheelchairAccessibilityComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
