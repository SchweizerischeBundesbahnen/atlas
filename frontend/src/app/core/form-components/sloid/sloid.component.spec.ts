import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SloidComponent } from './sloid.component';
import { FormModule } from '../../module/form.module';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';

describe('SloidComponent', () => {
  let component: SloidComponent;
  let fixture: ComponentFixture<SloidComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SloidComponent],
      imports: [
        FormModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [{ provide: TranslatePipe }],
    }).compileComponents();

    fixture = TestBed.createComponent(SloidComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
