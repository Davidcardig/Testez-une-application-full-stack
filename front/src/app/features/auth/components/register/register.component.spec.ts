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

import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';

describe('TEST UNITAIRE', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
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

    fixture = TestBed.createComponent(RegisterComponent);
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
      emailControl?.setValue('david@.com');
      expect(emailControl?.valid).toBeFalsy();
      expect(emailControl?.hasError('email')).toBeTruthy();
    });

    it('devrait requérir tous les champs', () => {
      expect(component.form.get('firstName')?.hasError('required')).toBeTruthy();
      expect(component.form.get('lastName')?.hasError('required')).toBeTruthy();
      expect(component.form.get('password')?.hasError('required')).toBeTruthy();
    });
  });

});



// TESTS D'INTÉGRATION
describe('TEST INTEGRATION', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let httpMock: HttpTestingController;
  let mockRouter: any;

  beforeEach(async () => {
    mockRouter = {
      navigate: jest.fn()
    };

    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
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
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });


  it('devrait enregistrer un utilisateur et naviguer vers /login en cas de succès', () => {
    const registerData = {
      email: 'test@test.com',
      firstName: 'David',
      lastName: 'Cardigos',
      password: 'test123'
    };

    component.form.setValue(registerData);
    component.submit();

    const req = httpMock.expectOne('api/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(registerData);

    req.flush(null);

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
    expect(component.onError).toBeFalsy();
  });

  it('devrait afficher une erreur en cas d\'échec de l\'enregistrement', () => {
    const registerData = {
      email: 'test@test.com',
      firstName: 'David',
      lastName: 'Cardigos',
      password: 'test123'
    };

    component.form.setValue(registerData);
    component.submit();

    const req = httpMock.expectOne('api/auth/register');
    req.flush(
      { message: 'Email already exists' },
      { status: 400, statusText: 'Bad Request' }
    );

    expect(mockRouter.navigate).not.toHaveBeenCalled();
    expect(component.onError).toBeTruthy();
  });

});

