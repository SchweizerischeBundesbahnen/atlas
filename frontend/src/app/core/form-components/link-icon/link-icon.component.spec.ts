import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LinkIconComponent } from './link-icon.component';

describe('LinkIconComponent', () => {
  let component: LinkIconComponent;
  let fixture: ComponentFixture<LinkIconComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    imports: [LinkIconComponent],
}).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LinkIconComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
