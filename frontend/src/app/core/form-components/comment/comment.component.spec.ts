import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommentComponent } from './comment.component';
import { AppTestingModule } from '../../../app.testing.module';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { InfoIconComponent } from '../info-icon/info-icon.component';

describe('CommentComponent', () => {
  let component: CommentComponent;
  let fixture: ComponentFixture<CommentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CommentComponent, InfoIconComponent],
      imports: [AppTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CommentComponent);
    component = fixture.componentInstance;
    component.formGroup = new UntypedFormGroup({
      comment: new UntypedFormControl('test'),
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.formGroup.value).toEqual({ comment: 'test' });
  });
});
