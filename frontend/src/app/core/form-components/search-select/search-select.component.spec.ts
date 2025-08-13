import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SearchSelectComponent } from './search-select.component';
import { NgSelectComponent, NgSelectModule } from '@ng-select/ng-select';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';

describe('SearchSelectComponent', () => {
  let component: SearchSelectComponent<unknown>;
  let fixture: ComponentFixture<SearchSelectComponent<unknown>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        NgSelectModule,
        ReactiveFormsModule,
        SearchSelectComponent,
        AtlasFieldErrorComponent,
      ],
      providers: [translateServiceProvider, provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(SearchSelectComponent);
    component = fixture.componentInstance;

    component.formGroup = new FormGroup({
      testControl: new FormControl(null),
    });
    component.controlName = 'testControl';

    fixture.detectChanges();
  });

  it('isDropdownOpen should return false', () => {
    component.ngSelect = jasmine.createSpyObj<NgSelectComponent>([], {
      isOpen: undefined,
    });
    expect(component.isDropdownOpen()).toBeFalse();
  });

  it('isDropdownOpen should return true', () => {
    component.ngSelect = jasmine.createSpyObj<NgSelectComponent>([], {
      isOpen: true,
    });
    expect(component.isDropdownOpen()).toBeTrue();
  });
});
