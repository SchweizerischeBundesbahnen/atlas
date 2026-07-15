import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { AtlasInfoIconComponent } from './atlas-info-icon.component';

describe('AtlasInfoIconComponent', () => {
  let component: AtlasInfoIconComponent;
  let fixture: ComponentFixture<AtlasInfoIconComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(AtlasInfoIconComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
