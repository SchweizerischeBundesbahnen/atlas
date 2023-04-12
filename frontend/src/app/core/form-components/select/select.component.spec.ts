import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SelectComponent } from './select.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { NgSelectComponent, NgSelectModule } from '@ng-select/ng-select';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

describe('SearchSelectComponent', () => {
  let component: SelectComponent<any>;
  let fixture: ComponentFixture<SelectComponent<any>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SelectComponent],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        NgSelectModule,
        ReactiveFormsModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SelectComponent);
    component = fixture.componentInstance;
    component.formGroup = new FormGroup({
      testControl: new FormControl(null),
    });
    component.controlName = 'testControl';
    component.ngSelect = jasmine.createSpyObj<NgSelectComponent>([], { isOpen: undefined });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('isDropdownOpen should return false', () => {
    expect(component.isDropdownOpen()).toBeFalse();
  });
});
