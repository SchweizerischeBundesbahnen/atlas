import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi, type Mocked } from 'vitest';

import { FotCommentDetailComponent } from './fot-comment-detail.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { MockAtlasButtonComponent } from '../../../../app.testing.mocks';
import { CommentComponent } from '../../../../core/form-components/comment/comment.component';
import { AtlasFieldErrorComponent } from '../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../core/components/detail-page-content/detail-page-content.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { ServicePointService } from '../../../../api/service/sepodi/service-point.service';

describe('FotCommentDetailComponent', () => {
  let component: FotCommentDetailComponent;
  let fixture: ComponentFixture<FotCommentDetailComponent>;

  let servicePointServiceSpy: Mocked<Pick<ServicePointService, 'getFotComment' | 'saveFotComment'>>;

  const route = {
    parent: { snapshot: { params: { servicePointNumber: 8504414 } } },
  };

  beforeEach(async () => {
    servicePointServiceSpy = {
      getFotComment: vi.fn(),
      saveFotComment: vi.fn(),
    };
    servicePointServiceSpy.getFotComment.mockReturnValue(of({ fotComment: 'Manu Hooligans', etagVersion: 3 }));
    servicePointServiceSpy.saveFotComment.mockReturnValue(of({ fotComment: 'New comment', etagVersion: 3 }));

    await TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        FotCommentDetailComponent,
        MockAtlasButtonComponent,
        CommentComponent,
        AtlasFieldErrorComponent,
        DetailPageContainerComponent,
        DetailPageContentComponent,
        DetailFooterComponent,
      ],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: ServicePointService, useValue: servicePointServiceSpy },
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
    expect(component.form.enabled).toBe(false);
    component.toggleEdit();
    expect(component.form.enabled).toBe(true);

    component.form.controls.fotComment.setValue('New comment');
    component.save();

    expect(servicePointServiceSpy.saveFotComment).toHaveBeenCalled();
    expect(component.form.controls.fotComment.value).toBe('New comment');
  });

  it('should display confirmation on dirty leave', () => {
    component.toggleEdit();
    expect(component.form.enabled).toBe(true);

    component.form.controls.fotComment.setValue('New comment');
    component.toggleEdit();
    expect(component.form.enabled).toBe(false);
  });
});
