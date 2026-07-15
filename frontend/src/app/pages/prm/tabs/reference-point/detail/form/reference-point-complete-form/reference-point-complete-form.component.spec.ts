import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';

import { ReferencePointCompleteFormComponent } from './reference-point-complete-form.component';
import { TextFieldComponent } from '../../../../../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent, AtlasInfoIconComponent } from '@atlas/form';
import { MockAtlasFieldErrorComponent } from '../../../../../../../app.testing.mocks';
import { AtlasSpacerComponent } from '../../../../../../../core/components/spacer/atlas-spacer.component';
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
      imports: [
        AppTestingModule,
        ReferencePointCompleteFormComponent,
        TextFieldComponent,
        AtlasLabelFieldComponent,
        MockAtlasFieldErrorComponent,
        AtlasSpacerComponent,
        AtlasInfoIconComponent,
        SelectComponent,
        CommentComponent,
      ],
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
