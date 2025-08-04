import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SearchSelectComponent } from './search-select.component';
import { NgSelectComponent, NgSelectModule } from '@ng-select/ng-select';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AtlasFieldErrorComponent } from '../atlas-field-error/atlas-field-error.component';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';

describe('SearchSelectComponent', () => {
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  let component: SearchSelectComponent<any>;
  // eslint-disable-next-line  @typescript-eslint/no-explicit-any
  let fixture: ComponentFixture<SearchSelectComponent<any>>;

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
    component.ngSelect = jasmine.createSpyObj<NgSelectComponent>([], {
      isOpen: undefined,
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('isDropdownOpen should return false', () => {
    expect(component.isDropdownOpen()).toBeFalse();
  });
});
