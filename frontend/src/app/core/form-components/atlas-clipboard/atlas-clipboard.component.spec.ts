import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AtlasClipboardComponent } from './atlas-clipboard.component';
import { FormModule } from '../../module/form.module';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';

describe('AtlasClipboardComponent', () => {
  let component: AtlasClipboardComponent;
  let fixture: ComponentFixture<AtlasClipboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        FormModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        AtlasClipboardComponent,
      ],
      providers: [{ provide: TranslatePipe }],
    }).compileComponents();

    fixture = TestBed.createComponent(AtlasClipboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
