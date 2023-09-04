import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CantonCardComponent } from './canton-card.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { By } from '@angular/platform-browser';
import { Canton } from '../../../../core/cantons/Canton';

const cantonAG: Canton = { short: 'AG', path: 'ag' };

describe('CantonCardComponent', () => {
  let component: CantonCardComponent;
  let fixture: ComponentFixture<CantonCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CantonCardComponent],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CantonCardComponent);
    component = fixture.componentInstance;
    fixture.componentInstance.canton = cantonAG;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get svg by canton', () => {
    //when
    const cantonFlag = fixture.debugElement.queryAll(By.css('.flag'));
    //then
    expect(cantonFlag.length).toBe(1);
    expect(cantonFlag[0].attributes['src']).toContain('assets/images/cantons/AG.svg');
  });
});
