import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LidiOverviewComponent } from './lidi-overview.component';
import { LinesComponent } from '../lines/lines.component';
import { SublinesComponent } from '../sublines/sublines.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule } from '@angular/router';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { CoreModule } from '../../../core/module/core.module';

describe('LidiOverviewComponent', () => {
  let component: LidiOverviewComponent;
  let fixture: ComponentFixture<LidiOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LidiOverviewComponent, LinesComponent, SublinesComponent],
      imports: [
        CoreModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        RouterModule.forRoot([]),
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LidiOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
