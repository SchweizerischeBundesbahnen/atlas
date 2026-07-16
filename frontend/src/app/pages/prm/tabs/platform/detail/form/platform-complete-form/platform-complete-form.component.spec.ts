import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';

import { PlatformCompleteFormComponent } from './platform-complete-form.component';
import { TextFieldComponent } from '../../../../../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent, AtlasInfoIconComponent } from '@atlas/form';
import { MockAtlasFieldErrorComponent } from '../../../../../../../app.testing.mocks';
import { AtlasSpacerComponent } from '../../../../../../../core/components/spacer/atlas-spacer.component';
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
      imports: [
        AppTestingModule,
        PlatformCompleteFormComponent,
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

    fixture = TestBed.createComponent(PlatformCompleteFormComponent);
    component = fixture.componentInstance;
    component.form = PlatformFormGroupBuilder.buildCompleteFormGroup();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
