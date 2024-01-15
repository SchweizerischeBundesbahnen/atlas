import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlatformCompleteFormComponent } from './platform-complete-form.component';
import { TextFieldComponent } from '../../../../../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent } from '../../../../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { MockAtlasFieldErrorComponent } from '../../../../../../../app.testing.mocks';
import { AtlasSpacerComponent } from '../../../../../../../core/components/spacer/atlas-spacer.component';
import { InfoIconComponent } from '../../../../../../../core/form-components/info-icon/info-icon.component';
import { SelectComponent } from '../../../../../../../core/form-components/select/select.component';
import { CommentComponent } from '../../../../../../../core/form-components/comment/comment.component';
import { AppTestingModule } from '../../../../../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { PlatformFormGroupBuilder } from '../platform-form-group';

describe('PlatformCompleteFormComponent', () => {
  let component: PlatformCompleteFormComponent;
  let fixture: ComponentFixture<PlatformCompleteFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        PlatformCompleteFormComponent,
        TextFieldComponent,
        AtlasLabelFieldComponent,
        MockAtlasFieldErrorComponent,
        AtlasSpacerComponent,
        InfoIconComponent,
        SelectComponent,
        CommentComponent,
      ],
      imports: [AppTestingModule],
      providers: [{ provide: TranslatePipe }],
    });
    fixture = TestBed.createComponent(PlatformCompleteFormComponent);
    component = fixture.componentInstance;
    component.form = PlatformFormGroupBuilder.buildCompleteFormGroup();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
