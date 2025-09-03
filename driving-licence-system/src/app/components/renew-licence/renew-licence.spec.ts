import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RenewLicence } from './renew-licence';

describe('RenewLicence', () => {
  let component: RenewLicence;
  let fixture: ComponentFixture<RenewLicence>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RenewLicence]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RenewLicence);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
