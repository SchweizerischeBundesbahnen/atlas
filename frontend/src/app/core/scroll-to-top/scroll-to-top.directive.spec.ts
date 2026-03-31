import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component } from '@angular/core';
import { By } from '@angular/platform-browser';
import { ScrollToTopDirective } from './scroll-to-top.directive';

@Component({
  template: `<div id="scrollbar-content-container" class="full-height">
    <div atlasScrollToTop id="some-child-component">Random Content</div>
  </div>`,
  imports: [ScrollToTopDirective],
})
class TestComponent {}

describe('ScrollToTopDirective', () => {
  let fixture: ComponentFixture<TestComponent>;

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
  });

  it('should scroll to top', () => {
    const scrollContainer = fixture.debugElement.query(
      By.css('#scrollbar-content-container')
    ).nativeElement;
    scrollContainer.scroll = vi.fn();

    fixture.detectChanges();

    expect(scrollContainer.scroll).toHaveBeenCalledExactlyOnceWith(0, 0);
  });
});
