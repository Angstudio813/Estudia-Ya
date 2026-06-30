import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LogrosRetos } from './logros-retos';

describe('LogrosRetos', () => {
  let component: LogrosRetos;
  let fixture: ComponentFixture<LogrosRetos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LogrosRetos],
    }).compileComponents();

    fixture = TestBed.createComponent(LogrosRetos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
