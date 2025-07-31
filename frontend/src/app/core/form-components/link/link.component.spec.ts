import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LinkComponent } from './link.component';
import { LinkIconComponent } from '../link-icon/link-icon.component';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { TranslatePipe } from '@ngx-translate/core';
import { provideHttpClient } from '@angular/common/http';

describe('LinkComponent', () => {
  let component: LinkComponent;
  let fixture: ComponentFixture<LinkComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LinkComponent, LinkIconComponent],
      providers: [TranslatePipe, provideHttpClient(), translateServiceProvider],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LinkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
