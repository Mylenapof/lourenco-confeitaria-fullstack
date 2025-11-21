import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CategoriaFilterComponent } from './categoria-filter.component';

describe('CategoriaFilterComponent', () => {
  let component: CategoriaFilterComponent;
  let fixture: ComponentFixture<CategoriaFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoriaFilterComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CategoriaFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
