import { ComponentFixture, TestBed } from '@angular/core/testing';
import { describe, expect, it, beforeEach } from 'vitest';
import { LinkIconComponent } from './link-icon.component';

describe('LinkIconComponent', () => {
  let component: LinkIconComponent;
  let fixture: ComponentFixture<LinkIconComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(LinkIconComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
