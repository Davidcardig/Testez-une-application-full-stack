import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';
import { Router } from '@angular/router';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SessionService } from 'src/app/services/session.service';

import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';

describe('TEST UNITAIRE', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [SessionService],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('Créer le composant', () => {
    expect(component).toBeTruthy();
  });

  describe('Validation du formulaire', () => {
    it('devrait valider un email correct', () => {
      const emailControl = component.form.get('email');
      emailControl?.setValue('test@example.com');
      expect(emailControl?.valid).toBeTruthy();
    });

    it('devrait invalider un email incorrect', () => {
      const emailControl = component.form.get('email');
      emailControl?.setValue('invalide@.com');
      expect(emailControl?.valid).toBeFalsy();
      expect(emailControl?.hasError('email')).toBeTruthy();
    });

    it('devrait requérir email et password', () => {
      const emailControl = component.form.get('email');
      const passwordControl = component.form.get('password');

      expect(emailControl?.valid).toBeFalsy();
      expect(emailControl?.hasError('required')).toBeTruthy();
      expect(passwordControl?.valid).toBeFalsy();
      expect(passwordControl?.hasError('required')).toBeTruthy();
    });
  });
});

// TESTS D'INTÉGRATION
describe('TEST INTEGRATION', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let httpMock: HttpTestingController;
  let mockRouter: any;
  let sessionService: SessionService;

  beforeEach(async () => {
    mockRouter = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [
        AuthService,
        SessionService,
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    sessionService = TestBed.inject(SessionService);
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('devrait connecter un utilisateur et naviguer vers /sessions en cas de succès', () => {
    const loginData = {
      email: 'test@test.com',
      password: 'test123'
    };

    const mockSessionInfo = {
      token: 'fake-jwt-token',
      type: 'Bearer',
      id: 1,
      username: 'test@test.com',
      firstName: 'David',
      lastName: 'Cardigos',
      admin: false
    };

    component.form.setValue(loginData);
    const logInSpy = jest.spyOn(sessionService, 'logIn');
    component.submit();

    const req = httpMock.expectOne('api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(loginData);

    req.flush(mockSessionInfo);

    expect(logInSpy).toHaveBeenCalledWith(mockSessionInfo);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
    expect(component.onError).toBeFalsy();
  });

  it('devrait afficher une erreur en cas d\'échec de la connexion', () => {
    const loginData = {
      email: 'test@test.com',
      password: 'wrongpassword'
    };

    component.form.setValue(loginData);
    const logInSpy = jest.spyOn(sessionService, 'logIn');
    component.submit();

    const req = httpMock.expectOne('api/auth/login');
    req.flush(
      { message: 'Invalid credentials' },
      { status: 401, statusText: 'Unauthorized' }
    );

    expect(logInSpy).not.toHaveBeenCalled();
    expect(mockRouter.navigate).not.toHaveBeenCalled();
    expect(component.onError).toBeTruthy();
  });
});
