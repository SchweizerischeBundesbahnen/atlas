import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DateIconComponent } from './date-icon.component';
import { beforeEach, describe, expect, it } from 'vitest';

describe('DateIconComponent', () => {
  let component: DateIconComponent;
  let fixture: ComponentFixture<DateIconComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DateIconComponent],
    });

    fixture = TestBed.createComponent(DateIconComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('enabled', true);
    fixture.detectChanges();
  });

  it('should be enabled', () => {
    expect(component.enabled()).toBe(true);
  });
});
