import { TestBed, inject } from '@angular/core/testing';

import { WodService } from './wod.service';

describe('WodService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [WodService]
    });
  });

  it('should be created', inject([WodService], (service: WodService) => {
    expect(service).toBeTruthy();
  }));
});
