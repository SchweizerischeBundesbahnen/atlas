import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UrlComponent } from './url.component';
import { AppTestingModule } from '../../../app.testing.module';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { InfoIconComponent } from '../info-icon/info-icon.component';

describe('UrlComponent', () => {
  let component: UrlComponent;
  let fixture: ComponentFixture<UrlComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UrlComponent, InfoIconComponent],
      imports: [AppTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UrlComponent);
    component = fixture.componentInstance;
    component.formGroup = new UntypedFormGroup({
      icon: new UntypedFormControl('https://www.sbb.ch'),
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.formGroup.value).toEqual({ icon: 'https://www.sbb.ch' });
  });
});
