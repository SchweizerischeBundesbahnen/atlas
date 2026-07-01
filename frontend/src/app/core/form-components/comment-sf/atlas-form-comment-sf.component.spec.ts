import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AtlasFormCommentSfComponent } from './atlas-form-comment-sf.component';

describe('CommentSfComponent', () => {
  let component: AtlasFormCommentSfComponent;
  let fixture: ComponentFixture<AtlasFormCommentSfComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AtlasFormCommentSfComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AtlasFormCommentSfComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
