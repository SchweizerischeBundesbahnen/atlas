import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FotCommentDetailComponent } from './fot-comment-detail.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { MockAtlasButtonComponent } from '../../../../app.testing.mocks';
import { CommentComponent } from '../../../../core/form-components/comment/comment.component';
import { AtlasFieldErrorComponent } from '../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { ServicePointsService } from '../../../../api';

describe('FotCommentDetailComponent', () => {
  let component: FotCommentDetailComponent;
  let fixture: ComponentFixture<FotCommentDetailComponent>;

  const servicePointService = jasmine.createSpyObj('ServicePointService', [
    'getFotComment',
    'saveFotComment',
  ]);
  servicePointService.getFotComment.and.returnValue(
    of({ fotComment: 'Manu Hooligans', etagVersion: 3 }),
  );
  servicePointService.saveFotComment.and.returnValue(
    of({ fotComment: 'New comment', etagVersion: 3 }),
  );
  const route = { parent: { snapshot: { params: { id: 8504414 } } } };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        FotCommentDetailComponent,
        MockAtlasButtonComponent,
        CommentComponent,
        AtlasFieldErrorComponent,
      ],
      imports: [AppTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: ServicePointsService, useValue: servicePointService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FotCommentDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should init form correclty', () => {
    expect(component).toBeTruthy();

    expect(component.form.controls.fotComment.value).toBe('Manu Hooligans');
  });

  it('should update comment', () => {
    expect(component.form.enabled).toBeFalse();
    component.toggleEdit();
    expect(component.form.enabled).toBeTrue();

    component.form.controls.fotComment.setValue('New comment');
    component.save();

    expect(servicePointService.saveFotComment).toHaveBeenCalled();
    expect(component.form.controls.fotComment.value).toBe('New comment');
  });

  it('should display confirmation on dirty leave', () => {
    component.toggleEdit();
    expect(component.form.enabled).toBeTrue();

    component.form.controls.fotComment.setValue('New comment');
    component.toggleEdit();
    expect(component.form.enabled).toBeFalse();
  });
});
