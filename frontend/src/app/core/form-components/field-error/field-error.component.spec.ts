import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatLegacyFormField as MatFormField } from '@angular/material/legacy-form-field';
import { FieldErrorComponent } from './field-error.component';
import { AppTestingModule } from '../../../app.testing.module';

describe('FieldErrorComponent', () => {
  let component: FieldErrorComponent;
  let fixture: ComponentFixture<FieldErrorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FieldErrorComponent],
      imports: [AppTestingModule],
      providers: [{ provide: MatFormField, useValue: { _control: {} } }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FieldErrorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
