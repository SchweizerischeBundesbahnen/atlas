import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DetailPageContainerComponent } from './detail-page-container.component';
import { AppTestingModule } from '../../../app.testing.module';

let component: DetailPageContainerComponent;
let fixture: ComponentFixture<DetailPageContainerComponent>;

describe('DetailPageContainerComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
    imports: [AppTestingModule, DetailPageContainerComponent],
}).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailPageContainerComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
