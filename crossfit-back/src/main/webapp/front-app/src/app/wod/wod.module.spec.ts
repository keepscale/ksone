import { WodModule } from './wod.module';

describe('WodModule', () => {
  let wodModule: WodModule;

  beforeEach(() => {
    wodModule = new WodModule();
  });

  it('should create an instance', () => {
    expect(wodModule).toBeTruthy();
  });
});
