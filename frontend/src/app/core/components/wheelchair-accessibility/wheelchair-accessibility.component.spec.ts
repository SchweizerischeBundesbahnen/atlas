import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';

import { WheelchairAccessibilityComponent } from './wheelchair-accessibility.component';
import { TranslatePipe } from '@ngx-translate/core';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { WheelchairAccessibilityInternalService } from '../../../api/service/prm/wheelchair-accessibility/wheelchair-accessibility-internal.service';
import { of } from 'rxjs';
import { mock, mockClear } from 'vitest-mock-extended';
import { WheelchairAccessibilityState } from '../../../api/model/wheelchairAccessibilityState';

const wheelchairAccessibilityInternalService = mock<WheelchairAccessibilityInternalService>();
wheelchairAccessibilityInternalService.getPlatformAccessibilityToday.mockReturnValue(of({}));
wheelchairAccessibilityInternalService.getPlatformAccessibility.mockReturnValue(
  of({
    rows: [{ accessibilityState: WheelchairAccessibilityState.Autonomy, from: new Date(), to: new Date() }],
  })
);

wheelchairAccessibilityInternalService.getStopPointAccessibilityToday.mockReturnValue(
  of({ state: WheelchairAccessibilityState.Autonomy })
);
wheelchairAccessibilityInternalService.getStopPointAccessibility.mockReturnValue(
  of({
    rows: [{ accessibilityState: WheelchairAccessibilityState.Autonomy, from: new Date(), to: new Date() }],
  })
);

describe('WheelchairAccessibilityComponent', () => {
  let component: WheelchairAccessibilityComponent;
  let fixture: ComponentFixture<WheelchairAccessibilityComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [WheelchairAccessibilityComponent],
      providers: [
        { provide: TranslatePipe },
        translateServiceProvider,
        {
          provide: WheelchairAccessibilityInternalService,
          useValue: wheelchairAccessibilityInternalService,
        },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(WheelchairAccessibilityComponent);
  });

  describe('for stopPoint', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('objectType', 'STOP_POINT');
      fixture.componentRef.setInput('sloid', 'ch:1:sloid:90064');
      fixture.componentRef.setInput('editMode', false);

      mockClear(wheelchairAccessibilityInternalService);
      component = fixture.componentInstance;

      fixture.detectChanges();
    });

    it('should show icon for today with overlay closed', () => {
      expect(component).toBeTruthy();
      expect(component.isOverlayOpen).toBe(false);

      expect(component.wheelchairAccessibilityToday).toBe(WheelchairAccessibilityState.Autonomy);
      expect(component.wheelchairAccessibility.length).toBe(0);
    });

    it('should open overlay and display accessibility', () => {
      component.toggleOverlay();

      expect(wheelchairAccessibilityInternalService.getStopPointAccessibility).toHaveBeenCalled();
      expect(component.wheelchairAccessibility.length).toBe(1);

      fixture.componentRef.setInput('editMode', true);
      fixture.detectChanges();
      expect(component.isOverlayOpen).toBe(false);
    });

    it('should open overlay and close it', () => {
      component.toggleOverlay();
      expect(component.isOverlayOpen).toBe(true);

      component.toggleOverlay();
      expect(component.isOverlayOpen).toBe(false);

      component.toggleOverlay();
      expect(component.isOverlayOpen).toBe(true);

      component.closeOverlay();
      expect(component.isOverlayOpen).toBe(false);
    });
  });

  describe('for platform', () => {
    beforeEach(() => {
      fixture.componentRef.setInput('objectType', 'PLATFORM');
      fixture.componentRef.setInput('sloid', 'ch:1:sloid:90064:0:1');
      fixture.componentRef.setInput('editMode', false);

      mockClear(wheelchairAccessibilityInternalService);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should open overlay and display accessibility', () => {
      component.toggleOverlay();

      expect(wheelchairAccessibilityInternalService.getPlatformAccessibility).toHaveBeenCalled();
      expect(component.wheelchairAccessibility.length).toBe(1);
    });
  });
});
