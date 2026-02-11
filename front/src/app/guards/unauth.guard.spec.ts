import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { expect } from '@jest/globals';
import { UnauthGuard } from './unauth.guard';
import { SessionService } from '../services/session.service';

describe('UnauthGuard', () => {
  let guard: UnauthGuard;
  let mockRouter: jest.Mocked<Router>;
  let mockSessionService: Partial<SessionService>;

  beforeEach(() => {
    mockRouter = {
      navigate: jest.fn()
    } as any;

    mockSessionService = {
      isLogged: false
    };

    TestBed.configureTestingModule({
      providers: [
        UnauthGuard,
        { provide: Router, useValue: mockRouter },
        { provide: SessionService, useValue: mockSessionService }
      ]
    });

    guard = TestBed.inject(UnauthGuard);
  });

  it('devrait être créé', () => {
    expect(guard).toBeTruthy();
  });

  describe('Tests unitaires', () => {
    describe('canActivate', () => {
      it('devrait retourner true quand l\'utilisateur n\'est pas connecté', () => {
        mockSessionService.isLogged = false;

        const result = guard.canActivate();

        expect(result).toBe(true);
        expect(mockRouter.navigate).not.toHaveBeenCalled();
      });

      it('devrait retourner false et rediriger vers rentals quand l\'utilisateur est connecté', () => {
        mockSessionService.isLogged = true;

        const result = guard.canActivate();

        expect(result).toBe(false);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['rentals']);
      });

      it('devrait appeler router.navigate avec le bon chemin lors de la redirection', () => {
        mockSessionService.isLogged = true;

        guard.canActivate();

        expect(mockRouter.navigate).toHaveBeenCalledTimes(1);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['rentals']);
      });
    });
  });
});

