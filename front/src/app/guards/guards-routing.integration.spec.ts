import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { Component } from '@angular/core';
import { Location } from '@angular/common';
import { expect } from '@jest/globals';
import { AuthGuard } from './auth.guard';
import { UnauthGuard } from './unauth.guard';
import { SessionService } from '../services/session.service';

// Composants factices pour les tests
@Component({ template: '' })
class DummyLoginComponent { }

@Component({ template: '' })
class DummySessionsComponent { }

@Component({ template: '' })
class DummyRentalsComponent { }

describe('Tests d\'intégration des Guards avec le routeur', () => {
  let router: Router;
  let location: Location;
  let sessionService: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          {
            path: 'login',
            component: DummyLoginComponent,
            canActivate: [UnauthGuard]
          },
          {
            path: 'sessions',
            component: DummySessionsComponent,
            canActivate: [AuthGuard]
          },
          {
            path: 'rentals',
            component: DummyRentalsComponent
          }
        ])
      ],
      declarations: [
        DummyLoginComponent,
        DummySessionsComponent,
        DummyRentalsComponent
      ],
      providers: [
        AuthGuard,
        UnauthGuard,
        SessionService
      ]
    });

    router = TestBed.inject(Router);
    location = TestBed.inject(Location);
    sessionService = TestBed.inject(SessionService);
  });

  it('devrait gérer le cycle complet de navigation : login -> sessions -> logout -> login', async () => {
    // État initial : non connecté
    sessionService.isLogged = false;
    await router.navigate(['/login']);
    expect(location.path()).toBe('/login');

    // Connexion
    sessionService.isLogged = true;

    // Accès à sessions
    await router.navigate(['/sessions']);
    expect(location.path()).toBe('/sessions');

    // Tentative d'accès à login (devrait rediriger vers rentals)
    await router.navigate(['/login']);
    expect(location.path()).toBe('/rentals');

    // Déconnexion
    sessionService.isLogged = false;

    // Tentative d'accès à sessions (devrait rediriger vers login)
    await router.navigate(['/sessions']);
    expect(location.path()).toBe('/login');
  });
});


