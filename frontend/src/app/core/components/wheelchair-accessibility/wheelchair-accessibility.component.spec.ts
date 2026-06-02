import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';

import { WheelchairAccessibilityComponent } from './wheelchair-accessibility.component';
import { TranslatePipe } from '@ngx-translate/core';
import { translateServiceProvider } from '../../../app.testing.mocks';

describe.only('WheelchairAccessibilityComponent', () => {
  let component: WheelchairAccessibilityComponent;
  let fixture: ComponentFixture<WheelchairAccessibilityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WheelchairAccessibilityComponent],
      providers: [{ provide: TranslatePipe }, translateServiceProvider],
    }).compileComponents();

    fixture = TestBed.createComponent(WheelchairAccessibilityComponent);
    fixture.componentRef.setInput('objectType', 'STOP_POINT');
    fixture.componentRef.setInput('sloid', 'ch:1:sloid:90064');

    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
