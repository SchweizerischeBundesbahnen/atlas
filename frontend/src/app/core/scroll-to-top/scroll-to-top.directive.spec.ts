import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { AppTestingModule } from '../../app.testing.module';
import { ScrollToTopDirective } from './scroll-to-top.directive';

@Component({
    template: ` <div id="scrollbar-content-container" class="full-height">
    <div scrollToTop id="some-child-component">Random Content</div>
  </div>`,
    standalone: false
})
class TestComponent {}

describe('ScrollToTopDirective', () => {
  let fixture: ComponentFixture<TestComponent>;
  let scrollContainer: DebugElement;

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      declarations: [ScrollToTopDirective, TestComponent],
      imports: [AppTestingModule],
    }).createComponent(TestComponent);

    scrollContainer = fixture.debugElement.query(By.css('#scrollbar-content-container'));
  });

  it('should scroll to top', () => {
    const scrollElement = scrollContainer.nativeElement;
    spyOn(scrollElement, 'scroll').and.callThrough();

    fixture.detectChanges();
    expect(scrollElement.scroll).toHaveBeenCalledWith(0, 0);
  });
});
