import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { LanguageSwitcherComponent } from './language-switcher.component';
import { By } from '@angular/platform-browser';
import { DateAdapter } from '@angular/material/core';

describe('LanguageSwitcherComponent', () => {
  let component: LanguageSwitcherComponent;
  let fixture: ComponentFixture<LanguageSwitcherComponent>;

  const dateAdapter = jasmine.createSpyObj('dateAdapter', ['setLocale']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LanguageSwitcherComponent],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [{ provide: DateAdapter, useValue: dateAdapter }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LanguageSwitcherComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('switching languages works', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should switch to "de"', () => {
      component.setLanguage('de');
      expect(component.currentLanguage).toBe('de');
      expect(dateAdapter.setLocale).toHaveBeenCalledWith('de');
    });

    it('should switch to "fr"', () => {
      component.setLanguage('fr');
      expect(component.currentLanguage).toBe('fr');
    });

    it('should switch to "it"', () => {
      component.setLanguage('it');
      expect(component.currentLanguage).toBe('it');
    });
  });

  describe('language switch looks fantastic and works', () => {
    it('should create links for languages', () => {
      const links = fixture.debugElement.queryAll(By.css('a'));
      expect(links.length).toBe(component.languages.length);
    });

    it('should link to french', () => {
      const links = fixture.debugElement.queryAll(By.css('a'));
      const frenchLink = links[1];
      frenchLink.nativeElement.click();
      fixture.detectChanges();

      expect(component.currentLanguage).toBe('fr');
      expect(frenchLink.nativeElement).toHaveClass('isSelected');
    });
  });
});
