import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReferencePointCompleteFormComponent } from './reference-point-complete-form.component';
import { TextFieldComponent } from '../../../../../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent } from '../../../../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { MockAtlasFieldErrorComponent } from '../../../../../../../app.testing.mocks';
import { AtlasSpacerComponent } from '../../../../../../../core/components/spacer/atlas-spacer.component';
import { InfoIconComponent } from '../../../../../../../core/form-components/info-icon/info-icon.component';
import { SelectComponent } from '../../../../../../../core/form-components/select/select.component';
import { CommentComponent } from '../../../../../../../core/form-components/comment/comment.component';
import { AppTestingModule } from '../../../../../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { ReferencePointFormGroupBuilder } from '../reference-point-form-group';

describe('ReferencePointCompleteFormComponent', () => {
  let component: ReferencePointCompleteFormComponent;
  let fixture: ComponentFixture<ReferencePointCompleteFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        ReferencePointCompleteFormComponent,
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
    fixture = TestBed.createComponent(ReferencePointCompleteFormComponent);
    component = fixture.componentInstance;
    component.form = ReferencePointFormGroupBuilder.buildCompleteFormGroup();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
