import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UrlComponent } from './url.component';
import { AppTestingModule } from '../../../app.testing.module';
import { FormControl, FormGroup } from '@angular/forms';
import { LinkIconComponent } from '../link-icon/link-icon.component';
import { InfoIconComponent } from '../info-icon/info-icon.component';
import { TextFieldComponent } from '../text-field/text-field.component';
import { AtlasLabelFieldComponent } from '../atlas-label-field/atlas-label-field.component';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { TranslatePipe } from '@ngx-translate/core';

describe('UrlComponent', () => {
  let component: UrlComponent;
  let fixture: ComponentFixture<UrlComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        UrlComponent,
        InfoIconComponent,
        LinkIconComponent,
        TextFieldComponent,
        AtlasLabelFieldComponent,
        AtlasFieldErrorComponent,
      ],
      imports: [AppTestingModule],
      providers: [{ provide: TranslatePipe }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UrlComponent);
    component = fixture.componentInstance;
    component.formGroup = new FormGroup({
      icon: new FormControl('https://www.sbb.ch'),
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.formGroup.value).toEqual({ icon: 'https://www.sbb.ch' });
  });
});
