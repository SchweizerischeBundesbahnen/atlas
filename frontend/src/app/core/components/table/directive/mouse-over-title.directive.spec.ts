import { MouseOverTitleDirective } from './mouse-over-title.directive';
import { Component } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { By } from '@angular/platform-browser';

@Component({
  template: ` <div [atlasMouseOverTitle]="transform" [mouseOverTitleValue]="value"></div> `,
  imports: [MouseOverTitleDirective],
})
class HostComponent {
  value = '';
  transform: (v: string) => Observable<string> = vi.fn().mockReturnValue(of('result'));
}

describe('MouseOverTitleDirective', () => {
  let fixture: ComponentFixture<HostComponent>;
  let component: HostComponent;

  beforeEach(() => {
    fixture = TestBed.createComponent(HostComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    const debugEl = fixture.debugElement.query(By.directive(MouseOverTitleDirective));
    fixture.detectChanges();

    expect(debugEl.injector.get(MouseOverTitleDirective)).toBeTruthy();
  });

  it('should return on empty or unchanged mouseOverTitleValue', () => {
    // when
    const el = fixture.debugElement.query(By.directive(MouseOverTitleDirective)).nativeElement;
    el.dispatchEvent(new Event('mouseover'));
    fixture.detectChanges();

    // then
    expect(component.transform).not.toHaveBeenCalled();
  });

  it('should set title and oldValue correctly after transform success', () => {
    // given
    component.value = 'test';
    fixture.detectChanges();

    // when
    const el = fixture.debugElement.query(By.directive(MouseOverTitleDirective)).nativeElement;
    el.dispatchEvent(new Event('mouseover'));
    fixture.detectChanges();

    // then
    expect(component.transform).toHaveBeenCalledExactlyOnceWith('test');
    expect(el.title).toEqual('result');
  });

  it('should set title to empty after transform error', () => {
    // given
    vi.spyOn(console, 'error').mockImplementation(() => undefined);
    component.value = 'test';
    component.transform = vi.fn().mockReturnValue(throwError(() => 'error'));

    const debugEl = fixture.debugElement.query(By.directive(MouseOverTitleDirective));
    debugEl.injector.get(MouseOverTitleDirective).title = 'old title';

    fixture.detectChanges();

    expect(debugEl.nativeElement.title).toEqual('old title');

    // when
    debugEl.nativeElement.dispatchEvent(new Event('mouseover'));
    fixture.detectChanges();

    // then
    expect(component.transform).toHaveBeenCalledExactlyOnceWith('test');
    expect(debugEl.nativeElement.title).toEqual('');
  });
});
