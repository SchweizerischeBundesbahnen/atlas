import { InfoLinkDirective } from './info-link.directive';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component } from '@angular/core';
import { By } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';
import { of } from 'rxjs';
import SpyObj = jasmine.SpyObj;

@Component({
    template: `<span infoLink infoLinkTranslationKey="TEST_TRANSLATION_KEY">Test</span>`,
    standalone: false
})
class TestComponent {}

describe('InfoLinkDirective', () => {
  let fixture: ComponentFixture<TestComponent>;
  let translateServiceSpy: SpyObj<TranslateService>;

  beforeEach(() => {
    translateServiceSpy = jasmine.createSpyObj<TranslateService>('TranslateServiceMock', ['get']);
    translateServiceSpy.get = jasmine.createSpy().and.returnValue(of('https://atlas.test.ch'));
    fixture = TestBed.configureTestingModule({
      declarations: [InfoLinkDirective, TestComponent],
      providers: [{ provide: TranslateService, useValue: translateServiceSpy }],
    }).createComponent(TestComponent);

    fixture.detectChanges();
  });

  it('should add atlas-info-link class to component host element', () => {
    const elementsWithInfoLinkDirective = fixture.debugElement.queryAll(
      By.directive(InfoLinkDirective)
    );
    expect(elementsWithInfoLinkDirective).toHaveSize(1);
    expect(elementsWithInfoLinkDirective[0].classes['atlas-info-link']).toBeTrue();
  });

  it('should handle click event', () => {
    const elementsWithInfoLinkDirective = fixture.debugElement.queryAll(
      By.directive(InfoLinkDirective)
    );
    expect(elementsWithInfoLinkDirective).toHaveSize(1);

    spyOn(window, 'open');
    spyOn(console, 'error');
    elementsWithInfoLinkDirective[0].nativeElement.click();
    fixture.detectChanges();
    expect(translateServiceSpy.get).toHaveBeenCalledOnceWith('TEST_TRANSLATION_KEY');
    expect(window.open).toHaveBeenCalledOnceWith('https://atlas.test.ch', '_blank');
    expect(console.error).not.toHaveBeenCalled();
  });
});
