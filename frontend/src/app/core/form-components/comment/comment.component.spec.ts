import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommentComponent } from './comment.component';
import { AppTestingModule } from '../../../app.testing.module';
import { FormControl, FormGroup } from '@angular/forms';
import { InfoIconComponent } from '../info-icon/info-icon.component';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { AtlasLabelFieldComponent } from '../atlas-label-field/atlas-label-field.component';
import { TextFieldComponent } from '../text-field/text-field.component';
import { TranslatePipe } from '@ngx-translate/core';

describe('CommentComponent', () => {
  let component: CommentComponent;
  let fixture: ComponentFixture<CommentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        CommentComponent,
        InfoIconComponent,
        AtlasFieldErrorComponent,
        InfoIconComponent,
        AtlasLabelFieldComponent,
        TextFieldComponent,
      ],
      imports: [AppTestingModule],
      providers: [{ provide: TranslatePipe }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CommentComponent);
    component = fixture.componentInstance;
    component.formGroup = new FormGroup({
      comment: new FormControl('test'),
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.formGroup.value).toEqual({ comment: 'test' });
  });
});
