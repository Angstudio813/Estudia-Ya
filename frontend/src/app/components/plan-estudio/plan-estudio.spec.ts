import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlanEstudio } from './plan-estudio';

describe('PlanEstudio', () => {
  let component: PlanEstudio;
  let fixture: ComponentFixture<PlanEstudio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlanEstudio],
    }).compileComponents();

    fixture = TestBed.createComponent(PlanEstudio);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
