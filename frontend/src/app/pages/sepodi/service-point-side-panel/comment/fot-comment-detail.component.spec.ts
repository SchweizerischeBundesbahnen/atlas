import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FotCommentDetailComponent } from './fot-comment-detail.component';
import { AuthService } from '../../../../core/auth/auth.service';

describe('FotCommentDetailComponent', () => {
  let component: FotCommentDetailComponent;
  let fixture: ComponentFixture<FotCommentDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FotCommentDetailComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(FotCommentDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
