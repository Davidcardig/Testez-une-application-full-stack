import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';

import { AppComponent } from './app.component';
import { SessionService } from './services/session.service';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let sessionService: SessionService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatToolbarModule
      ],
      declarations: [
        AppComponent
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('devrait créer l\'application', () => {
    expect(component).toBeTruthy();
  });

  describe('Tests unitaires', () => {
    describe('$isLogged()', () => {
      it('devrait retourner l\'Observable du SessionService', (done) => {
        jest.spyOn(sessionService, '$isLogged').mockReturnValue(of(true));

        component.$isLogged().subscribe(isLogged => {
          expect(isLogged).toBe(true);
          done();
        });
      });
    });

    describe('logout()', () => {
      it('devrait appeler sessionService.logOut() et naviguer vers la page d\'accueil', () => {
        const logOutSpy = jest.spyOn(sessionService, 'logOut');
        const navigateSpy = jest.spyOn(router, 'navigate');

        component.logout();

        expect(logOutSpy).toHaveBeenCalled();
        expect(navigateSpy).toHaveBeenCalledWith(['']);
      });
    });
  });

  describe('Tests d\'intégration', () => {
    it('devrait gérer le cycle complet de déconnexion', (done) => {
      sessionService.logIn({
        token: 'test-token',
        type: 'Bearer',
        id: 1,
        username: 'testuser',
        firstName: 'Test',
        lastName: 'User',
        admin: false
      });

      component.$isLogged().subscribe(isLogged => {
        if (isLogged) {
          const navigateSpy = jest.spyOn(router, 'navigate');
          component.logout();

          expect(sessionService.isLogged).toBe(false);
          expect(navigateSpy).toHaveBeenCalledWith(['']);
          done();
        }
      });
    });
  });

  describe('Rendu du template', () => {
    it('devrait afficher le titre "Yoga app"', () => {
      const compiled = fixture.nativeElement;
      const title = compiled.querySelector('mat-toolbar span');
      expect(title.textContent).toContain('Yoga app');
    });
  });
});

