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


    it('Etre invalide avec un format d\'email incorrect', () => {
      const emailControl = component.form.get('email');
      emailControl?.setValue('david@.com');
      expect(emailControl?.valid).toBeFalsy();
      expect(emailControl?.hasError('email')).toBeTruthy();
    });

    it('Etre valide avec un format d\'email correct', () => {
      const emailControl = component.form.get('email');
      emailControl?.setValue('test@example.com');
      expect(emailControl?.valid).toBeTruthy();
    });


    it('Etre invalide lorsque vide', () => {
      const firstNameControl = component.form.get('firstName');
      expect(firstNameControl?.valid).toBeFalsy();
      expect(firstNameControl?.hasError('required')).toBeTruthy();
    });

    it('Etre valide avec un prénom valide', () => {
      const firstNameControl = component.form.get('firstName');
      firstNameControl?.setValue('David');
      expect(firstNameControl?.valid).toBeTruthy();
    });


    it('devrait être invalide lorsque vide', () => {
      const lastNameControl = component.form.get('lastName');
      expect(lastNameControl?.valid).toBeFalsy();
      expect(lastNameControl?.hasError('required')).toBeTruthy();
    });

    it('devrait être valide avec un nom valide', () => {
      const lastNameControl = component.form.get('lastName');
      lastNameControl?.setValue('Cardigos');
      expect(lastNameControl?.valid).toBeTruthy();
    });



    it('Etre invalide lorsque vide', () => {
      const passwordControl = component.form.get('password');
      expect(passwordControl?.valid).toBeFalsy();
      expect(passwordControl?.hasError('required')).toBeTruthy();
    });

    it('Etre valide avec un mot de passe valide', () => {
      const passwordControl = component.form.get('password');
      passwordControl?.setValue('test1234546');
      expect(passwordControl?.valid).toBeTruthy();
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




  it('Enregistrer un utilisateur et naviguer vers la rout /login en cas de succès', () => {
    // Arrange
    const registerData = {
      email: 'test@test.com',
      firstName: 'David',
      lastName: 'Cardigos',
      password: 'test123'
    };

    component.form.setValue(registerData);

    // Act
    component.submit();

    // Assert
    const req = httpMock.expectOne('api/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(registerData);


    req.flush(null);

    // Vérifier que la navigation a eu lieu
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
    expect(component.onError).toBeFalsy();
  });

});

