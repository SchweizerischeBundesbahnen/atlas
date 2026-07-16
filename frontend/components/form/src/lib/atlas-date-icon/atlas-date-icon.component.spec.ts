import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { AtlasDateIconComponent } from './atlas-date-icon.component';

describe('AtlasDateIconComponent', () => {
  let component: AtlasDateIconComponent;
  let fixture: ComponentFixture<AtlasDateIconComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AtlasDateIconComponent],
    });

    fixture = TestBed.createComponent(AtlasDateIconComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('enabled', true);
    fixture.detectChanges();
  });

  it('should be enabled', () => {
    expect(component.enabled()).toBe(true);
  });
});
