import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlatformReducedFormComponent } from './platform-reduced-form.component';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasSpacerComponent } from '../../../../../core/components/spacer/atlas-spacer.component';
import {
  MockAtlasFieldErrorComponent,
  MockSelectComponent,
} from '../../../../../app.testing.mocks';
import { TextFieldComponent } from '../../../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent } from '../../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { AppTestingModule } from '../../../../../app.testing.module';

describe('PlatformReducedFormComponent', () => {
  let component: PlatformReducedFormComponent;
  let fixture: ComponentFixture<PlatformReducedFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        PlatformReducedFormComponent,
        MockSelectComponent,
        TextFieldComponent,
        MockAtlasFieldErrorComponent,
        AtlasLabelFieldComponent,
        AtlasSpacerComponent,
      ],
      imports: [AppTestingModule],
      providers: [{ provide: TranslatePipe }],
    });
    fixture = TestBed.createComponent(PlatformReducedFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
