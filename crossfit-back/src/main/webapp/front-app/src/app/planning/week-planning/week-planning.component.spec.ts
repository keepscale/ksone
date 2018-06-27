import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WeekPlanningComponent } from './week-planning.component';

describe('WeekPlanningComponent', () => {
  let component: WeekPlanningComponent;
  let fixture: ComponentFixture<WeekPlanningComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WeekPlanningComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WeekPlanningComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
