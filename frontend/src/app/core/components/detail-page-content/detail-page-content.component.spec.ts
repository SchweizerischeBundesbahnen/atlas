import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DetailPageContentComponent } from './detail-page-content.component';
import { AppTestingModule } from '../../../app.testing.module';

let component: DetailPageContentComponent;
let fixture: ComponentFixture<DetailPageContentComponent>;

describe('DetailPageContainerComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
    imports: [AppTestingModule, DetailPageContentComponent],
}).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailPageContentComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
