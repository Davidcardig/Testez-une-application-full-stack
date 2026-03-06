import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { User } from 'src/app/interfaces/user.interface';
import { SessionService } from 'src/app/services/session.service';
import { UserService } from 'src/app/services/user.service';

import { MeComponent } from './me.component';

describe('MeComponent - Tests unitaires', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  let mockUserService: jest.Mocked<UserService>;
  let mockSessionService: Partial<SessionService>;
  let mockRouter: Partial<Router>;
  let mockSnackBar: Partial<MatSnackBar>;

  const mockUser: User = {
    id: 1,
    email: 'john.doe@test.com',
    firstName: 'John',
    lastName: 'Doe',
    admin: false,
    password: 'password',
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-06-01'),
  };

  beforeEach(async () => {
    mockUserService = {
      getById: jest.fn().mockReturnValue(of(mockUser)),
      delete: jest.fn().mockReturnValue(of(null)),
    } as unknown as jest.Mocked<UserService>;

    mockSessionService = {
      sessionInformation: {
        id: 1,
        admin: false,
        token: 'token',
        type: 'Bearer',
        username: 'john.doe@test.com',
        firstName: 'John',
        lastName: 'Doe',
      },
      logOut: jest.fn(),
    };

    mockRouter = { navigate: jest.fn() };
    mockSnackBar = { open: jest.fn() };

    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
      ],
      providers: [
        { provide: UserService, useValue: mockUserService },
        { provide: SessionService, useValue: mockSessionService },
        { provide: Router, useValue: mockRouter },
        { provide: MatSnackBar, useValue: mockSnackBar },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
  });

  // ─── Création ────────────────────────────────────────────────────────────────

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  // ─── ngOnInit ────────────────────────────────────────────────────────────────

  it('devrait appeler userService.getById avec l\'id de la session au ngOnInit', () => {
    fixture.detectChanges();
    expect(mockUserService.getById).toHaveBeenCalledWith('1');
  });

  it('devrait affecter l\'utilisateur retourné à this.user', () => {
    fixture.detectChanges();
    expect(component.user).toEqual(mockUser);
  });

  // ─── back() ──────────────────────────────────────────────────────────────────

  it('devrait appeler window.history.back()', () => {
    fixture.detectChanges();
    const historySpy = jest.spyOn(window.history, 'back').mockImplementation(() => {});
    component.back();
    expect(historySpy).toHaveBeenCalled();
  });

  // ─── delete() ────────────────────────────────────────────────────────────────

  it('devrait appeler userService.delete avec l\'id de la session', () => {
    fixture.detectChanges();
    component.delete();
    expect(mockUserService.delete).toHaveBeenCalledWith('1');
  });

  it('devrait appeler sessionService.logOut après suppression', () => {
    fixture.detectChanges();
    component.delete();
    expect(mockSessionService.logOut).toHaveBeenCalled();
  });

});
