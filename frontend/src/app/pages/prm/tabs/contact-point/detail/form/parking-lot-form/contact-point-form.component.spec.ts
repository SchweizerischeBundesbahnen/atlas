import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ContactPointFormComponent} from './contact-point-form.component';
import {TextFieldComponent} from '../../../../../../../core/form-components/text-field/text-field.component';
import {AtlasLabelFieldComponent} from '../../../../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import {MockAtlasFieldErrorComponent} from '../../../../../../../app.testing.mocks';
import {AtlasSpacerComponent} from '../../../../../../../core/components/spacer/atlas-spacer.component';
import {InfoIconComponent} from '../../../../../../../core/form-components/info-icon/info-icon.component';
import {SelectComponent} from '../../../../../../../core/form-components/select/select.component';
import {CommentComponent} from '../../../../../../../core/form-components/comment/comment.component';
import {AppTestingModule} from '../../../../../../../app.testing.module';
import {TranslatePipe} from '@ngx-translate/core';
import {ContactPointFormGroupBuilder} from "../contact-point-form-group";

describe('ContactPointFormComponent', () => {
  let component: ContactPointFormComponent;
  let fixture: ComponentFixture<ContactPointFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        ContactPointFormComponent,
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
    fixture = TestBed.createComponent(ContactPointFormComponent);
    component = fixture.componentInstance;
    component.form = ContactPointFormGroupBuilder.buildFormGroup();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
