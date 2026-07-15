import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component } from '@angular/core';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { of } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { By } from '@angular/platform-browser';
import { AtlasInfoLinkDirective } from './atlas-info-link.directive';

@Component({
  imports: [AtlasInfoLinkDirective],
  template: `<span atlasInfoLink infoLinkTranslationKey="TEST_TRANSLATION_KEY">Test</span>`,
})
class TestComponent {}

describe('AtlasInfoLinkDirective', () => {
  let fixture: ComponentFixture<TestComponent>;

  const translateServiceSpy = {
    get: vi.fn().mockReturnValue(of('https://atlas.test.ch')),
  };

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      imports: [AtlasInfoLinkDirective, TestComponent],
      providers: [{ provide: TranslateService, useValue: translateServiceSpy }],
    }).createComponent(TestComponent);

    fixture.detectChanges();
  });

  it('should add atlas-info-link class to component host element', () => {
    const elementsWithInfoLinkDirective = fixture.debugElement.queryAll(By.directive(AtlasInfoLinkDirective));
    expect(elementsWithInfoLinkDirective).toHaveLength(1);
    expect(elementsWithInfoLinkDirective[0].classes['atlas-info-link']).toBe(true);
  });

  it('should handle click event', () => {
    const elementsWithInfoLinkDirective = fixture.debugElement.queryAll(By.directive(AtlasInfoLinkDirective));
    expect(elementsWithInfoLinkDirective).toHaveLength(1);

    vi.spyOn(window, 'open').mockImplementation(() => null);
    vi.spyOn(console, 'error');
    elementsWithInfoLinkDirective[0].nativeElement.click();
    fixture.detectChanges();
    expect(translateServiceSpy.get).toHaveBeenCalledTimes(1);
    expect(translateServiceSpy.get).toHaveBeenCalledWith('TEST_TRANSLATION_KEY');
    expect(window.open).toHaveBeenCalledTimes(1);
    expect(window.open).toHaveBeenCalledWith('https://atlas.test.ch', '_blank');
    expect(console.error).not.toHaveBeenCalled();
  });
});
