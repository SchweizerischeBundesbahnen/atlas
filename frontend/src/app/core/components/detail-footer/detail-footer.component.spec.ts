import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DetailFooterComponent } from './detail-footer.component';
import { AppTestingModule } from '../../../app.testing.module';

let component: DetailFooterComponent;
let fixture: ComponentFixture<DetailFooterComponent>;

describe('DetailFooterComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DetailFooterComponent],
      imports: [AppTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailFooterComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
